#include "windows.h"
#include "main.h"
#include <windows.h>
#include <vector>
#include <utility>
#include <iostream>
#include <cstring>
#include <strsafe.h>
#include <Commctrl.h>
#include <gdiplus.h>
#include <gdiplustypes.h>
#include <algorithm>
#include <uiautomation.h>
#include <comutil.h>
#include <Psapi.h>
#include <Shobjidl.h>

 // begin by urueda
#include <AccessBridgeCalls.h>

#ifndef uint
#define uint unsigned int
#endif
// end by urueda

const int ERROR_BUFFER_LEN = 2000;
TCHAR ErrorBuffer[ERROR_BUFFER_LEN];


/* JNI_OnLoad */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved){
	JNIEnv *env;
	if (jvm->GetEnv((void **)&env, JNI_VERSION_1_2))
		return JNI_ERR; /* JNI version not supported */

	return JNI_VERSION_1_2;
}

/* JNI_OnUnload */
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *jvm, void *reserved){
}

void throwUIAException(JNIEnv *env, const char* msg){
	jclass exClass = env->FindClass(UIAEXCEPTION_CLASSNAME);
	env->ThrowNew(exClass, msg);
}

void throwGDIException(JNIEnv *env, const char* msg){
	jclass exClass = env->FindClass(GDIEXCEPTION_CLASSNAME);
	env->ThrowNew(exClass, msg);
}

void throwGDIException(JNIEnv *env, int statusCode){
	jclass exClass = env->FindClass(GDIEXCEPTION_CLASSNAME);
	jmethodID status_constructor = env->GetMethodID(exClass, "<init>", "(I)V");
	jthrowable exObj = (jthrowable) env->NewObject(exClass, status_constructor, statusCode);
	env->Throw(exObj);
}

void throwWinApiException(JNIEnv *env, const char* msg){
	jclass exClass = env->FindClass(WINAPIEXCEPTION_CLASSNAME);
	env->ThrowNew(exClass, msg);
}

const TCHAR* errString(const char* functionName, DWORD errCode){
	LPVOID lpMsgBuf;
	LPVOID lpDisplayBuf;

	FormatMessage(
			FORMAT_MESSAGE_ALLOCATE_BUFFER |
			FORMAT_MESSAGE_FROM_SYSTEM |
			FORMAT_MESSAGE_IGNORE_INSERTS,
			NULL,
			errCode,
			MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
			(LPTSTR) &lpMsgBuf,
			0, NULL );

	lpDisplayBuf = ErrorBuffer;
	StringCchPrintf((LPTSTR)lpDisplayBuf,
			ERROR_BUFFER_LEN,
			TEXT("%s failed with error %d: %s"),
			functionName, errCode, lpMsgBuf);

	LocalFree(lpMsgBuf);
	return ErrorBuffer;
}


/* TerminateProcess */
JNI_SIG(jboolean, WINAPI_NS(TerminateProcess)) (JNIEnv * env, jclass cl, jlong hProcess, jlong exitCode){
	return TerminateProcess((HANDLE)hProcess, exitCode) != 0;
}


/* GetClassName */
JNI_SIG(jstring, WINAPI_NS(GetClassName))(JNIEnv *env, jclass, jlong hwnd){
	const int CL_NAME_LEN = 500;
	char name[CL_NAME_LEN];
	if(GetClassName((HWND)hwnd, name, CL_NAME_LEN) == 0)
		return 0;
	return env->NewStringUTF(name);
}

/* loadFile */
JNI_SIG(jbyteArray, WINAPI_NS(loadFile)) (JNIEnv * env, jclass cl, jstring name) {
	jbyteArray jb;
	const char *mfile = env->GetStringUTFChars(name, NULL);
	// std::cout << mfile << std::endl;
	HDC hdc = GetWindowDC(0);
	RECT rect;
	rect.left = 0;
	rect.top = 0;
	rect.right = 100;
	rect.bottom = 100;

	DrawEdge(hdc, &rect, EDGE_RAISED, BF_BOTTOMRIGHT | BF_TOPLEFT);
	TextOut(hdc, 0, 0, "bla", 3);
	env->ReleaseStringUTFChars(name, mfile);
	jb=env->NewByteArray(33);
	return (jb);
}

/* GetProcessId */
JNI_SIG(jlong, WINAPI_NS(GetProcessId))(JNIEnv *env, jclass cl, jlong hProcess) {
	DWORD ret = GetProcessId((HANDLE)hProcess);
	if(ret == 0)
		throwWinApiException(env, "GetProcessId() failed!");
	return (jlong) ret;
}

/* GetForegroundWindow */
JNI_SIG(jlong, WINAPI_NS(GetForegroundWindow)) (JNIEnv * env, jclass cl) {
	return (jlong)GetForegroundWindow();
}

/* SetForegroundWindow */
JNI_SIG(jboolean, WINAPI_NS(SetForegroundWindow)) (JNIEnv * env, jclass cl, jlong hwnd) {
	return SetForegroundWindow((HWND)hwnd);
}

/* WindowFromPoint */
JNI_SIG(jint, WINAPI_NS(WindowFromPoint))(JNIEnv * env, jclass cl, jint x, jint y){
	POINT p;
	p.x = x;
	p.y = y;
	return (int)WindowFromPoint(p);
}

/* ChildWindowFromPoint */
JNI_SIG(jint, WINAPI_NS(ChildWindowFromPoint))(JNIEnv * env, jclass cl, jint parentHwnd, jint x, jint y){
	HWND pHwnd = (HWND) parentHwnd;
	POINT p;
	p.x = x;
	p.y = y;
	return (int)ChildWindowFromPoint(pHwnd, p);
}

/* GetClientRect */
JNI_SIG(jintArray, WINAPI_NS(GetClientRect))
(JNIEnv * env, jclass cl, jint hwnd){
	RECT crect;
	BOOL success = GetClientRect((HWND)hwnd, &crect);

	jint rectArr[4];
	rectArr[0] = crect.left;
	rectArr[1] = crect.top;
	rectArr[2] = crect.right;
	rectArr[3] = crect.bottom;
	jintArray ret = env->NewIntArray(4);
	env->SetIntArrayRegion(ret, 0, 4, rectArr);
	return ret;
}

/* GetWindowThreadId */
JNI_SIG(jint, WINAPI_NS(GetWindowThreadId)) (JNIEnv * env, jclass cl, jint hwnd){
	return (int)GetWindowThreadProcessId((HWND)hwnd, NULL);
}

/* GetWindowProcessId */
JNI_SIG(jlong, WINAPI_NS(GetWindowProcessId)) (JNIEnv * env, jclass cl, jlong hwnd){
	DWORD pid;
	GetWindowThreadProcessId((HWND)hwnd, &pid);
	return pid;
}

/* SendMessage */
JNI_SIG(jlong, WINAPI_NS(SendMessage))
(JNIEnv *env, jclass cl, jlong hwnd, jlong msg, jlong wparam, jlong lparam){
	return SendMessage((HWND)hwnd, msg, wparam, lparam);
}


BOOL CALLBACK EnumWindowsProc(HWND hwnd, LPARAM lParam){
	std::vector<jlong>& handles = *((std::vector<jlong>*) lParam);
	handles.push_back((jlong)hwnd);
	return TRUE;
}

/* EnumWindows */
JNI_SIG(jlongArray, WINAPI_NS(EnumWindows)__)(JNIEnv *env, jclass){
	std::vector<jlong> handles;
	BOOL success = EnumWindows(EnumWindowsProc, (LPARAM)&handles);
	if(!success)
		return 0;
	jlongArray ret = env->NewLongArray(handles.size());
	env->SetLongArrayRegion(ret, (jsize)0, (jsize)handles.size(), (jlong*)&handles[0]);
	return ret;
}


BOOL CALLBACK EnumProcessWindowsProc(HWND hwnd, LPARAM lParam){
	std::pair<DWORD, std::vector<jlong> >& arg = *((std::pair<DWORD, std::vector<jlong> >*) lParam);

	DWORD pid = 0;
	GetWindowThreadProcessId(hwnd, &pid);
//	printf("pid: %d  hwnd: %d  hwnd-pid: %d\n", arg.first, hwnd, pid);fflush(stdout);
	if(pid == arg.first)
		arg.second.push_back((jlong)hwnd);
	return TRUE;
}


/* EnumWindows (for a process) */
JNI_SIG(jlongArray, WINAPI_NS(EnumWindows)__J)(JNIEnv *env, jclass, jlong pid){
	std::pair<DWORD, std::vector<jlong> > arg;
	arg.first = pid;
	BOOL success = EnumWindows(EnumProcessWindowsProc, (LPARAM)&arg);
	if(!success)
		return 0;
	jlongArray ret = env->NewLongArray(arg.second.size());
	env->SetLongArrayRegion(ret, (jsize)0, (jsize)arg.second.size(), (jlong*)&arg.second[0]);
	return ret;
}


/* EnumAllWindows */
JNI_SIG(jlongArray, WINAPI_NS(EnumAllWindows)__)(JNIEnv *env, jclass){
	std::vector<jlong> handles;
	handles.push_back((jlong)GetDesktopWindow());
	EnumChildWindows((HWND)handles[0], EnumWindowsProc, (LPARAM) &handles);

	jlongArray ret = env->NewLongArray(handles.size());
	env->SetLongArrayRegion(ret, (jsize)0, (jsize)handles.size(), (jlong*)&handles[0]);
	return ret;
}


/* EnumAllWindows (for a process) */
JNI_SIG(jlongArray, WINAPI_NS(EnumAllWindows)__J)(JNIEnv *env, jclass, jlong pid){
	std::pair<DWORD, std::vector<jlong> > arg;
	arg.first = pid;
	BOOL success = EnumWindows(EnumProcessWindowsProc, (LPARAM)&arg);
	if(!success)
		return 0;

	// add child windows also
	int length = arg.second.size();
	for(int i = 0; i < length; i++)
		EnumChildWindows((HWND)arg.second[i], EnumWindowsProc, (LPARAM) &arg.second);

	jlongArray ret = env->NewLongArray(arg.second.size());
	env->SetLongArrayRegion(ret, (jsize)0, (jsize)arg.second.size(), (jlong*)&arg.second[0]);
	return ret;
}


/* EnumChildWindows */
JNI_SIG(jlongArray, WINAPI_NS(EnumChildWindows))(JNIEnv *env, jclass, jlong hwndParent){
	std::vector<jlong> handles;
	EnumChildWindows((HWND)hwndParent, EnumWindowsProc, (LPARAM)&handles); // return value not used!!!
	jlongArray ret = env->NewLongArray(handles.size());
	env->SetLongArrayRegion(ret, (jsize)0, (jsize)handles.size(), (jlong*)&handles[0]);
	return ret;
}

/* IsWindow */
JNI_SIG(jboolean, WINAPI_NS(IsWindow)) (JNIEnv * env, jclass cl, jlong hwnd){
	return IsWindow((HWND)hwnd);
}

/* IsIconic */
JNI_SIG(jboolean, WINAPI_NS(IsIconic)) (JNIEnv * env, jclass cl, jlong hwnd){
	return IsIconic((HWND)hwnd);
}

/* IsWindowVisible */
JNI_SIG(jboolean, WINAPI_NS(IsWindowVisible)) (JNIEnv * env, jclass cl, jlong hwnd){
	return IsWindowVisible((HWND)hwnd);
}

/* GetWindowRect (array return value) */
JNI_SIG(jlongArray, WINAPI_NS(GetWindowRect))(JNIEnv *env, jclass, jlong hwnd){
	RECT crect;
	BOOL success = GetWindowRect((HWND)hwnd, &crect);

	jlong rectArr[4];
	rectArr[0] = crect.left;
	rectArr[1] = crect.top;
	rectArr[2] = crect.right;
	rectArr[3] = crect.bottom;
	jlongArray ret = env->NewLongArray(4);
	env->SetLongArrayRegion(ret, 0, 4, rectArr);
	return ret;
}

/* GetParent */
JNI_SIG(jint, WINAPI_NS(GetParent)) (JNIEnv * env, jclass cl, jint hwnd){
	return (jint)GetParent((HWND)hwnd);
}


/* GetWindow */
JNI_SIG(jlong, WINAPI_NS(GetWindow)) (JNIEnv * env, jclass cl, jlong hwnd, jlong uCmd){
	return (jlong)GetWindow((HWND)hwnd, (UINT) uCmd);
}

/* GetAncestor */
JNI_SIG(jint, WINAPI_NS(GetAncestor)) (JNIEnv * env, jclass cl, jint hwnd, jint gaFlags){
	return (jint)GetAncestor((HWND)hwnd, (UINT) gaFlags);
}


/* GetWindowTextLength */
JNI_SIG(jint, WINAPI_NS(GetWindowTextLength)) (JNIEnv * env, jclass cl, jint hwnd){
	return (jint)GetWindowTextLength((HWND)hwnd);
}

/* GetWindowText */
JNI_SIG(jstring, WINAPI_NS(GetWindowText))(JNIEnv *env, jclass, jlong hwnd){
	const int MAX_TEXT_LEN = 500;
	char name[MAX_TEXT_LEN];
	int res = GetWindowText((HWND)hwnd, name, MAX_TEXT_LEN);

	if(res == 0)
		return 0;

	return env->NewStringUTF(name);
}


/* GetShellWindow */
JNI_SIG(jint, WINAPI_NS(GetShellWindow)) (JNIEnv * env, jclass cl){
	return (jint)GetShellWindow();
}

/* GetDesktopWindow */
JNI_SIG(jlong, WINAPI_NS(GetDesktopWindow)) (JNIEnv * env, jclass cl){
	return (jlong)GetDesktopWindow();
}


