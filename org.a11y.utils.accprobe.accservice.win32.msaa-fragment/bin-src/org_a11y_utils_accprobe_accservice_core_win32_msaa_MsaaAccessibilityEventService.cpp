#include "org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessibilityEventService.h"

static JavaVM *jvm = NULL;
static jclass jcls = NULL;
static jmethodID jmethID = NULL;
static jobject jObj = NULL;
static JNIEnv* jenv=NULL;

#define BUF_SIZE 1024

BOOL APIENTRY DllMain( HMODULE hModule,
                       DWORD  ul_reason_for_call,
                       LPVOID lpReserved
					 )
{
	switch (ul_reason_for_call)
	{
		case DLL_PROCESS_ATTACH:
		hInstance = hModule;
		if(EVENT_DATA_READY==0){
		 EVENT_DATA_READY = RegisterWindowMessage(EVENT_DATA_READY_MSG);
		}
		RegisterWindowClass();
	case DLL_THREAD_ATTACH:
	case DLL_THREAD_DETACH:
	case DLL_PROCESS_DETACH:
		break;
	}
    
    return TRUE;
}

void RegisterWindowClass() {
	WNDCLASSEX wclsex;

	wclsex.cbSize = sizeof(WNDCLASSEX); 
	wclsex.style			= CS_HREDRAW | CS_VREDRAW;
	wclsex.cbClsExtra		= 0;
	wclsex.cbWndExtra		= 0;
	wclsex.hIcon			= 0;
	wclsex.hCursor			= 0;
	wclsex.hbrBackground	= (HBRUSH)(COLOR_WINDOW + 1);
	wclsex.hIconSm			= 0;
	wclsex.lpszMenuName		= 0;
	wclsex.lpszClassName	= _T("Event window");
	wclsex.lpfnWndProc		= WndProc;
	wclsex.hInstance		= hInstance;

	RegisterClassEx(&wclsex);
}

LRESULT CALLBACK WndProc(HWND hWnd, UINT Msg, WPARAM wParam, LPARAM lParam) {
	if(Msg == EVENT_DATA_READY) {
		Callback();
	}
	else return DefWindowProc(hWnd, Msg, wParam, lParam);

	return 0;
}

void Callback() {
	if(g_Env == NULL || g_obj == NULL) return;
	jclass cls =  g_Env->GetObjectClass(g_obj);
	jmethodID mid = g_Env->GetMethodID(cls, "winEventIPCallback", "()V");
	if(mid)
		g_Env->CallVoidMethod(g_obj, mid, NULL);
}

void ErrorHandler(LPCSTR pszErrorMessage) {
	printf(pszErrorMessage);
}

unsigned WINAPI CreateWndThread(LPVOID pThreadParam) {
	HANDLE hWnd = CreateWindow(_T("Event window"), NULL, WS_OVERLAPPEDWINDOW,
						CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT,
						NULL, NULL, hInstance, NULL);
	if(hWnd == NULL) {
		printf("Failed to create Event window");
		return 0;
	}

	if(jvm==0){
		return 0;
	}

	jint ret = jvm->AttachCurrentThread((void**)&g_Env, NULL);
		if(ret != 0) ErrorHandler("AttachCurrentThread failed");
	
	MSG Msg;
	while(GetMessage(&Msg, 0, 0, 0)) {
		TranslateMessage(&Msg);
		DispatchMessage(&Msg);
	}
	return Msg.wParam;
} 

VOID CALLBACK fWinEventOutProcess(HWINEVENTHOOK hook, DWORD evnt, HWND hwnd, 
                             LONG idObject, LONG idChild, 
                             DWORD dwEventThread, DWORD dwmsEventTime)
{
		int global =0;
		if(jvm!=NULL)
		{
			jenv->CallStaticVoidMethod(jcls, jmethID, (int)evnt,(int) hwnd, 
			(int) idObject, (int) idChild, (jlong)dwEventThread, (jlong)dwmsEventTime, (int)global);
		}
		
}

VOID CALLBACK fWinEventGlobalOutProcess(HWINEVENTHOOK hook, DWORD evnt, HWND hwnd, 
                             LONG idObject, LONG idChild, 
                             DWORD dwEventThread, DWORD dwmsEventTime)
{
		int global =1;
		if(jvm!=NULL)
		{
			jenv->CallStaticVoidMethod(jcls, jmethID, (int)evnt,(int) hwnd, 
			(int) idObject, (int) idChild,(jlong)dwEventThread,(jlong)dwmsEventTime,(int)global);
		}
		
}


