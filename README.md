## README

This is the github development root folder for TESTAR development. 
The software can be build with both ant and gradle.

### Gradle build

To build the gradle project and run it execute the following procedure;

1. run gradle installDist in the root of the project
2. change directory to testar/target/install/bin
3. run testar.bat

## Known issue
- The protocol needs to be compiled. Therefor choose edit protocol, compile and
close the dialog. The protocol .class is now available.
- Testar hangs at the end of the execution.

## GraphDb support
This version of Testar supports the output of data to an orientdb database.
Currently State, Widgets (as Vertex) and Actions (as Edges ) are stored.

### OrientDb

The current implementation contains a backend for orientdb

### Configuring graphdb
In order to use the graphdb it's advised to install graphdb on your machine You can download the
community edition from orientdb.com.

When orientdb is started the first time. The root password needs to be configured. Chooose "orientdb". This 
is required since it is currently hardcoded in the application.

In order to use the graphdb feature. A database must be created in orientdb

After starting Testar, it's required to configure the database settings in the tab "GraphDB".
- url : remote:<hostname>/&lt;database name>
- user: admin
- password: admin
- Check the checkbox.

