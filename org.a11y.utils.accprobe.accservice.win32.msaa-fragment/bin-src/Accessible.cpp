/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.
*
* Contributors:
* IBM Corporation
*******************************************************************************/ 
#include "Accessible.h"

void CAccessible::putErrorCode(CComBSTR key,CComBSTR val, JNIEnv* jenv, jobject obj){

	if(key!=NULL && val!=NULL){
		jstring jkey =jenv->NewString( (jchar*)key.m_str, key.Length());
		jstring jval = jenv->NewString( (jchar*)val.m_str, val.Length());
		jclass cls = jenv->GetObjectClass(obj);
        jmethodID methID = jenv->GetMethodID(cls, "putErrorCode", "(Ljava/lang/String;Ljava/lang/String;)V");
		jenv->CallObjectMethod(obj, methID, jkey, jval);
	}
}
//////////////////////////////////////////////////////////////////////
// CTOR's, DTOR's, initialization
//////////////////////////////////////////////////////////////////////

CAccessible::CAccessible() :
	m_spAccessible (NULL),
	m_lChildID (CHILDID_SELF),
	m_hwnd (NULL)
{
	Init();
}

CAccessible::CAccessible(const CAccessible &acc)
{
    this->m_hwnd = acc.m_hwnd;
	this->m_lChildID = acc.m_lChildID;
	this->m_spAccessible = acc.m_spAccessible;
	this->env = acc.env;
	this->jobj = acc.jobj;
}

CAccessible::CAccessible(IAccessible* pAccessible) :
	m_lChildID (CHILDID_SELF),
	m_hwnd (NULL)
{
	m_spAccessible = pAccessible;
	Init();
}

CAccessible::CAccessible(IAccessible* pAccessible, long lChildID) :
	m_hwnd (NULL)
{
	m_spAccessible = pAccessible;
	m_lChildID = lChildID;
	Init();
}

CAccessible::CAccessible(HWND hwnd) :
	m_spAccessible (NULL),
	m_lChildID (CHILDID_SELF)
{
	m_hwnd = hwnd;
	Init();
}

CAccessible::CAccessible(HWND hwnd, long lChildID) :
	m_spAccessible (NULL)
{
	m_hwnd = hwnd;
	m_lChildID = lChildID;
	Init();
	//printf("in CAccessible(hwnd=%d)\n", (int)hwnd);
}

CAccessible::CAccessible(const POINT& rPtScreen) :
	m_spAccessible (NULL),
	m_lChildID (CHILDID_SELF),
	m_hwnd (NULL)
{
	CoInitializeEx(NULL,COINIT_MULTITHREADED);
	CComVariant varChild;
	HRESULT hr = 
		AccessibleObjectFromPoint(rPtScreen, &m_spAccessible, &varChild);
if (SUCCEEDED(hr) || (m_spAccessible != NULL) || (varChild.vt == VT_I4)) {
		//printf("succeeded: handle=%d, mPAcc=%d, varChild=%d, VT_I4=%d\n", m_hwnd, m_spAccessible, varChild.vt, VT_I4);
		m_lChildID = varChild.lVal;
		Init();
	}
	else
		// Failure strategy:  NULL object.
		//printf("failed\n");
		m_spAccessible = NULL;
}

void CAccessible::Init()
{
	this->setJNI(NULL, NULL);
	CoInitializeEx(NULL,COINIT_MULTITHREADED);
	HRESULT hr;
	//printf("Init()\n");
	if ((m_hwnd != NULL) && (m_spAccessible == NULL)) {
		// Have a window handle but no IAccessible.
		// Try to get an accessible first from client, then from window.
		hr = AccessibleObjectFromWindow(
			m_hwnd, OBJID_CLIENT, IID_IAccessible, (void**) &m_spAccessible);
		if (FAILED(hr) || (m_spAccessible == NULL)) {
			hr = AccessibleObjectFromWindow(
				m_hwnd, OBJID_WINDOW, IID_IAccessible, (void**) &m_spAccessible);
			if (FAILED(hr))
				m_spAccessible = NULL;
		}
	}
	else if ((m_hwnd == NULL) && (m_spAccessible != NULL)) {
		// Have an IAccessible but no window handle.
		hr  = WindowFromAccessibleObject(m_spAccessible, &m_hwnd);
		if (FAILED(hr))	{
			RECT rec = GetLocation();
			m_hwnd = NULL;
		}
	}
}

CAccessible::~CAccessible()
{
	// Smart pointer automatically handles any Release of IAccessible.
	CoUninitialize();
}


//////////////////////////////////////////////////////////////////////
// Operators
//////////////////////////////////////////////////////////////////////

