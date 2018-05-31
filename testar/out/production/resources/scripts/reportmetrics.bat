@echo off
@REM script by urueda 2017
if "%1"=="" GOTO MISS
GOTO GO
:MISS
echo Argument missing
echo Use: reportmetrics metrics_folder
:GO
@FOR %%A IN (B C D E F G H I J K L M N) DO @metricstats.bat %1 %%%%A