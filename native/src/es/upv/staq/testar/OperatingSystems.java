/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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

package es.upv.staq.testar;

/**
 * Enumerates possible operating systems supported by Testar.
 */
public enum OperatingSystems {
    /**
     * Possible Operating Systems.
     */
    UNKNOWN(0), WINDOWS(1), UNIX(2), MAC(3), ANDROID(4),
    WINDOWS_7(5), WINDOWS_10(6), WEBDRIVER(7) ;

    // Internal value of the enum.
    private int value;

    /**
     * Enum constructor.
     * @param value value of the enum.
     */
    OperatingSystems(int value) {
        this.value = value;
    }

    /**
     * String representation of the enumeration type.
     * @return userfriendly name.
     */
    @Override
    public String toString() {
        if (value == 0) {
            return "Unknown";
        } else if (value == 1) {
            return "Windows";
        } else if (value == 2) {
            return "Unix";
        } else if (value == 3) {
            return "Mac";
        } else if (value == 4) {
            return "Android";
        } else if (value == 5) {
            return "Windows 7";
        } else if (value == 6){
            return "Windows 10";
        } else if (value == 7){
            return "WebDriver";
        } else {
            return super.toString();
        }
    }
}
