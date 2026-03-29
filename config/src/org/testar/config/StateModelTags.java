/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.config;

import org.testar.core.tag.Tag;
import org.testar.core.tag.TaggableBase;

public class StateModelTags extends TaggableBase {

    private static final long serialVersionUID = 2783951837457971138L;

    private StateModelTags() { }

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
