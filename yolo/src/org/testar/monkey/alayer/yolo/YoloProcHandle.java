package org.testar.monkey.alayer.yolo;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

import org.testar.monkey.alayer.devices.ProcessHandle;

public class YoloProcHandle implements ProcessHandle {
	private final long pid;
	public YoloProcHandle(long pid){ this.pid = pid; }
	public long pid() { return pid; }

	public String name() { return "Process: " + String.valueOf(pid); }

	public void kill() {
		String osName = System.getProperty("os.name").toLowerCase();

		try {
			Process process;
			if (osName.contains("win")) {
				process = Runtime.getRuntime().exec("taskkill /PID " + pid);
			} else {
				process = Runtime.getRuntime().exec("kill " + pid);
			}

			int exitCode = process.waitFor();
			if (exitCode == 0) {
				System.out.println("Process with PID " + pid + " killed successfully.");
			} else {
				System.err.println("Failed to kill process with PID " + pid + ".");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isRunning() {         
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		List<String> inputArguments = runtimeMXBean.getInputArguments();

		for (String inputArgument : inputArguments) {
			if (inputArgument.startsWith("-Dpid=")) {
				String pidStr = inputArgument.substring(6);
				try {
					int runningPid = Integer.parseInt(pidStr);
					if (runningPid == pid) {
						return true;
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}

}
