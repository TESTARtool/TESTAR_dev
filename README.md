## README

This is the github development root folder for TESTAR development. 
The software can be build with both ant and gradle.

### Gradle build

To build the gradle project and run it execute the following procedure;

1. run gradle installDist in the root of the project
2. change directory to testar/target/install/bin
3. run testar.bat

#### Running Gradle in Eclipse
To use Gradle within Eclipse the
[eclipse plugin](https://docs.gradle.org/3.3/userguide/eclipse_plugin.html) is added to the root project. The following
steps need to be performed to start development in Eclipse;
1. Open a command prompt in the root of the project
2. Execute `gradle eclipse` (this will generate the required Eclipse information)
3. Open Eclipse (make sure the workspace folder is outside the project folder) and choose File -&gt; Import existing project into workspace
4. Browse to the directory where the Testar souces are located and import the project
5. Right-Click on the project -&gt; Select Configure -&gt; Add gradle nature.

After these steps it is possible to execute gradle tasks. A separate view for Gradle is available.

The procedure presented in this chapter requires the installation of the [buildship](https://projects.eclipse.org/projects/tools.buildship) plugin. This plugin comes pre-installed with Eclipse Neon.

## Known issue
- The protocol needs to be compiled. Therefor choose edit protocol, compile and
close the dialog. The protocol .class is now available.
- Testar hangs at the end of the execution.

## GraphDb support
This version of Testar supports the output of data to an http://orientdb.com database.
Currently only the state in each step is exported. 

### Configuring graphdb
In order to use the graphdb feature it's advised to install a graph database on your machine  The current implementation 
of Testar has a backend for Orientdb. You can download the
community edition from [orientdb](orientdb.com). Follow the installation instructions to install
the database on your machine. 

When orientdb is started the first time. The root password needs to be configured. Make sure you remember thes password.

In order to use the graphdb feature. A database must be created in Orientdb. To do this follow the following procedure;
- Start the database server (ORIENTDB_HOME/bin/server.bat)
- Start orientdb studio in a webbrowser [http://localhost:2480](http://localhost:2480)
- Choose "New DB" and provide the name, root user and password. (The database will also get a default admin/adimin 
user/password).

After starting Testar, it's required to configure the database settings in the tab "GraphDB".
- url : remote:<hostname>/&lt;database name> (for instance remote:/localhost/demo)
- user: admin
- password: admin
- Check the checkbox.

When Testar finishes, the data be inspected in the database. The easiest way to see the complete
graph is to type the following query in the graph tab "Select * From E". This will display the complete
graph.

See the manuals of orientdb to learn about the possibilities to query the model.