bool CAccessible::IsEqualRectangles(const RECT& rc1, const RECT& rc2)
{
	return 
		rc1.left==rc2.left && rc1.right==rc2.right &&
		rc1.top==rc2.top && rc1.bottom==rc2.bottom;
}

//------------------------------------------------------------------//

bool CAccessible::operator <(const CAccessible& rhs) const
{
	bool bIsLessThan;
	RECT thisRect( GetLocation() );
	RECT rhsRect( rhs.GetLocation() );

	if (thisRect.bottom < rhsRect.bottom)
		bIsLessThan = true;
	else if (thisRect.bottom > rhsRect.bottom)
		bIsLessThan = false;
	else if (thisRect.left < rhsRect.left)
		bIsLessThan = true;
	else
		bIsLessThan = false;
	return bIsLessThan;
}

//------------------------------------------------------------------//

bool CAccessible::operator==(const CAccessible& rhs) const
{
    bool bSame = false;
	int nrAccessibleInterfaces = 0;

	// Figure out if we have 0, 1, or 2 good IAccessible pointers.
	if (m_spAccessible != NULL)
		++nrAccessibleInterfaces;
	if (rhs.m_spAccessible != NULL)
		++nrAccessibleInterfaces;

	if ((nrAccessibleInterfaces == 2) &&
		(m_spAccessible.GetInterfacePtr() == rhs.m_spAccessible.GetInterfacePtr()) &&
		(m_lChildID == rhs.m_lChildID))
		// Identical, non-NULL interface pointers.
		bSame = true;
	else if (nrAccessibleInterfaces == 2)
	{
		// Two good pointers, different values.  If same IUnknown, we'll assume
		// the objects match (this is general, documented COM technology practice).
		HRESULT hr1, hr2;
		IUnknown *pUnk1 = NULL;
		IUnknown *pUnk2 = NULL;

		hr1 = m_spAccessible->QueryInterface(IID_IUnknown, (void**) &pUnk1);
		hr2 = rhs.m_spAccessible->QueryInterface(IID_IUnknown, (void**) &pUnk2);

		if (SUCCEEDED(hr1) && SUCCEEDED(hr2))
			bSame = ((pUnk1 == pUnk2) && (m_lChildID == rhs.m_lChildID));
		if (SUCCEEDED(hr1)) pUnk1->Release();
		if (SUCCEEDED(hr2)) pUnk2->Release();

		if (! bSame) {
			// IUnknown's don't match.  However, if HWND's and other prop's line up,
			// we'll interpret it as two separate proxies for same UI object.
			bSame = (m_hwnd	== rhs.m_hwnd);
			bSame = bSame && (m_lChildID == rhs.m_lChildID);
			bSame = bSame && (GetName() == rhs.GetName());
			bSame = bSame && (GetRole() == rhs.GetRole());
			bSame = bSame && (GetState() == rhs.GetState());
			bSame = bSame && (GetValue() == rhs.GetValue());
			bSame = bSame && (GetChildCount() == rhs.GetChildCount());
			if (bSame) {
			    RECT rc1(GetLocation());
				RECT rc2(rhs.GetLocation());
				bSame = IsEqualRectangles(rc1, rc2);
			}
		}
	}

	else if (nrAccessibleInterfaces == 0)
		// No good Accessible interfaces.  Make decision on HWND alone.
		bSame = ((m_hwnd == rhs.m_hwnd) && (m_lChildID == rhs.m_lChildID));

	else
		bSame = false;
	
    return bSame;
}


CAccessible& CAccessible::operator =(const CAccessible& acc)
{

	this->m_hwnd = acc.m_hwnd;
	this->m_lChildID = acc.m_lChildID;
	this->m_spAccessible = acc.m_spAccessible;

	return *this;
}

//////////////////////////////////////////////////////////////////////
// Methods
//////////////////////////////////////////////////////////////////////


void CAccessible::DoDefaultAction() const
{
	HRESULT hr;
	CComVariant varChild(m_lChildID);
	if (m_spAccessible != NULL)
		// Try invoke action;  but no exception thrown if err return.
		hr = m_spAccessible->accDoDefaultAction(varChild);
}

CAccessible* CAccessible::GetParent() const
{
	CAccessible *accParent = NULL;

	if (m_lChildID != CHILDID_SELF) {
		// Back up from simple element to CHILDID_SELF of full object.
		accParent = new CAccessible(m_spAccessible, CHILDID_SELF);
	}
	else {
		// Must walk MSAA tree.
		IDispatchPtr spdispParent = NULL;
		HRESULT hr = m_spAccessible->get_accParent(&spdispParent);
		if (SUCCEEDED(hr) && (spdispParent != NULL)) {
			IAccessiblePtr spAcc = spdispParent;
			accParent = new CAccessible(spAcc);
		}
	}

	return  accParent;
}


