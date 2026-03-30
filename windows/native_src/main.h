#include "windows.h"

#ifndef _MAIN__H
#define _MAIN__H

#define WINAPI_NS(method) Java_org_testar_windows_Windows_##method
#define JNI_SIG(ret, method) JNIEXPORT ret JNICALL method

#define GDIEXCEPTION_CLASSNAME "org/testar/windows/exceptions/GDIException"
#define WINAPIEXCEPTION_CLASSNAME "org/testar/windows/exceptions/WinApiException"
#define UIAEXCEPTION_CLASSNAME "org/testar/windows/exceptions/UIAException"

#endif