VOID CALLBACK fWinEvent(HWINEVENTHOOK hook, DWORD evnt, HWND hwnd, 
                             LONG idObject, LONG idChild, 
                             DWORD dwEventThread, DWORD dwmsEventTime)
{
	if(EVENT_DATA_READY==0){
		EVENT_DATA_READY = RegisterWindowMessage(EVENT_DATA_READY_MSG);
	}
	IAccessible *pAcc = NULL;
	VARIANT varChild;
	
	HRESULT hr = NULL;

	hr= AccessibleObjectFromEvent(hwnd, idObject, idChild, &pAcc, &varChild);
		
	if (hr==S_OK && pAcc!=NULL)
	{
		IAccessible2* pAcc2 = CAccessible::getIA2FromMsaa(pAcc);
		
			LPACCDATA data;
			if(pAcc2!=  NULL){
				data = CAccessible::BuildIa2Data(pAcc2, varChild, evnt, hwnd, idObject, idChild, dwEventThread, dwmsEventTime);
			}
			else{
				data = CAccessible::BuildData(pAcc, varChild, evnt, hwnd, idObject, idChild, dwEventThread, dwmsEventTime);
			}
			if(CAccessible::writeFileMap(data))
			SendMessage(HWND_BROADCAST, EVENT_DATA_READY, 0, 0);
		
		if(pAcc2!=  NULL){
			pAcc2->Release();
			pAcc2 = NULL;
		}
		pAcc->Release();
		pAcc = NULL;
	}

}



JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessibilityEventService_createAccessibleObjectFromEvent
	(JNIEnv * pEnv, jclass jcls, jint hwnd, 
                             jint idObject, jint idChild ) {
	IAccessible *pAcc = NULL;
	VARIANT varChild;
	HRESULT hr = AccessibleObjectFromEvent((HWND)hwnd, 
		(DWORD)idObject, (DWORD) idChild, &pAcc, &varChild);
		
	if (hr==S_OK && pAcc!=NULL)
	{
		CAccessible* cacc = new CAccessible(pAcc,varChild.lVal);
		return (int)cacc;	
	}
	return 0;
}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessibilityEventService_internalSetWinEventHook
(JNIEnv * env, jobject jobj, jint emin, jint emax, jint pid, jint tid, jint flag){

	if (jvm == NULL) {
		env->GetJavaVM(&jvm);
	}	
	
	if(EVENT_DATA_READY==0){
		EVENT_DATA_READY = RegisterWindowMessage(EVENT_DATA_READY_MSG);
	}

    jcls = env->GetObjectClass(jobj);
	jenv = env;

	HWINEVENTHOOK hook = NULL;
	if (hook == NULL) {
			//jfieldID fid = env->GetStaticFieldID(cls, "INSTALL_UNIVERSAL_WINEVENTHOOK", "Z");
			HMODULE modHandle = NULL;
			if(flag < WINEVENT_INCONTEXT){
				jmethID = env->GetStaticMethodID(jcls, "winEventCallback", "(IIIIJJI)V");	
				modHandle = GetModuleHandle(NULL);
				if(pid==0){
					hook = SetWinEventHook(emin, emax, modHandle ,&fWinEventGlobalOutProcess,pid, tid, flag);
				}else{
					hook = SetWinEventHook(emin, emax, modHandle ,&fWinEventOutProcess,pid, tid, flag);
				}
			}
			else{
				modHandle = (HMODULE)hInstance;
				hook = SetWinEventHook(emin, emax, modHandle ,&fWinEvent,pid, tid, flag);
			}
	}

	return (int)hook;
}

JNIEXPORT jboolean JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessibilityEventService_internalRemoveWinEventHook
(JNIEnv *env, jobject jca, jint hook){
	/*if(jvm!=NULL){
		jvm->DetachCurrentThread();
		jvm = NULL;
		jcls = NULL;
	}
	
	if(g_obj){
		env->DeleteGlobalRef(g_obj);
		g_obj = NULL;
		g_Env = NULL;
		PostThreadMessage(uThreadId, WM_QUIT, 0, 0);
	}*/
	bool ret = UnhookWinEvent( (HWINEVENTHOOK ) hook);
	return ret;
}


JNIEXPORT jlong JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessibilityEventService_internalGetClockTicks
(JNIEnv *env, jclass obj){
	return (jlong) GetTickCount();
}