/* GetDesktopWindow */
JNI_SIG(jlong, WINAPI_NS(GetWindowLong)) (JNIEnv * env, jclass cl, jlong hwnd, jlong nIndex){
	return (jlong)GetWindowLong((HWND) hwnd, nIndex);
}


/* GetNextWindow */
JNI_SIG(jlong, WINAPI_NS(GetNextWindow)) (JNIEnv * env, jclass cl, jlong hWnd, jlong wCmd){
	return (jlong)GetNextWindow((HWND)hWnd, (UINT)wCmd);
}

/* GetToolBarItemRect */
JNI_SIG(jintArray, WINAPI_NS(GetToolBarItemRect))
(JNIEnv *env, jclass, jint hwnd, jint index, jboolean dropdown){

	HWND _hwnd = (HWND) hwnd;
	DWORD pid;
	GetWindowThreadProcessId(_hwnd, &pid);
	HANDLE phandle = OpenProcess(PROCESS_VM_OPERATION | PROCESS_VM_READ | PROCESS_VM_WRITE, FALSE, pid);
	LPVOID vspace = VirtualAllocEx(phandle, NULL, sizeof(RECT), MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);

	LRESULT res = SendMessage((HWND)hwnd, dropdown ? TB_GETITEMDROPDOWNRECT : TB_GETITEMRECT,
			index, (LPARAM)vspace);

	RECT _rect;
	SIZE_T bytesRead;
	BOOL success = ReadProcessMemory(phandle, vspace, (LPVOID)&_rect, sizeof(RECT), &bytesRead);
	//	ErrString("wufffinger WUMM", GetLastError());

	success = VirtualFreeEx(phandle, vspace, 0, MEM_RELEASE);

	CloseHandle(phandle);

	jint data[4];
	data[0] = _rect.left;
	data[1] = _rect.top;
	data[2] = _rect.right;
	data[3] = _rect.bottom;
	jintArray ret = env->NewIntArray(4);
	env->SetIntArrayRegion(ret, 0, 4, data);
	return ret;
}


/* GetToolBarButtonCount */
JNI_SIG(jint, WINAPI_NS(GetToolBarButtonCount))
(JNIEnv *env, jclass, jint hwnd){
	return SendMessage((HWND)hwnd, TB_BUTTONCOUNT, 0, 0);
}

/* GetToolButtonInfo */
JNI_SIG(jintArray, WINAPI_NS(GetToolButtonInfo))
(JNIEnv *env, jclass, jint hwnd, jint index){

	HWND _hwnd = (HWND) hwnd;
	DWORD pid;
	GetWindowThreadProcessId(_hwnd, &pid);
	HANDLE phandle = OpenProcess(PROCESS_VM_OPERATION | PROCESS_VM_READ | PROCESS_VM_WRITE, FALSE, pid);

	//allocate memory for the TBBUTTON structure
	LPVOID vspace = VirtualAllocEx(phandle, NULL, sizeof(TBBUTTON), MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);

	LRESULT res = SendMessage((HWND)hwnd, TB_GETBUTTON, index, (LPARAM)vspace);

	TBBUTTON _tbBtn;
	SIZE_T bytesRead;
	BOOL success = ReadProcessMemory(phandle, vspace, (LPVOID)&_tbBtn, sizeof(TBBUTTON), &bytesRead);
	//	ErrString("wufffinger WUMM", GetLastError());

	success = VirtualFreeEx(phandle, vspace, 0, MEM_RELEASE);

	CloseHandle(phandle);

	jint data[6];
	data[0] = _tbBtn.iBitmap;
	data[1] = _tbBtn.idCommand;
	data[2] = _tbBtn.fsState;
	data[3] = _tbBtn.fsStyle;
	data[4] = _tbBtn.dwData;
	data[5] = _tbBtn.iString;
	jintArray ret = env->NewIntArray(6);
	env->SetIntArrayRegion(ret, 0, 6, data);
	return ret;
}


/* GetToolButtonText */
JNI_SIG(jstring, WINAPI_NS(GetToolButtonText))
(JNIEnv *env, jclass, jint hwnd, jint idCommand){

	int strLen = SendMessage((HWND)hwnd, TB_GETBUTTONTEXT, idCommand, NULL);

	if(strLen <= 0)
		return 0;

	HWND _hwnd = (HWND) hwnd;
	DWORD pid;
	GetWindowThreadProcessId(_hwnd, &pid);
	HANDLE phandle = OpenProcess(PROCESS_VM_OPERATION | PROCESS_VM_READ | PROCESS_VM_WRITE, FALSE, pid);

	//allocate memory for the TBBUTTON structure
	LPVOID vspace = VirtualAllocEx(phandle, NULL, sizeof(char) * strLen + 1, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);

	LRESULT res = SendMessage((HWND)hwnd, TB_GETBUTTONTEXT, idCommand, (LPARAM)vspace);

	char* str = new char[strLen + 1];
	SIZE_T bytesRead;
	BOOL success = ReadProcessMemory(phandle, vspace, (LPVOID)str, sizeof(char) * strLen + 1, &bytesRead);

	success = VirtualFreeEx(phandle, vspace, 0, MEM_RELEASE);
	CloseHandle(phandle);

	jstring ret = env->NewStringUTF(str);
	delete[] str;

	return ret;
}


/* CloseHandle */
JNI_SIG(jboolean, WINAPI_NS(CloseHandle)) (JNIEnv * env, jclass cl, jlong hObject){
	return (jboolean)CloseHandle((HANDLE)hObject);
}

/* ExitProcess */
JNI_SIG(void, WINAPI_NS(ExitProcess)) (JNIEnv * env, jclass cl, jlong exitCode){
	ExitProcess(exitCode);
}

/* CreateProcess */
JNI_SIG(jlongArray, WINAPI_NS(CreateProcess)) (JNIEnv * env, jclass cl, jstring applicationName,
		jstring commandLine, jboolean inheritHandles, jlong creationFlags,
		jobjectArray environment, jstring currentDir, jstring desktop,
		jstring title, jlongArray startupInfo){

	const char *sCommandLine = env->GetStringUTFChars(commandLine, NULL);

	STARTUPINFO si;
	PROCESS_INFORMATION pi;

	ZeroMemory( &si, sizeof(si) );
	si.cb = sizeof(si);
	ZeroMemory( &pi, sizeof(pi) );

	BOOL success = CreateProcess( NULL,   // No module name (use command line)
						(LPSTR)sCommandLine,        // Command line
						NULL,           // Process handle not inheritable
						NULL,           // Thread handle not inheritable
						FALSE,          // Set handle inheritance to FALSE
						creationFlags,
						NULL,           // Use parent's environment block
						NULL,           // Use parent's starting directory
						&si,            // Pointer to STARTUPINFO structure
						&pi );           // Pointer to PROCESS_INFORMATION structure

	env->ReleaseStringUTFChars(commandLine, sCommandLine);

	if(!success){
		throwWinApiException(env, "Unable to start process!");
	    return 0;
	}

	jlong retArr[4];
	retArr[0] = (jlong)pi.hProcess;
	retArr[1] = (jlong)pi.hThread;
	retArr[2] = (jlong)pi.dwProcessId;
	retArr[3] = (jlong)pi.dwThreadId;

	jlongArray ret = env->NewLongArray(4);
	env->SetLongArrayRegion(ret, 0, 4, retArr);
	return ret;
}


/* GetScrollBarInfo */
JNI_SIG(jintArray, WINAPI_NS(GetScrollBarInfo))
(JNIEnv *env, jclass, jint hwnd, jint idObject){

	SCROLLBARINFO sbi;
	sbi.cbSize = sizeof(SCROLLBARINFO);

	if(GetScrollBarInfo((HWND)hwnd, (LONG)idObject, &sbi)){
		jint data[13];
		data[0] = sbi.rcScrollBar.left;
		data[1] = sbi.rcScrollBar.top;
		data[2] = sbi.rcScrollBar.right;
		data[3] = sbi.rcScrollBar.bottom;
		data[4] = sbi.dxyLineButton;
		data[5] = sbi.xyThumbTop;
		data[6] = sbi.xyThumbBottom;
		data[7] = sbi.rgstate[0];
		data[8] = sbi.rgstate[1];
		data[9] = sbi.rgstate[2];
		data[10] = sbi.rgstate[3];
		data[11] = sbi.rgstate[4];
		data[12] = sbi.rgstate[5];

		jintArray ret = env->NewIntArray(13);
		env->SetIntArrayRegion(ret, 0, 13, data);
		return ret;
	}else{
		return 0;
	}
}

/* GetSystemMetrics */
JNI_SIG(jint, WINAPI_NS(GetSystemMetrics))
(JNIEnv *env, jclass, jint nIndex){
	return GetSystemMetrics(nIndex);
}

/* GetMenuItemRect */
JNI_SIG(jintArray, WINAPI_NS(GetMenuItemRect))
(JNIEnv *env, jclass cl, jint hwnd, jint hmenu, jint uitem){
	RECT _rect;
	bool res = GetMenuItemRect((HWND)hwnd, (HMENU) hmenu, uitem, &_rect);
	if(res == 0)
		return 0;

	jint data[4];
	data[0] = _rect.left;
	data[1] = _rect.top;
	data[2] = _rect.right;
	data[3] = _rect.bottom;
	jintArray ret = env->NewIntArray(4);
	env->SetIntArrayRegion(ret, 0, 4, data);
	return ret;
}

/* GetMenu */
JNI_SIG(jlong, WINAPI_NS(GetMenu)) (JNIEnv *env, jclass cl, jlong hwnd){
	return (jlong)GetMenu((HWND)hwnd);
}

/* GetMenuBarInfo */
JNI_SIG(jintArray, WINAPI_NS(GetMenuBarInfo))
(JNIEnv* env, jclass, jint hwnd, jint idObject, jint idItem){

	MENUBARINFO _mbi;
	_mbi.cbSize = sizeof(MENUBARINFO);
	bool res = GetMenuBarInfo((HWND)hwnd, idObject, idItem, &_mbi);

	if(!res)
		return 0;

	jint data[8];
	data[0] = _mbi.rcBar.left;
	data[1] = _mbi.rcBar.top;
	data[2] = _mbi.rcBar.right;
	data[3] = _mbi.rcBar.bottom;
	data[4] = (jint)_mbi.hMenu;
	data[5] = (jint)_mbi.hwndMenu;
	data[6] = (jint)_mbi.fBarFocused;
	data[7] = (jint)_mbi.fFocused;
	jintArray ret = env->NewIntArray(8);
	env->SetIntArrayRegion(ret, 0, 8, data);
	return ret;
}


/* GetMenuItemCount */
JNI_SIG(jint, WINAPI_NS(GetMenuItemCount)) (JNIEnv *env, jclass cl, jint hMenu){
	return (jint)GetMenuItemCount((HMENU)hMenu);
}


/* GetSubMenu */
JNI_SIG(jint, WINAPI_NS(GetSubMenu)) (JNIEnv *env, jclass cl, jint hMenu, jint nPos){
	return (jint)GetSubMenu((HMENU)hMenu, nPos);
}


/* GetSystemMenu */
JNI_SIG(jlong, WINAPI_NS(GetSystemMenu)) (JNIEnv *env, jclass cl, jlong hWnd, jboolean bRevert){
	return (jlong)GetSystemMenu((HWND)hWnd, (bool)bRevert);
}


/* GetMenuString */
JNI_SIG(jstring, WINAPI_NS(GetMenuString))(JNIEnv *env, jclass, jint hMenu, jint uIDItem, jint uFlag){
	const int STR_LEN = 500;
	char str[STR_LEN];
	int res = GetMenuString((HMENU)hMenu, uIDItem, str, STR_LEN, uFlag);

	if(res == 0)
		return 0;

	return env->NewStringUTF(str);
}


/* ListView_GetItemCount */
JNI_SIG(jint, WINAPI_NS(ListView_1GetItemCount))
(JNIEnv *env, jclass, jint hwnd){
	return ListView_GetItemCount((HWND)hwnd);
}


/* ListView_GetItemText */
JNI_SIG(jstring, WINAPI_NS(ListView_1GetItemText))(JNIEnv *env, jclass, jint hwnd, jint iItem, jint iSubItem){
	const int STR_LEN = 500;
	//
	HWND _hwnd = (HWND) hwnd;
	DWORD pid;
	GetWindowThreadProcessId(_hwnd, &pid);
	HANDLE phandle = OpenProcess(PROCESS_VM_OPERATION | PROCESS_VM_READ | PROCESS_VM_WRITE, FALSE, pid);

	//allocate memory for the TBBUTTON structure
	LPVOID vspaceStr = VirtualAllocEx(phandle, NULL, sizeof(char) * STR_LEN + 1, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);
	LPVOID vspaceStruct = VirtualAllocEx(phandle, NULL, sizeof(LVITEM), MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);

	LVITEM lvi;
	lvi.iItem = iItem;
	lvi.iSubItem = 0;
	lvi.pszText = (LPSTR)vspaceStr;
	lvi.cchTextMax = STR_LEN;

	SIZE_T bytesWritten;
	BOOL success = WriteProcessMemory(phandle, vspaceStruct, (LPCVOID)&lvi, sizeof(LVITEM), &bytesWritten);

	LRESULT res = SendMessage((HWND)hwnd, LVM_GETITEMTEXT, iItem, (LPARAM)vspaceStruct);

	char strBuffer[STR_LEN + 1];

	SIZE_T bytesRead;
	success = ReadProcessMemory(phandle, vspaceStr, (LPVOID)strBuffer, sizeof(char) * STR_LEN + 1, &bytesRead);

	success = VirtualFreeEx(phandle, vspaceStruct, 0, MEM_RELEASE);
	success = VirtualFreeEx(phandle, vspaceStr, 0, MEM_RELEASE);
	CloseHandle(phandle);

	jstring ret = env->NewStringUTF(strBuffer);

	return ret;
}



