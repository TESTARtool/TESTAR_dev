rem @echo off

Set infile=%1
Set old=%2
Set old=%old:"=%
Set new=%3
Set new=%new:"=%
Set outfile=%4
Set cmdlineprefix="(gc %infile%) -replace '%old%', '%new%'
if "%outfile%" == "" (
   Set cmdline=%cmdlineprefix% "
) else (
   Set cmdline=%cmdlineprefix% | Out-File -encoding ASCII %outfile% "
)
if "%infile%" == "" (
   @echo Usage:
   @echo   %0% ^<infile^> ^<oldtext^> ^<newtext^> ^<outfile^>
   @echo Parameter description:
   @echo   ^<infile^>  is the  mandatory sourcefile
   @echo   ^<oldtext^>  is the  mandatory REGEX to be replaced. surround with  double quotes when the text contains spaces
   @echo   ^<newtext^>  is the  mandatory replacement text. surround with  double quotes when the text contains spaces
   @echo   ^<outfile^>  is the  optional target file. by default the output is written to console.
   @echo Example:
   @echo   %0% ..\template_test.settings "SequenceLength = \d+" "SequenceLength = 1000" settings\desktop_generic_temporaloracles\test.settings
   @echo   replace the current SequenceLength with a SequenceLength of 1000.
   @echo   %0% ..\template_test.settings "#{0,1}ApplicationVersion =.*" "ApplicationVersion = test_85_len1000" settings\desktop_generic_temporaloracles\test.settings
   @echo   replace the current ApplicationVersion or #ApplicationVersion with a ApplicationVersion of "test_85_len1000".
   @echo Usecase:
   @echo  Continuous model generation and model checking inside TESTAR, without the need to manually update the settings file through the TESTAR GUI.
   exit /b
)
powershell -Command %cmdline%