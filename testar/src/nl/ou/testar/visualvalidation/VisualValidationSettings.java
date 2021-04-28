/***************************************************************************************************
 *
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
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

package nl.ou.testar.visualvalidation;

import nl.ou.testar.visualvalidation.ocr.OcrConfiguration;
import org.testar.settings.ExtendedSettingBase;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VisualValidationSettings extends ExtendedSettingBase<VisualValidationSettings> {
    public Boolean enabled;
    public OcrConfiguration ocrConfiguration;
    // The selected protocol will be set automatically when we initialize TESTAR.
    public String protocol;

    @Override
    public int compareTo(VisualValidationSettings other) {
        int res = -1;
        if ((enabled.equals(other.enabled)) &&
                (ocrConfiguration.compareTo(other.ocrConfiguration) == 0)) {
            res = 0;
        }
        return res;
    }

    @Override
    public String toString() {
        return "VisualValidationSettings{" +
                "enabled=" + enabled +
                '}';
    }

    public static VisualValidationSettings CreateDefault() {
        VisualValidationSettings DefaultInstance = new VisualValidationSettings();
        DefaultInstance.enabled = true;
        DefaultInstance.ocrConfiguration = OcrConfiguration.CreateDefault();
        return DefaultInstance;
    }
}
