## README

This is the github development root folder for TESTAR development. 
The software can be build with both ant and gradle.

### Required tools to build the software

In order to build the native code, a view manual steps need to be executed;

1. In order to build the windows native code, Nmake and the compile for Microsoft visual studio are required.
These tools can be downloaded using the following [link](https://www.visualstudio.com/thank-you-downloading-visual-studio/?sku=BuildTools&rel=15#).
2. Install the Visual Studio tools on your machine (remember the path where the tools are installed)
3. Download [compile_w10.bat](https://github.com/florendg/testar_floren/releases/download/PERFORMANCE/compile_w10.bat) 
and [clean_w10.bat](https://github.com/florendg/testar_floren/releases/download/PERFORMANCE/clean_w10.bat)
4. Copy clean.bat and compile.bat to the folder windows/native_src within the TESTAR project
5. Adapt compile.bat and clean.bat. Set *PATH* to the installation folder used in step 2.
CALL "C:<*PATH*>\2017\BuildTools\Common7\Tools\VsDevCmd.bat" -arch=x64

It should be possible to build the project using the instructions provided in the next section

### Gradle build

To build the Gradle project and run it execute the following procedure;

1. Run `.\gradlew installDist` in the root of the project
2. Change directory to testar/target/install/bin
3. Run testar.bat

It is also possible to generate a zip file containing TESTAR. This zip can be extracted on any other machine
that has a 64-bit Windows operating system and Visual Studio redistributable installed. A proper way of using
TESTAR is to run the tool in a virtual-machine.
To build the zip execute the following command.

1. Run `.\gradlew distZip` in the root of the project. 
2. Extract the zip on the machine where TESTAR is used.

NOTE: TESTAR requires Visual Redistributable which can be downloaded from the following
 [link](https://go.microsoft.com/fwlink/?LinkId=746572) .Also a JAVA 1.8 JDK is required.

#### Running Gradle in Eclipse
The following procedure has been performed

1. Create a new empty workspace for Eclipse in a folder which is not the folder that contains the source
code.
2. Select File -> Import to open the import dialog
3. Select Gradle -> Existing Gradle project to open te import dialog 
4. Select the folder that contains the root of the source code and start the import


#### Running TESTAR from Gradle
TESTAR can be started using a gradle command from the root of the project.
1. .\gradlew runTestar

##### In Eclipse
Within Eclipse, TESTAR can be executed by running the task runTestar which is available in the map custom_testar.
To debug the application with the runTestar task, provide your onw run configuration in which the option -DDEBUG is set.

#### Debug TESTAR from Gradle
In order to debug the TESTAR code, you must run;
1. .\gradlew -DDEBUG=true runTestar.  

Optionally you can build TESTAR (.\gradlew -DDBEBUG=true distZip ), copy the result to 
the machine where you want to run TESTAR and run TESTAR on the target machine. This allows
the user to debug TESTAR from a different machine. 

## Known issue
- Currently, only the protocols ``Protocol_desktop_generic``, ``Protocol_desktop_generic`` and ``accessibility_wcag2ict``
support the graph database. Other protocols need to be adapted  (see issue #52)

## Graph database support
This version of TESTAR supports the output of data to an http://orientdb.com database.
Currently Widget's, Actions, States and there relation are recorded to the graph database.

### Restrictions on the use of the graph database
The model currently implemented consists of States, Actions and Widgets. The widgets are stored from
the method deriveActions. The protocols ``Protocol_desktop_generic``, ``Protocol_desktop_generic`` and ``accessibility_wcag2ict`` 
contain an implementation which shows how the method storeWidget could be used.
When the storeWidget method is not called, TESTAR will raise an exception and stop working.

### using graphdb on the local filesystem
The easiest way to use the graph database support is to write the database to the local file system
After starting TESTAR, it's required to configure the database settings in the tab "GraphDB".
- url : plocal:&lt;path&gt; (for instance plocal:output/testdb) 
- user: admin
- password: admin
- Check the checkbox.

### using graphdb with orientdb server
In order to use the graphdb feature it's advised to install a graph database on your machine  The current implementation 
of TESTAR has a backend for Orientdb. You can download the
community edition from [orientdb](orientdb.com). Follow the installation instructions to install
the database on your machine. 

When orientdb is started the first time. The root password needs to be configured. Make sure you remember thes password.

In order to use the graphdb feature. A database must be created in Orientdb. To do this follow the following procedure;
- Start the database server (ORIENTDB_HOME/bin/server.bat)
- Start orientdb studio in a webbrowser [http://localhost:2480](http://localhost:2480)
- Choose "New DB" and provide the name, root user and password. (The database will also get a default admin/adimin 
user/password).

After starting TESTAR, it's required to configure the database settings in the tab "GraphDB".
- url : remote:<hostname>/&lt;database name> (for instance remote:/localhost/demo)
- user: admin
- password: admin
- Check the checkbox.

When TESTAR finishes, the data be inspected in the database. The easiest way to see the complete
graph is to type the following query in the graph tab "Select * From E". This will display the complete
graph.

### Requirements for a protocol when using GraphDB.

A part of the interaction with the graph database occurs in the method *deriveActions*. In this method, the available 
widgets are stored as they are derived within the protocol. When using the graph database extension, the user needs
to be aware the widgets are stored. When this is not done, the model in the database is incomplete. A sample can be 
found in the protocol *desktop_generic_graphdb.java*.

### Exploring the graph database with Gremlin.

[Gremlin](http://tinkerpop.apache.org/docs/current/reference/#_tinkerpop3) is a graph traversel engine which can be used 
to query a graph database. OrientDB supports Gremlin and provides an implementation of the traversal engine in it's 
community release.

To start Gremlin run the following command; orientdb-gremlin. This command is delivered with the community 
edition of Orientdb.

See the manuals of orientdb to learn about the possibilities to query the model.