long CAccessible::GetChildCount() const
{
    long count = 0;
	if (m_lChildID == CHILDID_SELF) {
        m_spAccessible->get_accChildCount(&count);
    }
    return count;
}
CAccessible** CAccessible::GetChildren(long* cCount)
{
	CAccessible **children = NULL;
    if (m_lChildID == CHILDID_SELF)  {
        long count = GetChildCount();
        long obtained = 0;
		if( count > MAX_children){
			return NULL;
		}
        CComVariant *childVars = new CComVariant[count];
		
        HRESULT hResult = AccessibleChildren(m_spAccessible, 0, count, childVars, &obtained);
		if(hResult!=S_OK){
			putErrorCode(_T("accessibleChildren"),getHRESULTString(hResult), env, jobj);
		}
		*cCount = obtained;
        if (hResult == S_OK) {
			children = new CAccessible*[obtained];
			for(int index=0; index< obtained; index++){
				CComVariant childVar = childVars[index];
				if (childVar.vt == VT_I4) {
					children[index] = new CAccessible(m_spAccessible, childVar.lVal);
				} else if (childVar.vt == VT_DISPATCH && childVar.pdispVal != NULL){
					IDispatch *pChild = childVar.pdispVal;
					IAccessible *accChild = NULL;
					hResult = pChild->QueryInterface(IID_IAccessible, (void**) &accChild);
					if (hResult == S_OK) {
						children[index] = new CAccessible(accChild);
					}
					pChild->Release();
				}
			}
		}
		delete[] childVars;
	}else{
			putErrorCode(_T("accessibleChildren"),_T("No Children- simple element"), env, jobj);
	}
    return children;
}
CAccessible* CAccessible::GetChild(long index)
{
	CAccessible *child = NULL;
    if (m_lChildID == CHILDID_SELF)  {
        long count = GetChildCount();
        long obtained = 0;
        CComVariant *childVars = new CComVariant[count];
        HRESULT hResult = AccessibleChildren(m_spAccessible, 0, count, childVars, &obtained);
		if(hResult!=S_OK){
			putErrorCode(_T("accChild"),getHRESULTString(hResult), env, jobj);
		}
        if (index < obtained && hResult == S_OK) {
			CComVariant childVar = childVars[index];
            if (childVar.vt == VT_I4) {
				child = new CAccessible(m_spAccessible, childVar.lVal);
            } else if (childVar.vt == VT_DISPATCH && childVar.pdispVal != NULL){
				IDispatch *pChild = childVar.pdispVal;
                IAccessible *accChild = NULL;
                hResult = pChild->QueryInterface(IID_IAccessible, (void**) &accChild);
                if (hResult == S_OK) {
					child = new CAccessible(accChild);
                }
				pChild->Release();
			}
		}

		delete[] childVars;
	}
    return child;
}

CComBSTR CAccessible::GetDefaultAction() const
{
	CComVariant varID(m_lChildID);
	
	CComBSTR bstrResult = NULL;
	HRESULT hr = m_spAccessible->get_accDefaultAction(varID, &bstrResult);
	if(hr!=S_OK){
		putErrorCode(_T("accessibleAction"),getHRESULTString(hr), env, jobj);
	}
	return bstrResult;
}

CComBSTR CAccessible::GetDescription() const
{
	CComVariant varID(m_lChildID);
	
	CComBSTR bstrResult;
	HRESULT hr = m_spAccessible->get_accDescription(varID, &bstrResult);
	if(hr!=S_OK){
		putErrorCode(_T("accessibleDescription"),getHRESULTString(hr), env, jobj);
	}
	return bstrResult;
}

CComVariant CAccessible::GetFocus() const
{
	CComVariant varRet;
	HRESULT hr = m_spAccessible->get_accFocus(&varRet);
	if(hr!=S_OK){
		putErrorCode(_T("hasFocus"),getHRESULTString(hr), env, jobj);
	}
	return varRet;
}

CComBSTR CAccessible::GetHelp() const
{
	CComVariant varID(m_lChildID);

	CComBSTR bstrResult;
	HRESULT hr = m_spAccessible->get_accHelp(varID, &bstrResult);
	if(hr!=S_OK){
		putErrorCode(_T("accessibleHelp"),getHRESULTString(hr), env, jobj);
	}
	return bstrResult;
}

