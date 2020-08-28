@echo OFF
Set ssefile=%2
Set loops=%1
if "%loops%" == "" (
    @echo Usage: 
    @echo   %0% ^<number^> ^<sse file^>
    @echo Parameter description:
    @echo   ^<number^>  is the  mandatory number of TESTAR runs 
    @echo   ^<ssefile^> is optional filename for the protocol to use. By Default, the .sse file in the .\settings directory will be used
    @echo Example:
    @echo   %0% 5 desktop_generic_temporaloracles.sse
    @echo Usecase:
    @echo   Temporal Model check inside TESTAR can only occur after the last test sequence because the State Model database is locked and released after the final test run.
    @echo   With Sequences = 1 in the test.settings file and using this batch file, the effect of model checking after each test sequence can be mimicked. 
    exit /b
)   

FOR /L %%x IN (1 1 %loops%) DO call :runTESTAR %%x 
exit /b

:runTESTAR
echo TESTAR run count: %1 / %loops%
if "%ssefile%" == "" (
    call testar.bat 
) else (
    echo run TESTAR using commandline option: %ssefile% 
    call testar.bat sse=%ssefile% 
) 
