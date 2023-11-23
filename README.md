## README

This is the github development root folder for TESTAR development.  
The software can be build with gradle.

### Import Gradle project into Eclipse (similar for other IDEs with Gradle)

1. Create a new workspace
2. Select File -> Import to open the import dialog
3. Select Gradle -> Existing Gradle project to open the import dialog 
4. Select the folder that contains the root of the source code and start the import

It should be possible to build the project using the instructions provided in the next section

### Gradle tasks

`gradlew` is the instruction to use the gradle wrapper. 

TESTAR will download the wrapper dependencies in the system and will use it to compile.  
The gradle wrapper version indicated inside `TESTAR_dev\gradle\wrapper\gradle-wrapper.properties`  
Check the [Gradle Compatibility Matrix](https://docs.gradle.org/current/userguide/compatibility.html)  

#### gradlew build (Files Compilation)
`gradlew build` task : is configured to compile TESTAR project at Java level for error and warning checking.  
NOTE that this task doesn't generate an executable distribution by default.

#### windows.dll (Allows TESTAR execution on Windows)
TESTAR includes by default the file `windows.dll` inside `\testar\resources\windows10\` directory, which allows to run TESTAR on Windows 10 systems. 
If you do not need to modify Windows API native calls, the following java level compilation instructions are enough. 

#### gradlew installDist (Create TESTAR Distribution)
`gradlew installDist` task : creates a runnable TESTAR distribution in the `\testar\target\install\testar\bin\` directory.  
By default, `windows.dll` is copied from `\testar\resources\windows10\` directory and is ready to use.

1. Run `.\gradlew installDist` in the root of the project, or `TESTAR_dev -> distribution -> installDist` with the IDE
2. Change directory to `\testar\target\install\testar\bin\`
3. Run testar.bat

#### gradlew distZip (Creates a TESTAR Distribution)
It is also possible to generate a zip file containing TESTAR.  
This zip can be extracted on any other machine that has a 64-bit Windows operating system and Visual Studio redistributable installed.  
A proper way of using TESTAR is to run the tool in a virtual-machine.  
To build the zip execute the following command.  

1. Run `.\gradlew distZip` in the root of the project. 
2. Extract the zip on the machine where TESTAR is used.

#### gradlew windowsDistribution (Create a new windows.dll file)
_We recommend users ignore this task if they DO NOT need to modify Windows API native calls._ 
`gradlew windowsDistribution` task : Create a new file `windows.dll`, which has preference over the default one. 
This tasks requires the installation of Visual Studio C++ tools:  
https://github.com/TESTARtool/TESTAR_dev/wiki/Development:-Update-Windows-UIAutomation-(windows.dll)

#### Running Gradle in Eclipse
The following procedure has been performed

1. Create a new empty workspace for Eclipse in a folder which is not the folder that contains the source code.
2. Select File -> Import to open the import dialog
3. Select Gradle -> Existing Gradle project to open te import dialog 
4. Select the folder that contains the root of the source code and start the import

#### Running TESTAR from Gradle
`gradlew runTestar` task : creates a TESTAR distribution with `gradlew installDist` task, and executes TESTAR from the runnable file `\testar\target\install\testar\bin\testar.bat`

TESTAR can be started using a gradlew command from the root of the project.
1. `.\gradlew runTestar` 

#### In Eclipse
Within Eclipse, TESTAR can be executed by running the task runTestar which is available in the map custom_testar.
To debug the application with the runTestar task, provide your onw run configuration in which the option -DDEBUG is set.

#### Debug TESTAR from Gradle
In order to debug the TESTAR code, you must run;
1. `.\gradlew -DDEBUG=true runTestar.` 

Optionally you can build TESTAR (.\gradlew -DDBEBUG=true distZip ), copy the result to the machine where you want to run TESTAR and run TESTAR on the target machine.  
This allows the user to debug TESTAR from a different machine.  

#### Debug TESTAR with IntelliJ IDE
It is possible to add breakpoints and run TESTAR in debugging mode using IntelliJ.  
For this, it is necessary to replicate the output structure of the TESTAR dependencies.  

IntelliJ IDE:
1. Run from the gradle perspective `gradle debuggingDistribution`
2. Change the run execution of TESTAR to point to `TESTAR_dev\testar`
3. Execute `testar/src/org/testar/monkey/Main.java` with IntelliJ debugging mode

After the debugging execution, it is recommended to clean the output files by running `gradle cleanDebugging` from the IntelliJ gradle perspective.  

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

		SuspiciousTags -> The errors that TESTAR will search in the execution

Example: 

``testar sse=desktop_generic ShowVisualSettingsDialogOnStartup=false Sequences=5 SequenceLength=100 Mode=Generate SUTConnectorValue=" ""C:\\Program Files\\VideoLAN\\VLC\\vlc.exe"" " SuspiciousTags=".*[eE]rror.*|.*[eE]xcep[ct]ion.*"``

## State Model / Graph database support
TESTAR uses orientdb graph database http://orientdb.com , to create TESTAR GUI State Models.  
Detected Widget's, Actions, States and their respective relations are recorded to this graph database.  

### Use of the State Mode and the graph database
The State Model consists on Widgets and States obtained from getState() method together with Actions of deriveActions() method. This model is stored in three different layers: Abstract, Concrete and Sequence.

The protocols ``desktop_generic_statemodel`` and ``webdriver_statemodel`` contain the default settings implementation which shows how TESTAR State Model could be used.

### Download OrientDB 3.0.34 GA Community Edition (August 31st, 2020)
https://www.orientdb.org/download  
https://repo1.maven.org/maven2/com/orientechnologies/orientdb-community/3.0.34/orientdb-community-3.0.34.zip  

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
		DataStoreDirectory = C:\\Users\\testar\\Desktop\\orientdb-community-3.0.34\\databases
		DataStoreDB = testar
		DataStoreUser = testar
		DataStorePassword = testar
		
## Docker chromedriver image
https://hub.docker.com/u/testartool

https://hub.docker.com/r/testartool/testar-chromedriver

## Supported Operative Systems
If you encounter any errors, please create an issue and provide details about your operating system, Java, and TESTAR version.  

### Windows (For Desktop and Web systems under test)

- Windows 10 x64 is the operating system with the most active support due to being the one used by the project developers. 
- Windows 11, Server 2016, Server 2019, and Server 2022 are also recommended because they are compatible with the Windows API. 
- Windows 7 and Server 2012, are not officially maintained. You can find an old development version in [master_windows7](https://github.com/TESTARtool/TESTAR_dev/tree/master_windows7). 

### Ubuntu (For Web systems under test)

- Ubuntu 20 and 22 LTS are operating system versions that have been used to run the chromedriver package of TESTAR for testing web applications.  
``NOTE:`` It is important to use an environment with Graphical User Interface or to install and enable the Xvfb (X virtual framebuffer) server.  

### macOS (For Web systems under test)

- macOS Monterey (version 12) operating system is used in the GitHub Action to run the chromedriver package of TESTAR for testing web applications.  
``NOTE:`` The host architecture of tested macOS Monterey is an AMD64. The ARM architecture may require at least different software components, such as seleniarm docker images and chromedriver arm versions.   

### Android

It is possible to prepare an Emulator, Docker container, or Virtual Machine that contains Android to test mobile applications. [Regardless of the TESTAR host machine, it is necessary to connect to a local or remote Appium service for testing mobile applications](https://github.com/TESTARtool/TESTAR_dev/wiki/TESTAR-and-Appium-for-Mobile-Systems).  

## Supported Java SE versions

TESTAR needs Java Standard Edition (SE) to run and we support the Java SE Long-Term Support (LTS) versions that are currently under Oracle Extended Support  ( https://www.oracle.com/java/technologies/java-se-support-roadmap.html ) . Other versions might work.  

- Java 8 support has been maintained up until TESTAR v2.6.6. From this point forward, it is important to note that future updates may introduce incompatibility issues with Java 8 due to potential changes in libraries.  
- Starting from TESTAR v2.6.7, we offer official support for Java 11, 17, and 21, which are the last three LTS versions. If you encounter any errors, please create an issue and provide details about your operating system, Java, and TESTAR version.  

## Increase Java memory
https://github.com/TESTARtool/TESTAR_dev/wiki/Development:-Increase-Java-memory

## Known issues
https://github.com/TESTARtool/TESTAR_dev/issues

## Release notes
https://github.com/TESTARtool/TESTAR_dev/wiki/TESTAR-release-notes

## Required tools to create a windows.dll to update UIAutomation API
https://github.com/TESTARtool/TESTAR_dev/wiki/Development:-Update-Windows-UIAutomation-(windows.dll)
