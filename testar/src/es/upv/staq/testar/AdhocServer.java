package es.upv.staq.testar;

import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.MouseButtons;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AdhocServer
{
  private static BufferedReader adhocTestServerReader = null;
  private static BufferedWriter adhocTestServerWriter = null;

  private static ServerSocket adhocTestServerSocket = null;
  private static Socket adhocTestSocket = null;

  public static void startAdhocServer() {
    new Thread(){
      public void run(){
        int port = 47357;
        try {
          adhocTestServerSocket = new ServerSocket(port);
          System.out.println("AdhocTest Server started @" + port);
          adhocTestSocket = adhocTestServerSocket.accept();
          System.out.println("AdhocTest Client engaged");
          adhocTestServerReader = new BufferedReader(new InputStreamReader(adhocTestSocket.getInputStream()));
          adhocTestServerWriter = new BufferedWriter(new OutputStreamWriter(adhocTestSocket.getOutputStream()));
        } catch(Exception e){
          stopAdhocServer();
        }
      }
    }.start();
  }

  // by urueda
  public static void stopAdhocServer(){
    if (adhocTestServerSocket != null){
      try {
        if (adhocTestServerReader != null)
          adhocTestServerReader.close();
        if (adhocTestServerWriter != null)
          adhocTestServerWriter = null;
        if (adhocTestSocket != null)
          adhocTestSocket.close();
        adhocTestServerSocket.close();
        adhocTestServerSocket = null;
        System.out.println(" AdhocTest Server sttopped  " );
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static Object[] compileAdhocTestServerEvent(String event){
    //Pattern p = Pattern.compile(BriefActionRolesMap.LC + "\\((\\d+.\\d+),(\\d+.\\d+)\\)");
    Pattern p = Pattern.compile("LC\\((\\d+.\\d+),(\\d+.\\d+)\\)");
    Matcher m = p.matcher(event);
    if (m.find())
      return new Object[]{MouseButtons.BUTTON1,new Double(m.group(1)),new Double(m.group(2))};

    //p = Pattern.compile(BriefActionRolesMap.RC + "\\((\\d+.\\d+),(\\d+.\\d+)\\)");
    p = Pattern.compile("RC\\((\\d+.\\d+),(\\d+.\\d+)\\)");
    m = p.matcher(event);
    if (m.find())
      return new Object[]{MouseButtons.BUTTON3,new Double(m.group(1)),new Double(m.group(2))};

    //p = Pattern.compile(BriefActionRolesMap.T + "\\((.*)\\)");
    p = Pattern.compile("T\\((.*)\\)");
    m = p.matcher(event);
    if (m.find()){
      String text = m.group(1);
      return new Object[]{ KBKeys.contains(text) ? KBKeys.valueOf(text) : text };
    }

    return null;
  }

  public static void waitReaderWriter (Object self) {
    while(adhocTestServerReader == null || adhocTestServerWriter == null) {
      synchronized(self){
        try {
          self.wait(10);
        } catch (InterruptedException e) {}
      }
    }
  }

  public static String adhocRead () throws IOException {
    return adhocTestServerReader.readLine().trim();
  }

  public static void adhocWrite (String message) throws IOException {
    adhocTestServerWriter.write(message + "\r\n");
    adhocTestServerWriter.flush();
  }
}