CComBSTR CAccessible::GetHelpTopic() const
{
	CComVariant varID(m_lChildID);

	CComBSTR bstrResult;

	long pidTopic =0;
	HRESULT hr = m_spAccessible->get_accHelpTopic( &bstrResult,varID, &pidTopic);
	if(hr!=S_OK){
		putErrorCode(_T("accessibleHelpTopic"),getHRESULTString(hr), env, jobj);
	}
	return bstrResult;
}

CComBSTR CAccessible::GetHelpTopic(long *pdwIDTopic) const
{
	CComVariant varID(m_lChildID);

	CComBSTR bstrResult;
	HRESULT hr = m_spAccessible->get_accHelpTopic(&bstrResult, varID, pdwIDTopic );
	if(hr!=S_OK){
		putErrorCode(_T("accessibleHelpTopic"),getHRESULTString(hr), env, jobj);
	}
	return bstrResult;
}

CComBSTR CAccessible::GetKeyboardShortcut() const
{
	CComVariant varID(m_lChildID);
	
	CComBSTR bstrResult;
	HRESULT hr = m_spAccessible->get_accKeyboardShortcut(varID, &bstrResult);

	if ((hr == S_OK) && (bstrResult != NULL))
	{
		bstrResult.ToUpper();
	}else{
		
		putErrorCode(_T("accessibleKeyboardShortcut"),getHRESULTString(hr), env, jobj);
	
	}

	// uppercase it so a will sound like a letter (long a) rather than a word (short a).
	return bstrResult;	
}

CComBSTR CAccessible::GetName() const
{
	CComVariant varID(m_lChildID);

	CComBSTR bstrResult;
	HRESULT hr = m_spAccessible->get_accName(varID, &bstrResult);
	if(hr!=S_OK){
		putErrorCode(_T("accessibleName"),getHRESULTString(hr), env, jobj);
	}
	return bstrResult;

}

long CAccessible::GetRole() const
{
	CComVariant varID(m_lChildID);
	CComVariant varRole;
	long accRole = 0xDEADBEEF;		// Assuming DEADBEEF is invalid role value.

	HRESULT hr = m_spAccessible->get_accRole(varID, &varRole);
	if (SUCCEEDED(hr)) {
		if (varRole.vt == VT_I4)
			accRole = varRole.lVal;
		// PDF (& perhaps other vendors) have created custom roles, returned as BSTR's.
		// Currently we recognize only some custom PDF roles.
		else if (varRole.vt == VT_BSTR) {
			CComBSTR ssRole;
			ssRole = varRole.bstrVal;
			if (ssRole == _T("Page"))
				accRole = ROLE_VENDOR_PDF_PAGE;
			else if (ssRole == _T("Signature"))
				accRole = ROLE_VENDOR_PDF_DIGITAL_SIG;
			else {
				#ifdef _DEBUG
					CComBSTR ssErrMsg;
					ssErrMsg =  SysAllocString(L"CAccessible.GetRole() finds unexpected string role value of ");
					ssErrMsg.Append(ssRole);
					//::MessageBox(NULL, (LPCWSTR)ssErrMsg.m_str, _T("MDR Exception"), MB_OK);
				#endif
				//throw CMdrException(__FILE__,__LINE__);
			}
		}
	}
	if(hr!=S_OK){
		putErrorCode(_T("accessibleMsaaRole"),getHRESULTString(hr), env, jobj);
	}
	return accRole;
}

CComBSTR CAccessible::GetRoleAsString() const{
	CComVariant varID(m_lChildID);
	CComVariant varRole;
	HRESULT hr = m_spAccessible->get_accRole(varID, &varRole);
	if (SUCCEEDED(hr)) {
		if(varRole.vt == VT_BSTR){
			return varRole.bstrVal ;
		}
	}
	if(hr!=S_OK){
		putErrorCode(_T("accessibleMsaaRole"),getHRESULTString(hr), env, jobj);
	} 
	return 0;
}

CComVariant CAccessible::GetSelection() const
{
	CComVariant varChild;
	//CAccessible *accChild = NULL;
	
	HRESULT hr = m_spAccessible->get_accSelection(&varChild);
	if(hr!=S_OK){
		putErrorCode(_T("accessibleSelection"),getHRESULTString(hr), env, jobj);
	}
	return varChild;
}

long CAccessible::GetState() const
{
	CComVariant varID(m_lChildID);
	CComVariant varState;

	HRESULT hr = m_spAccessible->get_accState(varID,   &varState);
	if(hr!=S_OK){
		putErrorCode(_T("accessibleState"),getHRESULTString(hr), env, jobj);
	}
	return varState.lVal;

}

bool CAccessible::IsValueAvailable() const
{
	CComVariant varID(m_lChildID);
	CComBSTR bstrResult = NULL;
	HRESULT hr = m_spAccessible->get_accValue(varID, &bstrResult);
	return SUCCEEDED(hr) && (bstrResult != NULL);
}

