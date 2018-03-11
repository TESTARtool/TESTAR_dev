:: set MSVCDIR=C:\Program Files (x86)\Microsoft Visual Studio 10.0\VC\bin
:: call "%MSVCDIR%\vcvars32.bat"

CALL "C:\Program Files\Microsoft SDKs\Windows\v7.1\Bin\SetEnv.cmd" /x64

nmake -f Makefile_w7