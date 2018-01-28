:: set MSVCDIR=C:\Programme\Microsoft Visual Studio 10.0\VC\bin
:: call "%MSVCDIR%\vcvars32.bat"

CALL "C:\Program Files (x86)\Microsoft Visual Studio\2017\BuildTools\Common7\Tools\VsDevCmd.bat" -arch=x64

nmake -f Makefile_w10 clean