/* ListView_GetItemRect */
JNI_SIG(jintArray, WINAPI_NS(ListView_1GetItemRect))(JNIEnv *env, jclass, jint hwnd, jint iItem, jint flags){

	DWORD pid;
	GetWindowThreadProcessId((HWND)hwnd, &pid);
	HANDLE phandle = OpenProcess(PROCESS_VM_OPERATION | PROCESS_VM_READ | PROCESS_VM_WRITE, FALSE, pid);

	LPVOID vspaceRect = VirtualAllocEx(phandle, NULL, sizeof(RECT), MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);

	RECT _rect;
	_rect.left = flags;

	SIZE_T bytesWritten;
	BOOL success = WriteProcessMemory(phandle, vspaceRect, (LPCVOID)&_rect, sizeof(RECT), &bytesWritten);

	LRESULT res = SendMessage((HWND)hwnd, LVM_GETITEMRECT, iItem, (LPARAM)vspaceRect);

	SIZE_T bytesRead;
	success = ReadProcessMemory(phandle, vspaceRect, (LPVOID)&_rect, sizeof(RECT), &bytesRead);

	success = VirtualFreeEx(phandle, vspaceRect, 0, MEM_RELEASE);
	CloseHandle(phandle);

	jint data[4];
	data[0] = _rect.left;
	data[1] = _rect.top;
	data[2] = _rect.right;
	data[3] = _rect.bottom;
	jintArray ret = env->NewIntArray(4);
	env->SetIntArrayRegion(ret, 0, 4, data);

	return ret;
}


/* GetAsyncKeyState */
JNI_SIG(jint, WINAPI_NS(GetAsyncKeyState))
(JNIEnv *env, jclass cl, jint vKey){
	return GetAsyncKeyState(vKey);
}

/* SetCursorPos */
JNI_SIG(jboolean, WINAPI_NS(SetCursorPos))
(JNIEnv *env, jclass, jint x, jint y){
	return SetCursorPos(x, y);
}


/* SetPixel */
JNI_SIG(jint, WINAPI_NS(SetPixel))
(JNIEnv * env, jclass cl, jint hdc, jint x, jint y, jint color){
	return SetPixel((HDC)hdc, x, y, color);
}

/* RGBMacro */
JNI_SIG(jint, WINAPI_NS(RGBMacro))
(JNIEnv * env, jclass cl, jint red, jint green, jint blue){
	return RGB(red, green, blue);
}

/* TextOut */
JNI_SIG(jboolean, WINAPI_NS(TextOut))
(JNIEnv * env, jclass cl, jint hdc, jint x, jint y, jstring text){

	const char* sText = env->GetStringUTFChars(text, NULL);
	jboolean ret = TextOut((HDC)hdc, x, y, sText, strlen(sText));
	env->ReleaseStringUTFChars(text, sText);

	return ret;
}

/* GetWindowDC */
JNI_SIG(jint, WINAPI_NS(GetWindowDC))
(JNIEnv * env, jclass cl, jint hwnd){
	return (int)GetWindowDC((HWND) hwnd);
}

/* InvalidateRect */
JNI_SIG(jboolean, WINAPI_NS(InvalidateRect))
(JNIEnv * env, jclass cl, jint hwnd, jint x, jint y, jint width, jint height, jboolean bErase){

	RECT rect;
	rect.left = x;
	rect.top = y;
	rect.right = x + width;
	rect.bottom = y + height;

	return InvalidateRect((HWND) hwnd, &rect, (int) bErase);
}

/* SelectObject */
JNI_SIG(jlong, WINAPI_NS(SelectObject))(JNIEnv * env, jclass cl, jlong hdc, jlong hgdiobj){
	return (jlong)SelectObject((HDC)hdc, (HGDIOBJ)hgdiobj);
}

/* DeleteObject */
JNI_SIG(jboolean, WINAPI_NS(DeleteObject))
(JNIEnv * env, jclass cl, jint hgdiobj){
	return DeleteObject((HGDIOBJ) hgdiobj);
}

/* FillRect */
JNI_SIG(jint, WINAPI_NS(FillRect))(JNIEnv * env, jclass cl, jint hdc, jint left, jint top,
		jint right, jint bottom, jint brush){

	RECT _rect;
	_rect.left = left;
	_rect.top = top;
	_rect.right = right;
	_rect.bottom = bottom;
	return FillRect((HDC)hdc, &_rect, (HBRUSH)brush);
}

/* CreateSolidBrush */
JNI_SIG(jint, WINAPI_NS(CreateSolidBrush))
(JNIEnv * env, jclass cl, jint color){
	return (jint)CreateSolidBrush((COLORREF) color);
}

/* ReleaseDC */
JNI_SIG(jint, WINAPI_NS(ReleaseDC))
(JNIEnv * env, jclass cl, jint hwnd, jint hdc){
	return ReleaseDC((HWND) hwnd, (HDC) hdc);
}


/* Rectangle */
JNI_SIG(jboolean, WINAPI_NS(Rectangle))
(JNIEnv * env, jclass cl, jint hdc, jint nLeftRect, jint nTopRect, jint nRightRect, jint nBottomRect){
	return Rectangle((HDC) hdc, nLeftRect, nTopRect, nRightRect, nBottomRect);
}

/* GetComboBoxInfo */
JNI_SIG(jintArray, WINAPI_NS(GetComboBoxInfo))
(JNIEnv *env, jclass, jint hwnd){

	COMBOBOXINFO _cbi;
	_cbi.cbSize = sizeof(COMBOBOXINFO);
	BOOL succ = GetComboBoxInfo((HWND)hwnd, &_cbi);

	if(succ == 0)
		return 0;

	//TODO: return Array!!!

	return 0;
}


/* GetCursorPos */
JNI_SIG(jdoubleArray, WINAPI_NS(GetCursorPos)) (JNIEnv * env, jclass){
	POINT p;
	BOOL success = GetCursorPos(&p);

	if(!success)
		return 0;

	jdouble retArr[2];
	retArr[0] = p.x;
	retArr[1] = p.y;

	jdoubleArray ret = env->NewDoubleArray(2);
	env->SetDoubleArrayRegion(ret, 0, 2, retArr);
	return ret;
}


/* SetWindowPos */
JNI_SIG(jboolean, WINAPI_NS(SetWindowPos)) (JNIEnv * env, jclass,
		jlong hwnd, jlong hwndInsertAfter, jlong x, jlong y,
		jlong cx, jlong cy, jlong uFlags){
	return SetWindowPos((HWND)hwnd, (HWND)hwndInsertAfter, x, y, cx, cy, uFlags);
}


/* ShowWindow */
JNI_SIG(jboolean, WINAPI_NS(ShowWindow)) (JNIEnv * env, jclass, jlong hwnd, jlong nCmdShow){
	return ShowWindow((HWND) hwnd, nCmdShow);
}


/* PeekMessage */
JNI_SIG(jlongArray, WINAPI_NS(PeekMessage)) (JNIEnv * env, jclass,
		jlong hwnd, jlong wMsgFilterMin, jlong wMsgFilterMax,
		jlong wRemoveMsg){

	MSG msg;
	BOOL success = PeekMessage(&msg, (HWND)hwnd, wMsgFilterMin, wMsgFilterMax, wRemoveMsg);

	if(!success)
		return 0;

	jlong retArr[7];
	retArr[0] = (jlong)msg.hwnd;
	retArr[1] = msg.message;
	retArr[2] = msg.wParam;
	retArr[3] = msg.lParam;
	retArr[4] = msg.time;
	retArr[5] = msg.pt.x;
	retArr[6] = msg.pt.y;

	jlongArray ret = env->NewLongArray(7);
	env->SetLongArrayRegion(ret, 0, 7, retArr);
	return ret;
}

/* GetMessage */
JNI_SIG(jlongArray, WINAPI_NS(GetMessage)) (JNIEnv * env, jclass,
		jlong hwnd, jlong wMsgFilterMin, jlong wMsgFilterMax){
	MSG msg;
	BOOL success = GetMessage(&msg, (HWND)hwnd, wMsgFilterMin, wMsgFilterMax);

	if(!success)
		return 0;

	jlong retArr[7];
	retArr[0] = (jlong)msg.hwnd;
	retArr[1] = msg.message;
	retArr[2] = msg.wParam;
	retArr[3] = msg.lParam;
	retArr[4] = msg.time;
	retArr[5] = msg.pt.x;
	retArr[6] = msg.pt.y;

	jlongArray ret = env->NewLongArray(7);
	env->SetLongArrayRegion(ret, 0, 7, retArr);
	return ret;

}


/* TranslateMessage */
JNI_SIG(jboolean, WINAPI_NS(TranslateMessage)) (JNIEnv * env, jclass,
		jlongArray lpMsg){

	jlong* lpMsgArr = env->GetLongArrayElements(lpMsg, NULL);

	MSG msg;
	msg.hwnd = (HWND)lpMsgArr[0];
	msg.message = lpMsgArr[1];
	msg.wParam = lpMsgArr[2];
	msg.lParam = lpMsgArr[3];
	msg.time = lpMsgArr[4];
	msg.pt.x = lpMsgArr[5];
	msg.pt.y = lpMsgArr[6];

	env->ReleaseLongArrayElements(lpMsg, lpMsgArr, 0);
	return TranslateMessage(&msg);
}

/* DispatchMessage */
JNI_SIG(jlong, WINAPI_NS(DispatchMessage)) (JNIEnv * env, jclass,
		jlongArray lpMsg){
	jlong* lpMsgArr = env->GetLongArrayElements(lpMsg, NULL);

	MSG msg;
	msg.hwnd = (HWND)lpMsgArr[0];
	msg.message = lpMsgArr[1];
	msg.wParam = lpMsgArr[2];
	msg.lParam = lpMsgArr[3];
	msg.time = lpMsgArr[4];
	msg.pt.x = lpMsgArr[5];
	msg.pt.y = lpMsgArr[6];

	env->ReleaseLongArrayElements(lpMsg, lpMsgArr, 0);
	return DispatchMessage(&msg);
}

/* GetModuleHandleEx */
JNI_SIG(jlong, WINAPI_NS(GetModuleHandleEx)) (JNIEnv * env, jclass,
		jlong dwFlags, jstring lpModuleName){
	const char *nativeModuleName = env->GetStringUTFChars(lpModuleName, NULL);
	HMODULE hm;
	BOOL success = GetModuleHandleEx(dwFlags, nativeModuleName, &hm);
	env->ReleaseStringUTFChars(lpModuleName, nativeModuleName);
	if(!success)
		throwWinApiException(env, "Failed to get module handle!");
	return (jlong)hm;
}


/* MonitorFromPoint */
JNI_SIG(jlong, WINAPI_NS(MonitorFromPoint)) (JNIEnv * env, jclass,
		jlong x, jlong y, jlong dwFlags){
	POINT p;
	p.x = x;
	p.y = y;
	return (jlong)MonitorFromPoint(p, dwFlags);
}


/* GdiplusStartup */
JNI_SIG(jlong, WINAPI_NS(GdiplusStartup)) (JNIEnv * env, jclass){
	Gdiplus::GdiplusStartupInput 	gdiplusStartupInput;
	ULONG_PTR   					gdiplusToken;

	Gdiplus::Status status = Gdiplus::GdiplusStartup(&gdiplusToken, &gdiplusStartupInput, NULL);
	if (Gdiplus::Ok != status)
		throwGDIException(env, status);
	return (jlong) gdiplusToken;
}


/* GdiplusShutdown */
JNI_SIG(void, WINAPI_NS(GdiplusShutdown)) (JNIEnv * env, jclass,
		jlong gdiplusToken){
	Gdiplus::GdiplusShutdown(gdiplusToken);
}


/* GetMonitorInfo */
JNI_SIG(jlongArray, WINAPI_NS(GetMonitorInfo)) (JNIEnv * env, jclass,
		jlong hMonitor){
	MONITORINFO mi;
	mi.cbSize = sizeof(MONITORINFO);
	BOOL success = GetMonitorInfo((HMONITOR)hMonitor, &mi);

	if(!success){
		throwWinApiException(env, "Unable to retrieve monitor info!");
		return 0;
	}

	jlong retArr[10];
	retArr[0] = mi.cbSize;
	retArr[1] = mi.rcMonitor.left;
	retArr[2] = mi.rcMonitor.top;
	retArr[3] = mi.rcMonitor.right;
	retArr[4] = mi.rcMonitor.bottom;
	retArr[5] = mi.rcWork.left;
	retArr[6] = mi.rcWork.top;
	retArr[7] = mi.rcWork.right;
	retArr[8] = mi.rcWork.bottom;
	retArr[9] = mi.dwFlags;

	jlongArray ret = env->NewLongArray(10);
	env->SetLongArrayRegion(ret, 0, 10, retArr);
	return ret;
}