//Mem Map start
JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessibilityEventService_createFileMapping
	(JNIEnv * pEnv, jclass, jint lProtect, jint dwMaximumSizeHigh, 
	jint dwMaximumSizeLow, jstring name) {
	
	HANDLE hFile = INVALID_HANDLE_VALUE;
	HANDLE hMapFile = NULL;

	LPCWSTR lpName = (LPCWSTR) pEnv->GetStringChars(name, NULL);

	hMapFile = CreateFileMapping(hFile, NULL, lProtect, dwMaximumSizeHigh, 
									 dwMaximumSizeLow, lpName);

	if(hMapFile == NULL) {
		ErrorHandler("Can not create file mapping object");
	}

	if(GetLastError() == ERROR_ALREADY_EXISTS) {
		ErrorHandler("File mapping object already exists");
		CloseHandle(hMapFile);		
	}

	pEnv->ReleaseStringChars(name, (jchar*)lpName);

	// if hMapFile is NULL, just return NULL, or return the handle
	return (jint)hMapFile;
}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessibilityEventService_openFileMapping
	(JNIEnv * pEnv, jclass, jint dwDesiredAccess, 
	jboolean bInheritHandle, jstring name) {
	
	HANDLE hMapFile = NULL;

	LPCWSTR lpName = (LPCWSTR) pEnv->GetStringChars(name, NULL);
	hMapFile = OpenFileMapping(dwDesiredAccess, bInheritHandle, lpName);
	if(hMapFile == NULL) ErrorHandler("Can not open file mapping object");
	pEnv->ReleaseStringChars(name, (jchar*)lpName);

	return (jint)hMapFile;
}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessibilityEventService_mapViewOfFile
	(JNIEnv *, jclass, jint hMapFile, jint dwDesiredAccess, 
	jint dwFileOffsetHigh, jint dwFileOffsetLow, jint dwNumberOfBytesToMap) {
	
	PVOID pView = NULL;
	pView = MapViewOfFile((HANDLE)hMapFile, dwDesiredAccess, 
						  dwFileOffsetHigh, dwFileOffsetLow, dwNumberOfBytesToMap);
	if(pView == NULL) ErrorHandler("Can not map view of file");

	return (jint)pView;
}

JNIEXPORT jboolean JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessibilityEventService_unmapViewOfFile
	(JNIEnv *, jclass, jint lpBaseAddress) {

	BOOL bRet = UnmapViewOfFile((PVOID)lpBaseAddress);
	if(!bRet) ErrorHandler("Can not unmap view of file");

	return bRet;
}

JNIEXPORT jboolean JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessibilityEventService_closeHandle
	(JNIEnv *env, jclass jcls, jint hObject) {

	return CloseHandle((HANDLE)hObject);
}

JNIEXPORT void JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessibilityEventService_writeToMem
	(JNIEnv * pEnv, jclass, jint lpBaseAddress, jstring content) {

	LPCSTR pszContent = pEnv->GetStringUTFChars(content, NULL);
	PVOID pView = (PVOID)lpBaseAddress;
	LPSTR pszCopy = (LPSTR)pView;
	strcpy(pszCopy, pszContent);
	pEnv->ReleaseStringUTFChars(content, pszContent);
}

JNIEXPORT jobjectArray JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessibilityEventService_readFromMem
	(JNIEnv * env, jclass, jint lpBaseAddress) {

	PVOID pView = (PVOID)lpBaseAddress;
	LPACCDATA pszContent = (LPACCDATA)pView;
	jclass stringClass = env->FindClass("java/lang/String");
	//populate array
	//set name
	jobjectArray jarray = env->NewObjectArray(6, stringClass, env->NewString((jchar*)"",0));
	jstring name =  env->NewString( (jchar*)pszContent->name, (jsize)lstrlen(pszContent->name));
	env->SetObjectArrayElement(jarray,0,name);
	//set role
	jstring role =  env->NewString( (jchar*)pszContent->role, (jsize)lstrlen(pszContent->role));
	env->SetObjectArrayElement(jarray,1,role);
	//set state
	jstring state =  env->NewString( (jchar*)pszContent->state, (jsize)lstrlen(pszContent->state));
	env->SetObjectArrayElement(jarray,2,state);
	//set event
	TCHAR stEvnt[256];
	_ltow_s(pszContent->evnt,stEvnt,10);
	jstring evnt =  env->NewString( (jchar*)stEvnt, (jsize)lstrlen(stEvnt));
	env->SetObjectArrayElement(jarray,3,evnt);
	//set time
	TCHAR stTime[256];
	_ltow_s(pszContent->milliseconds,stTime,10);
	jstring time =  env->NewString( (jchar*)stTime, (jsize)lstrlen(stTime));
	env->SetObjectArrayElement(jarray,4,time);
	//set miscData
	jstring data =  env->NewString( (jchar*)pszContent->miscData, (jsize)lstrlen(pszContent->miscData));
	env->SetObjectArrayElement(jarray,5,data);


	return jarray;
}

JNIEXPORT jboolean JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessibilityEventService_initThread
	(JNIEnv * pEnv, jobject jobj) {
	
	if(g_obj){
		pEnv->DeleteGlobalRef(g_obj);
		g_obj = NULL;
		g_Env = NULL;
		PostThreadMessage(uThreadId, WM_QUIT, 0, 0);
	}

	HANDLE hThread;
	hThread = (HANDLE)_beginthreadex(NULL, 0, &CreateWndThread, NULL, 0, &uThreadId);
	if(!hThread) 
	{
		printf("Failed to create thread");
		return false;
	}
	g_obj = pEnv->NewGlobalRef(jobj);
	return true;
}

JNIEXPORT jboolean JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessibilityService_internalCoInitialize
(JNIEnv * env, jclass cls){
	HRESULT hr = CoInitializeEx(NULL,COINIT_APARTMENTTHREADED);
	return(SUCCEEDED(hr));
}

JNIEXPORT void JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessibilityService_internalCoUnInitialize
(JNIEnv * env, jclass cls){
	CoUninitialize();
}