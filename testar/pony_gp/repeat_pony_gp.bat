@echo off
REM script by urueda 2017
FOR /L %%i IN (1,1,30) DO ( powershell ".\py.bat | tee out_%%i.txt" )