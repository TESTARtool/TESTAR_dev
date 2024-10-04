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

package org.testar.coverage.jacoco;

import java.io.FileOutputStream;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * https://github.com/jacoco/jacoco/blob/master/org.jacoco.examples/src/org/jacoco/examples/MBeanClient.java
 * 
 * This example connects to a JaCoCo agent that runs with the option
 * <code>jmx=yes</code> and requests execution data. The collected data is
 * dumped to a local file.
 */
public final class MBeanClient {
	private static final Logger logger = LogManager.getLogger();

	private String service_url;

	public MBeanClient(String service_ip_address, int service_port) {
		// Initialize the JMX service to connect with the Jacoco Agent
		service_url = "service:jmx:rmi:///jndi/rmi://" + service_ip_address + ":" + service_port + "/jmxrmi";
	}

	public interface IProxy {
		String getVersion();

		String getSessionId();

		void setSessionId(String id);

		byte[] getExecutionData(boolean reset);

		void dump(boolean reset);

		void reset();
	}

	/**
	 * Use JMX service to connect with the JVM and extract one JaCoCo report file.
	 * IF success return the string that represents the path of this jacoco.exec file,
	 * ELSE return empty string.
	 *
	 * @param destFile
	 * @throws Exception
	 * @return string that contains extracted jacoco.exec file
	 */
	public String dumpJacocoReport(String destJacocoFileName) {
		try {
			// Open connection to the coverage agent:
			final JMXServiceURL url = new JMXServiceURL(service_url);
			final JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			final MBeanServerConnection connection = jmxc.getMBeanServerConnection();

			final IProxy proxy = (IProxy) MBeanServerInvocationHandler
					.newProxyInstance(connection, new ObjectName("org.jacoco:type=Runtime"), IProxy.class, false);

			// Retrieve dump and write to file:
			final byte[] data = proxy.getExecutionData(false);
			final FileOutputStream localFile = new FileOutputStream(destJacocoFileName);
			localFile.write(data);
			localFile.close();

			logger.trace("MBeanClient extracted a jacoco report exec file: " + destJacocoFileName);

			// Close connection:
			jmxc.close();

		} catch(Exception e) {
			logger.error("MBeanClient was not able to dump the jacoco exec file " + destJacocoFileName);
			// return an empty string to indicate we didn't create any jacoco.exec report
			return "";
		}

		return destJacocoFileName;
	}
}
