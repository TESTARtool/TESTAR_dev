#include "windows.h"

#ifndef _MAIN__H
#define _MAIN__H

#define WINAPI_NS(method) Java_org_testar_monkey_alayer_windows_Windows_##method
#define JNI_SIG(ret, method) JNIEXPORT ret JNICALL method

#define GDIEXCEPTION_CLASSNAME "org/testar/monkey/alayer/windows/GDIException"
#define WINAPIEXCEPTION_CLASSNAME "org/testar/monkey/alayer/windows/WinApiException"
#define UIAEXCEPTION_CLASSNAME "org/testar/monkey/alayer/windows/UIAException"

#endif