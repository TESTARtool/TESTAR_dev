/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2025 Open Universiteit - www.ou.nl
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

package org.testar.environment;

/**
 * Provides access to the environment in which the application is running.
 * This interface should be realized by an operating specific implementation. Since the core module is standalone module
 * without dependencies towards other modules, we specific the interface in the core module so that we can
 * use the IEnvironment within the core module. During the initialization phase of the application the realization of
 * IEnvironment needs to be set. This construction creates an abstraction layer between the logic and the operating
 * system on which the application is running.
 */
public class Environment {
    private static IEnvironment instance;

    /**
     * Get the environment interface.
     * @return The environment interface or UnknownEnvironment.
     */
    public static IEnvironment getInstance() {
        return (instance != null) ? instance : new UnknownEnvironment();
    }

    /**
     * Sets the actual implementation of the interface.
     * @param implementation The concrete implementation of the interface.
     */
    public static void setInstance(IEnvironment implementation) {
        if (implementation == null) {
            throw new IllegalArgumentException("Environment implementation cannot be set to null");
        }
        instance = implementation;
    }

}
