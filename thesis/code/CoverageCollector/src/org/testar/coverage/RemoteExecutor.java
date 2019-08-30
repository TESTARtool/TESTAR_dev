package org.testar.coverage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class RemoteExecutor {

	private String username;

	private String password;

	private String hostname;

	private int portNumber;

	private JSch jsch = new JSch();

	private Session session;

	private ChannelSftp sftpChannel;

	private boolean isConnected = false;

	private String currentPath;

	private int readTimeout = 10000;
	private int connectTimeout = 30000;

	private File privateKey = null;

	private byte[] privateKeyArray = null;

	/**
	 * Create a SSH connection object. First call connect() to use this connection.
	 * 
	 * @param hostname
	 * @param port
	 * @param username
	 * @param password
	 */
	public RemoteExecutor(String hostname, int port, String username, String password) {
		this.hostname = hostname;
		this.portNumber = port;
		this.username = username;
		this.password = password;
	}

	/**
	 * Create a SSH connection object. First call connect() or connectWithKeys() to
	 * use this connection.
	 * 
	 * 
	 * @param hostname
	 * @param port
	 * @param username
	 * @param password
	 * @param privateKeyPath
	 * @throws IOException
	 */
	public RemoteExecutor(String hostname, int port, String username, String password, String privateKeyPath)
			throws IOException {
		this.hostname = hostname;
		this.portNumber = port;
		this.username = username;
		this.password = password;
		this.privateKey = new File(privateKeyPath);
		this.privateKeyArray = readKey(this.privateKey);
	}

	/**
	 * Set read timeout in milliseconds (default: 10000)
	 *
	 * @param readTimeout the new read timeout
	 */
	/*
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	/**
	 * Sets the connect timeout in milliseconds (default: 30000).
	 *
	 * @param connectTimeout the new connect timeout
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * Connect to the server using private/public keys.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void connectWithKeys() throws IOException {
		if (privateKey == null) {
			throw new IOException("No private key provided");
		}
		boolean functionFinished = false;
		try {
			session = jsch.getSession(username, hostname, portNumber);
			session.setServerAliveCountMax(0);
			session.setServerAliveInterval(readTimeout);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setConfig("PreferredAuthentications", "publickey");

			jsch.addIdentity(username, privateKeyArray, null, new byte[0]);

			UserInfoPassword ui = new UserInfoPassword();
			session.setUserInfo(ui);
			session.connect(connectTimeout);

			if (session.isConnected()) {
				sftpChannel = (ChannelSftp) session.openChannel("sftp");
				sftpChannel.connect(connectTimeout);
				sftpChannel.setXForwarding(false);

				setConnectedStatus();
				pwd(); // set our current path
			}
			functionFinished = true;
		} catch (JSchException e) {
			throw new IOException("Error connecting with keys: " + e.getMessage());
		} finally {
			// check exception situation
			if (!functionFinished) {
				disconnect();
			}
			// else check connection status
			else if (!isConnected()) {
				disconnect();
				throw new IOException("Connection establishment failed and aborted");
			}
		}
	}

	/**
	 * Disconnect from the server. This should ALWAYS be called when shutting down
	 * and a successful connection was made.
	 */
	public void disconnect() {
		try {
			if (sftpChannel != null) {
				sftpChannel.disconnect();
				sftpChannel = null;
			}
		} catch (Exception e) {
		}
		try {
			if (session != null) {
				session.disconnect();
				session = null;
			}
		} catch (Exception e) {
		}
		setConnectedStatus();
	}

	/**
	 * Sets the connected status which is based on a connected session and connected
	 * sftp-channel.
	 */
	private synchronized void setConnectedStatus() {
		isConnected = false;
		if (session != null && session.isConnected()) {
			if (sftpChannel != null && sftpChannel.isConnected()) {
				isConnected = true;
			}
		}
	}

	/**
	 * Are we still connected to the server?.
	 *
	 * @return true, if we are still connected to the server
	 */
	public synchronized boolean isConnected() {
		setConnectedStatus();
		return isConnected;
	}

	/**
	 * Copy the specified remote file to the specified output stream (SFTP).
	 *
	 * @param from the remote file
	 * @param os   the output stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void downloadStream(String from, OutputStream os) throws IOException {
		if (isConnected()) {
			try {
				sftpChannel.get(from, os);
				return;
			} catch (SftpException e) {
				setConnectedStatus();
				throw new IOException(
						"DOWNLOAD command failed: " + e.getMessage() + ". Arguments: " + from + " to stream");
			}
		}
		throw new IOException("Not connected");
	}

	/**
	 * Execute a shell command on the remote server.
	 *
	 * @param command the command
	 * @return the string result from the stdout
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String execCommand(String command) throws IOException {
		if (isConnected()) {
			ChannelExec execChannel = null;
			try {
				execChannel = (ChannelExec) session.openChannel("exec");

				execChannel.setCommand(command);
				execChannel.setInputStream(null);
				execChannel.setErrStream(System.err);

				InputStream inputStream = execChannel.getInputStream();
				InputStream errorInputStream = execChannel.getErrStream();

				execChannel.connect(); // Triggers the command to be executed

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream, Charset.defaultCharset()));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				BufferedReader errReader = new BufferedReader(
						new InputStreamReader(errorInputStream, Charset.defaultCharset()));
				StringBuilder errSb = new StringBuilder();
				while ((line = errReader.readLine()) != null) {
					errSb.append(line);
				}

				if (errSb.length() != 0) {
					throw new IOException("Exec command failed with: " + errSb.toString() + ". Other output: "
							+ sb.toString() + ". Argument: " + command);
				}
				return sb.toString();
			} catch (JSchException e) {
				setConnectedStatus();
				throw new IOException("Exec command failed: " + e.getMessage() + ". Argument: " + command);
			} finally {
				if (execChannel != null) {
					execChannel.disconnect();
				}
			}
		}
		throw new IOException("Not connected");
	}

	/**
	 * Retrieve the current selected path (SFTP).
	 *
	 * @return the current selected path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String pwd() throws IOException {
		if (isConnected()) {
			try {
				currentPath = sftpChannel.pwd();
				return currentPath;
			} catch (SftpException e) {
				setConnectedStatus();
				throw new IOException("PWD command failed: " + e.getMessage());
			}
		}
		throw new IOException("Not connected");
	}
	
	private class UserInfoPassword implements com.jcraft.jsch.UserInfo {
		@Override
		public String getPassword() {
			return password;
		}

		@Override
		public boolean promptYesNo(String str) {
			return true;
		}

		@Override
		public String getPassphrase() {
			return null;
		}

		@Override
		public boolean promptPassphrase(String message) {
			return true;
		}

		@Override
		public boolean promptPassword(String message) {
			return true;
		}

		@Override
		public void showMessage(String message) {
		}
	}

	private byte[] readKey(File key) throws IOException {
		InputStream is = null;
		byte[] bytes = new byte[(int) key.length()];
		try {
			is = new FileInputStream(key.getAbsolutePath());
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file " + key.getName());
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return bytes;
	}
}
