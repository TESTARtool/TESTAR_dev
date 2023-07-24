package org.fruit.monkey.btrace;

import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.ListUtils;
import org.openqa.selenium.Cookie;
import org.testar.monkey.alayer.webdriver.WdDriver;

@AllArgsConstructor
public class BtraceApiClient {

    private String host;

    private final static String STATUS_STARTED = "STARTED";

    private long startTime;

    public BtraceApiClient(String host) {
        this.host = host;
    }

    public boolean startRecordingMethodInvocation() throws BtraceApiException{
        startTime = System.currentTimeMillis();
        return true;
    }

    public List<MethodInvocation> finishRecordingMethodInvocation() throws BtraceApiException {
        for (Cookie c: WdDriver.getRemoteWebDriver().manage().getCookies()) {
            System.out.println(c);
        }
        Cookie sessionCookie = WdDriver.getRemoteWebDriver().manage().getCookieNamed("testarSequenceToken");
        if (startTime == 0 || sessionCookie == null) {
            System.out.println("Wrong session ");
            return ListUtils.EMPTY_LIST;
        }

        String sessionId = sessionCookie.getValue();
        long duration = System.currentTimeMillis() - startTime;
        BtraceGetMethodsRequest request = new BtraceGetMethodsRequest(host, sessionId, duration);
        BtraceFinishRecordingResponse response = request.send();

        return convertToMethodInvocations(response.getMethodsExecuted());
    }

    private List<MethodInvocation> convertToMethodInvocations(List<BtraceFinishRecordingResponse.MethodEntry> methodEntries) {
        Map<BtraceFinishRecordingResponse.MethodEntry, Long> countedMethodEntries =
                methodEntries.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        return countedMethodEntries.entrySet()
                                   .stream()
                                   .map(entry -> new MethodInvocation(entry.getKey().getClassName(),
                                                                            entry.getKey().getMethodName(),
                                                                            entry.getKey().getParameterTypes(),
                                                                            entry.getValue())).collect(Collectors.toList());
    }
}
