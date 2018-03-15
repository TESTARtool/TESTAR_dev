/***************************************************************************************************
*
* Copyright (c) 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* 3. Neither the name of the copyright holder nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/


package es.upv.staq.testar.samples;

import java.io.*;
import java.net.*;

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
