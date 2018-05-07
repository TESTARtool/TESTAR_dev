@REM script by urueda 2016
@if "%1"=="" GOTO MISS
@FOR /D %%A IN (*.*) DO @avgmetric %%A\output\metrics %1
@GOTO EOF
:MISS
@echo %%? argument is missing
@echo Info from avgmetric:
@echo --------------------
@avgmetric
:EOF