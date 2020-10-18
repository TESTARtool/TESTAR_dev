@echo off
set APPBASE="C:\BlueJ\bluej"
set CP=%APPBASE%\lib\bluej.jar;"C:\Program Files\Java\jdk1.8.0_181"\lib\tools.jar
"C:\Program Files\Java\jdk1.8.0_181\bin\java" -cp %CP% bluej.Boot  %1 %2 %3 %4 %5 %6 %7 %8 %9
