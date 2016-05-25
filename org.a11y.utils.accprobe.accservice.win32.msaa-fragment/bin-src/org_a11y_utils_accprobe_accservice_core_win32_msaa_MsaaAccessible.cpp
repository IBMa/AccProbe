#include "org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible.h"

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetWindowHandle
  (JNIEnv *env, jobject jca)
{
    return (int) GetCppObjectRef(env, jca)->GetHwnd();
}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetAddress
  (JNIEnv *env, jobject jca)
{
	return (int) GetCppObjectRef(env, jca)->GetIAccessiblePtr();
}

JNIEXPORT jboolean JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalPutValue
(JNIEnv *env, jobject jca, jint childId, jstring value){
	IAccessiblePtr pAcc = GetCppObjectRef(env, jca)->GetIAccessiblePtr();
	LPCWSTR lpVal = (LPCWSTR) env->GetStringChars(value, NULL);
	CComBSTR valString(lpVal);
	VARIANT varId;
	varId.vt = VT_I4;
	varId.lVal = childId;
	HRESULT hr = pAcc->put_accValue(varId,valString);
	if(hr!=S_OK){
		CAccessible::putErrorCode(_T("put_accValue"),CAccessible::getHRESULTString(hr), env, jca);
	}
	return (hr == S_OK);
}

JNIEXPORT jboolean JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalDoDefaultAction
  (JNIEnv *env, jobject jca, jint val)
{
	IAccessiblePtr pAcc = GetCppObjectRef(env, jca)->GetIAccessiblePtr();
	VARIANT varId;
	varId.vt = VT_I4;
	varId.lVal = val;
	HRESULT hr = pAcc->accDoDefaultAction(varId);
	if(hr!=S_OK){
		CAccessible::putErrorCode(_T("accDoDefaultAction"),CAccessible::getHRESULTString(hr), env, jca);
	}
	return (hr == S_OK);
}

JNIEXPORT jboolean JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalSelect
  (JNIEnv *env, jobject jca, jint flag, jint val)
{
	IAccessiblePtr pAcc = GetCppObjectRef(env, jca)->GetIAccessiblePtr();
	VARIANT varId;
	varId.vt = VT_I4;
	varId.lVal = val;
	HRESULT hr = pAcc->accSelect(flag,varId);
	if(hr!=S_OK){
		CAccessible::putErrorCode(_T("accSelect"),CAccessible::getHRESULTString(hr), env, jca);
	}
	return (hr == S_OK);
}


JNIEXPORT jobject JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalLocation
(JNIEnv *env, jobject jca , jint val){
	RECT rect;							// Result returned here.
    memset(&rect, 0, sizeof(RECT));
	HRESULT hr = NOERROR;
	VARIANT varId;
	varId.vt = VT_I4;
	varId.lVal = val;
	IAccessiblePtr pAcc = GetCppObjectRef(env, jca)->GetIAccessiblePtr();
	long width = 0, height = 0;
	// First try using the Accessible interface.
	if (pAcc != NULL) {

		hr = pAcc->accLocation(
			&rect.left, &rect.top, &width, &height, CComVariant(varId));
	}

	jobject pt = NULL; 
	
	if(hr == S_OK){
		rect.right   = rect.left + width;
		rect.bottom  = rect.top  + height;

		jclass ptCls = env->FindClass("java/awt/Rectangle");
		pt = env->AllocObject(ptCls);

		jfieldID fid = env->GetFieldID (ptCls, "x", "I");
		env->SetIntField (pt, fid, rect.left);
		fid = env->GetFieldID (ptCls, "y", "I");
		env->SetIntField (pt, fid, rect.top);
		fid = env->GetFieldID (ptCls, "width", "I");
		env->SetIntField (pt, fid, rect.right-rect.left);
		fid = env->GetFieldID (ptCls, "height", "I");
		env->SetIntField (pt, fid, rect.bottom-rect.top);
	}else{
		CAccessible::putErrorCode(_T("accLocation"),CAccessible::getHRESULTString(hr), env, jca);
	}
	return pt;		
}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetChildId
  (JNIEnv *env, jobject jca)
{
    return GetCppObjectRef(env, jca)->GetChildID();
}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalInitFromHwnd
  (JNIEnv *env, jclass cls, jint hwnd, jint childID)
{
	if (childID <= CHILDID_SELF) {
		childID = CHILDID_SELF;
	}
	return (int) new CAccessible((HWND) hwnd, childID);
}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalInitFromHtmlElement
  (JNIEnv *env, jclass cls, jint htmlRef)
{
	IUnknown *pUnk = (IUnknown*) htmlRef;
	IServiceProvider *pProv = NULL;
	IAccessible *pAcc = NULL;
	CAccessible *acc = NULL;
	HRESULT res;

	res = pUnk->QueryInterface(IID_IServiceProvider, (void**) &pProv);
	if (SUCCEEDED(res) && pProv) {
		//out << "Got provider %d\n", pProv);
		res = pProv->QueryService(IID_IAccessible, IID_IAccessible, (void**) &pAcc);
		//out << "Got iAcc %d\n", pAcc);
		if (SUCCEEDED(res) && pAcc) {
			//out << "Got iAcc\n" << endl;
			acc = new CAccessible(pAcc);
	}
	}

	return (int) acc;
}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalInitFromPoint
  (JNIEnv *env, jclass cls, jint x, jint y)
{
POINT pt;
pt.x = x;
pt.y = y;
return (int) new CAccessible(pt);
}

