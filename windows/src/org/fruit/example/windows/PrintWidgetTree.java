/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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

  public static void main(String[] args) {

    if (args.length < 1 || !new File(args[0]).exists()) {
      System.out.println("Invalid command line arguments!");
      return;
    }

    SUT system = WinProcess.fromExecutable(args[0], false); // run the given executable

    Util.pause(5);
    State state = new UIAStateBuilder().apply(system);   // get the system's current state

    // print the role of each widget and a short description
    for (Widget widget: state) {
      // indent
      for (int i = 0; i < Util.depth(widget); i++) {
        System.out.print("  ");
      }
      // print widget info
      System.out.printf("%s  %s\n",
          widget.get(Role, Roles.Widget),
          widget.get(Desc, "<desc unavailable>"));
    }

    system.stop();              // shut down the system
  }
}
