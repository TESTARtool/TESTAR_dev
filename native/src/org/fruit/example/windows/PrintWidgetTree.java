/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.example.windows;

import static org.fruit.alayer.Tags.Desc;
import static org.fruit.alayer.Tags.Role;

import java.io.File;

import org.fruit.Util;
import org.fruit.alayer.Roles;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;
import org.fruit.alayer.windows.UIAStateBuilder;
import org.fruit.alayer.windows.WinProcess;

public class PrintWidgetTree {
	
	public static void main(String[] args){
	
		if(args.length < 1 || !new File(args[0]).exists()){
			System.out.println("Invalid command line arguments!");
			return;
		}
				
		SUT system = WinProcess.fromExecutable(args[0]); // run the given executable
		
		Util.pause(5);
		State state = new UIAStateBuilder().apply(system);   // get the system's current state

		// print the role of each widget and a short description
		for(Widget widget : state){    
			// indent
			for(int i = 0; i < Util.depth(widget); i++)  
				System.out.print("  ");

			// print widget info
			System.out.printf("%s  %s\n", 
					widget.get(Role, Roles.Widget), 
					widget.get(Desc, "<desc unavailable>"));
		}
	
		system.stop();              // shut down the system
	}
}