JNIEXPORT void JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalDispose
  (JNIEnv *env, jobject jca)
{
    delete GetCppObjectRef(env, jca);
	//delete jvm;
}

JNIEXPORT jstring JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetClassNameFromHwnd
  (JNIEnv *env, jclass cls, jint hwnd)
{
    return CreateString(env, CAccessible::GetClassNameFromHwnd((HWND) hwnd));
}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetAccessibleParent
  (JNIEnv *env, jobject jca)
{
    return (int) GetCppObjectRef(env, jca)->GetParent();
}

JNIEXPORT jintArray JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetAccessibleChildren
(JNIEnv *env, jobject jca){
	long count = 0;
	CAccessible** children = GetCppObjectRef(env, jca)->GetChildren(&count);	
	jintArray accChldrn = NULL; 
	if (count >0 && children!=NULL)
	{
			accChldrn = env->NewIntArray(count);
			jint* elements = new jint[count];
			for (int i=0; i <count; i++){
				elements[i] = (int)children[i];
			}
			
			env->SetIntArrayRegion(accChldrn,0,count,elements);
			
	}
	return accChldrn;

}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetAccessibleChildCount
  (JNIEnv *env, jobject jca)
{
    return (int) GetCppObjectRef(env, jca)->GetChildCount();
}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetAccessibleChild
  (JNIEnv *env, jobject jca, jint childID)
{
	return (int) GetCppObjectRef(env, jca)->GetChild(childID);
}

JNIEXPORT jboolean JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalHasFocus
(JNIEnv *env, jobject jca)
{
	CAccessible* cacc = GetCppObjectRef(env, jca);
	if(cacc==NULL){
		return NULL;
	}
	return cacc->IsFocused();
}

JNIEXPORT jstring JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetAccessibleName
  (JNIEnv *env, jobject jca)
{
	CAccessible* cacc = GetCppObjectRef(env, jca);
	if(cacc==NULL){
		return NULL;
	}
    return CreateString(env, cacc->GetName());
}

JNIEXPORT jstring JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetAccessibleValue
  (JNIEnv *env, jobject jca)
{
	CAccessible* cacc = GetCppObjectRef(env, jca);
	if(cacc==NULL){
		return NULL;
	}
    return CreateString(env, cacc->GetValue());
}

JNIEXPORT jstring JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetAccessibleHelp
  (JNIEnv *env, jobject jca)
{
	CAccessible* cacc = GetCppObjectRef(env, jca);
	if(cacc==NULL){
		return NULL;
	}
    return CreateString(env, cacc->GetHelp());
}

