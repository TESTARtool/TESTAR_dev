## README

This is the github development root folder for TESTAR development. 
The software can be build with both ant and gradle.

### Required tools to create TESTAR executable distribution

In order to build the native code, a view manual steps need to be executed;

1. In order to build the windows native code, Nmake and the compile for Microsoft visual studio are required.
These tools can be downloaded using the following [link](https://www.visualstudio.com/thank-you-downloading-visual-studio/?sku=BuildTools&rel=15#).
2. Install the Visual Studio tools on your machine (remember the path where the tools are installed)
3. Download [compile_w10.bat](https://github.com/florendg/testar_floren/releases/download/PERFORMANCE/compile_w10.bat) 
and [clean_w10.bat](https://github.com/florendg/testar_floren/releases/download/PERFORMANCE/clean_w10.bat)
4. Copy clean.bat and compile.bat to the folder windows/native_src within the TESTAR project
5. Adapt compile.bat and clean.bat. Set *PATH* to the installation folder used in step 2.
CALL "C:<*PATH*>\2017\BuildTools\Common7\Tools\VsDevCmd.bat" -arch=x64

### Import Gradle project into Eclipse (similar for other IDEs with Gradle)

1. Create a new empty workspace for Eclipse in a folder which is not the folder that contains the source
code.
2. Select File -> Import to open the import dialog
3. Select Gradle -> Existing Gradle project to open te import dialog 
4. Select the folder that contains the root of the source code and start the import

It should be possible to build the project using the instructions provided in the next section

### Gradle tasks

`gradlew` is the instruction to use the gradle wrapper. 

This basically means that TESTAR will download in the system, and will use to compile, 
the gradle version indicated inside `TESTAR_dev\gradle\wrapper\gradle-wrapper.properties`

#### gradlew build (Files Compilation)
`gradlew build` task : is configured to compile TESTAR project at Java level for error and warning checking. 
NOTE that this task doesn't generate an executable distribution by default.

#### windows.dll (Allows TESTAR execution on Windows)
TESTAR includes by default the file `windows.dll` inside `\testar\resources\windows10\` directory, which allows to run TESTAR on Windows 10 systems.

#### gradlew windowsDistribution (Allows TESTAR execution on Windows)
`gradlew windowsDistribution` task : uses the `Required tools to build the software` (see above) to create a new file `windows.dll`, which has preference over the default one.

NOTE: TESTAR requires Visual Redistributable which can be downloaded from the following
 [link]( https://go.microsoft.com/fwlink/?LinkId=746572 ). Also a JAVA 1.8 JDK is required.

#### gradlew installDist (Create TESTAR Distribution)
`gradlew installDist` task : creates a runnable TESTAR distribution in the `\testar\target\install\testar\bin\` directory.
By default, `windows.dll` should be copied from `\testar\resources\windows10\` directory and overwritten by the new dll file if the `gradlew windowsDistribution` task was executed.

1. Run `.\gradlew installDist` in the root of the project, or `TESTAR_dev -> distribution -> installDist` with the IDE
2. Change directory to `\testar\target\install\testar\bin\`
3. Run testar.bat

#### gradlew distZip (Creates a TESTAR Distribution)
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
`gradlew runTestar` task : creates a TESTAR distribution with `gradlew installDist` task, and executes TESTAR from the runnable file `\testar\target\install\testar\bin\testar.bat`

TESTAR can be started using a gradlew command from the root of the project.
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

### How to execute TESTAR distribution

#### Running TESTAR binaries (obtained with gradlew installDist/distZip) from command line

TESTAR allow its execution and settings configuration from the command line. By default is executed with the selected protocol (.sse file) and the test.settings values of that protocol.

From the command line it is also possible to select the desired protocol to execute TESTAR and change the values of the test.settings.

The protocol to be executed can be selected using the "sse" parameter next to the name of the desired protocol. Ex: testar sse=desktop_generic

Other settings are input using the pairs "parameterX=valueX" separated by space. Ex: testar ShowVisualSettingsDialogOnStartup=false Mode=Generate

Certain characters such the slashes or the quotation marks must be entered in a double way to respect the treaty of special characters.

Some of the most interesting parameters that can help to integrate TESTAR as an CI tool are:

		sse -> to change the protocol

		ShowVisualSettingsDialogOnStartup -> To run TESTAR without the GUI

		Mode -> TESTAR execution Mode (Spy, Generate, Record, Replay, View)

		SUTConnector & SUTConnectorValue -> The way to link with the desired application to be tested

		Sequences & SequenceLength -> The number of iterations and actions that TESTAR will execute

		SuspiciousTitles -> The errors that TESTAR will search in the execution

Example: 

``testar sse=desktop_generic ShowVisualSettingsDialogOnStartup=false Sequences=5 SequenceLength=100 Mode=Generate SUTConnectorValue=" ""C:\\Program Files\\VideoLAN\\VLC\\vlc.exe"" " SuspiciousTitles=".*[eE]rror.*|.*[eE]xcep[ct]ion.*"``

## State Model / Graph database support
TESTAR uses orientdb graph database http://orientdb.com , to create TESTAR GUI State Models.
Detected Widget's, Actions, States and their respective relations are recorded to this graph database.

### Use of the State Mode and the graph database
The State Model consists on Widgets and States obtained from getState() method together with Actions of deriveActions() method. This model is stored in three different layers: Abstract, Concrete and Sequence.

The protocols ``desktop_generic_statemodel`` and ``webdriver_statemodel`` contain the default settings implementation which shows how TESTAR State Model could be used.

### Download OrientDB 3.0.34 GA Community Edition (August 31st, 2020)
https://www.orientdb.org/download
https://s3.us-east-2.amazonaws.com/orientdb3/releases/3.0.34/orientdb-3.0.34.zip

``Warning: Since August 2020 there is version 3.1.X of OrientDB, however TESTAR currently requires the use of versions 3.0.X``

### Install and configure OrientDB Server
In order to use the graphdb feature it's advised to install a graph database on your machine or in a remote server.

Follow the installation instructions about how to configure TESTAR State Model on slide 28:
https://testar.org/images/development/TESTAR_webdriver_state_model.pdf 

Also TESTAR HandsOn (Section 6) contains more information about State Model settings: https://testar.org/images/development/Hands_on_TESTAR_Training_Manual_2020_October_14.pdf

When orientdb is started the first time. The root password needs to be configured. Make sure you remember this password.

In order to use the graphdb feature. A database must be created in OrientDB. To do this follow the following procedure:
- Start the database server (ORIENTDB_HOME/bin/server.bat)
- Start orientdb studio in a webbrowser [http://localhost:2480](http://localhost:2480)
- Choose "New DB" and provide the name, root user and password. (The database will also get a default admin/admin  user/password).
- Go to Security tab and create a new user (testar/testar) with an active status and the admin role

### Using OrientDB graphdb on the local filesystem
OrientDB graph database can be used remotely or locally.
Default TESTAR settings are predefined to connect with remote mode to a local OrientDB server:

		StateModelEnabled = true
		DataStore = OrientDB
		DataStoreType = remote
		DataStoreServer = localhost
		DataStoreDB = testar
		DataStoreUser = testar
		DataStorePassword = testar

Also is possible to connect at file level without deploy the OrientDB locally:

		StateModelEnabled = true
		DataStore = OrientDB
		DataStoreType = plocal
		DataStoreDirectory = C:\\Users\\testar\\Desktop\\orientdb-3.0.34\\databases
		DataStoreDB = testar
		DataStoreUser = testar
		DataStorePassword = testar
		
## Docker chromedriver image
https://hub.docker.com/u/testartool

https://hub.docker.com/r/testartool/testar-chromedriver

## Known issues
https://github.com/TESTARtool/TESTAR_dev/issues

## Release notes
https://github.com/TESTARtool/TESTAR_dev/wiki/TESTAR-release-notes
