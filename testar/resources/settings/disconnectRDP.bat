@echo off

if not "%1"=="am_admin" (powershell start -verb runas '%0' am_admin & exit /b)

for /f "skip=1 tokens=3" %%s in ('query user %USERNAME%') do (
  %windir%\System32\tscon.exe %%s /dest:console
)