CComBSTR CAccessible::GetValue() const
{
	CComVariant varID(m_lChildID);
	TCHAR bRes[256];
	CComBSTR bstrResult;
	HRESULT hr = m_spAccessible->get_accValue(varID, &bstrResult);

	if(hr == S_OK && bstrResult){
		return bstrResult;
	}else{
		if( varID.vt == VT_BSTR){
			lstrcpy(bstrResult,(LPCTSTR)varID.bstrVal);
		}else if( varID.vt == VT_I4){
			long r1;
			r1 = varID.lVal;
			_ltow_s(r1, bRes,10);
			bstrResult = CComBSTR(bRes);
		}
	}
	if(hr!=S_OK){
		putErrorCode(_T("accessibleValue"),getHRESULTString(hr), env, jobj);
	}
	return bstrResult;
}

CComBSTR CAccessible::GetRoleText() const
{
	return CAccessible::GetRoleText( GetRole() );
}

CComBSTR CAccessible::GetRoleText(long accRole)
{
	// ### BUG: No accomodation for vendor defined roles (non-Microsoft roles)
	// ### such as PDF's "Page" role (returned from IAccessible::get_accRole as string, not int).
	TCHAR ch[MAX_strlen];
	::GetRoleText(accRole, ch, MAX_strlen);
	CComBSTR str = ch;

	return str;
}

//------------------------------------------------------------------//

CComBSTR CAccessible::GetWndClassName() const
{
	return GetClassNameFromHwnd(m_hwnd);
}

//------------------------------------------------------------------//

CComBSTR CAccessible::GetWndText(HWND hwnd)
{
	int nChars = 0;							// Buffer size
	TCHAR* pszText = NULL;					// TCHAR buffer.
	CComBSTR ssText;						// Return value.

	nChars = ::GetWindowTextLength(hwnd);	// Docs unclear if this is bytes or chars
	nChars += 2;							// Add space for terminating char (add 2, just in case DBCS / UNICODE issue).
	if (nChars)
		pszText = new TCHAR[nChars];
	if (pszText) {
		nChars = ::GetWindowText(hwnd, pszText, nChars);
		if (nChars > 0)
			ssText = pszText;
		delete[] pszText;
	}
	return ssText;
}

//------------------------------------------------------------------//

CComBSTR CAccessible::GetClassNameFromHwnd(HWND hwnd)
{
	int n = 0;								// Number of chars in class name, 0 on error.
	TCHAR cName[MAX_strlen];				// Buffer for Win32 call
	CComBSTR ssClassName(_T(""));			// Return value
	_tcsnset(cName, 0, MAX_strlen);
	if (hwnd != NULL)
		n = GetClassName(hwnd, cName, MAX_strlen-1);
	if (n > 0)
		ssClassName = cName;
	return ssClassName;
}

//------------------------------------------------------------------//

bool CAccessible::IsLink() const
{
	return (GetRole() == ROLE_SYSTEM_LINK);
}

//------------------------------------------------------------------//

bool CAccessible::IsControl() const
{
	// Started with HPR 3.02 version of MDR, then added add'l roles.  List items,
	// menu items, not included in "is a control" list.  These are considered
	// sub-components of the container (e.g. List, Menu) which is a control.
    switch(GetRole()) {
	case ROLE_SYSTEM_BUTTONDROPDOWN:	// DR 3.04 CKL add
	case ROLE_SYSTEM_BUTTONDROPDOWNGRID:// DR 3.04 CKL add
	case ROLE_SYSTEM_BUTTONMENU:		// DR 3.04 CKL add
	case ROLE_SYSTEM_CHECKBUTTON:		// DR 3.04 CKL add
	case ROLE_SYSTEM_COMBOBOX:
	case ROLE_SYSTEM_DIAL:				// DR 3.04 CKL add
	case ROLE_SYSTEM_DROPLIST:			// DR 3.04 CKL add
	case ROLE_SYSTEM_HOTKEYFIELD:		// DR 3.04 CKL add
	case ROLE_SYSTEM_LINK:
    case ROLE_SYSTEM_LIST:				// Also applies to PDF listbox object.
	case ROLE_SYSTEM_LISTITEM:			// PTB - For Flash Reader
	case ROLE_SYSTEM_MENUBAR:			// DR 3.04 CKL add
	case ROLE_SYSTEM_MENUPOPUP:			// DR 3.04 CKL add
    case ROLE_SYSTEM_OUTLINE:
	case ROLE_SYSTEM_PAGETAB:			// DR 3.04 kip add
	case ROLE_SYSTEM_PUSHBUTTON:		// DR 3.04 CKL add
	case ROLE_SYSTEM_RADIOBUTTON:		// DR 3.04 CKL add
	case ROLE_SYSTEM_SLIDER:
	case ROLE_SYSTEM_SPINBUTTON:
		return true;

	case ROLE_SYSTEM_TEXT:				// DR 3.04 CKL add
		if ((GetState() & STATE_SYSTEM_READONLY) == 0)
			return true;
		else
			return false;  // Read only text fields are not controls

	default:
		return false;
    }
}

