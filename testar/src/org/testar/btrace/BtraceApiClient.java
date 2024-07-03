package org.testar.btrace;

import lombok.AllArgsConstructor;
import org.apache.commons.collections.ListUtils;
import org.openqa.selenium.Cookie;
import org.testar.monkey.alayer.webdriver.WdDriver;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class BtraceApiClient {

    private String host;

    private final static String STATUS_STARTED = "STARTED";

    private long startTime;
    private String sequenceId;

    public BtraceApiClient(String host) {
        this.host = host;
    }

    public boolean startRecordingMethodInvocation() throws BtraceApiException{
        startTime = System.currentTimeMillis();
//        if(this.sequenceId!=null) {
//            Cookie sessionCookie = WdDriver.getRemoteWebDriver().manage().getCookieNamed("testarSequenceToken");
//            String sessionId = sessionCookie.getValue();
//            BtraceStartRecordingRequest request = new BtraceStartRecordingRequest(this.host, sessionId);
//            BtraceStartRecordingResponse response = request.send();
//            System.out.println(response);
//        }
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
        this.sequenceId = sessionId;
        long duration = System.currentTimeMillis() - startTime;
        BtraceGetMethodsRequest request = new BtraceGetMethodsRequest(host, sessionId, duration);
        BtraceFinishRecordingResponse response = request.send();

        for (BtraceFinishRecordingResponse.MethodEntry m: (List<BtraceFinishRecordingResponse.MethodEntry>)(response.getMethodsExecuted())){
            System.out.println(m.methodName);
            System.out.println(m.parameterTypes);
            System.out.println(m.className);
            System.out.println("----------------");
        }

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
