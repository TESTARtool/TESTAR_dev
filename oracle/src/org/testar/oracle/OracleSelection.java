/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections8.Reflections;

public class OracleSelection {

	private static String[] oraclePackages = {
			"org.testar.oracles.generic.visual",
			"org.testar.oracles.web.accessibility",
			"org.testar.oracles.web.invariants",
	};

	public static List<String> getAvailableOracles() {
		List<String> oracleNames = new ArrayList<>();
		for (String pkg : oraclePackages) {
			try {
				Reflections reflections = new Reflections(pkg);
				Set<Class<? extends Oracle>> oracleClasses = reflections.getSubTypesOf(Oracle.class);

				for (Class<? extends Oracle> oracleClass : oracleClasses) {
					oracleNames.add(oracleClass.getSimpleName());
				}
			} catch (Exception e) {
				System.out.println("Error loading package: " + pkg);
			}
		}
		return oracleNames;
	}

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