/* UpdateLayeredWindow */
JNI_SIG(void, WINAPI_NS(UpdateLayeredWindow)) (JNIEnv * env, jclass,
		jlong hwnd, jlong hdcDst, jlong pptDstX, jlong pptDstY,
		jlong psizeCX, jlong psizeCY, jlong hdcSrc, jlong pptSrcX,
		jlong pptSrcY, jlong crKey, jint pblendOp, jint pblendFlags,
		jint pblendSCAlpha, jint pblendAlphaFormat, jlong dwFlags){

	POINT pptDst;
	pptDst.x = pptDstX;
	pptDst.y = pptDstY;

	SIZE psize;
	psize.cx = psizeCX;
	psize.cy = psizeCY;

	POINT pptSrc;
	pptSrc.x = pptSrcX;
	pptSrc.y = pptSrcY;

	BLENDFUNCTION pblend;
	pblend.BlendOp = pblendOp;
	pblend.BlendFlags = pblendFlags;
	pblend.SourceConstantAlpha = pblendSCAlpha;
	pblend.AlphaFormat = pblendAlphaFormat;

	BOOL success = UpdateLayeredWindow((HWND)hwnd, (HDC)hdcDst, &pptDst, &psize, (HDC)hdcSrc, &pptSrc, crKey, &pblend, dwFlags);
	if(!success)
		throwWinApiException(env, "Unable to update layered window!");
}

/* GetDC */
JNI_SIG(jlong, WINAPI_NS(GetDC))(JNIEnv *env, jclass, jlong hwnd){
	HDC ret = GetDC((HWND)hwnd);
	if(ret == NULL)
		throwWinApiException(env, "Unable to get device context!");
	return (jlong) ret;
}

/* CreateCompatibleDC */
JNI_SIG(jlong, WINAPI_NS(CreateCompatibleDC))(JNIEnv *env, jclass,
		jlong hdc){
	return (jlong)CreateCompatibleDC((HDC)hdc);
}

/* CreateCompatibleBitmap */
JNI_SIG(jlong, WINAPI_NS(CreateCompatibleBitmap))(JNIEnv *env, jclass,
		jlong hdc, jlong nWidth, jlong nHeight){
	return (jlong)CreateCompatibleBitmap((HDC)hdc, nWidth, nHeight);
}


/* Gdiplus_Graphics_FromHDC */
JNI_SIG(jlong, WINAPI_NS(Gdiplus_1Graphics_1FromHDC))(JNIEnv *env,
		jclass, jlong hdc){
	return (jlong)Gdiplus::Graphics::FromHDC((HDC)hdc);
}


/* Gdiplus_Graphics_Clear */
JNI_SIG(jlong, WINAPI_NS(Gdiplus_1Graphics_1Clear))(JNIEnv *env, jclass,
		jlong graphics, jint alpha, jint red, jint blue, jint green){
	Gdiplus::Graphics *pg = reinterpret_cast<Gdiplus::Graphics*>(graphics);
	return pg->Clear(Gdiplus::Color(alpha, red, blue, green));
}


/* Gdiplus_Graphics_DrawLine */
JNI_SIG(jlong, WINAPI_NS(Gdiplus_1Graphics_1DrawLine))(JNIEnv *env,
		jclass, jlong graphics, jlong pen, jdouble x1, jdouble y1,
		jdouble x2, jdouble y2){
	Gdiplus::Graphics *pg = reinterpret_cast<Gdiplus::Graphics*>(graphics);
	Gdiplus::Pen *pp = reinterpret_cast<Gdiplus::Pen*>(pen);
	return pg->DrawLine(pp, (Gdiplus::REAL)x1, (Gdiplus::REAL)y1, (Gdiplus::REAL)x2, (Gdiplus::REAL)y2);
}


/* Gdiplus_Graphics_DrawRectangle */
JNI_SIG(jlong, WINAPI_NS(Gdiplus_1Graphics_1DrawRectangle))(JNIEnv *env,
		jclass, jlong graphics, jlong pen, jdouble x, jdouble y,
		jdouble width, jdouble height){
	Gdiplus::Graphics *pg = reinterpret_cast<Gdiplus::Graphics*>(graphics);
	Gdiplus::Pen *pp = reinterpret_cast<Gdiplus::Pen*>(pen);
	return pg->DrawRectangle(pp, (Gdiplus::REAL)x, (Gdiplus::REAL)y, (Gdiplus::REAL)width, (Gdiplus::REAL)height);
}


/* Gdiplus_Graphics_FillRectangle */
JNI_SIG(jlong, WINAPI_NS(Gdiplus_1Graphics_1FillRectangle))(JNIEnv *env,
		jclass, jlong graphics, jlong brush, jdouble x, jdouble y,
		jdouble width, jdouble height){
	Gdiplus::Graphics *pg = reinterpret_cast<Gdiplus::Graphics*>(graphics);
	Gdiplus::Brush *pb = reinterpret_cast<Gdiplus::Brush*>(brush);
	return pg->FillRectangle(pb, (Gdiplus::REAL)x, (Gdiplus::REAL)y, (Gdiplus::REAL)width, (Gdiplus::REAL)height);
}


/* Gdiplus_Graphics_DrawEllipse */
JNI_SIG(jlong, WINAPI_NS(Gdiplus_1Graphics_1DrawEllipse))(JNIEnv *env,
		jclass, jlong graphics, jlong pen, jdouble x, jdouble y,
		jdouble width, jdouble height){
	Gdiplus::Graphics *pg = reinterpret_cast<Gdiplus::Graphics*>(graphics);
	Gdiplus::Pen *pp = reinterpret_cast<Gdiplus::Pen*>(pen);
	return pg->DrawEllipse(pp, (Gdiplus::REAL)x, (Gdiplus::REAL)y, (Gdiplus::REAL)width, (Gdiplus::REAL)height);
}


/* Gdiplus_Graphics_FillEllipse */
JNI_SIG(jlong, WINAPI_NS(Gdiplus_1Graphics_1FillEllipse))(JNIEnv *env,
		jclass, jlong graphics, jlong brush, jdouble x, jdouble y,
		jdouble width, jdouble height){
	Gdiplus::Graphics *pg = reinterpret_cast<Gdiplus::Graphics*>(graphics);
	Gdiplus::Brush *pb = reinterpret_cast<Gdiplus::Brush*>(brush);
	return pg->FillEllipse(pb, (Gdiplus::REAL)x, (Gdiplus::REAL)y, (Gdiplus::REAL)width, (Gdiplus::REAL)height);
}


/* Gdiplus_Graphics_DrawString */
JNI_SIG(jlong, WINAPI_NS(Gdiplus_1Graphics_1DrawString))(JNIEnv *env,
		jclass, jlong graphics, jstring text, jlong font, jdouble x,
		jdouble y, jlong brush){

	Gdiplus::Graphics *pg = reinterpret_cast<Gdiplus::Graphics*>(graphics);
	Gdiplus::Brush *pb = reinterpret_cast<Gdiplus::Brush*>(brush);
	Gdiplus::Font *pf = reinterpret_cast<Gdiplus::Font*>(font);
	const char *nativeText = env->GetStringUTFChars(text, NULL);

	jlong ret = 0;
	int len = MultiByteToWideChar(CP_UTF8, 0, nativeText, -1, NULL, 0);
	if (len > 1){
		wchar_t* buffer = (wchar_t*) malloc(sizeof(wchar_t) * len);
		MultiByteToWideChar(CP_UTF8, 0, nativeText, -1, buffer, len);

		Gdiplus::PointF origin(x, y);
		ret = pg->DrawString(buffer, len, pf, origin, pb);
		free(buffer);
	}
	env->ReleaseStringUTFChars(text, nativeText);

	return ret;
}


/* Gdiplus_Pen_Create */
JNI_SIG(jlong, WINAPI_NS(Gdiplus_1Pen_1Create))(JNIEnv *env, jclass,
		jint alpha, jint red, jint green, jint blue, jdouble width){
	return (jlong)new Gdiplus::Pen(Gdiplus::Color(alpha, red, green, blue), width);
}

/* Gdiplus_Pen_SetColor */
JNI_SIG(jlong, WINAPI_NS(Gdiplus_1Pen_1SetColor))(JNIEnv *env, jclass,
		jlong pen, jint alpha, jint red, jint green, jint blue){
	Gdiplus::Pen *pp = reinterpret_cast<Gdiplus::Pen*>(pen);
	return pp->SetColor(Gdiplus::Color(alpha, red, green, blue));
}

/* Gdiplus_Pen_SetWidth */
JNI_SIG(jlong, WINAPI_NS(Gdiplus_1Pen_1SetWidth))(JNIEnv *env,
		jclass, jlong pen, jdouble width){
	Gdiplus::Pen *pp = reinterpret_cast<Gdiplus::Pen*>(pen);
	return pp->SetWidth((Gdiplus::REAL)width);
}

/* Gdiplus_Pen_Destroy */
JNI_SIG(void, WINAPI_NS(Gdiplus_1Pen_1Destroy))(JNIEnv *env, jclass, jlong pen){
	delete reinterpret_cast<Gdiplus::Pen*>(pen);
}

/* Gdiplus_SolidBrush_Create */
JNI_SIG(jlong, WINAPI_NS(Gdiplus_1SolidBrush_1Create))(JNIEnv *env,
		jclass, jint alpha, jint red, jint green, jint blue){
	return (jlong)new Gdiplus::SolidBrush(Gdiplus::Color(alpha, red, green, blue));
}

/* Gdiplus_SolidBrush_SetColor */
JNI_SIG(jlong, WINAPI_NS(Gdiplus_1SolidBrush_1SetColor))(JNIEnv *env,
		jclass, jlong brush, jint alpha, jint red, jint green, jint blue){
	Gdiplus::SolidBrush *pb = (Gdiplus::SolidBrush*)(brush);
	return pb->SetColor(Gdiplus::Color(alpha, red, green, blue));
}

/* Gdiplus_SolidBrush_Destroy */
JNI_SIG(void, WINAPI_NS(Gdiplus_1SolidBrush_1Destroy))(JNIEnv *env, jclass, jlong brush){
	delete reinterpret_cast<Gdiplus::SolidBrush*>(brush);
}

/* Gdiplus_Font_Create */
JNI_SIG(jlong, WINAPI_NS(Gdiplus_1Font_1Create))(JNIEnv *env, jclass,
		jlong fontFamily, jdouble size, jint style, jint unit){
	return (jlong) new Gdiplus::Font(reinterpret_cast<Gdiplus::FontFamily*>(fontFamily), (Gdiplus::REAL)size, style, (Gdiplus::Unit)unit);
}

/* Gdiplus_Font_Destroy */
JNI_SIG(void, WINAPI_NS(Gdiplus_1Font_1Destroy))(JNIEnv *env, jclass, jlong font){
	delete reinterpret_cast<Gdiplus::Font*>(font);
}


/* Gdiplus_FontFamily_Create */
JNI_SIG(jlong, WINAPI_NS(Gdiplus_1FontFamily_1Create))(JNIEnv *env, jclass,
		jstring fontName){
	const int MAX_STRING_LEN = 500;
	const char *nativeFontName = env->GetStringUTFChars(fontName, NULL);

	// convert to wchar
	size_t convertedChars = 0;
	wchar_t wcstring[MAX_STRING_LEN];
	mbstowcs_s(&convertedChars, wcstring, min(strlen(nativeFontName) + 1, MAX_STRING_LEN), nativeFontName, _TRUNCATE);

	Gdiplus::FontFamily* ret = new Gdiplus::FontFamily(wcstring);
	env->ReleaseStringUTFChars(fontName, nativeFontName);
	return (jlong) ret;
}

/* Gdiplus_FontFamily_Destroy */
JNI_SIG(void, WINAPI_NS(Gdiplus_1FontFamily_1Destroy))(JNIEnv *env,
		jclass, jlong fontFamily){
	delete reinterpret_cast<Gdiplus::FontFamily*>(fontFamily);
}


// defaul Callback Function for windows
LRESULT CALLBACK WndProc(HWND hwnd, UINT Message, WPARAM wParam, LPARAM lParam) {
	switch(Message)
	{
	case WM_CLOSE:
		DestroyWindow(hwnd);
		break;
	case WM_DESTROY:
		PostQuitMessage(0);
		break;
	default:
		return DefWindowProc(hwnd, Message, wParam, lParam);
	}
	return 0;
}


/* CreateWindowEx */
JNI_SIG(jlong, WINAPI_NS(CreateWindowEx)) (JNIEnv * env, jclass, jlong dwExStyle,
		jstring lpClassName, jstring lpWindowName, jlong dwStyle, jlong x,
		jlong y, jlong nWidth, jlong nHeight, jlong hwndParent, jlong hMenu,
		jlong hInstance, jlong lpParam){

	const char *nativeClassName = (lpClassName == 0) ? "Default Custom Class With Standard Callback" : env->GetStringUTFChars(lpClassName, NULL);
	const char *nativeWindowName = env->GetStringUTFChars(lpWindowName, NULL);


	// use custom window class as default (with default callback!)
	if(lpClassName == 0){
		WNDCLASSEX WndClass;
		WndClass.cbSize    	= sizeof(WNDCLASSEX);
		WndClass.style     	= NULL;
		WndClass.lpfnWndProc   = WndProc;
		WndClass.cbClsExtra	= 0;
		WndClass.cbWndExtra	= 0;
		WndClass.hInstance 	= (HINSTANCE)hInstance;
		WndClass.hIcon     	= LoadIcon(NULL, IDI_APPLICATION);
		WndClass.hCursor   	= LoadCursor(NULL, IDC_ARROW);
		WndClass.hbrBackground = (HBRUSH)(COLOR_WINDOW+1);
		WndClass.lpszMenuName  = NULL;
		WndClass.lpszClassName = nativeClassName;
		WndClass.hIconSm   	= LoadIcon(NULL, IDI_APPLICATION);
		RegisterClassEx(&WndClass);
	}

	HWND ret = CreateWindowEx(dwExStyle, nativeClassName, nativeWindowName, dwStyle, x, y, nWidth,
			nHeight, (HWND) hwndParent, (HMENU) hMenu, (HINSTANCE) hInstance, (LPVOID)lpParam);

	env->ReleaseStringUTFChars(lpWindowName, nativeWindowName);
	if(lpClassName != 0)
		env->ReleaseStringUTFChars(lpClassName, nativeClassName);

	return (jlong)ret;
}

