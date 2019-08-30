import java.io.IOException;

import org.testar.coverage.RemoteExecutor;

public class TestRemote {

	public static void main(String[] args) {
		RemoteExecutor remoteExecutor = null;
		String restartCommand = "/home/test/restartSUT";
		String usernameSUTServer = "eddy";
		String hostnameSUTServer = "192.168.1.35";
		String keyPathSUTServer = "c:\\users\\testar\\.ssh\\id_test_rsa";
		int port = 22;
		try {
			remoteExecutor = new RemoteExecutor(hostnameSUTServer, port, usernameSUTServer, null, keyPathSUTServer);
			remoteExecutor.connectWithKeys();
			String result = remoteExecutor.execCommand(restartCommand);
			System.out.println("Command result:\n" + result);
			
		} catch (IOException e) {
			System.out.println("Failed to execute command '" + restartCommand + "' on " + hostnameSUTServer + ": " + e.getMessage());
		} finally {
			if (remoteExecutor != null) {
				remoteExecutor.disconnect();
			}
		}


	}

}
