#one of the 2 wil work, the other will fail without sideeffects
#css 20190716 for @home
CALL "C:\Program Files (x86)\Microsoft Visual Studio\2017\BuildTools\Common7\Tools\VsDevCmd.bat" -arch=x64

#css 20190716 for @work
call "C:\Program Files (x86)\Microsoft Visual Studio\2017\Community\Common7\Tools\VsDevCmd.bat" -arch=x64
nmake -f Makefile_w10