#include "org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaWindowService.h"

static JavaVM *jvm = NULL;
static jclass jcls = NULL;
static jmethodID jmethID = NULL;
static int callBackCount=0;
static HWND handle =0;
static set<int> pids;
static set<int>::iterator it;
static HHOOK hk= 0;
const int MAX_SIZE = 99;
static set<int> winList;

BOOL CALLBACK myChildProc(HWND hwnd, LPARAM lParam){
	if (!IsWindowVisible(hwnd))
        return TRUE;
	if(IsChild(handle,hwnd)){
		DWORD pid1,pid2=0;
		GetWindowThreadProcessId(handle, &pid1);
		GetWindowThreadProcessId(hwnd, &pid2);
		if (pid1!=0 && pid2!=0 && pid1!=pid2 && pids.find(pid2)==pids.end()){
			
			LPTSTR buf1 = (LPTSTR)LocalAlloc(LPTR, 255);
			LPTSTR buf2 = (LPTSTR)LocalAlloc(LPTR, 255);

			int x=256;
			int py = GetClassName(handle,buf1,255);
			int cy  = GetClassName(hwnd,buf2,255);
			if( py!=0 && cy!=0){
				if(lstrcmpW(buf1,buf2)){
					pids.insert(pid2);
					winList.insert((int)hwnd);
					callBackCount++;
				}
			}
		}
	}
	return TRUE;
}

BOOL CALLBACK myEnumFunc(HWND hwnd, LPARAM lParam){
	if (!IsWindowVisible(hwnd))
        return TRUE;

	 handle = hwnd;
	DWORD pid=0;
	GetWindowThreadProcessId(handle, &pid);
	it = pids.find(pid);
	if(it==pids.end()){
		pids.insert(pid);
		winList.insert((int)handle);
	}
	callBackCount++;
	EnumChildWindows(handle, myChildProc, (LPARAM)NULL);
		
	return TRUE;
}

JNIEXPORT jintArray JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaWindowService_internalGetWindowsList
(JNIEnv *env, jclass cls){
	callBackCount =0;
	pids.clear();
	winList.clear();
	jintArray jiarray = NULL;
	int wlist[MAX_SIZE] = {0};
	if ( EnumWindows(myEnumFunc,(LPARAM)NULL)){
		int i=0;
		for( it = winList.begin(); it != winList.end(); it++ ) {
    		wlist[i] = *it;
			i++;
		}

		jiarray = (jintArray) env->NewIntArray((jsize) i);
		env->SetIntArrayRegion(jiarray,0,(jsize)i, (jint *)wlist);
	}
	return jiarray;	
}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaWindowService_internalGetProcessId
(JNIEnv *env, jobject jca , jint hwnd){
			DWORD dwProcessID;
			GetWindowThreadProcessId ( (HWND)hwnd, &dwProcessID);
			//PrintProcessNameAndID( dwProcessID );
			return (int)dwProcessID;  
}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaWindowService_internalGetCurrentProcessId
(JNIEnv *env, jobject jca){
	return (int) GetCurrentProcessId();
}

void PrintProcessNameAndID( DWORD processID )
{
    TCHAR szProcessName[MAX_PATH] = TEXT("<unknown>");

    // Get a handle to the process.

    HANDLE hProcess = OpenProcess( PROCESS_QUERY_INFORMATION |
                                   PROCESS_VM_READ,
                                   FALSE, processID );

    // Get the process name.

    if (NULL != hProcess )
    {
        HMODULE hMod;
        DWORD cbNeeded;

        if ( EnumProcessModules( hProcess, &hMod, sizeof(hMod), 
             &cbNeeded) )
        {
            GetModuleBaseName( hProcess, hMod, szProcessName, 
                               sizeof(szProcessName)/sizeof(TCHAR) );
        }
    }

     CloseHandle( hProcess );
}

LRESULT CALLBACK myWndProc( int nCode, WPARAM wParam, LPARAM lParam){
	//ofstream out("temp.log", ios::app); 
	CWPSTRUCT* pwp = (CWPSTRUCT*)lParam;
	if(nCode <0){
		return CallNextHookEx( hk, nCode, wParam,lParam );
	}

	if (nCode==HC_ACTION)
	{ 
		if (pwp->message==WM_CREATE)
		{
		
		//	out << "in myWndProc; msg=" << pwp->message << "; nCode=" <<nCode
		//	<<";hwnd=" << pwp->hwnd <<endl;

			HWND hwnd = pwp->hwnd;

			DWORD dwProcessID;
			GetWindowThreadProcessId (hwnd, &dwProcessID);

			JNIEnv* jenv;
			int res = jvm->AttachCurrentThread((void **)&jenv, NULL);
			if( res <0)
			{
				return CallNextHookEx( hk, nCode, wParam,lParam );
			}

			jenv->CallStaticVoidMethod(jcls, jmethID, (int)hwnd );

			jvm->DetachCurrentThread();

		}
	}
	return CallNextHookEx( hk, nCode, wParam,lParam );
}