/* GetCurrentModule */
JNI_SIG(jlong, WINAPI_NS(GetCurrentModule)) (JNIEnv * env, jclass){
	HMODULE hModule = NULL;
	if(GetModuleHandleEx( GET_MODULE_HANDLE_EX_FLAG_FROM_ADDRESS, (LPCTSTR)WndProc, &hModule) == 0)
		return 0;
	return (jlong) hModule;
}


/* OpenProcess */
JNI_SIG(jlong, WINAPI_NS(OpenProcess)) (JNIEnv * env, jclass,
		jlong dwDesiredAccess, jboolean bInheritHandle, jlong dwProcessId){
	HANDLE ret = OpenProcess(dwDesiredAccess, bInheritHandle, dwProcessId);

	if(ret == NULL)
		throwWinApiException(env, errString("OpenProcess", GetLastError()));
	return (jlong) ret;
}


/* WaitForSingleObject */
JNI_SIG(jlong, WINAPI_NS(WaitForSingleObject)) (JNIEnv * env, jclass,
		jlong hHandle, jlong dwMilliseconds){
	DWORD ret = WaitForSingleObject((HANDLE)hHandle, dwMilliseconds);

	if(ret == WAIT_FAILED)
		throwWinApiException(env, errString("WaitForSingleObject", ret));
	return (jlong) ret;
}

/* WaitForInputIdle */
JNI_SIG(jlong, WINAPI_NS(WaitForInputIdle)) (JNIEnv * env, jclass, jlong hProcess){
	DWORD ret = WaitForInputIdle((HANDLE)hProcess, INFINITE); //If dwMilliseconds is INFINITE, the function does not return until the process is idle.

	if(ret == WAIT_FAILED)
		throwWinApiException(env, errString("WaitForInputIdle", ret));
	return (jlong) ret;
}

/* GetExitCodeProcess */
JNI_SIG(jlong, WINAPI_NS(GetExitCodeProcess)) (JNIEnv * env, jclass, jlong hProcess){
	DWORD exitCode;
	if(!GetExitCodeProcess((HANDLE)hProcess, &exitCode))
		throwWinApiException(env, errString("GetExitCodeProcess", GetLastError()));
	return (jlong) exitCode;
}


/* CoInitializeEx */
JNI_SIG(jlong, WINAPI_NS(CoInitializeEx)) (JNIEnv * env, jclass, jlong pvReserved, jlong dwCoInit){
	HRESULT ret = CoInitializeEx((LPVOID)pvReserved, (DWORD)dwCoInit);
	if(FAILED(ret))
		throwWinApiException(env, errString("CoInitializeEx", ret));
	return ret;
}

/* CoCreateInstance */
JNI_SIG(jlong, WINAPI_NS(CoCreateInstance)) (JNIEnv * env, jclass, jlong rclsid,
		jlong pUnkOuter, jlong dwClsContext, jlong riid){
	LPVOID ppv;
	HRESULT ret = CoCreateInstance(*(CLSID*)rclsid, (LPUNKNOWN)pUnkOuter, dwClsContext, *(CLSID*)riid, &ppv);
	if(FAILED(ret))
		throwWinApiException(env, errString("CoCreateInstance", ret));
	return (jlong)ppv;
}

/* Get_CLSID_CUIAutomation_Ptr */
JNI_SIG(jlong, WINAPI_NS(Get_1CLSID_1CUIAutomation_1Ptr)) (JNIEnv * env, jclass){
	return (jlong) &CLSID_CUIAutomation;
}

/* Get_IID_IUIAutomation_Ptr */
JNI_SIG(jlong, WINAPI_NS(Get_1IID_1IUIAutomation_1Ptr)) (JNIEnv * env, jclass){
	return (jlong) &IID_IUIAutomation;
}


/* Get_CLSID_ApplicationActivationManager_Ptr */
JNI_SIG(jlong, WINAPI_NS(Get_1CLSID_1ApplicationActivationManager_1Ptr)) (JNIEnv * env, jclass){
	return (jlong) &CLSID_ApplicationActivationManager;
}

/* Get_IID_IApplicationActivationManager_Ptr */
JNI_SIG(jlong, WINAPI_NS(Get_1IID_1IApplicationActivationManager_1Ptr)) (JNIEnv * env, jclass){
	return (jlong) &IID_IApplicationActivationManager;
}


/* IUnknown_QueryInterface */
JNI_SIG(jlong, WINAPI_NS(IUnknown_1QueryInterface)) (JNIEnv * env, jclass,
		jlong pIUnknown, jlong pIID){
	IUnknown* iu = (IUnknown*)pIUnknown;
	void* iface;
	HRESULT res = iu->QueryInterface(*(IID*)pIID, &iface);
	if(FAILED(res))
		return 0;
	return (jlong) iface;
}

/* IUnknown_AddRef */
JNI_SIG(jlong, WINAPI_NS(IUnknown_1AddRef)) (JNIEnv * env, jclass, jlong pIUnknown){
	return ((IUnknown*)pIUnknown)->AddRef();
}

/* IUnknown_Release */
JNI_SIG(jlong, WINAPI_NS(IUnknown_1Release)) (JNIEnv * env, jclass, jlong pIUnknown){
	return ((IUnknown*)pIUnknown)->Release();
}

/* CoUninitialize */
JNI_SIG(void, WINAPI_NS(CoUninitialize)) (JNIEnv * env, jclass){
	CoUninitialize();
}

/* IUIAutomation_GetRootElement */
JNI_SIG(jlong, WINAPI_NS(IUIAutomation_1GetRootElement)) (JNIEnv * env, jclass,
		jlong pIUIAutomation){
	IUIAutomationElement* root;
	HRESULT res = ((IUIAutomation*)pIUIAutomation)->GetRootElement(&root);
	if(FAILED(res))
		throwUIAException(env, errString("IUIAutomation_GetRootElement", res));
	return (jlong)root;
}

/* IUIAutomationElement_get_AcceleratorKey */
JNI_SIG(jstring, WINAPI_NS(IUIAutomationElement_1get_1AcceleratorKey)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElement, jboolean fromCache){
	BSTR value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedAcceleratorKey(&value) : el->get_CurrentAcceleratorKey(&value);
	if (FAILED(hr))
		return 0;

	jstring ret = env->NewStringUTF(_com_util::ConvertBSTRToString(value));
	SysFreeString(value);
	return ret;
}

/* IUIAutomationElement_get_AccessKey */
JNI_SIG(jstring, WINAPI_NS(IUIAutomationElement_1get_1AccessKey)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElement, jboolean fromCache){
	BSTR value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedAccessKey(&value) : el->get_CurrentAccessKey(&value);
	if (FAILED(hr))
		return 0;

	jstring ret = env->NewStringUTF(_com_util::ConvertBSTRToString(value));
	SysFreeString(value);
	return ret;
}

/* IUIAutomationElement_get_HelpText */
JNI_SIG(jstring, WINAPI_NS(IUIAutomationElement_1get_1HelpText)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElement, jboolean fromCache){
	BSTR value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedHelpText(&value) : el->get_CurrentHelpText(&value);
	if (FAILED(hr))
		return 0;

	jstring ret = env->NewStringUTF(_com_util::ConvertBSTRToString(value));
	SysFreeString(value);
	return ret;
}

/* IUIAutomationElement_get_ItemType */
JNI_SIG(jstring, WINAPI_NS(IUIAutomationElement_1get_1ItemType)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElement, jboolean fromCache){
	BSTR value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedItemType(&value) : el->get_CurrentItemType(&value);
	if (FAILED(hr))
		return 0;

	jstring ret = env->NewStringUTF(_com_util::ConvertBSTRToString(value));
	SysFreeString(value);
	return ret;
}

/* IUIAutomationElement_get_ItemStatus */
JNI_SIG(jstring, WINAPI_NS(IUIAutomationElement_1get_1ItemStatus)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElement, jboolean fromCache){
	BSTR value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedItemStatus(&value) : el->get_CurrentItemStatus(&value);
	if (FAILED(hr))
		return 0;

	jstring ret = env->NewStringUTF(_com_util::ConvertBSTRToString(value));
	SysFreeString(value);
	return ret;
}


/* IUIAutomationElement_get_LocalizedControlType */
JNI_SIG(jstring, WINAPI_NS(IUIAutomationElement_1get_1LocalizedControlType)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElement, jboolean fromCache){
	BSTR value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedLocalizedControlType(&value) : el->get_CurrentLocalizedControlType(&value);
	if (FAILED(hr))
		return 0;

	jstring ret = env->NewStringUTF(_com_util::ConvertBSTRToString(value));
	SysFreeString(value);
	return ret;
}


/* IUIAutomationElement_get_ClassName */
JNI_SIG(jstring, WINAPI_NS(IUIAutomationElement_1get_1ClassName)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElement, jboolean fromCache){
	BSTR value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedClassName(&value) : el->get_CurrentClassName(&value);
	if (FAILED(hr))
		return 0;

	jstring ret = env->NewStringUTF(_com_util::ConvertBSTRToString(value));
	SysFreeString(value);
	return ret;
}


/* IUIAutomationElement_get_Name */
JNI_SIG(jstring, WINAPI_NS(IUIAutomationElement_1get_1Name)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElement, jboolean fromCache){
	BSTR value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedName(&value) : el->get_CurrentName(&value);
	if (FAILED(hr))
		return 0;

	jstring ret = env->NewStringUTF(_com_util::ConvertBSTRToString(value));
	SysFreeString(value);
	return ret;
}

HRESULT GetValuePatternFromElement(IUIAutomationElement *pElement, PATTERNID patternId, BSTR *pValuePattern) {
    *pValuePattern = NULL;

    BSTR bstr;
    IUIAutomationValuePattern *pUIAValuePattern;
    HRESULT hr = pElement->GetCurrentPatternAs(patternId, IID_PPV_ARGS(&pUIAValuePattern));
    if (SUCCEEDED(hr) && (pUIAValuePattern != NULL)) {
        // Cached version doesn't seem to work ?
        hr = pUIAValuePattern->get_CurrentValue(&bstr);
        if (SUCCEEDED(hr) && bstr != NULL) {
            *pValuePattern = bstr;
            //SysFreeString(bstr);
        }
        pUIAValuePattern->Release();
        pUIAValuePattern = NULL;
    }

    return hr;
}

/* IUIAutomationElement_get_ValuePattern */
JNI_SIG(jstring, WINAPI_NS(IUIAutomationElement_1get_1ValuePattern)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElement, jlong patternId) {
	BSTR value;
    IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
    HRESULT hr = GetValuePatternFromElement(el, patternId, &value);
    if (FAILED(hr))
        return 0;

    jstring ret = env->NewStringUTF(_com_util::ConvertBSTRToString(value));
    SysFreeString(value);
    return ret;
}

/* IUIAutomationElement_get_ProviderDescription */
JNI_SIG(jstring, WINAPI_NS(IUIAutomationElement_1get_1ProviderDescription)) (JNIEnv * env,
		jclass, jlong pIUIAutomationElement, jboolean fromCache){
	BSTR value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedProviderDescription(&value) : el->get_CurrentProviderDescription(&value);
	if (FAILED(hr))
		return 0;

	jstring ret = env->NewStringUTF(_com_util::ConvertBSTRToString(value));
	SysFreeString(value);
	return ret;
}

/* IUIAutomationElement_get_FrameworkId */
JNI_SIG(jstring, WINAPI_NS(IUIAutomationElement_1get_1FrameworkId)) (JNIEnv * env,
		jclass, jlong pIUIAutomationElement, jboolean fromCache){
	BSTR value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedFrameworkId(&value) : el->get_CurrentFrameworkId(&value);
	if (FAILED(hr))
		return 0;

	jstring ret = env->NewStringUTF(_com_util::ConvertBSTRToString(value));
	SysFreeString(value);
	return ret;
}

/* IUIAutomationElement_get_ControlType */
JNI_SIG(jlong, WINAPI_NS(IUIAutomationElement_1get_1ControlType)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElement, jboolean fromCache){
	CONTROLTYPEID value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedControlType(&value) : el->get_CurrentControlType(&value);
	if (FAILED(hr))
		return 0;
	return (jlong) value;
}

/* IUIAutomationElement_get_Culture */
JNI_SIG(jlong, WINAPI_NS(IUIAutomationElement_1get_1Culture)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElement, jboolean fromCache){
	int value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedCulture(&value) : el->get_CurrentCulture(&value);
	if (FAILED(hr))
		return 0;
	return (jlong) value;
}

/* IUIAutomationElement_get_Orientation */
JNI_SIG(jlong, WINAPI_NS(IUIAutomationElement_1get_1Orientation)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElement, jboolean fromCache){
	OrientationType value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedOrientation(&value) : el->get_CurrentOrientation(&value);
	if (FAILED(hr))
		return 0;
	return (jlong) value;
}


/* IUIAutomationElement_get_ProcessId */
JNI_SIG(jlong, WINAPI_NS(IUIAutomationElement_1get_1ProcessId)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElement, jboolean fromCache){
	int value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedProcessId(&value) : el->get_CurrentProcessId(&value);
	if (FAILED(hr))
		return 0;
	return (jlong) value;
}


/* IUIAutomationElement_get_NativeWindowHandle */
JNI_SIG(jlong, WINAPI_NS(IUIAutomationElement_1get_1NativeWindowHandle)) (JNIEnv * env,
		jclass, jlong pIUIAutomationElement, jboolean fromCache){
	UIA_HWND value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedNativeWindowHandle(&value) : el->get_CurrentNativeWindowHandle(&value);
	if (FAILED(hr))
		return 0;
	return (jlong) value;
}


