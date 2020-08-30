@echo OFF

set /A loops = 10
Set ssefile=desktop_generic_temporaloracles
Set settingsfile=settings\desktop_generic_temporaloracles\test.settings
FOR /L %%x IN (1 1 %loops%) DO (
	echo tweaking settingsfile for the %%x loop out of %loops%
	call batchreplace_win.bat  %settingsfile% "Sequences = \d+" "Sequences = %%x"                                    %settingsfile%
	call batchreplace_win.bat  %settingsfile% "SequenceLength = \d+" "SequenceLength = 1000"                         %settingsfile%
	call batchreplace_win.bat  %settingsfile% "ShowVisualSettingsDialogOnStartup =.*" "ShowVisualSettingsDialogOnStartup = false"   %settingsfile%
	rem call batchreplace_win  %settingsfile% "#{0,1}ApplicationName =.*" "ApplicationName = notepad_Build18363.1016" %settingsfile%
	call batchreplace_win.bat  %settingsfile% "#{0,1}ApplicationVersion =.*" "ApplicationVersion = sweep1_%%x_x1000" %settingsfile%
	echo kickoff next run
	call testar.bat sse=%ssefile%
)
echo reset option in  settings file to use the GUI again.
call batchreplace_win.bat  %settingsfile% "ShowVisualSettingsDialogOnStartup =.*" "ShowVisualSettingsDialogOnStartup = true"   %settingsfile%
exit /b

