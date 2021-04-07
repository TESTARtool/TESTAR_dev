/*******************************************************************************
 * Copyright (c) 2009, 2020 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Evgeny Mandrikov - initial API and implementation
 *    Fernando Pastor Ricos - adapt original code for TESTAR purposes
 *
 *******************************************************************************/

package org.testar.jacoco;

import java.io.File;

/**
 * https://github.com/jacoco/jacoco/blob/master/org.jacoco.examples/src/org/jacoco/examples/MBeanClient.java
 */


import java.io.FileOutputStream;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.testar.OutputStructure;

/**
 * This example connects to a JaCoCo agent that runs with the option
 * <code>jmx=yes</code> and requests execution data. The collected data is
 * dumped to a local file.
 */
public final class MBeanClient {

	private static final String SERVICE_URL = "service:jmx:rmi:///jndi/rmi://localhost:5000/jmxrmi";

	private MBeanClient() {}
	
	public interface IProxy {
		String getVersion();

		String getSessionId();

		void setSessionId(String id);

		byte[] getExecutionData(boolean reset);

		void dump(boolean reset);

		void reset();
	}

	/**
	 * Execute the example.
	 *
	 * @param args
	 * @throws Exception
	 */
	public static String dumpJaCoCoReport() throws Exception {

		String destFile = OutputStructure.outerLoopOutputDir + File.separator 
				+ "jacoco-"
				+ OutputStructure.executedSUTname + "_sequence_" + OutputStructure.sequenceInnerLoopCount 
				+ ".exec";

		// Open connection to the coverage agent:
		final JMXServiceURL url = new JMXServiceURL(SERVICE_URL);
		final JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
		final MBeanServerConnection connection = jmxc
				.getMBeanServerConnection();

		final IProxy proxy = (IProxy) MBeanServerInvocationHandler
				.newProxyInstance(connection,
						new ObjectName("org.jacoco:type=Runtime"), IProxy.class,
						false);

		// Retrieve JaCoCo version and session id:
		System.out.println("Version: " + proxy.getVersion());
		System.out.println("Session: " + proxy.getSessionId());

		// Retrieve dump and write to file:
		final byte[] data = proxy.getExecutionData(false);
		final FileOutputStream localFile = new FileOutputStream(destFile);
		localFile.write(data);
		localFile.close();

		// Close connection:
		jmxc.close();

		return destFile;
	}

}