/* IUIAutomationElement_get_IsContentElement */
JNI_SIG(jboolean, WINAPI_NS(IUIAutomationElement_1get_1IsContentElement)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElement, jboolean fromCache){
	BOOL value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedIsContentElement(&value) : el->get_CurrentIsContentElement(&value);
	if (FAILED(hr))
		return 0;
	return (jboolean) value;
}

/* IUIAutomationElement_get_IsControlElement */
JNI_SIG(jboolean, WINAPI_NS(IUIAutomationElement_1get_1IsControlElement)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElement, jboolean fromCache){
	BOOL value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedIsControlElement(&value) : el->get_CurrentIsControlElement(&value);
	if (FAILED(hr))
		return 0;
	return (jboolean) value;
}

/* IUIAutomationElement_get_IsEnabled */
JNI_SIG(jboolean, WINAPI_NS(IUIAutomationElement_1get_1IsEnabled)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElement, jboolean fromCache){
	BOOL value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedIsEnabled(&value) : el->get_CurrentIsEnabled(&value);
	if (FAILED(hr))
		return 1;
	return (jboolean) value;
}

/* IUIAutomationElement_get_HasKeyboardFocus */
JNI_SIG(jboolean, WINAPI_NS(IUIAutomationElement_1get_1HasKeyboardFocus)) (JNIEnv * env,
		jclass, jlong pIUIAutomationElement, jboolean fromCache){
	BOOL value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedHasKeyboardFocus(&value) : el->get_CurrentHasKeyboardFocus(&value);
	if (FAILED(hr))
		return 0;

	return (jboolean) value;
}


/* IUIAutomationElement_get_IsKeyboardFocusable */
JNI_SIG(jboolean, WINAPI_NS(IUIAutomationElement_1get_1IsKeyboardFocusable)) (JNIEnv * env,
		jclass, jlong pIUIAutomationElement, jboolean fromCache){
	BOOL value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedIsKeyboardFocusable(&value) : el->get_CurrentIsKeyboardFocusable(&value);
	if (FAILED(hr))
		return 0;
	return (jboolean) value;
}


/* IUIAutomationElement_GetPattern */
JNI_SIG(jlong, WINAPI_NS(IUIAutomationElement_1GetPattern)) (JNIEnv * env, jclass,
		jlong pElement, jlong patternId, jboolean fromCache){
	IUIAutomationElement* el = (IUIAutomationElement*) pElement;
	IUnknown* ret;
	HRESULT hr = fromCache ? el->GetCachedPattern((PATTERNID)patternId, &ret) : el->GetCurrentPattern((PATTERNID)patternId, &ret);
	if (FAILED(hr) || ret == 0)
		return 0;
	return (jlong) ret;
}


/* IUIAutomationElement_get_IsOffscreen */
JNI_SIG(jboolean, WINAPI_NS(IUIAutomationElement_1get_1IsOffscreen)) (JNIEnv * env,
		jclass, jlong pIUIAutomationElement, jboolean fromCache){
	BOOL value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedIsOffscreen(&value) : el->get_CurrentIsOffscreen(&value);
	if (FAILED(hr))
		return 0;
	return (jboolean) value;
}

/* IUIAutomationElement_get_BoundingRectangle */
JNI_SIG(jlongArray, WINAPI_NS(IUIAutomationElement_1get_1BoundingRectangle)) (JNIEnv * env,
		jclass, jlong pIUIAutomationElement, jboolean fromCache){
	RECT value;
	IUIAutomationElement* el = (IUIAutomationElement*) pIUIAutomationElement;
	HRESULT hr = fromCache ? el->get_CachedBoundingRectangle(&value) : el->get_CurrentBoundingRectangle(&value);
	if (FAILED(hr))
		return 0;

	jlong retArr[4];
	retArr[0] = value.left;
	retArr[1] = value.top;
	retArr[2] = value.right;
	retArr[3] = value.bottom;
	jlongArray ret = env->NewLongArray(4);
	env->SetLongArrayRegion(ret, 0, 4, retArr);
	return ret;
}


/* IUIAutomationElementArray_get_Length */
JNI_SIG(jlong, WINAPI_NS(IUIAutomationElementArray_1get_1Length)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElementArray){
    int length;
	IUIAutomationElementArray* ela = (IUIAutomationElementArray*) pIUIAutomationElementArray;
	HRESULT hr = ela->get_Length(&length);
	if (FAILED(hr)){
		throwUIAException(env, errString("IUIAutomationElementArray_get_Length", hr));
		return 0;
	}
	return (jlong) length;
}


/* IUIAutomationElementArray_GetElement */
JNI_SIG(jlong, WINAPI_NS(IUIAutomationElementArray_1GetElement)) (JNIEnv * env, jclass,
		jlong pIUIAutomationElementArray, jint idx){
	IUIAutomationElement* el;
	IUIAutomationElementArray* ela = (IUIAutomationElementArray*) pIUIAutomationElementArray;
	HRESULT hr = ela->GetElement(idx, &el);
	if (FAILED(hr))
		return 0;
	return (jlong) el;
}


/* IUIAutomation_ElementFromPoint */
JNI_SIG(jlong, WINAPI_NS(IUIAutomation_1ElementFromPoint)) (JNIEnv * env, jclass,
		jlong pIUIAutomation, jlong x, jlong y){
	IUIAutomationElement* el;
	IUIAutomation* uia = (IUIAutomation*) pIUIAutomation;
	POINT p;
	p.x = x;
	p.y = y;
	HRESULT hr = uia->ElementFromPoint(p, &el);
	if (FAILED(hr))
		return 0;
	return (jlong) el;
}


/* IUIAutomation_ElementFromHandle */
JNI_SIG(jlong, WINAPI_NS(IUIAutomation_1ElementFromHandle)) (JNIEnv * env, jclass,
		jlong pIUIAutomation, jlong hwnd){
	IUIAutomationElement* el;
	IUIAutomation* uia = (IUIAutomation*) pIUIAutomation;
	HRESULT hr = uia->ElementFromHandle((UIA_HWND)hwnd, &el);
	if (FAILED(hr))
		return 0;
	return (jlong) el;
}

/* IUIAutomation_ElementFromHandleBuildCache */
JNI_SIG(jlong, WINAPI_NS(IUIAutomation_1ElementFromHandleBuildCache)) (JNIEnv * env, jclass,
		jlong pIUIAutomation, jlong hwnd, jlong pCacheRequest){
	IUIAutomationElement* el;
	IUIAutomation* uia = (IUIAutomation*) pIUIAutomation;
	IUIAutomationCacheRequest* cr = (IUIAutomationCacheRequest*) pCacheRequest;
	HRESULT hr = uia->ElementFromHandleBuildCache((UIA_HWND)hwnd, cr, &el);
	if (FAILED(hr))
		return 0;
	return (jlong) el;
}


/* IUIAutomationElement_FindAll */
JNI_SIG(jlong, WINAPI_NS(IUIAutomationElement_1FindAll)) (JNIEnv * env, jclass,
		jlong pElement, jlong treeScope, jlong pCondition){
	IUIAutomationElement* el = (IUIAutomationElement*) pElement;
	IUIAutomationCondition* ac = (IUIAutomationCondition*) pCondition;
	IUIAutomationElementArray* ret;
	HRESULT hr = el->FindAll((TreeScope)treeScope, ac, &ret);
	if (FAILED(hr))
		throwUIAException(env, errString("IUIAutomationElement_FindAll", hr));
	return (jlong) ret;
}


/* IUIAutomationElement_FindAllBuildCache */
JNI_SIG(jlong, WINAPI_NS(IUIAutomationElement_1FindAllBuildCache)) (JNIEnv * env, jclass,
		jlong pElement,	jlong treeScope, jlong pCondition, jlong pCacheRequest){
	IUIAutomationElement* el = (IUIAutomationElement*) pElement;
	IUIAutomationCondition* ac = (IUIAutomationCondition*) pCondition;
	IUIAutomationCacheRequest* cr = (IUIAutomationCacheRequest*) pCacheRequest;
	IUIAutomationElementArray* ret;
	HRESULT hr = el->FindAllBuildCache((TreeScope)treeScope, ac, cr, &ret);
	if (FAILED(hr))
		throwUIAException(env, errString("IUIAutomationElement_FindAllBuildCache", hr));
	return (jlong) ret;
}

/* IUIAutomation_CreateCacheRequest */
JNI_SIG(jlong, WINAPI_NS(IUIAutomation_1CreateCacheRequest)) (JNIEnv * env, jclass,
		jlong pAutomation){
	IUIAutomation* uia = (IUIAutomation*) pAutomation;
	IUIAutomationCacheRequest* ret;
	HRESULT hr = uia->CreateCacheRequest(&ret);
	if (FAILED(hr))
		throwUIAException(env, errString("IUIAutomation_CreateCacheRequest", hr));
	return (jlong) ret;
}


/* IUIAutomation_CreateTrueCondition */
JNI_SIG(jlong, WINAPI_NS(IUIAutomation_1CreateTrueCondition)) (JNIEnv * env, jclass,
		jlong pAutomation){
	IUIAutomation* uia = (IUIAutomation*) pAutomation;
	IUIAutomationCondition* ret;
	HRESULT hr = uia->CreateTrueCondition(&ret);
	if (FAILED(hr))
		throwUIAException(env, errString("IUIAutomation_CreateTrueCondition", hr));
	return (jlong) ret;
}


/* IUIAutomation_CreateAndCondition */
JNI_SIG(jlong, WINAPI_NS(IUIAutomation_1CreateAndCondition)) (JNIEnv * env, jclass,
		jlong pAutomation, jlong pCond1, jlong pCond2){
	IUIAutomation* uia = (IUIAutomation*) pAutomation;
	IUIAutomationCondition* c1 = (IUIAutomationCondition*) pCond1;
	IUIAutomationCondition* c2 = (IUIAutomationCondition*) pCond2;
	IUIAutomationCondition* ret;
	HRESULT hr = uia->CreateAndCondition(c1, c2, &ret);
	if (FAILED(hr))
		throwUIAException(env, errString("IUIAutomation_CreateAndCondition", hr));
	return (jlong) ret;
}

/* IUIAutomation_CompareElements */
JNI_SIG(jboolean, WINAPI_NS(IUIAutomation_1CompareElements)) (JNIEnv * env, jclass,
		jlong pAutomation, jlong pEl1, jlong pEl2){
	IUIAutomation* uia = (IUIAutomation*) pAutomation;
	IUIAutomationElement* el1 = (IUIAutomationElement*) pEl1;
	IUIAutomationElement* el2 = (IUIAutomationElement*) pEl2;
	BOOL ret;
	HRESULT hr = uia->CompareElements(el1, el2, &ret);
	if (FAILED(hr))
		throwUIAException(env, errString("IUIAutomation_1CompareElements", hr));
	return ret;
}


/* IUIAutomation_CreatePropertyCondition */
JNI_SIG(jlong, WINAPI_NS(IUIAutomation_1CreatePropertyCondition__JJLjava_lang_String_2)) (JNIEnv * env, jclass,
		jlong pAutomation, jlong propertyId, jstring value){
	IUIAutomation* uia = (IUIAutomation*) pAutomation;
	IUIAutomationCondition* ret;

	const char *cstrValue = env->GetStringUTFChars(value, NULL);
	BSTR bstrValue = _com_util::ConvertStringToBSTR(cstrValue);

    VARIANT var;
    var.vt = VT_BSTR;
    var.bstrVal = bstrValue;
	HRESULT hr = uia->CreatePropertyCondition(propertyId, var, &ret);
	SysFreeString(bstrValue);
	env->ReleaseStringUTFChars(value, cstrValue);

	if (FAILED(hr))
		throwUIAException(env, errString("IUIAutomation_CreatePropertyCondition", hr));
	return (jlong) ret;
}


/* IUIAutomation_CreatePropertyCondition */
JNI_SIG(jlong, WINAPI_NS(IUIAutomation_1CreatePropertyCondition__JJZ)) (JNIEnv * env, jclass,
		jlong pAutomation, jlong propertyId, jboolean value){
	IUIAutomation* uia = (IUIAutomation*) pAutomation;
	IUIAutomationCondition* ret;

    VARIANT var;
    var.vt = VT_BOOL;
    var.boolVal = value;
	HRESULT hr = uia->CreatePropertyCondition(propertyId, var, &ret);

	if (FAILED(hr))
		throwUIAException(env, errString("IUIAutomation_CreatePropertyCondition", hr));
	return (jlong) ret;
}


/* IUIAutomation_CreatePropertyCondition */
JNI_SIG(jlong, WINAPI_NS(IUIAutomation_1CreatePropertyCondition__JJI)) (JNIEnv * env, jclass,
		jlong pAutomation, jlong propertyId, jint value){
	IUIAutomation* uia = (IUIAutomation*) pAutomation;
	IUIAutomationCondition* ret;

    VARIANT var;
    var.vt = VT_I4;
    var.lVal = value;
	HRESULT hr = uia->CreatePropertyCondition(propertyId, var, &ret);

	if (FAILED(hr))
		throwUIAException(env, errString("IUIAutomation_CreatePropertyCondition", hr));
	return (jlong) ret;
}


/* IUIAutomation_get_ControlViewCondition */
JNI_SIG(jlong, WINAPI_NS(IUIAutomation_1get_1ControlViewCondition)) (JNIEnv * env, jclass,
		jlong pAutomation){
	IUIAutomation* uia = (IUIAutomation*) pAutomation;
	IUIAutomationCondition* ret;
	HRESULT hr = uia->get_ControlViewCondition(&ret);

	if (FAILED(hr))
		throwUIAException(env, errString("IUIAutomation_get_ControlViewCondition", hr));
	return (jlong) ret;
}


