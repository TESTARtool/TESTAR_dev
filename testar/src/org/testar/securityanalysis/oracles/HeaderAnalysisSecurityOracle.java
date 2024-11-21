/***************************************************************************************************
 *
 * Copyright (c) 2022 - 2024 Open Universiteit - www.ou.nl
 * Copyright (c) 2022 - 2024 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.securityanalysis.oracles;

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v130.network.Network;
import org.openqa.selenium.devtools.v130.network.model.Headers;
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