bool CAccessible::IsFocused() const
{
	bool bRet = false;
	if (m_spAccessible) {
		if (GetState() & STATE_SYSTEM_FOCUSED)
			bRet = true;
	} else {
		HWND hwnd = ::GetFocus();
		if (hwnd == m_hwnd)
			bRet = true;
	}
	return bRet;	
}

//------------------------------------------------------------------//

bool CAccessible::IsInsideOf(CAccessible& rAcc2) const
{
	RECT rec1 = GetLocation();
	RECT rec2 = rAcc2.GetLocation();
	return	rec2.top  <= rec1.top  && rec1.bottom <= rec2.bottom &&
			rec2.left <= rec1.left && rec1.right  <= rec2.right;

}

//------------------------------------------------------------------//

bool CAccessible::IsVisible() const
{
	long state = GetState();
	long role = GetRole();
	bool bResult = false;

    if (state & STATE_SYSTEM_INVISIBLE)
        bResult = false;
    else if (state & STATE_SYSTEM_OFFSCREEN)
        bResult = false;
	else if ((role != ROLE_SYSTEM_SCROLLBAR) && (role != ROLE_SYSTEM_INDICATOR))
        bResult = true;
	else
        bResult = false;

	return bResult;

	// A code snippet from the Crunchy work, commented out by that vendor:
	// "There is a small read-only edit whose value is empty string in "Organize Favorites" dialog.
    //  So we need "4 < rec1.right - rec1.left && rec1.bottom - rec1.top != 0"
    // return (4 < (AccRect.right - AccRect.left)) && ((AccRect.bottom - AccRect.top) != 0);
}

//------------------------------------------------------------------//

//------------------------------------------------------------------//

RECT CAccessible::GetLocation() const
{
	RECT rect;							// Result returned here.
    memset(&rect, 0, sizeof(RECT));
	HRESULT hr = NOERROR;

	// First try using the Accessible interface.
	if (m_spAccessible != NULL) {
		long width = 0, height = 0;
		hr = m_spAccessible->accLocation(
			&rect.left, &rect.top, &width, &height, CComVariant(m_lChildID));
		if (SUCCEEDED(hr)) {
			rect.right   = rect.left + width;
			rect.bottom  = rect.top  + height;
		}
	}
	else
		hr = E_FAIL;

	// If IAccessible didn't work out, try using the window handle if we've got one.
	if (FAILED(hr) && (m_hwnd != NULL)) {
		// ### This section not unit tested.
		/*
		WINDOWINFO wi;
		memset(&wi, 0, sizeof(WINDOWINFO));
		wi.cbSize = sizeof(WINDOWINFO);
		BOOL bRC = ::GetWindowInfo(m_hwnd, &wi);
		if (bRC) {
			rect = wi.rcWindow;
			hr = NOERROR;
		}
		else
			hr = E_FAIL;
	}

	if (FAILED(hr))
	    memset(&rect, -1, sizeof(RECT));
		*/
	}
		return rect;		
}

LPACCDATA CAccessible::BuildData(IAccessible* pAcc, VARIANT varChild, DWORD evnt, 
								 HWND hwnd, LONG idObject, LONG idChild, DWORD dwEventThread, DWORD dwmsEventTime){
	LPACCDATA accData = new ACCDATA;
	//name
	    CComBSTR bstrName;
        pAcc->get_accName(varChild, &bstrName);
		if(bstrName == NULL){
			bstrName = CComBSTR("<None>");
		}
		//Role
		VARIANT var;
		pAcc->get_accRole(varChild, &var);
		TCHAR bRole[256];
		_ltow_s(var.lVal, bRole,10);
		if(var.vt == VT_BSTR){
			lstrcpy(bRole,(LPCTSTR)var.bstrVal);
		}

		//State
		pAcc->get_accState(varChild, &var);
		long bState = var.lVal;
		TCHAR st[256];
		_ltow_s(bState,st,10);

		//miscData
		 CComBSTR bstrData =  CreateMiscData(hwnd, idObject,idChild,dwEventThread);
		
		 // direct conversion from BSTR to LPCTSTR only works in Unicode
		lstrcpy(accData->name,(LPCTSTR)bstrName);
		lstrcpy(accData->role,bRole);
		lstrcpy(accData->state, st);
		lstrcpy(accData->miscData, (LPCTSTR)bstrData);
		accData->evnt = evnt;
		accData->milliseconds = dwmsEventTime;
		return accData;
}