/* IUIAutomationCacheRequest_AddProperty */
JNI_SIG(void, WINAPI_NS(IUIAutomationCacheRequest_1AddProperty)) (JNIEnv * env, jclass,
		jlong pRequest, jlong propertyId){
	IUIAutomationCacheRequest* cr = (IUIAutomationCacheRequest*) pRequest;
	HRESULT hr = cr->AddProperty(propertyId);
	if (FAILED(hr))
		throwUIAException(env, errString("IUIAutomationCacheRequest_AddProperty", hr));
}


/* IUIAutomationCacheRequest_AddPattern */
JNI_SIG(void, WINAPI_NS(IUIAutomationCacheRequest_1AddPattern)) (JNIEnv * env, jclass,
		jlong pRequest, jlong patternId){
	IUIAutomationCacheRequest* cr = (IUIAutomationCacheRequest*) pRequest;
	HRESULT hr = cr->AddPattern(patternId);
	if (FAILED(hr))
		throwUIAException(env, errString("IUIAutomationCacheRequest_AddPattern", hr));
}


/* IUIAutomationCacheRequest_put_TreeFilter */
JNI_SIG(void, WINAPI_NS(IUIAutomationCacheRequest_1put_1TreeFilter)) (JNIEnv * env, jclass,
		jlong pRequest, jlong pFilter){
	IUIAutomationCacheRequest* cr = (IUIAutomationCacheRequest*) pRequest;
	IUIAutomationCondition* cond = (IUIAutomationCondition*) pFilter;
	HRESULT hr = cr->put_TreeFilter(cond);
	if (FAILED(hr))
		throwUIAException(env, errString("IUIAutomationCacheRequest_put_TreeFilter", hr));
}

/* IUIAutomationCacheRequest_put_TreeScope */
JNI_SIG(void, WINAPI_NS(IUIAutomationCacheRequest_1put_1TreeScope)) (JNIEnv * env, jclass,
		jlong pRequest, jlong treeScope){
	IUIAutomationCacheRequest* cr = (IUIAutomationCacheRequest*) pRequest;
	HRESULT hr = cr->put_TreeScope((TreeScope)treeScope);
	if (FAILED(hr))
		throwUIAException(env, errString("IUIAutomationCacheRequest_put_TreeScope", hr));
}


/* IUIAutomationCacheRequest_put_AutomationElementMode */
JNI_SIG(void, WINAPI_NS(IUIAutomationCacheRequest_1put_1AutomationElementMode)) (JNIEnv * env, jclass,
		jlong pRequest, jlong mode){
	IUIAutomationCacheRequest* cr = (IUIAutomationCacheRequest*) pRequest;
	HRESULT hr = cr->put_AutomationElementMode((AutomationElementMode)mode);
	if (FAILED(hr))
		throwUIAException(env, errString("IUIAutomationCacheRequest_put_AutomationElementMode", hr));
}


/* IUIAutomationElement_GetCachedChildren */
JNI_SIG(jlong, WINAPI_NS(IUIAutomationElement_1GetCachedChildren)) (JNIEnv * env, jclass,
		jlong pElement){
	IUIAutomationElement* el = (IUIAutomationElement*) pElement;
	IUIAutomationElementArray* ret;
	HRESULT hr = el->GetCachedChildren(&ret);
	if (FAILED(hr))
		return 0;
	return (jlong)ret;
}


/* IUIAutomationElement_GetRuntimeId */
JNI_SIG(jlongArray, WINAPI_NS(IUIAutomationElement_1GetRuntimeId)) (JNIEnv * env, jclass,
		jlong pElement){
	IUIAutomationElement* el = (IUIAutomationElement*) pElement;
	SAFEARRAY* ridArr;
	HRESULT hr = el->GetRuntimeId(&ridArr);
	if (FAILED(hr))
		return 0;

	ULONG elements = ridArr->rgsabound[0].cElements;
	LONG lbound = ridArr->rgsabound[0].lLbound;

	jlong* jRidArr = new jlong[elements];
	for(LONG i = 0; i < elements; i++){
		LONG ridPart;
		ridPart = *((LONG*)ridArr->pvData + (lbound + i));
		jRidArr[i] = ridPart;
	}
	jlongArray ret = env->NewLongArray(elements);
	env->SetLongArrayRegion(ret, 0, elements, jRidArr);
	delete[] jRidArr;
	SafeArrayDestroy(ridArr);
	return ret;
}


// begin by urueda
/* IUIAutomationElement_GetCurrentPropertyValue */
JNI_SIG(jobject, WINAPI_NS(IUIAutomationElement_1GetCurrentPropertyValue)) (JNIEnv * env, jclass,
		jlong pElement, jlong propertyId, jboolean fromCache){

	IUIAutomationElement* el = (IUIAutomationElement*) pElement;

    VARIANT var;
	VariantInit(&var);
	HRESULT hr = fromCache ? el->GetCachedPropertyValueEx(propertyId,TRUE,&var) :
							 el->GetCurrentPropertyValue(propertyId,&var);

	jobject ret = 0;

	if (FAILED(hr))
		return ret;

	jclass cls;
	jmethodID mid;

	switch(var.vt){
	case VT_BOOL:
		cls = env->FindClass("java/lang/Boolean");
		mid = env->GetStaticMethodID(cls, "valueOf", "(Z)Ljava/lang/Boolean;");
		ret = env->CallStaticObjectMethod(cls, mid, var.boolVal);
		break;
	case VT_R8:
	    cls = env->FindClass("java/lang/Double");
	    mid = env->GetStaticMethodID(cls, "valueOf", "(D)Ljava/lang/Double;");
	    ret = env->CallStaticObjectMethod(cls, mid, var.dblVal);
	    break;
	}

	return ret;
}
// end by urueda

/* IUIAutomationElement_GetPropertyValueEx */
JNI_SIG(jobject, WINAPI_NS(IUIAutomationElement_1GetPropertyValueEx)) (JNIEnv * env, jclass,
		jlong pElement, jlong propertyId, jboolean ignoreDefaultValue, jboolean fromCache){

	return 0; // NOT YET IMPLEMENTED!

	IUIAutomationElement* el = (IUIAutomationElement*) pElement;
	VARIANT var;
	VariantInit(&var);
	HRESULT hr = fromCache ? el->GetCachedPropertyValueEx(propertyId, ignoreDefaultValue, &var) : el->GetCurrentPropertyValueEx(propertyId, ignoreDefaultValue, &var);
	if (FAILED(hr))
		return 0;

	jobject ret = 0;
	const char* cstr = 0;
	jmethodID mid;
	jclass cls;


	switch(var.vt){
	case VT_BSTR:
					cstr = _com_util::ConvertBSTRToString(*var.pbstrVal);
					ret = env->NewStringUTF(cstr);
					delete[] cstr;
					break;
	case VT_R8:
	    			cls = env->FindClass("java/lang/Double");
	    			mid = env->GetStaticMethodID(cls, "valueOf", "(D)Ljava/lang/Double;");
	    			ret = env->CallStaticObjectMethod(cls, mid, var.dblVal);
	    			break;
	case VT_UNKNOWN:
					cls = env->FindClass("java/lang/Long");
					mid = env->GetStaticMethodID(cls, "valueOf", "(J)Ljava/lang/Long;");
					ret = env->CallStaticObjectMethod(cls, mid, var.punkVal);
					break;
	case VT_I4:
					cls = env->FindClass("java/lang/Long");
					mid = env->GetStaticMethodID(cls, "valueOf", "(J)Ljava/lang/Long;");
					ret = env->CallStaticObjectMethod(cls, mid, var.lVal);
					break;
	case VT_BOOL:
					cls = env->FindClass("java/lang/Boolean");
					mid = env->GetStaticMethodID(cls, "valueOf", "(Z)Ljava/lang/Boolean;");
					ret = env->CallStaticObjectMethod(cls, mid, var.boolVal);
					break;
	}

	if(var.vt >= VT_ARRAY){
		if(var.vt == VT_ARRAY + VT_R8){    // double array
			ULONG elements = var.parray->rgsabound[0].cElements;
			LONG lbound = var.parray->rgsabound[0].lLbound;
			jdouble* jdArr = new jdouble[elements];

			for(LONG i = 0; i < elements; i++){
				DOUBLE v;
				v = *(((DOUBLE*)var.parray->pvData) + (lbound + i));
				jdArr[i] = v;
			}
			ret = env->NewDoubleArray(elements);
			env->SetDoubleArrayRegion((jdoubleArray)ret, 0, elements, jdArr);
			delete[] jdArr;
		}
	}


	VariantClear(&var);

	return ret;
}

/* IUIAutomationElement_get_AutomationId */
JNI_SIG(jstring, WINAPI_NS(IUIAutomationElement_1get_1AutomationId)) (JNIEnv * env, jclass,
		jlong pElement, jboolean fromCache){
	BSTR value;
	IUIAutomationElement* el = (IUIAutomationElement*) pElement;
	HRESULT hr = fromCache ? el->get_CachedAutomationId(&value) : el->get_CurrentAutomationId(&value);
	if (FAILED(hr))
		return 0;

	jstring ret = env->NewStringUTF(_com_util::ConvertBSTRToString(value));
	SysFreeString(value);
	return ret;
}


/* SafeArrayDestroy */
JNI_SIG(void, WINAPI_NS(SafeArrayDestroy)) (JNIEnv * env, jclass,
		jlong pArray){
	SAFEARRAY* arr = (SAFEARRAY*) pArray;
	HRESULT hr = SafeArrayDestroy(arr);
	if (FAILED(hr))
		throwWinApiException(env, errString("SafeArrayDestroy", hr));
}


/* SafeArrayGetIntElement */
JNI_SIG(jlong, WINAPI_NS(SafeArrayGetIntElement)) (JNIEnv * env, jclass,
		jlong pArray, jlong idx){
	SAFEARRAY* arr = (SAFEARRAY*) pArray;
	LONG arrIdx = idx;
	int el;
	HRESULT hr = SafeArrayGetElement(arr, &arrIdx, &el);
	if (FAILED(hr))
		throwWinApiException(env, errString("SafeArrayGetIntElement", hr));
	return (jlong) el;
}


/* SafeArrayGetUBound */
JNI_SIG(jlong, WINAPI_NS(SafeArrayGetUBound)) (JNIEnv * env, jclass,
		jlong pArray, jlong dim){
	SAFEARRAY* arr = (SAFEARRAY*) pArray;
	LONG ret;
	HRESULT hr = SafeArrayGetUBound(arr, (LONG)dim, &ret);
	if (FAILED(hr))
		throwWinApiException(env, errString("SafeArrayGetUBound", hr));
	return (jlong)ret;
}


/* EnumProcesses */
JNI_SIG(jlongArray, WINAPI_NS(EnumProcesses)) (JNIEnv * env, jclass){
	const int MAX_PROCESSES = 1024;
    DWORD pids[MAX_PROCESSES], bytesReturned;

    if(!EnumProcesses(pids, sizeof(pids), &bytesReturned)){
    	throwWinApiException(env, errString("EnumProcesses", GetLastError()));
        return 0;
    }

    DWORD amount = bytesReturned / sizeof(DWORD);

    if(amount == 0)
    	return env->NewLongArray(0);

    jlong* pidArr = new jlong[amount];
    for(int i = 0; i < amount; i++)
    	pidArr[i] = pids[i];

	jlongArray ret = env->NewLongArray(amount);
	env->SetLongArrayRegion(ret, 0, amount, pidArr);
	delete[] pidArr;
	return ret;
}


/* EnumProcessModules */
JNI_SIG(jlongArray, WINAPI_NS(EnumProcessModules)) (JNIEnv * env, jclass,
		jlong hProcess){
	const int MAX_PROCESS_MODULES = 1024;
    HMODULE hModules[MAX_PROCESS_MODULES];
    DWORD cbNeeded;

    if(!EnumProcessModules((HANDLE)hProcess, hModules, sizeof(hModules), &cbNeeded)){
    	throwWinApiException(env, errString("EnumProcessModules", GetLastError()));
        return 0;
    }

    DWORD amount = cbNeeded / sizeof(DWORD);

    if(amount == 0)
    	return env->NewLongArray(0);

    jlong* hmArr = new jlong[amount];
    for(int i = 0; i < amount; i++)
    	hmArr[i] = (jlong)hModules[i];

	jlongArray ret = env->NewLongArray(amount);
	env->SetLongArrayRegion(ret, 0, amount, hmArr);
	delete[] hmArr;
	return ret;
}


/* GetModuleBaseName */
JNI_SIG(jstring, WINAPI_NS(GetModuleBaseName)) (JNIEnv * env, jclass,
		jlong hProcess, jlong hModule){
    TCHAR pName[MAX_PATH] = TEXT("<unknown>");
	int slength = GetModuleBaseName((HANDLE) hProcess, (HMODULE) hModule, pName, sizeof(pName) / sizeof(TCHAR) );

	if(slength == 0){
    	throwWinApiException(env, errString("GetModuleBaseName", GetLastError()));
    	return 0;
	}

	return env->NewStringUTF(pName);
}


/* IUIAutomationWindowPattern_get_CanMaximize */
JNI_SIG(jboolean, WINAPI_NS(IUIAutomationWindowPattern_1get_1CanMaximize)) (JNIEnv * env, jclass,
		jlong pElement, jboolean fromCache){
	IUIAutomationWindowPattern* pattern = (IUIAutomationWindowPattern*) pElement;
	BOOL ret;
	HRESULT hr = fromCache ? pattern->get_CachedCanMaximize(&ret) : pattern->get_CurrentCanMaximize(&ret);

	if(FAILED(hr))
		return 0;
	return (jboolean) ret;
}


