/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.util;

import org.w3c.dom.Node;

public final class AndroidNodeParser {

	public static String getStringAttribute(Node xmlNode, String attributeName) {
		try {
			xmlNode.getAttributes().getNamedItem(attributeName).getNodeValue();
		} catch(Exception e) {
			return "";
		}

		return xmlNode.getAttributes().getNamedItem(attributeName).getNodeValue(); 
	}

	public static Integer getIntegerAttribute(Node xmlNode, String attributeName) {
		try {
			Integer.parseInt(xmlNode.getAttributes().getNamedItem(attributeName).getNodeValue());
		} catch(Exception e) {
			return -1;
		}

		return Integer.parseInt(xmlNode.getAttributes().getNamedItem(attributeName).getNodeValue());
	}

	public static Double getDoubleAttribute(Node xmlNode, String attributeName) {
		try {
			Double.parseDouble(xmlNode.getAttributes().getNamedItem(attributeName).getNodeValue());
		} catch(Exception e) {
			return -1.0;
		}

		return Double.parseDouble(xmlNode.getAttributes().getNamedItem(attributeName).getNodeValue());
	}

	public static Boolean getBooleanAttribute(Node xmlNode, String attributeName) {
		try {
			Boolean.parseBoolean(xmlNode.getAttributes().getNamedItem(attributeName).getNodeValue());
		} catch(Exception e) {
			return false;
		}

		return Boolean.parseBoolean(xmlNode.getAttributes().getNamedItem(attributeName).getNodeValue());
	}
}
