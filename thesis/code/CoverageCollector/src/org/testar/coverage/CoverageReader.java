package org.testar.coverage;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CoverageReader {

	private static final Logger logger = LoggerFactory.getLogger(CoverageReader.class);

	// Credential data to access remote machine
	private String username;
	private String hostname;
	private int port;
	private String keyPath;

	/**
	 * Instantiates a new coverage reader.
	 *
	 * @param username the user  name
	 * @param hostname the host name
	 * @param port the port
	 * @param keyPath the key path
	 */
	public CoverageReader(String username, String hostname, int port, String keyPath) {
		this.username = username;
		this.hostname = hostname;
		this.port = port;
		this.keyPath = keyPath;
	}

	/**
	 * Reset coverage data on SUT.
	 *
	 * @param resetCommand the reset command
	 * @throws IOException when execution of remote command fails
	 */
	public void resetCoverageData(String resetCommand) throws IOException {
		logger.info("Reset coverage data");
		executeRemoteCommand(resetCommand);
	}

	/**
	 * Dump coverage data from SUT to remote machine.
	 *
	 * @param dumpCommand the dump command
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void dumpCoverageData(String dumpCommand) throws IOException {
		logger.info("Dump coverage data");
		executeRemoteCommand(dumpCommand);
	}

	/**
	 * Retrieve coverage data.
	 *
	 * @param sequence the sequence
	 * @param actions the actions
	 * @param command the command
	 * @param xmlFilename the XML filename
	 * @return the coverage data
	 */
	public CoverageData retrieveCoverageData(int sequence, int actions, String command, String xmlFilename) {
		logger.info("Retrieve coverage data");
		
		RemoteExecutor remoteExecutor = null;
		ByteArrayInputStream inStream = null;
		ByteArrayOutputStream outStream = null;
				
		try {
			remoteExecutor = new RemoteExecutor(hostname, port, username, null, keyPath);
			remoteExecutor.connectWithKeys();
			String result = remoteExecutor.execCommand(command);
			logger.debug("Command result:\n{}", result);
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			SAXParser saxParser = factory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			CoverageXmlHandler handler = new CoverageXmlHandler(sequence, actions);
			xmlReader.setContentHandler(handler);
			
			// OutputStream
			outStream = new ByteArrayOutputStream();
			
			remoteExecutor.downloadStream(xmlFilename, outStream);
			
			inStream = new ByteArrayInputStream(outStream.toByteArray());
			InputSource xmlInputSource = new InputSource(inStream);
			xmlReader.parse(xmlInputSource);
			
			return handler.getCoverageData();
		} catch (IOException | SAXException | ParserConfigurationException e) {
			logger.error("Failed to execute command '" + command + "' on " + hostname + ": " + e.getMessage(), e);
		} finally {
			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					// Nothing can be done here
				}
			}
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					// Nothing can be done here
				}
			}
			System.out.println();
			if (remoteExecutor != null) {
				remoteExecutor.disconnect();
			}
		}
		return null;
	}

	/**
	 * Execute a remote command.
	 *
	 * @param command the command to execute
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void executeRemoteCommand(String command) throws IOException {
		logger.info("Execute remote command");
		
		RemoteExecutor remoteExecutor = null;
		
		try {
			remoteExecutor = new RemoteExecutor(hostname, port, username, null, keyPath);
			remoteExecutor.connectWithKeys();
			String result = remoteExecutor.execCommand(command);
			logger.debug("Command result:\n{}", result);
						
		} finally {
			if (remoteExecutor != null) {
				remoteExecutor.disconnect();
			}
		}
	}

	public static void main(String args[]) {
		// Credentials
		String usernameSUT = args[0];
		String hostnameSUT = args[1];
		String keyPathSUT = args[2];
		int port = 22;

		logger.info("Execute remote command");
		
		RemoteExecutor remoteExecutor = null;
		
		try {
			remoteExecutor = new RemoteExecutor(hostnameSUT, port, usernameSUT, null, keyPathSUT);
			remoteExecutor.connectWithKeys();
			String result = remoteExecutor.execCommand("ls -l");
			logger.debug("Command result:\n{}", result);
						
		} catch (IOException e) {
			logger.error("Failed to execute command: " + e.getMessage(), e);
		} finally {
			if (remoteExecutor != null) {
				remoteExecutor.disconnect();
			}
		}
	}
}