JNIEXPORT jstring JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetAccessibleHelpTopic
  (JNIEnv *env, jobject jca)
{	
	CAccessible* cacc = GetCppObjectRef(env, jca);
	if(cacc==NULL){
		return NULL;
	}
    return CreateString(env, cacc->GetHelpTopic());
}

JNIEXPORT jstring JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetAccessibleKeyboardShortcut
  (JNIEnv *env, jobject jca)
{
	CAccessible* cacc = GetCppObjectRef(env, jca);
	if(cacc==NULL){
		return NULL;
	}
    return CreateString(env, cacc->GetKeyboardShortcut());
}

JNIEXPORT jstring JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetAccessibleAction
  (JNIEnv *env, jobject jca)
{
	CAccessible* cacc = GetCppObjectRef(env, jca);
	if(cacc==NULL){
		return NULL;
	}
    return CreateString(env, cacc->GetDefaultAction());
}

JNIEXPORT jstring JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetAccessibleDescription
  (JNIEnv *env, jobject jca)
{
	CAccessible* cacc = GetCppObjectRef(env, jca);
	if(cacc==NULL){
		return NULL;
	}
    return CreateString(env, cacc->GetDescription());
}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetAccessibleRoleAsInt
  (JNIEnv *env, jobject jca)
{
	CAccessible* cacc = GetCppObjectRef(env, jca);
	if(cacc==NULL){
		return NULL;
	}
    return (int) cacc->GetRole();
}

JNIEXPORT jstring JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetAccessibleRole
  (JNIEnv *env, jobject jca)
{
	CAccessible* cacc = GetCppObjectRef(env, jca);
	if(cacc==NULL){
		return NULL;
	}
    return CreateString(env, cacc->GetRoleAsString());
}

JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetAccessibleState
  (JNIEnv *env, jobject jca)
{	
	CAccessible* cacc = GetCppObjectRef(env, jca);
	if(cacc==NULL){
		return NULL;
	}
    return (int) GetCppObjectRef(env, jca)->GetState();
}

JNIEXPORT jintArray JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetAccessibleSelection
  (JNIEnv *env, jobject jca)
{	
	CAccessible* cacc = GetCppObjectRef(env, jca);
	if(cacc==NULL){
		return NULL;
	}
	CComVariant varChild = cacc->GetSelection();
	CAccessible *accChild = NULL;
	
	int selected[99];
	int index =0;
	IAccessible* m_spAcc = GetCppObjectRef(env, jca)->GetIAccessiblePtr();
	if (varChild.vt == VT_I4) {
			accChild = new CAccessible(m_spAcc, varChild.lVal);
			//selected = new int;
			selected[index++] = (int) accChild;
		}else if (varChild.vt == VT_DISPATCH) {
			// QI the returned dispatch pointer for an IAccessible interface
			CComQIPtr<IAccessible, &IID_IAccessible> spNewNode(varChild.pdispVal);
			if (spNewNode != NULL) 
				accChild = new CAccessible(spNewNode, CHILDID_SELF);
				//	selected = new int;
					selected[index++] = (int) accChild;
		}
		else if (varChild.vt == VT_UNKNOWN) {
			//  Support for multiple selections implemented.
			IUnknown* pUnk = varChild.punkVal;
			IEnumVARIANT* pEnum = NULL;

			HRESULT hr = pUnk->QueryInterface(IID_IEnumVARIANT,  
                        (void**)&pEnum); 
		//	selected = new int;
				if (SUCCEEDED(hr) && pEnum) 
				{ 
					VARIANT v; 
					VariantInit(&v); 
					pEnum->Reset();
					while(hr == S_OK){
						hr = pEnum->Next(1, &v, NULL); 
						if (v.vt == VT_I4) {
							accChild = new CAccessible(m_spAcc, v.lVal);
							selected[index++] = (int) accChild;
						}
					}
				pEnum->Release();
				pEnum = NULL;
					
				}
		}
		jintArray selections = NULL;
		if (index >0)
		{
			selections = env->NewIntArray(selected != NULL ? index : 0);
			jint* elements = new jint[index];
			if (selected != NULL) {
				for (int i=0; i <index; i++){
						elements[i] = selected[i];
				}
				env->SetIntArrayRegion(selections,0,index,elements);
			}
		}
		return selections;
}