bool CAccessible::writeFileMap(LPACCDATA accData){

	HANDLE hMapFile = OpenFileMapping(
                   FILE_MAP_ALL_ACCESS,   // read/write access
                   FALSE,                 // do not inherit the name
                   fileName); 
	
   if (hMapFile == NULL) 
   { 
	  printf("Could not open file mapping object while writing (%d).\n", 
			 GetLastError());
	  return false;
   } 
   LPACCDATA pBuf = new ACCDATA;
	   
   pBuf = (LPACCDATA) MapViewOfFile(hMapFile, // handle to map object
			   FILE_MAP_ALL_ACCESS,  // read/write permission
			   0,                    
			   0,                    
			   BUF_SIZE);  
   // name of mapping object 
   if (pBuf == NULL) 
   { 
	  printf("Could not map view of file (%d).\n", 
			 GetLastError()); 
	  return false;
   }
   //pBuf = new ACCDATA;
   //pBuf = accData;
	lstrcpy( pBuf->name, accData->name);
	lstrcpy(pBuf->role,accData->role);
	lstrcpy(pBuf->state, accData->state);
	lstrcpy(pBuf->miscData, accData->miscData);
   pBuf->evnt= accData->evnt;
   pBuf->milliseconds= accData->milliseconds;
   //lstrcpy(pBuf,name);
   UnmapViewOfFile(pBuf);
   CloseHandle(hMapFile);
  // printf("writing message :%S \n", pBuf);
   return true;

}

IAccessible2* CAccessible::getIA2FromMsaa(IAccessible *pAcc) {

		IAccessible2 *pAcc2 = NULL;
		IServiceProvider *pProv = NULL;
		HRESULT hr;
		if(pAcc){ 
			hr = pAcc->QueryInterface(IID_IServiceProvider, (void**) &pProv);
		}
	    
		if(SUCCEEDED(hr) && pProv){ 
			hr = pProv->QueryService(IID_IAccessible,IID_IAccessible2,(void**)&pAcc2); 
			if(SUCCEEDED(hr) && pAcc2){
				return pAcc2;
			}
		}
		return pAcc2;
	}

LPACCDATA CAccessible::BuildIa2Data(IAccessible2* pAcc, VARIANT varChild, DWORD evnt, 
								 HWND hwnd, LONG idObject, LONG idChild, DWORD dwEventThread, DWORD dwmsEventTime){
	LPACCDATA accData = new ACCDATA;
	//name
	    CComBSTR bstrName;
        pAcc->get_accName(varChild, &bstrName);
		if(bstrName == NULL){
			bstrName = CComBSTR("<None>");
		}
		//Role
		VARIANT var;
		long rl;
		TCHAR bRole[256];
		pAcc->role(&rl);
		if( rl==IA2_ROLE_UNKNOWN){
			HRESULT hr = pAcc->get_accRole(varChild, &var);
			if (SUCCEEDED(hr)) {
				if( var.vt == VT_I4){
					rl = var.lVal;
					_ltow_s(rl, bRole,10);
				}
				else if(var.vt == VT_BSTR){
					lstrcpy(bRole,(LPCTSTR)var.bstrVal);
				}
			}
		}else{
			//rl = var.lVal;
			_ltow_s(rl, bRole,10);
		}

		//State
		pAcc->get_accState(varChild, &var);
		long bState = var.lVal;
		TCHAR st[256];
		_ltow_s(bState,st,10);

		//miscData
		CComBSTR bstrMiscData = CreateMiscData(hwnd, idObject,idChild, dwEventThread);
		 IAccessibleText* cTxt = NULL;
		 if (pAcc)
		 {
		  HRESULT hr = pAcc->QueryInterface(IID_IAccessibleText, (void **)&cTxt);
		// caret/text events
		  if(SUCCEEDED(hr) && cTxt!=NULL){		
			 long offset= -1;
			 long start= -1;
			 long end=-1;
			 TCHAR caretOffset[256], selStart[256], selEnd[256];
			 IA2TextSegment newText;
			 IA2TextSegment oldText;
			 //caret Offset
			 hr = cTxt->get_caretOffset(&offset);
			 if( SUCCEEDED(hr) && offset>=0){
				 _ltow_s(offset,caretOffset,10);
				 bstrMiscData.Append(_T("Caret Offset="));
				bstrMiscData.Append(caretOffset);
			 }
			 //old Text
			 hr = cTxt->get_oldText(&oldText);
			 if( hr == S_OK){
				 bstrMiscData.Append(_T("; oldText="));
				 bstrMiscData.Append(oldText.text);
			 }
			 //new Text
			 hr = cTxt->get_newText(&newText);
			 if( hr ==S_OK){
				 bstrMiscData.Append("; newText=");
				// CComBSTR newtxt= CComBSTR(newText.text);
				 bstrMiscData.Append(newText.text);
			 }
			 //Selection start and end
			 hr= cTxt->get_selection(0,&start, &end);

			 if( SUCCEEDED(hr) && start>=0 && end >=0){
				 _ltow_s(start,selStart,10);
				 bstrMiscData.Append("; selStart=");
				bstrMiscData.Append(selStart);
				_ltow_s(end,selEnd,10);
				 bstrMiscData.Append("; selEnd=");
				bstrMiscData.Append(selEnd);
			 }
			  
		  }
		}

    // direct conversion from BSTR to LPCTSTR only works in Unicode
		lstrcpy(accData->name,(LPCTSTR)bstrName);
		lstrcpy(accData->role, bRole);
		lstrcpy(accData->state, st);
		lstrcpy(accData->miscData,(LPCTSTR)bstrMiscData);
		accData->evnt = evnt;
		accData->milliseconds = dwmsEventTime;

		return accData;
}

