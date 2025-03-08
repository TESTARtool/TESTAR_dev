/***************************************************************************************************
 *
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.oracles;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class OracleSelection {

	private static String[] oraclePackages = {
			"org.testar.oracles.generic.visual",
			"org.testar.oracles.web.accessibility",
	};

	public static List<Oracle> loadExtendedOracles(String selectedOracles) {
		List<Oracle> oraclesList = new ArrayList<>();

		// Split the input string by commas to get individual oracle names
		String[] oracleNames = selectedOracles.split(",");

		for (String oracleName : oracleNames) {
			oracleName = oracleName.trim();

			for (String pkg : oraclePackages) {
				try {
					// Construct the full class name dynamically
					String fullClassName = pkg + "." + oracleName;

					// Use reflection to dynamically load the class
					Class<?> oracleClass = Class.forName(fullClassName);

					// Check if the class implements the Oracle interface
					if (Oracle.class.isAssignableFrom(oracleClass)) {
						// Instantiate the oracle (assuming a default constructor)
						Constructor<?> constructor = oracleClass.getDeclaredConstructor();
						Oracle oracleInstance = (Oracle) constructor.newInstance();
						oraclesList.add(oracleInstance);
						break;
					}
				} catch (ClassNotFoundException e) {
					// Continue trying other packages
				} catch (Exception e) {
					System.out.println("Error instantiating " + oracleName + ": " + e.getMessage());
				}
			}
		}

		return oraclesList;
	}

}
