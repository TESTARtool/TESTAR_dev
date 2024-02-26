:: We use nmake to compile the makefile that builds the windows DLL
:: Thus, you need to install nmake c++ tools
:: Packages needed: C++ tools, CLI tools, and one Windows SDK package for libraries dependencies
::
:: Visual Studio 2017 Build Tools: https://aka.ms/vs/15/release/vs_buildtools.exe
:: Visual Studio 2019 Build Tools: https://aka.ms/vs/16/release/vs_buildtools.exe
:: Visual Studio 2022 Build Tools: https://aka.ms/vs/17/release/vs_buildtools.exe

:: CALL "C:\Program Files (x86)\Microsoft Visual Studio\2017\BuildTools\Common7\Tools\VsDevCmd.bat" -arch=x64
CALL "C:\Program Files (x86)\Microsoft Visual Studio\2022\BuildTools\Common7\Tools\VsDevCmd.bat" -arch=x64

:: First clean
nmake -f Makefile_w10 clean
:: Then compile
nmake -f Makefile_w10