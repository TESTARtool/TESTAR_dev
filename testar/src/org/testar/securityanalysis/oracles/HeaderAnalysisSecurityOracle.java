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
        System.out.println("HeaderAnalysisSecurityOracle, getVerdict()");

        List<NetworkDataDto> datas = networkCollector.getDataBySequence(lastSequenceActionNumber);
        for (NetworkDataDto data : datas) {
            if (lastSequenceActionNumber < data.sequence)
                lastSequenceActionNumber = data.sequence;

            if (data.type == "Headers") {
                for (Map.Entry<String, String> header : data.data.entrySet()) {
                    if (header.getKey().equals("Set-Cookie")) {
                        if (!header.getValue().contains("Secure;")) {
                            securityResultWriter.WriteResult(WdDriver.getCurrentUrl(), "614", "cookie not set secure: " + header.getKey() + " " + header.getValue());
                            System.out.println("Header insecure:");
                            System.out.println(header.getValue());
                        } else {
                            System.out.println("Header secure:");
                            System.out.println(header.getValue());
                        }
                    }
                }
            }
        }

        return Verdict.OK;
    }
}
