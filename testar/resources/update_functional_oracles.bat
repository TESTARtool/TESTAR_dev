@echo off
timeout 5

set "folder=functional_oracles"
set "library=../lib/testar.jar"

setlocal enabledelayedexpansion
set "parent=%~dp0%folder%"
for /r "%parent%" %%i in (*.class) do (
    set "fullpath=%%i"
    set "relativepath=%folder%!fullpath:%parent%=!"
	jar uf %library% !relativepath!
)

call testar.bat && exit