/* IUIAutomationWindowPattern_get_CanMinimize */
JNI_SIG(jboolean, WINAPI_NS(IUIAutomationWindowPattern_1get_1CanMinimize)) (JNIEnv * env, jclass,
		jlong pElement, jboolean fromCache){
	IUIAutomationWindowPattern* pattern = (IUIAutomationWindowPattern*) pElement;
	BOOL ret;
	HRESULT hr = fromCache ? pattern->get_CachedCanMinimize(&ret) : pattern->get_CurrentCanMinimize(&ret);

	if(FAILED(hr))
		return 0;
	return (jboolean) ret;
}


/* IUIAutomationWindowPattern_get_IsModal */
JNI_SIG(jboolean, WINAPI_NS(IUIAutomationWindowPattern_1get_1IsModal)) (JNIEnv * env, jclass,
		jlong pElement, jboolean fromCache){
	IUIAutomationWindowPattern* pattern = (IUIAutomationWindowPattern*) pElement;
	BOOL ret;
	HRESULT hr = fromCache ? pattern->get_CachedIsModal(&ret) : pattern->get_CurrentIsModal(&ret);

	if(FAILED(hr))
		return 0;
	return (jboolean) ret;
}

/* IUIAutomationWindowPattern_get_IsTopmost */
JNI_SIG(jboolean, WINAPI_NS(IUIAutomationWindowPattern_1get_1IsTopmost)) (JNIEnv * env, jclass,
		jlong pElement, jboolean fromCache){
	IUIAutomationWindowPattern* pattern = (IUIAutomationWindowPattern*) pElement;
	BOOL ret;
	HRESULT hr = fromCache ? pattern->get_CachedIsTopmost(&ret) : pattern->get_CurrentIsTopmost(&ret);

	if(FAILED(hr))
		return 0;
	return (jboolean) ret;
}


/* IUIAutomationWindowPattern_get_WindowInteractionState */
JNI_SIG(jlong, WINAPI_NS(IUIAutomationWindowPattern_1get_1WindowInteractionState)) (JNIEnv * env, jclass,
		jlong pElement, jboolean fromCache){
	IUIAutomationWindowPattern* pattern = (IUIAutomationWindowPattern*) pElement;
	WindowInteractionState ret;
	HRESULT hr = fromCache ? pattern->get_CachedWindowInteractionState(&ret) : pattern->get_CurrentWindowInteractionState(&ret);

	if(FAILED(hr))
		return 0;
	return (jlong) ret;
}


/* IUIAutomationWindowPattern_get_WindowVisualState */
JNI_SIG(jlong, WINAPI_NS(IUIAutomationWindowPattern_1get_1WindowVisualState)) (JNIEnv * env, jclass,
		jlong pElement, jboolean fromCache){
	IUIAutomationWindowPattern* pattern = (IUIAutomationWindowPattern*) pElement;
	WindowVisualState ret;
	HRESULT hr = fromCache ? pattern->get_CachedWindowVisualState(&ret) : pattern->get_CurrentWindowVisualState(&ret);

	if(FAILED(hr))
		return 0;
	return (jlong) ret;
}


/* Gdiplus_Graphics_DrawImage */
JNI_SIG(void, WINAPI_NS(Gdiplus_1Graphics_1DrawImage)) (JNIEnv * env, jclass,
		jlong pGraphics, jlong pImage, jlong x, jlong y){
	Gdiplus::Graphics *pg = (Gdiplus::Graphics*) pGraphics;
	Gdiplus::Image* img = (Gdiplus::Image*) pImage;

	Gdiplus::Status status = pg->DrawImage(img, (INT)x, (INT)y);

	if (Gdiplus::Ok != status)
    	throwGDIException(env, errString("Gdiplus_Graphics_DrawImage", status));
}


/* Gdiplus_Graphics_DrawImage */
JNI_SIG(void, WINAPI_NS(Gdiplus_1Graphics_1DrawImage__JJJJJJJJ_3I)) (JNIEnv * env, jclass,
		jlong pGraphics, jlong x, jlong y, jlong width, jlong height, jlong imgWidth,
		jlong imgHeight, jlong pixelFormat, jintArray data){

	jint* cdata = (jint*) env->GetPrimitiveArrayCritical(data, 0);

	if(cdata == NULL)
		throwGDIException(env, "Out of memory!");

	Gdiplus::Bitmap* bmp = new Gdiplus::Bitmap(imgWidth, imgHeight, imgWidth * 4, pixelFormat, (BYTE*)cdata);
	Gdiplus::Graphics* pg = (Gdiplus::Graphics*) pGraphics;

	Gdiplus::Status status = pg->DrawImage(bmp, (Gdiplus::REAL)x, (Gdiplus::REAL)y, (Gdiplus::REAL)width, (Gdiplus::REAL)height);

	env->ReleasePrimitiveArrayCritical(data, cdata, 0);

	if (Gdiplus::Ok != status)
    	throwGDIException(env, errString("Gdiplus_Graphics_DrawImage", status));
}


/* Gdiplus_Bitmap_Destroy */
JNI_SIG(void, WINAPI_NS(Gdiplus_1Bitmap_1Destroy))(JNIEnv *env, jclass, jlong pBitmap){
	delete (Gdiplus::Bitmap*)pBitmap;
}

/**
  * GetProcessMemoryInfo
  * by urueda */
JNI_SIG(jlong, WINAPI_NS(GetProcessMemoryInfo)) (JNIEnv *env, jclass cl, jlong processID){

    HANDLE hProcess;
    PROCESS_MEMORY_COUNTERS pmc;

    hProcess = OpenProcess( PROCESS_QUERY_INFORMATION | PROCESS_VM_READ,
                            FALSE, processID );
	jlong memUse = 0;
    if (NULL == hProcess)
        return memUse;

    if ( GetProcessMemoryInfo( hProcess, &pmc, sizeof(pmc)) )
    {
		memUse = (jlong) pmc.WorkingSetSize;
    }

    CloseHandle( hProcess );

	return memUse;

}

/**
  * GetProcessTimes
  * by urueda */
JNI_SIG(jlongArray, WINAPI_NS(GetProcessTimes)) (JNIEnv *env, jclass cl, jlong processID) {

    static HANDLE self;

	self = OpenProcess( PROCESS_QUERY_INFORMATION | PROCESS_VM_READ,
						FALSE, processID );

	FILETIME ftime, fsys, fuser;
	ULARGE_INTEGER sys, user;

	GetProcessTimes(self, &ftime, &ftime, &fsys, &fuser);
	memcpy(&sys, &fsys, sizeof(FILETIME));
	memcpy(&user, &fuser, sizeof(FILETIME));

	jlong cpu[2];
	cpu[0] = (jlong) user.QuadPart / 10000; // 100ns ticks -> ms
	cpu[1] = (jlong) sys.QuadPart / 10000; // 100ns ticks -> ms

	jlongArray ret = env->NewLongArray(2);
	env->SetLongArrayRegion(ret, 0, 2, cpu);
	return ret;

}


/* IApplicationActivationManager_ActivateApplication */
JNI_SIG(jlong, WINAPI_NS(IApplicationActivationManager_1ActivateApplication)) (JNIEnv * env, jclass,
		jlong pAppActMngr, jstring appUserModelId, jstring arguments, jint options){

	IApplicationActivationManager* aam = (IApplicationActivationManager*) pAppActMngr;

    const wchar_t *id;
    const wchar_t *args;

    const jchar *raw = env->GetStringChars(appUserModelId, 0);
    id = reinterpret_cast<const wchar_t*>(raw);
    env->ReleaseStringChars(appUserModelId, raw);

    raw = env->GetStringChars(arguments, 0);
    args = reinterpret_cast<const wchar_t*>(raw);
    env->ReleaseStringChars(arguments, raw);

	DWORD pid;

    DWORD ops = 2;

	HRESULT hr = aam->ActivateApplication(id, args, (ACTIVATEOPTIONS) options, &pid);


	if (FAILED(hr))
		throwWinApiException(env, errString("IApplicationActivationManager_1ActivateApplication", hr));


	return (jlong) pid;

}


std::wstring Java_To_WStr(JNIEnv *env, jstring string)
{
    std::wstring value;

    const jchar *raw = env->GetStringChars(string, 0);
    jsize len = env->GetStringLength(string);

    value.assign(raw, raw + len);

    env->ReleaseStringChars(string, raw);

    return value;
}

/**
  * InitializeAccessBridge
  * by ferpasri & urueda (copy from Windows 7) */
JNI_SIG(jboolean, WINAPI_NS(InitializeAccessBridge)) (JNIEnv * env, jclass){

	MSG msg;
	BOOL result = initializeAccessBridge();

    if (result != FALSE) {
		
        while (GetMessage(&msg, NULL, 0, 0)) {
            TranslateMessage(&msg);
            DispatchMessage(&msg);
        }
        shutdownAccessBridge();
		
    }

	return result;
	
}

/**
  * GetAccessibleContext
  * by urueda (based on ferpasri) (copy from Windows 7) */
JNI_SIG(jlongArray, WINAPI_NS(GetAccessibleContext)) (JNIEnv * env, jclass, jlong hwnd){
	
	HWND window = (HWND)hwnd;
	jlongArray ret = 0;

	if (IsJavaWindow(window)){

		long vmid;
		AccessibleContext ac;
	
		if (GetAccessibleContextFromHWND(window, &vmid, &ac)) {
				
			jlong vmidAC[2];
			
			vmidAC[0] = (jlong) vmid;
			vmidAC[1] = (jlong) ac;
						
			ret = env->NewLongArray(2); // vmid x ac
			env->SetLongArrayRegion(ret, (jsize)0, (jsize)2, (jlong*)&vmidAC[0]); 
		
		}
	
	}
	
	return ret;
	
}

/**
  * GetHWNDFromAccessibleContext
  * by urueda (copy from Windows 7) */
JNI_SIG(jlong, WINAPI_NS(GetHWNDFromAccessibleContext)) (JNIEnv * env, jclass, jlong vmid, jlong ac){

    HWND window = getHWNDFromAccessibleContext((long) vmid, (long) ac);

	return (jlong) window;
	
}

/**
  * GetAccessibleChildFromContext
  * by urueda (copy from Windows 7) */
JNI_SIG(jlong, WINAPI_NS(GetAccessibleChildFromContext)) (JNIEnv * env, jclass, jlong vmid, jlong ac, jint i){

	AccessibleContext child = GetAccessibleChildFromContext(vmid, ac, (int)i);
		
	return (jlong) child;

}

/**
  * by urueda (copy from Windows 7) */			   
char* wchart2String(JNIEnv * env, wchar_t *value){

	char bf[sizeof(value)/sizeof(wchar_t)];
		
	sprintf(bf, "%ws", value);
	
	return bf;
	
}

/**
  * by urueda (copy from Windows 7) */			   
char* jint2String(JNIEnv * env, jint value){

	char bf[64];
	
	sprintf(bf, "%d", value);
	
	return bf;
	
}

/**
  * GetAccessibleContextProperties
  * by urueda (copy from Windows 7) */
JNI_SIG(jobjectArray, WINAPI_NS(GetAccessibleContextProperties)) (JNIEnv * env, jclass, jlong vmid, jlong ac){
	
	jobjectArray ret = 0;
	
	AccessibleContextInfo info;

	if (GetAccessibleContextInfo((long)vmid, (AccessibleContext)ac, &info)){
		
		const int ACCESSIBLE_PROPERTIES = 9;
		
		ret = env->NewObjectArray(ACCESSIBLE_PROPERTIES, env->FindClass("java/lang/String"), nullptr);
		
		env->SetObjectArrayElement(ret, 0, env->NewStringUTF(wchart2String(env, info.role)));
		env->SetObjectArrayElement(ret, 1, env->NewStringUTF(wchart2String(env, info.name)));
		env->SetObjectArrayElement(ret, 2, env->NewStringUTF(wchart2String(env, info.description)));
		env->SetObjectArrayElement(ret, 3, env->NewStringUTF(jint2String(env, info.x)));
		env->SetObjectArrayElement(ret, 4, env->NewStringUTF(jint2String(env, info.y)));
		env->SetObjectArrayElement(ret, 5, env->NewStringUTF(jint2String(env, info.width)));
		env->SetObjectArrayElement(ret, 6, env->NewStringUTF(jint2String(env, info.height)));
		env->SetObjectArrayElement(ret, 7, env->NewStringUTF(jint2String(env, info.indexInParent)));
		env->SetObjectArrayElement(ret, 8, env->NewStringUTF(jint2String(env, info.childrenCount)));
	
	}
	
	return ret;
	
}

/**
  * GetProcessNameFromHWND
  * by urueda (copy from Windows 7) */
JNI_SIG(jstring, WINAPI_NS(GetProcessNameFromHWND)) (JNIEnv * env, jclass, jlong hwnd){

	HWND window = (HWND) hwnd;
	DWORD  pid;
	HANDLE handle;
	
	TCHAR processName[256];	
	jstring ret = 0;

	GetWindowThreadProcessId(window, &pid);
	handle = OpenProcess(PROCESS_QUERY_LIMITED_INFORMATION, FALSE, pid);

	if (handle) {

		DWORD nameSize = ARRAYSIZE(processName);
		
		if (QueryFullProcessImageName(handle, 0, processName, &nameSize)){

			ret = env->NewStringUTF(processName);
		
		}	
		
		CloseHandle(handle);
	
	}
	
	return ret;

}