REM -------------------------------------
REM Automated tests runs (by urueda 2017)
REM How-to:
REM		Put this script in a folder
REM		which contains a folder for
REM		each TESTAR setup to be run
REM Note: set TESTAR headless mode
REM Example folder structure:
REM		experiment
REM		|-- runtests.bat
REM		|-- setup-1
REM		|   |-- run.bat
REM		|   |-- test.settings
REM		|   |-- ...
REM		|-- setup-2
REM		|   |-- run.bat
REM		|   |-- test.settings
REM		|   |-- ...
REM		|-- ...
REM		|-- setup-n
REM		|   |-- run.bat
REM		|   |-- test.settings
REM		|   |-- ...
REM -------------------------------------

@echo off
FOR /D %%A IN (*.*) DO (
	cd %%A
	echo === will run %%A ===
	powershell ".\run.bat | tee %%A_out.txt"
	cd ..
)