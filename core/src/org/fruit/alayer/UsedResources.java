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


package org.fruit.alayer;

/**
 * Container to save used resources for an action.
 */
public class UsedResources {

    private final long userCPU;
    private final long systemCPU;
    private final double RAMBase;
    private final double RAMPeak;

    /**
     * Constructs new instance of a UsedResources object.
     * @param userCPU cpu time spent in user space.
     * @param systemCPU cpu time spent in system space
     * @param ramBase minimum used memory.
     * @param ramPeak Max Memory usage of the SUT during test execution.
     */
    public UsedResources(long userCPU, long systemCPU, double ramBase, double ramPeak) {
        this.userCPU =userCPU;
        this.systemCPU = systemCPU;
        this.RAMBase = ramBase;
        this.RAMPeak = ramPeak;
    }

    @Override
    public String toString() {
        return "{ userCPU:" + userCPU + ",systemCPU: " + systemCPU + ",ramBase:" + RAMBase + ",ramPeak:" + RAMPeak + "}";
    }
}
