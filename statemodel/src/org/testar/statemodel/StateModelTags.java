/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.statemodel;

import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.TaggableBase;

public class StateModelTags extends TaggableBase  {

	private static final long serialVersionUID = 2783951837457971138L;

	private StateModelTags () {}

	/**
	 * State Model settings 
	 */

	public static final Tag<Boolean> StateModelEnabled = Tag.from("StateModelEnabled", Boolean.class, 
			"Enable or disable the State Model feature");

	public static final Tag<String> DataStore = Tag.from("DataStore", String.class, 
			"The graph database we use to store the State Model: OrientDB");

	public static final Tag<String> DataStoreType = Tag.from("DataStoreType", String.class, 
			"The mode we use to connect to the database: remote or plocal");

	public static final Tag<String> DataStoreServer = Tag.from("DataStoreServer", String.class, 
			"IP address to connect if we desired to use remote mode");

	public static final Tag<String> DataStoreDirectory = Tag.from("DataStoreDirectory", String.class, 
			"Path of the directory on which local OrientDB exists, if we use plocal mode");

	public static final Tag<String> DataStoreDB = Tag.from("DataStoreDB", String.class, 
			"The name of the desired database on which we want to store the State Model.");

	public static final Tag<String> DataStoreUser = Tag.from("DataStoreUser", String.class, 
			"User credential to authenticate TESTAR in OrientDB");

	public static final Tag<String> DataStorePassword = Tag.from("DataStorePassword", String.class, 
			"Password credential to authenticate TESTAR in OrientDB");

	public static final Tag<String> DataStoreMode = Tag.from("DataStoreMode", String.class, 
			"Indicate how TESTAR should store the model objects in the database: instant, delayed, hybrid, none");

	public static final Tag<String> ActionSelectionAlgorithm = Tag.from("ActionSelectionAlgorithm", String.class, 
			"State Model Action Selection mechanism to explore the SUT: random or unvisited");

	public static final Tag<Boolean> StateModelStoreWidgets = Tag.from("StateModelStoreWidgets", Boolean.class, 
			"Save all widget tree information in the State Model every time TESTAR discovers a new Concrete State");

	public static final Tag<Boolean> ResetDataStore = Tag.from("ResetDataStore", Boolean.class, 
			"WARNING: Delete all existing State Models from the selected database before creating a new one");

}