LRESULT CALLBACK myCBTProc( int nCode, WPARAM wParam, LPARAM lParam){
	//ofstream out("temp.log", ios::app); 
	//out << "in mycbtProc nCode=" <<nCode <<endl;

	if(nCode != HCBT_CREATEWND){
		return CallNextHookEx( hk, nCode, wParam,lParam );
	}

	//CBT_CREATEWND* wnd = (CBT_CREATEWND*)lParam;
	
	HWND hwnd = (HWND) wParam;
	
	JNIEnv* jenv;
	int res = jvm->AttachCurrentThread((void **)&jenv, NULL);
	if( res <0)
	{
		return CallNextHookEx( hk, nCode, wParam,lParam );
	}
	jenv->CallStaticVoidMethod(jcls, jmethID, (int)hwnd);

	jvm->DetachCurrentThread();
	
	
	return 0;
}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaWindowService_internalSetWindowsHook
(JNIEnv *env, jobject jca, jint idHook){
//	jcls = cls;
	jclass cls = env->GetObjectClass(jca);
	if (jvm == NULL) env->GetJavaVM(&jvm);

	if (hk == NULL) {
			jfieldID fid = env->GetStaticFieldID(cls, "INSTALL_UNIVERSAL_WINEVENTHOOK", "Z");
			jmethID = env->GetStaticMethodID(cls, "windowCallback", "(I)V");	
			if (idHook == WH_CBT)
				hk =	SetWindowsHookEx( WH_CBT, myCBTProc ,GetModuleHandle(NULL),(DWORD)NULL);
			else
				hk =	SetWindowsHookEx( WH_CALLWNDPROC, myWndProc ,GetModuleHandle(NULL),(DWORD)NULL);
	}
	//out << "returning hk=" << hk <<endl;
	return (int)hk;
}

JNIEXPORT jboolean JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaWindowService_internalRemoveWindowsHook
(JNIEnv *env, jobject jca, jint hook){

	return UnhookWindowsHookEx((HHOOK) hook);

}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaWindowService_internalGetActiveWindow
(JNIEnv *enc, jobject jca){
	return (int) GetActiveWindow();
}

JNIEXPORT void JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaWindowService_internalSetWindowPosition
(JNIEnv *enc, jclass jca, jint hwnd, jint pos){
	 SetWindowPos((HWND)hwnd, 
                   (HWND)pos, 0, 0, 0, 0, SWP_NOSIZE |SWP_NOMOVE|SWP_NOACTIVATE); 

}

JNIEXPORT void JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaWindowService_internalShowWindow
(JNIEnv *enc, jclass jca, jint hwnd){
	 
	ShowWindow((HWND) hwnd, 1);

}


JNIEXPORT void JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaWindowService_setLayeredWindowAttributes
(JNIEnv *env, jclass that, jint hwnd, jint crKey, jchar bAlpha, jint dwFlags)
{
	long ws = GetWindowLongW((HWND)hwnd,GWL_EXSTYLE);
        ws =  ws | WS_EX_LAYERED;
        ws = ws| WS_EX_TRANSPARENT;
    SetWindowLong((HWND)hwnd, GWL_EXSTYLE, ws);
	SetLayeredWindowAttributes((HWND)hwnd, crKey, bAlpha, dwFlags);
}


JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaWindowService_getSystemMetricsWidth
(JNIEnv *env, jclass that)
{
 return GetSystemMetrics(SM_CXSCREEN); 
}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaWindowService_getSystemMetricsHeight
(JNIEnv *env, jclass that)
{
	return GetSystemMetrics(SM_CYSCREEN);
}

JNIEXPORT jobject JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaWindowService_internalGetCurrentCursorLocation
  (JNIEnv *env, jclass jcls)
{
		jobject pt=NULL;
		POINT point; 
		bool ret = GetCursorPos(&point);
		if(ret){
			jclass ptCls = env->FindClass("java/awt/Point");
			pt = env->AllocObject(ptCls);
			jfieldID fid = env->GetFieldID (ptCls, "x", "I");
			env->SetIntField (pt, fid, point.x);
			fid = env->GetFieldID (ptCls, "y", "I");
			env->SetIntField (pt, fid, point.y);
		}
		return pt;
}

JNIEXPORT jstring JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaWindowService_internalGetWindowClass
(JNIEnv *env, jclass cls, jint hwnd){

	CComBSTR str = CAccessible::GetClassNameFromHwnd((HWND)hwnd);
	if(str!=NULL){
		return  env->NewString( (jchar*)str.m_str, str.Length());
	}
	return NULL;
}