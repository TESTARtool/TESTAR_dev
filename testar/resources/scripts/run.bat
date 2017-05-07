@echo off
REM ---------------------------
REM SCRIPT by urueda@UPVLC 2016
REM ---------------------------

REM How much Java Heap GB to use?
set MEM=1

REM Search for Java JDK8
where java | findstr "jdk1.8" > __javajdkbin.tmp
set /p javajdkbin=<__javajdkbin.tmp
del __javajdkbin.tmp

echo TESTAR requires JDK 1.8 x64, trying to run with:
"%javajdkbin%" -version

REM Search for powershell
where powershell > __powershell.tmp
set /p powershell=<__powershell.tmp
del __powershell.tmp

REM Get current time
echo %date:~6,4%_%date:~3,2%_%date:~0,2%__%time:~0,2%_%time:~3,2%_%time:~6,2%.dbg.log> __datetime.tmp
set /p datetime=<__datetime.tmp
REM Hours between 0 and 9 fix
set datetime=%datetime: =0%
del __datetime.tmp

REM Run TESTAR
echo."%powershell%" | find /I "powershell.exe">nul && (
  REM With powershell
  echo Powershell found
  echo "%javajdkbin%" -ea -server -Xmx%MEM%g %* -jar testar.jar > powershellcmd.bat
  "%powershell%" ".\powershellcmd.bat | tee output\%datetime%"
  del powershellcmd.bat
) || (
  REM without powershell
  echo Powershell not found
  "%javajdkbin%" -ea -server -Xmx%MEM%g %* -jar testar.jar
)

set javajdkbin=
set powershell=
set datetime=
REM PAUSE