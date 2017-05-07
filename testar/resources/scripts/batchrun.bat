@echo off
REM ---------------------------
REM SCRIPT by urueda@UPVLC 2017
REM ---------------------------

if "%1"=="" GOTO MISS
GOTO GO
:MISS
echo Argument missing
echo Use: batchrun number_of_runs
:GO
@for /L %%i in (1,1,%1) do (
	@echo Starting RUN %%i - out of %1 -
	@run.bat -Dheadless=true
	@echo finished RUN %%i - out of %1 -
)