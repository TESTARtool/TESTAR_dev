package org.testar.securityanalysis.oracles;

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v101.network.Network;
import org.openqa.selenium.devtools.v101.network.model.Headers;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.securityanalysis.NetworkCollector;
import org.testar.securityanalysis.NetworkDataDto;
import org.testar.securityanalysis.SecurityResultWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeaderAnalysisSecurityOracle extends BaseSecurityOracle {
    private int lastSequenceActionNumber = 0;
    private NetworkCollector networkCollector = new NetworkCollector();

    public HeaderAnalysisSecurityOracle(SecurityResultWriter securityResultWriter)
    {
        super(securityResultWriter);
    }

    @Override
    public void addListener(DevTools devTools)
    {
        devTools.addListener(Network.responseReceivedExtraInfo(),
                responseReceived -> {
                    Headers headers = responseReceived.getHeaders();
                    if (!headers.isEmpty()) {
                        NetworkDataDto data = new NetworkDataDto();
                        data.type = "Headers";
                        data.requestId = responseReceived.getRequestId().toString();
                        data.data = new HashMap<>();
                        headers.forEach((key, value) -> {
                            data.data.put(key, value.toString());
                        });
                        networkCollector.addData(data);
                    }
                });
    }

    @Override
    public Verdict getVerdict()
    {
        List<NetworkDataDto> datas = networkCollector.getDataBySequence(lastSequenceActionNumber);
        for (NetworkDataDto data : datas) {
            if (lastSequenceActionNumber < data.sequence)
                lastSequenceActionNumber = data.sequence;

            if (data.type == "Headers") {
                validateHeaderContainsFlag(data.data, "Set-Cookie", "Secure;");
                validateHeaderContainsFlag(data.data, "X-XSS-Protection", "1; mode=block");
                validateHeaderContainsFlag(data.data, "X-Content-Type-Options", "nosniff");
                validateHeaderIsPresent(data.data, "Strict-Transport-Security");
                validateHeaderIsPresent(data.data, "X-Frame-Options");
                validateHeaderIsPresent(data.data, "X-Content-Type-Options");
                validateHeaderIsPresent(data.data, "X-XSS-Protection");
            }
        }

        return Verdict.OK;
    }

    private void validateHeaderContainsFlag(Map<String, String> headers, String name, String flag)
    {
        String header = headers.get(name);
        if (header != null) {
            if (!header.contains(flag)) {
                securityResultWriter.WriteResult(WdDriver.getCurrentUrl(), "614", name + " header does not contain " + flag + " flag");
            }
        }
    }

    private void validateHeaderIsPresent(Map<String, String> headers, String name)
    {
        if (headers.get(name) == null)
            securityResultWriter.WriteResult(WdDriver.getCurrentUrl(), "614", name + " header is not present");
    }
}
