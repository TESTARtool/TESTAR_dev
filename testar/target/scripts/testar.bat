@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  testar startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and TESTAR_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Dlogback.configurationFile=logback.xml" "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\testar.jar;%APP_HOME%\lib\jsyntaxpane-1.1.5.jar;%APP_HOME%\lib\sikulixapi.jar;%APP_HOME%\lib\eye2.jar;%APP_HOME%\lib\tess4j-4.0.0.jar;%APP_HOME%\lib\jul-to-slf4j-1.7.2.jar;%APP_HOME%\lib\asm-3.3.1.jar;%APP_HOME%\lib\commons-io-2.6.jar;%APP_HOME%\lib\cglib-2.2.2.jar;%APP_HOME%\lib\jai_imageio-1.1.jar;%APP_HOME%\lib\jai-imageio-core-1.4.0.jar;%APP_HOME%\lib\native.jar;%APP_HOME%\lib\libatspi.jar;%APP_HOME%\lib\graphdb.jar;%APP_HOME%\lib\windows.jar;%APP_HOME%\lib\linux.jar;%APP_HOME%\lib\junit-jupiter-api-5.0.0-M3.jar;%APP_HOME%\lib\core.jar;%APP_HOME%\lib\tgherkin.jar;%APP_HOME%\lib\jnativehook-2.1.0.jar;%APP_HOME%\lib\logback-classic-1.2.2.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\orientdb-graphdb-2.2.29.jar;%APP_HOME%\lib\gremlin-groovy-2.6.0.jar;%APP_HOME%\lib\gremlin-java-2.6.0.jar;%APP_HOME%\lib\groovy-1.8.9.jar;%APP_HOME%\lib\antlr4-4.5.jar;%APP_HOME%\lib\logback-core-1.2.2.jar;%APP_HOME%\lib\orientdb-server-2.2.29.jar;%APP_HOME%\lib\orientdb-tools-2.2.29.jar;%APP_HOME%\lib\orientdb-client-2.2.29.jar;%APP_HOME%\lib\orientdb-core-2.2.29.jar;%APP_HOME%\lib\pipes-2.6.0.jar;%APP_HOME%\lib\blueprints-core-2.6.0.jar;%APP_HOME%\lib\commons-configuration-1.6.jar;%APP_HOME%\lib\commons-collections-3.2.2.jar;%APP_HOME%\lib\ivy-2.3.0.jar;%APP_HOME%\lib\ant-1.8.3.jar;%APP_HOME%\lib\jansi-1.5.jar;%APP_HOME%\lib\jline-0.9.94.jar;%APP_HOME%\lib\antlr-2.7.7.jar;%APP_HOME%\lib\asm-commons-3.2.jar;%APP_HOME%\lib\asm-util-3.2.jar;%APP_HOME%\lib\asm-analysis-3.2.jar;%APP_HOME%\lib\asm-tree-3.2.jar;%APP_HOME%\lib\asm-3.2.jar;%APP_HOME%\lib\antlr4-runtime-4.5.jar;%APP_HOME%\lib\ST4-4.0.8.jar;%APP_HOME%\lib\antlr-runtime-3.5.2.jar;%APP_HOME%\lib\mail-1.4.7.jar;%APP_HOME%\lib\jaxb-impl-2.2.3.jar;%APP_HOME%\lib\jackson-databind-2.6.0.jar;%APP_HOME%\lib\snappy-java-1.1.0.1.jar;%APP_HOME%\lib\concurrentlinkedhashmap-lru-1.4.1.jar;%APP_HOME%\lib\jettison-1.3.3.jar;%APP_HOME%\lib\hppc-0.6.0.jar;%APP_HOME%\lib\commons-logging-1.1.1.jar;%APP_HOME%\lib\ant-launcher-1.8.3.jar;%APP_HOME%\lib\org.abego.treelayout.core-1.0.1.jar;%APP_HOME%\lib\jaxb-api-2.2.2.jar;%APP_HOME%\lib\activation-1.1.jar;%APP_HOME%\lib\jackson-annotations-2.6.0.jar;%APP_HOME%\lib\jackson-core-2.6.0.jar;%APP_HOME%\lib\stax-api-1.0.1.jar;%APP_HOME%\lib\commons-lang-2.4.jar;%APP_HOME%\lib\commons-digester-1.8.jar;%APP_HOME%\lib\commons-beanutils-core-1.8.0.jar;%APP_HOME%\lib\stax-api-1.0-2.jar;%APP_HOME%\lib\commons-beanutils-1.7.0.jar

@rem Execute testar
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %TESTAR_OPTS%  -classpath "%CLASSPATH%" org.fruit.monkey.Main %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable TESTAR_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%TESTAR_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
