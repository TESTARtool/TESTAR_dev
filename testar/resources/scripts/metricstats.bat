@echo off
REM script by urueda 2016
REM verdict,minCvg(%),maxCvg(%),maxpath,states,actions,SUTRAM(KB),SUTCPU(%),TestRAM(MB),TestCPU(s),fitness
if "%1"=="" GOTO MISS
if "%2"=="" GOTO MISS
GOTO AVG
:MISS
echo Argument missing
echo Use: metricstats foldername %%?
echo where ? is oneof:
REM echo A == verdict
echo B == FAILS
echo C == minCvg(%)
echo D == maxCvg(%)
echo E == maxpath
echo F == graph-states
echo G == abstract-states
echo H == graph-actions
echo I == test-actions
echo J == SUTRAM(KB)
echo K == SUTCPU(%)
echo L == TestRAM(MB)
echo M == TestCPU(s)
echo N == fitness  
GOTO EOF
:AVG
SETLOCAL EnableDelayedExpansion
set array=
set idx=0
set passnumber=0
set failnumber=0
FOR /F %%f IN ('dir /b %1\*.csv') DO (
	FOR /F "skip=1 tokens=1-14* delims=," %%A IN (%1\%%f) DO (
		SET coldirty=%2
		set col=!coldirty: =!
		REM echo %%f = !col!
		set verdictdirty=%%A
		set verdict=!verdictdirty: =!
		if [!verdict!]==[PASS] set /a passnumber=!passnumber!+1
		if [!verdict!]==[FAIL] set /a failnumber=!failnumber!+1
		set array[!idx!]=!col!
		set /a idx=idx+1
	)
)
set /a end=idx-1
set equation=0
FOR /L %%a IN (0,1,!end!-1) DO (
	set equation=!equation!+!array[%%a]!
)
set equation=(!equation!)/!idx!
REM echo equation is = !equation!
FOR /F "tokens=1,*" %%A IN (
	'powershell !equation!'
) DO (
	set avg=%%A
	set avg=!avg:,=.!
)

REM standard deviation
set stdeveq=0
set pe=)
FOR /L %%a IN (0,1,!end!-1) DO (
	set stdeveq=!stdeveq!+[math]::pow(!avg!-!array[%%a]!,2!pe!
)
set stdeveq=[math]::sqrt((!stdeveq!)/!idx!)
REM echo stdev equation is = !stdeveq!
FOR /F "tokens=1,*" %%A IN (
	'powershell !stdeveq!'
) DO (
	set stdev=%%A
	set stdev=!stdev:,=.!
)

if "%2"=="%%B" echo %1 !idx!-runs FAILS = !avg! stdev=!stdev! ... pass = !passnumber! and fail = !failnumber!
if "%2"=="%%C" echo %1 !idx!-runs minCvg(%) = !avg! stdev=!stdev! ... pass = !passnumber! and fail = !failnumber!
if "%2"=="%%D" echo %1 !idx!-runs maxCvg(%) = !avg! stdev=!stdev! ... pass = !passnumber! and fail = !failnumber!
if "%2"=="%%E" echo %1 !idx!-runs maxpath = !avg! stdev=!stdev! ... pass = !passnumber! and fail = !failnumber!
if "%2"=="%%F" echo %1 !idx!-runs graph-states = !avg! stdev=!stdev! ... pass = !passnumber! and fail = !failnumber!
if "%2"=="%%G" echo %1 !idx!-runs abstract-states = !avg! stdev=!stdev! ... pass = !passnumber! and fail = !failnumber!
if "%2"=="%%H" echo %1 !idx!-runs graph-actions = !avg! stdev=!stdev! ... pass = !passnumber! and fail = !failnumber!
if "%2"=="%%I" echo %1 !idx!-runs test-actions = !avg! stdev=!stdev! ... pass = !passnumber! and fail = !failnumber!
if "%2"=="%%J" echo %1 !idx!-runs SUTRAM(KB) = !avg! stdev=!stdev! ... pass = !passnumber! and fail = !failnumber!
if "%2"=="%%K" echo %1 !idx!-runs SUTCPU(%) = !avg! stdev=!stdev! ... pass = !passnumber! and fail = !failnumber!
if "%2"=="%%L" echo %1 !idx!-runs TestRAM(MB) = !avg! stdev=!stdev! ... pass = !passnumber! and fail = !failnumber!
if "%2"=="%%M" echo %1 !idx!-runs TestCPU(s) = !avg! stdev=!stdev! ... pass = !passnumber! and fail = !failnumber!
if "%2"=="%%N" echo %1 !idx!-runs fitness = !avg! stdev=!stdev! ... pass = !passnumber! and fail = !failnumber!
:EOF