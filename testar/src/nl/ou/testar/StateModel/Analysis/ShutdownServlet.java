package nl.ou.testar.StateModel.Analysis;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;

@SuppressWarnings("serial")
public class ShutdownServlet extends HttpServlet {
	
	private final Server server;
	
	public ShutdownServlet(Server server) {
		this.server = server;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Shutting down the server...");
            new Thread() {
                @Override
                public void run() {
                    try {
                        server.stop();
                    } catch (Exception ex) {
                        System.out.println("Failed to stop Jetty server");
                    }
                }
            }.start();
	}
	
	public boolean isServerRunning() {
		return !server.isStopped();
	}

}