JNIEXPORT jobject JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalGetAccessibleLocation
  (JNIEnv *env, jobject jca)
{
	CAccessible *acc = GetCppObjectRef(env, jca);
	jobject pt = NULL;
	if (acc != NULL) {
		RECT rect = acc->GetLocation();
		jclass ptCls = env->FindClass("java/awt/Rectangle");
		pt = env->AllocObject(ptCls);
		jfieldID fid = env->GetFieldID (ptCls, "x", "I");
		env->SetIntField (pt, fid, rect.left);
		fid = env->GetFieldID (ptCls, "y", "I");
		env->SetIntField (pt, fid, rect.top);
		fid = env->GetFieldID (ptCls, "width", "I");
		env->SetIntField (pt, fid, rect.right-rect.left);
		fid = env->GetFieldID (ptCls, "height", "I");
		env->SetIntField (pt, fid, rect.bottom-rect.top);
	}
	return pt;
}



JNIEXPORT jboolean JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalDrawRectangle
(JNIEnv *env, jobject jca, jint x, jint y, jint wt, jint ht){
	//HWND hWnd = GetCppObjectRef(env,jca)->GetHwnd();
	//ShowWindow(hWnd, 1);
	GdiplusStartupInput gdiplusStartupInput;
    ULONG_PTR           gdiplusToken;
	GdiplusStartup(&gdiplusToken, &gdiplusStartupInput, NULL);
    HDC  hdc = GetDC(NULL);
//	 PAINTSTRUCT  ps;

  //  HDC hdc = BeginPaint(hWnd, &ps);

    Graphics graphics(hdc);
	Pen      pen(Color::Red,penWidth);
	//RECT lpRect = GetCppObjectRef(env,jca)->GetLocation();
	RectF rec;
	rec.X= x;
	rec.Y= y;
	rec.Width = wt;
	rec.Height = ht;
	Status st = graphics.DrawRectangle(&pen,rec);
//	EndPaint(hWnd, &ps);
	if ( st== 0){
		return true;
	}
	//ReleaseDC(NULL, hdc);
    return false;
}

JNIEXPORT jboolean JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalEraseRectangle
(JNIEnv *env, jobject jca, jint left, jint top, jint right, jint bottom){
	RECT lpRect;
	lpRect.left = left-penWidth;
	lpRect.top = top-penWidth;	
	lpRect.right = right+penWidth;
	lpRect.bottom = bottom+penWidth;
	HWND dsk = GetDesktopWindow();

	if(RedrawWindow(NULL, &lpRect, NULL, RDW_ERASE|RDW_INVALIDATE|RDW_ALLCHILDREN)){
		UpdateWindow(dsk);
		return true;
	}
    return false;
}

JNIEXPORT jboolean JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_msaa_MsaaAccessible_internalEraseDesktop
(JNIEnv *env, jclass jcls){
	HWND dsk = GetDesktopWindow();

	if(RedrawWindow(NULL, NULL, NULL, RDW_ERASE|RDW_INVALIDATE|RDW_ALLCHILDREN)){
		UpdateWindow(dsk);
		return true;
	}
    return false;
}

CAccessible* GetCppObjectRef (JNIEnv *env, jobject jca)
{
	jclass cls = env->GetObjectClass(jca);
    jmethodID methID = env->GetMethodID(cls, "internalRef", "()I");
	CAccessible* cacc =(CAccessible*) env->CallIntMethod(jca, methID);
	if( cacc!=NULL){
		cacc->setJNI(env,jca);
	}
	return cacc;
}

jstring CreateString(JNIEnv *env, CComBSTR str)
{
	if(str==NULL){
		return 0;
	}
	return  env->NewString( (jchar*)str.m_str, str.Length());
}
