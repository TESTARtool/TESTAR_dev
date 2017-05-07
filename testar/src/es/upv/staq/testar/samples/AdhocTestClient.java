/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2015):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This sample is distributed FREE of charge under the TESTAR license, as an open        *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package es.upv.staq.testar.samples;

import java.io.*;
import java.net.*;

import org.fruit.alayer.actions.BriefActionRolesMap;
import org.fruit.alayer.devices.KBKeys;

/**
 * AdhocTest client example.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class AdhocTestClient {

	private static final String x = "280.0"; // event parameter
	private static final String y = "395.0"; // event parameter
	private static final KBKeys key = KBKeys.VK_K; // event parameter
	private static final String text = "something"; // event parameter
	
	public static void main(String argv[]) {
		boolean notrun = true;
		Socket socketClient = null;
		do{
			try {
				socketClient = new Socket("localhost", 47357); // TESTAR must be on "AdhocTest" mode
				System.out.println("AdhocTest client engaged");
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(socketClient.getInputStream()));
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(socketClient.getOutputStream()));
				notrun = false;
				int i = 0;
				boolean alive = true;
				String action, result;
				while (reader.readLine().trim().equals("READY") && alive){
					if (i == 0)
						//action = BriefActionRolesMap.LC + "(" + x + "," + y + ")"; // mouse_left_click at (x,y) point
						action = "LC(" + x + "," + y + ")"; // mouse_left_click at (x,y) point
					else
					  //action = BriefActionRolesMap.T + "(" + /*key*/ text + ")"; // adhoc test sequence consisting of a repeating event
					  action = "T(" + /*key*/ text + ")"; // adhoc test sequence consisting of a repeating event
					i++;
					writer.write(action + "\r\n");
					writer.flush();
					result = reader.readLine().trim();
					if (result.equals("???"))
						System.out.println("[" + i + "] Unrecognised action: " + action);
					else if (result.equals("404"))
						System.out.println("[" + i + "] Invalid action:      " + action);
					else if (result.equals("FAIL"))
						System.out.println("[" + i + "] Action failed:       " + action);
					else if (result.equals("OK"))
						System.out.println("[" + i + "] Action succeded:     " + action);
					else{
						System.out.println("[" + i + "] Unexpected server token: " + result);
						alive = false;
					}
				}
			} catch (java.io.IOException e){
				System.out.println("AdhocTest client waiting for server ...");
			} catch (java.lang.NullPointerException npe){} // socket channels vanished
			if (socketClient != null){
				try {
					socketClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("AdhocTest client finished");
		} while(notrun); 
	}

}