CComBSTR CAccessible::CreateMiscData(HWND hwnd, LONG idObject, LONG idChild, DWORD threadId){
	CComBSTR str= "";
	TCHAR objid[256], childId[256];
	char handle[255]={0};
	sprintf(handle,"%X",(int)hwnd);
	_ltow_s(idObject,objid,10);
	_ltow_s(idChild,childId,10);
	
	TCHAR thId[256];
	_ltow_s(threadId,thId,10);
	str.Append(_T("hwnd="));
	str.Append(handle);
	str.Append("; objectId=");
	str.Append(objid);
	str.Append("; childId=");
	str.Append(childId);
	str.Append("; threadId=");
	str.Append(thId);
	str.Append("; windowClass=");
	CComBSTR cls = GetClassNameFromHwnd(hwnd);
	str.Append(cls);
	str.Append("; ");

	return str;
}

void CAccessible::setJNI(JNIEnv* env,jobject jobj){
 this->env= env;
 this->jobj=jobj;
}


CComBSTR CAccessible::getHRESULTString(HRESULT hr){

	switch(hr){
		case	S_OK: 
			return _T("S_OK");
		case	S_FALSE: 
			return _T("S_FALSE");
		case	E_NOTIMPL : 
			return _T("E_NOTIMPL");
		case	E_FAIL: 
			return _T("E_FAIL");
		case	E_OUTOFMEMORY: 
			return _T("E_OUTOFMEMORY");
		case	E_INVALIDARG: 
			return _T("E_INVALIDARG");
		case	E_NOINTERFACE: 
			return _T("E_NOINTERFACE");
		case	E_POINTER: 
			return _T("E_POINTER");
		case	E_HANDLE: 
			return _T("E_HANDLE");
		case	E_ABORT: 
			return _T("E_ABORT");
		case	E_ACCESSDENIED: 
			return _T("E_ACCESSDENIED");
		case	CO_E_NOTINITIALIZED: 
			return _T("CO_E_NOTINITIALIZED");
		case	CO_E_ALREADYINITIALIZED: 
			return _T("CO_E_ALREADYINITIALIZED");
		case	RPC_E_CANTCALLOUT_ININPUTSYNCCALL: 
			return _T("RPC_E_CANTCALLOUT_ININPUTSYNCCALL");
		case	RPC_E_WRONG_THREAD: 
			return _T("RPC_E_WRONG_THREAD");
		case	RPC_E_THREAD_NOT_INIT: 
			return _T("RPC_E_THREAD_NOT_INIT");
		case DISP_E_MEMBERNOTFOUND:
			return _T("DISP_E_MEMBERNOTFOUND");
		case RPC_S_SERVER_UNAVAILABLE:
			return _T("RPC_S_SERVER_UNAVAILABLE");
		default: 
			LPTSTR errtxt = NULL;

		FormatMessage(
		   FORMAT_MESSAGE_FROM_SYSTEM
		   |FORMAT_MESSAGE_ALLOCATE_BUFFER
		   |FORMAT_MESSAGE_IGNORE_INSERTS,  
		   NULL, 
		   hr,
		   MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
		   (LPTSTR)&errtxt, 
		   0,
		   NULL);
	
			return CComBSTR(errtxt);
			break;
	}


}