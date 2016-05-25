/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.
*
* Contributors:
* Kavitha Teegala
*******************************************************************************/ 

#include "CAccessible2.h"


//////////////////////////////////////////////////////////////////////
// CTOR's, DTOR's, initialization
//////////////////////////////////////////////////////////////////////
BOOL APIENTRY DllMain( HMODULE hModule,
                       DWORD  ul_reason_for_call,
                       LPVOID lpReserved
					 )
{
	switch (ul_reason_for_call)
	{
	case DLL_PROCESS_ATTACH:
	case DLL_THREAD_ATTACH:
	case DLL_THREAD_DETACH:
	case DLL_PROCESS_DETACH:
		break;
	}
    
    return TRUE;
}

void putErrorCode(CComBSTR key,CComBSTR val, JNIEnv* jenv, jobject obj){

	if(key!=NULL && val!=NULL){
		jstring jkey =jenv->NewString( (jchar*)key.m_str, key.Length());
		jstring jval = jenv->NewString( (jchar*)val.m_str, val.Length());
		jclass cls = jenv->GetObjectClass(obj);
        jmethodID methID = jenv->GetMethodID(cls, "putErrorCode", "(Ljava/lang/String;Ljava/lang/String;)V");
		jenv->CallObjectMethod(obj, methID, jkey, jval);
	}
}

CAccessible2::CAccessible2() :
CAccessible(),
m_spAccessible2(NULL)
{
	Init();
}

CAccessible2::CAccessible2(const CAccessible2 &acc)
{
    this->m_hwnd = acc.m_hwnd;
	this->m_lChildID = acc.m_lChildID;
	this->m_spAccessible = acc.m_spAccessible;
	this->m_spAccessible2 = acc.m_spAccessible2;
}

CAccessible2::CAccessible2(IAccessible2* pAccessible):
m_spAccessible2 (pAccessible),
CAccessible( getIAFromIA2(pAccessible))
{
	Init();
}

CAccessible2::CAccessible2(IAccessible2* pAccessible, long lChildID):
m_spAccessible2 (pAccessible),
CAccessible( getIAFromIA2(pAccessible),lChildID)
{
	Init();
}

CAccessible2::CAccessible2(HWND hwnd):
m_spAccessible2 (NULL),
CAccessible(hwnd)
{
	Init();
}

CAccessible2::CAccessible2(HWND hwnd, long lChildID):
	m_spAccessible2 (NULL),
	CAccessible(hwnd,lChildID)
{	
	Init();
}

CAccessible2::CAccessible2(const POINT& rPtScreen) :
	m_spAccessible2 (NULL),
	CAccessible(rPtScreen)
{
	CoInitializeEx(NULL,COINIT_MULTITHREADED);
	CComVariant varChild;
	HRESULT hr = 
		AccessibleObjectFromPoint(rPtScreen, &m_spAccessible, &varChild);
	if (SUCCEEDED(hr) || (m_spAccessible != NULL) || (varChild.vt == VT_I4)) {
		//printf("succeeded: handle=%d, mPAcc=%d, varChild=%d, VT_I4=%d\n", m_hwnd, m_spAccessible, varChild.vt, VT_I4);
		m_lChildID = varChild.lVal;
		IServiceProvider *pProv = NULL;
		HRESULT res = m_spAccessible->QueryInterface(IID_IServiceProvider, (void**) &pProv);

		if(SUCCEEDED(res) && pProv){ 
			res = pProv->QueryService(IID_IAccessible,IID_IAccessible2,(void**)&m_spAccessible2);
		}
		else
		{
			m_spAccessible2 = NULL;
		}
		Init();
	}
	else
		// Failure strategy:  NULL object.
		//printf("failed\n");
		m_spAccessible2 = NULL;
}

void CAccessible2::Init()
{	
	CoInitializeEx(NULL,COINIT_APARTMENTTHREADED);
	HRESULT hr;
	IAccessible2 *pAcc2 = NULL;
	IServiceProvider *pProv = NULL;
	
	//try to get IAccessible2 from IAccessible
	if(m_spAccessible!=NULL && m_spAccessible2==NULL){ 
		hr = m_spAccessible->QueryInterface(IID_IServiceProvider, (void**) &pProv);
		
		if(SUCCEEDED(hr) && pProv){ 
			hr = pProv->QueryService(IID_IAccessible,IID_IAccessible2,(void**)&pAcc2); 
			if(SUCCEEDED(hr) && pAcc2){
				m_spAccessible2 = pAcc2;
			}
		}
	}
	// Have a window handle but no IAccessible2.
		// Try to get an accessible2 first from SODC Client, then from Client.
	
	if ((m_hwnd != NULL) && (m_spAccessible2 == NULL)) {

		if (!InitFromObjSodcClient()){

			InitFromObjClient();
		}
	}		
	else if ((m_hwnd == NULL) && (m_spAccessible2 != NULL)) {
		// Have an IAccessible but no window handle.
		hr  = WindowFromAccessibleObject( m_spAccessible2, &m_hwnd);
		if (FAILED(hr)) m_hwnd = NULL;
	}

}

bool CAccessible2::InitFromObjSodcClient(){
		IAccessible* acc = NULL;
		IServiceProvider *pProv = NULL;
		HRESULT res;

		HRESULT hr = AccessibleObjectFromWindow(
			m_hwnd, OBJID_SODC_CLIENT, IID_IAccessible, (void**) &acc);
		
		if ( SUCCEEDED(hr) && acc) {
			res = acc->QueryInterface(IID_IServiceProvider, (void**) &pProv);
	
			if(SUCCEEDED(res) && pProv){ 
					res = pProv->QueryService(IID_IAccessible,IID_IAccessible2,(void**)&m_spAccessible2);
			
				if(SUCCEEDED(res) && m_spAccessible2){ 
					m_spAccessible = acc;
					return true;
				}

			}
		}	
		return false;
}

bool CAccessible2::InitFromObjClient(){
		IAccessible* acc = NULL;
		IServiceProvider *pProv = NULL;
		HRESULT res;

		HRESULT hr = AccessibleObjectFromWindow(
			m_hwnd, OBJID_CLIENT, IID_IAccessible, (void**) &acc);
		
		if ( SUCCEEDED(hr) && acc) {
			res = acc->QueryInterface(IID_IServiceProvider, (void**) &pProv);
	
			if(SUCCEEDED(res) && pProv){ 
					res = pProv->QueryService(IID_IAccessible,IID_IAccessible2,(void**)&m_spAccessible2);
			
				if(SUCCEEDED(res) && m_spAccessible2){ 
					m_spAccessible = acc;
					return true;
				}

			}
		}
	
		return false;
}

CAccessible2::~CAccessible2()
{
	CoUninitialize();
	// Smart pointer automatically handles any Release of IAccessible2.
}


//////////////////////////////////////////////////////////////////////
// Operators
//////////////////////////////////////////////////////////////////////


//------------------------------------------------------------------//

bool CAccessible2::operator <(const CAccessible2& rhs) const
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

bool CAccessible2::operator==(const CAccessible2& rhs) const
{
    bool bSame = false;
	int nrAccessibleInterfaces = 0;

	// Figure out if we have 0, 1, or 2 good IAccessible pointers.
	if (m_spAccessible2 != NULL)
		++nrAccessibleInterfaces;
	if (rhs.m_spAccessible2 != NULL)
		++nrAccessibleInterfaces;

	if ((nrAccessibleInterfaces == 2) &&
		(m_spAccessible2.GetInterfacePtr() == rhs.m_spAccessible2.GetInterfacePtr()) &&
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

		hr1 = m_spAccessible2->QueryInterface(IID_IUnknown, (void**) &pUnk1);
		hr2 = rhs.m_spAccessible2->QueryInterface(IID_IUnknown, (void**) &pUnk2);

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


CAccessible2& CAccessible2::operator =(const CAccessible2& acc)
{

	this->m_hwnd = acc.m_hwnd;
	this->m_lChildID = acc.m_lChildID;
	this->m_spAccessible = acc.m_spAccessible;
	this->m_spAccessible2 = acc.m_spAccessible2;

	return *this;
}

//////////////////////////////////////////////////////////////////////
// Methods
//////////////////////////////////////////////////////////////////////

long CAccessible2::GetChildCount() const
{
    long count = 0;
	if (m_lChildID == CHILDID_SELF) {
       HRESULT hr = m_spAccessible2->get_accChildCount(&count);
	   if(hr!=S_OK){
		putErrorCode(_T("accessibleChildCount"),getHRESULTString(hr), env, jobj);
	   }
    }
    return count;
}

CAccessible2** CAccessible2::GetChildren(long* cCount)
{
	CAccessible2 **children = NULL;
    if (m_lChildID == CHILDID_SELF)  {
        long count = GetChildCount();
        long obtained = 0;
		if( count > MAX_children){
			return NULL;
		}
        CComVariant *childVars = new CComVariant[count];
		HRESULT hResult = AccessibleChildren(m_spAccessible2, 0, count, childVars, &obtained);
		*cCount = obtained;
        if (hResult==S_OK) {
			children = new CAccessible2*[obtained];
			for(int index=0; index< obtained; index++){
				CComVariant childVar = childVars[index];
				if (childVar.vt == VT_I4) {
					children[index] = new CAccessible2(m_spAccessible2, childVar.lVal);
				} else if (childVar.vt == VT_DISPATCH && childVar.pdispVal != NULL){
					IDispatch *pChild = childVar.pdispVal;
					IAccessible2 *accChild = NULL;
					hResult = pChild->QueryInterface(IID_IAccessible2, (void**) &accChild);
					if (hResult==S_OK) {
						children[index] = new CAccessible2(accChild);
					}
					pChild->Release();
				}
			}
		}else{
			  	putErrorCode(_T("accessibleChildren"),getHRESULTString(hResult), env, jobj);
	   }
		delete[] childVars;
	}
    return children;
}
CAccessible2* CAccessible2::GetChild(long index)
{
	CAccessible2 *child = NULL;
    if (m_lChildID == CHILDID_SELF)  {
        long count = GetChildCount();
        long obtained = 0;
        CComVariant *childVars = new CComVariant[count];
		
	    HRESULT hr = AccessibleChildren(m_spAccessible2, 0, count, childVars, &obtained);
		if(hr!=S_OK){
			putErrorCode(_T("accessibleChild"),getHRESULTString(hr), env, jobj);
		}
        if (index < obtained && hr == S_OK) {
			CComVariant childVar = childVars[index];
            if (childVar.vt == VT_I4) {
				child = new CAccessible2(m_spAccessible2, childVar.lVal);
            } else if (childVar.vt == VT_DISPATCH && childVar.pdispVal != NULL)     {
				IDispatch *pChild = childVar.pdispVal;
                IAccessible2 *accChild = NULL;
				hr = pChild->QueryInterface(IID_IAccessible2, (void**) &accChild);
				if (hr == S_OK) {
						child = new CAccessible2(accChild);
					}
				pChild->Release();
			}
        }
		delete[] childVars;
    }

    return child;
}


long CAccessible2::GetAccessibleStateAsLong() const{
	
	long childID =0;
	
	CComVariant varID(childID);
	VARIANT varState;
	long accState = 0xDEADBEEF;		// Assuming DEADBEEF is invalid role value.

	if (m_spAccessible2){
		//m_spAccessible2->get_uniqueID(&childID);
		HRESULT hr = m_spAccessible2->get_accState(varID, &varState);
		if (SUCCEEDED(hr)) {
			accState = varState.lVal;
		}else{
			putErrorCode(_T("accessibleState"),getHRESULTString(hr), env, jobj);
	   }
	}
	return accState;
}

CAccessible2* CAccessible2::GetAccParent() const{
{
	CAccessible2* accParent = NULL;

	if (m_lChildID != CHILDID_SELF) {
		// Back up from simple element to CHILDID_SELF of full object.
		accParent = new CAccessible2(m_spAccessible2, CHILDID_SELF);
	}
	else {
		IDispatchPtr spdispParent = NULL;
		HRESULT hr = m_spAccessible2->get_accParent(&spdispParent);
		if (SUCCEEDED(hr) && (spdispParent != NULL)) {
			IAccessiblePtr spAcc = spdispParent;
			IAccessible2* pAcc2 = getIA2FromIA(spAcc);
			if (pAcc2!=NULL){
				accParent = new CAccessible2(pAcc2);
			}
		}
		if(hr!=S_OK){
			putErrorCode(_T("accessibleParent"),getHRESULTString(hr), env, jobj);
	   }
	}

	return  accParent;
}

}

CComBSTR CAccessible2::GetAccessibleApplicationName() const{

	  IAccessibleApplication* cApp = NULL;
	  CComBSTR str = NULL;
	  if (m_spAccessible2)
	  {
		  HRESULT hr = m_spAccessible2->QueryInterface(IID_IAccessibleApplication, (void **)&cApp);

		  if(SUCCEEDED(hr)){
			 
		  hr = cApp->get_appName(&str);
  			  if(hr!=S_OK){
					putErrorCode(_T("accessibleApplicationName"),getHRESULTString(hr), env, jobj);
			  }
			  if(SUCCEEDED(hr)){
				return str;
			  }
		  }
	  }

	  return str;

}
CComBSTR CAccessible2::GetAccessibleApplicationVersion() const{

	  IAccessibleApplication* cApp = NULL;
	  CComBSTR str("");
	  if (m_spAccessible2)
	  {
		  HRESULT hr = m_spAccessible2->QueryInterface(IID_IAccessibleApplication, (void **)&cApp);

		  if(SUCCEEDED(hr)){
			  CComBSTR version;
			  hr = cApp->get_appVersion(&version);
			  if(SUCCEEDED(hr)){
				str = version;
			  }
		  }
	  }

	  return str;

}


CComBSTR CAccessible2::GetAccessibleValueMin()  const{

	 IAccessibleValue* cVal = NULL;
	  CComBSTR str("");
	  char* st = new char[50];
	  if (m_spAccessible2)
	  {
		  HRESULT hr = m_spAccessible2->QueryInterface(IID_IAccessibleValue, (void **)&cVal);
	
		  if(SUCCEEDED(hr)){
			  VARIANT minValue;
			  hr = cVal->get_minimumValue(&minValue);
			  if(SUCCEEDED(hr)){
				/*  if(minValue.vt==5){
					DOUBLE dStr = minValue.dblVal;
					sprintf(st,"%g",dStr);
					      LONGLONG llVal;
                LONG lVal;
                BYTE bVal;
                SHORT iVal;
                FLOAT fltVal;
                DOUBLE dblVal;
				  }*/
				int swth = minValue.vt;

				  switch(swth)
				  {
					  	case 0:
							//FormatString(
							sprintf(st,"%ld",minValue.llVal);
							break;

						case 1:
							//LONG lStr = minValue.lVal;
							sprintf(st,"%ld",minValue.lVal);
							break;

						case 2:
							//BYTE bStr = minValue.bVal;
							sprintf(st,"%s",minValue.bVal);
							break;

						case 3:
							//SHORT sStr = minValue.iVal;
							sprintf(st,"%d",minValue.iVal);
							break;

						case 4:
							//FLOAT fStr = minValue.fltVal;
							sprintf(st,"%f",minValue.fltVal);
							break;

						case 5:
							//DOUBLE dStr = minValue.dblVal;
							sprintf(st,"%g",minValue.dblVal);
							break;

						default:
							sprintf(st,"%s",minValue.dblVal);
							break;
				  } 
			  }
			  str = (CComBSTR) st;
		  }
	  }
	  return str;		
}

CComBSTR CAccessible2::GetAccessibleValueMax()  const{
	 IAccessibleValue* cVal = NULL;
	  CComBSTR str("");
	  char* st = new char[50];
	  if (m_spAccessible2)
	  {
		  HRESULT hr = m_spAccessible2->QueryInterface(IID_IAccessibleValue, (void **)&cVal);
	
		  if(SUCCEEDED(hr)){
			  VARIANT maxValue;
			  hr = cVal->get_maximumValue(&maxValue);
			  if(SUCCEEDED(hr)){
				int swth = maxValue.vt;

				  switch(swth)
				  {
					  	case 0:
							sprintf(st,"%ld",maxValue.llVal);
							break;

						case 1:
							//LONG lStr = minValue.lVal;
							sprintf(st,"%ld",maxValue.lVal);
							break;

						case 2:
							//BYTE bStr = minValue.bVal;
							sprintf(st,"%ld",maxValue.bVal);
							break;

						case 3:
							//SHORT sStr = minValue.iVal;
							sprintf(st,"%d",maxValue.iVal);
							break;

						case 4:
							//FLOAT fStr = minValue.fltVal;
							sprintf(st,"%f",maxValue.fltVal);
							break;

						case 5:
							//DOUBLE dStr = minValue.dblVal;
							sprintf(st,"%g",maxValue.dblVal);
							break;

						default:
							sprintf(st,"%ld",maxValue.lVal);
							break;
				  } 
			  }
			  str = (CComBSTR) st;
		  }
	  }
				  

	  return str;
}

IAccessibleApplication* CAccessible2::GetAccessibleApplication()  const{
	IAccessibleApplication* cApp = NULL;
	
	  if (m_spAccessible2)
	  {
		  HRESULT hr = m_spAccessible2->QueryInterface(IID_IAccessibleApplication, (void **)&cApp);
		  if(hr!=S_OK){
					putErrorCode(_T("accessibleApplication"),getHRESULTString(hr), env, jobj);
		 }
		  if(SUCCEEDED(hr)&& cApp!=NULL){
		
			  //got IAccessibleApplciationPtr
			  return cApp;
		  }
	  }

	return cApp;
}
IAccessibleValue* CAccessible2::GetAccessibleValue()  const{
	IAccessibleValue* cVal = NULL;
	
	  if (m_spAccessible2)
	  {
		  HRESULT hr = m_spAccessible2->QueryInterface(IID_IAccessibleValue, (void **)&cVal);
		  if(hr!=S_OK){
				putErrorCode(_T("accessibleValue2"),getHRESULTString(hr), env, jobj);
		  }
		  if(SUCCEEDED(hr)){
		
			  //got IAccessibleValuePtr
			  return cVal;
		  }
	  }

	return cVal;
}

IAccessibleAction* CAccessible2::GetAccessibleAction() const{

	IAccessibleAction* cAct = NULL;
	
	  if (m_spAccessible2)
	  {
		  HRESULT hr = m_spAccessible2->QueryInterface(IID_IAccessibleAction, (void **)&cAct);
		  if(hr!=S_OK){
				putErrorCode(_T("accessibleAction2"),getHRESULTString(hr), env, jobj);
		  }
		  if(SUCCEEDED(hr)&& cAct!=NULL){
		
			  //got IAccessibleValuePtr
			  return cAct;
		  }
	  }

	return cAct;
}

IAccessibleComponent* CAccessible2::GetAccessibleComponent() const{
	IAccessibleComponent* cComp = NULL;
	
	  if (m_spAccessible2)
	  {
		  HRESULT hr = m_spAccessible2->QueryInterface(IID_IAccessibleComponent, (void **)&cComp);
		  if(hr!=S_OK){
				putErrorCode(_T("accessibleComponent"),getHRESULTString(hr), env, jobj);
		  }
		  if(SUCCEEDED(hr) && cComp!=NULL){
		
			  //got IAccessibleComponent Ptr
			  return cComp;
		  }
	  }

	return cComp;
}


IAccessibleHyperlink* CAccessible2::GetAccessibleHyperlink() const{
	IAccessibleHyperlink* chypLink = NULL;
	
	  if (m_spAccessible2)
	  {
		  HRESULT hr = m_spAccessible2->QueryInterface(IID_IAccessibleHyperlink, (void **)&chypLink);
		  if(hr!=S_OK){
				putErrorCode(_T("accessibleHyperlink"),getHRESULTString(hr), env, jobj);
		  }	
		  if(SUCCEEDED(hr) && chypLink!=NULL){
		
			  //got IAccessibleValuePtr
			  return chypLink;
		  }
	  }

	return chypLink;
}

IAccessibleHypertext* CAccessible2::GetAccessibleHypertext() const{
	IAccessibleHypertext* chypText = NULL;
	
	  if (m_spAccessible2)
	  {
		  HRESULT hr = m_spAccessible2->QueryInterface(IID_IAccessibleHypertext, (void **)&chypText);
		  if(hr!=S_OK){
				putErrorCode(_T("accessibleHypertext"),getHRESULTString(hr), env, jobj);
		  }	
		  if(SUCCEEDED(hr) &&chypText!=NULL ){
		
			  //got IAccessibleValuePtr
			  return chypText;
		  }
	  }

	return chypText;
}

IAccessibleTable*  CAccessible2::GetAccessibleTable()  const{

	IAccessibleTable* cTbl = NULL;
	
	  if (m_spAccessible2)
	  {
		  HRESULT hr = m_spAccessible2->QueryInterface(IID_IAccessibleTable, (void **)&cTbl);
		  if(hr!=S_OK){
				putErrorCode(_T("accessibleTable"),getHRESULTString(hr), env, jobj);
		  }	
		  if(SUCCEEDED(hr)&& cTbl!=NULL){
		
			  //got IAccessibleTablePtr
			  return cTbl;
		  }
	  }

	return cTbl;
}

IAccessibleTable2*  CAccessible2::GetAccessibleTable2()  const{

	IAccessibleTable2* cTbl = NULL;
	IServiceProvider *pProv = NULL;
		HRESULT hr;
		
	  if (m_spAccessible2)
	  {
		  HRESULT hr = m_spAccessible2->QueryInterface(IID_IServiceProvider, (void**) &pProv);
		  if(SUCCEEDED(hr) && pProv){ 
			hr = pProv->QueryService(IID_IAccessible,IID_IAccessibleTable2,(void**)&cTbl);
		  }
		  if(hr!=S_OK){
				putErrorCode(_T("accessibleTable2"),getHRESULTString(hr), env, jobj);
		  }	
		  if(SUCCEEDED(hr)&& cTbl!=NULL){
		
			  //got IAccessibleTablePtr
			  return cTbl;
		  }
	  }

	return cTbl;
}

IAccessibleTableCell*  CAccessible2::GetAccessibleTableCell()  const{

	IAccessibleTableCell* cTcell = NULL;
	IServiceProvider *pProv = NULL;
		HRESULT hr;
		
	  if (m_spAccessible2)
	  {
		  HRESULT hr = m_spAccessible2->QueryInterface(IID_IServiceProvider, (void**) &pProv);
		  if(SUCCEEDED(hr) && pProv){ 
			hr = pProv->QueryService(IID_IAccessible,IID_IAccessibleTableCell,(void**)&cTcell);
		  }
		  if(hr!=S_OK){
				putErrorCode(_T("accessibleTableCell"),getHRESULTString(hr), env, jobj);
		  }	
		  if(SUCCEEDED(hr)&& cTcell!=NULL){
		
			  //got IAccessibleTablePtr
			  return cTcell;
		  }
	  }

	return cTcell;
}
 IAccessibleText* CAccessible2::GetAccessibleText()  const{
	
	  IAccessibleText* cTxt = NULL;
	  if (m_spAccessible2)
	  {
		  HRESULT hr = m_spAccessible2->QueryInterface(IID_IAccessibleText, (void **)&cTxt);
		  if(hr!=S_OK){
				putErrorCode(_T("accessibleText"),getHRESULTString(hr), env, jobj);
		  }	
		  if(SUCCEEDED(hr) && cTxt!=NULL){
		
			  //got IAccessibleTablePtr
			  return cTxt;
		  }
	  }

	return cTxt;
}

IAccessibleImage* CAccessible2::GetAccessibleImage()  const{
	IAccessibleImage* cImg = NULL;
	  if (m_spAccessible2)
	  {
		  HRESULT hr = m_spAccessible2->QueryInterface(IID_IAccessibleImage, (void **)&cImg);
		  if(hr!=S_OK){
				putErrorCode(_T("accessibleImage"),getHRESULTString(hr), env, jobj);
		  }	
		  if(SUCCEEDED(hr)&& cImg!=NULL){
		
			  //got IAccessibleImagePtr
			  return cImg;
		  }
	  }

	return cImg;
}

IAccessibleEditableText* CAccessible2::GetAccessibleEditableText()  const{
	IAccessibleEditableText* cTxt = NULL;
	  if (m_spAccessible2)
	  {
		  HRESULT hr = m_spAccessible2->QueryInterface(IID_IAccessibleEditableText, (void **)&cTxt);
		  if(hr!=S_OK){
				putErrorCode(_T("accessibleEditableText"),getHRESULTString(hr), env, jobj);
		  }		
		  if(SUCCEEDED(hr)&& cTxt!=NULL){
		
			  //got IAccessibleTablePtr
			  return cTxt;
		  }
	  }
	return cTxt;

}

long CAccessible2::GetIA2Role() const{
	CComVariant varID(m_lChildID);
	CComVariant varRole;
	long accRole = IA2_ROLE_UNKNOWN;	

	HRESULT hr = m_spAccessible2->role(&accRole);
	if(hr!=S_OK){
		putErrorCode(_T("accessibleRole"),getHRESULTString(hr), env, jobj);
	}	
	if( accRole==IA2_ROLE_UNKNOWN){
		hr = m_spAccessible2->get_accRole(varID, &varRole);
		if(hr!=S_OK){
			putErrorCode(_T("accessibleRole"),getHRESULTString(hr), env, jobj);
		}	
		if (SUCCEEDED(hr)&&varRole.vt == VT_I4) {
			accRole = varRole.lVal;
		}
	}
	return accRole;
}

CComBSTR CAccessible2::GetRoleAsString() const{
	CComVariant varID(m_lChildID);
	CComVariant varRole;
	HRESULT hr = m_spAccessible2->get_accRole(varID, &varRole);
	if(hr!=S_OK){
		putErrorCode(_T("accessibleRole"),getHRESULTString(hr), env, jobj);
	}	
	if (SUCCEEDED(hr)) {
		if(varRole.vt == VT_BSTR){
			return varRole.bstrVal ;
		}
	}
	return 0;
}

	long CAccessible2::GetNRelations() const { 
		long rCount =0;
		HRESULT hr ;
		if(m_spAccessible2){
			hr = m_spAccessible2->get_nRelations(&rCount);
			if(hr!=S_OK){
				putErrorCode(_T("accessibleRelationCount"),getHRESULTString(hr), env, jobj);
			}	
		}
		return rCount;
	}		
		
	CComBSTR CAccessible2::GetRelation(long relationIndex) const {
		IAccessibleRelation* accRel =NULL;
		HRESULT hr ;
		CComBSTR str = NULL;
		if(m_spAccessible2){		
			hr = m_spAccessible2->get_relation(relationIndex, &accRel);
		}
		if(hr!=S_OK){
			putErrorCode(_T("accessibleRelation"),getHRESULTString(hr), env, jobj);
		}
		if( SUCCEEDED(hr) && accRel)
		{
			hr = accRel->get_relationType(&str);
			if( SUCCEEDED(hr)){
				str.Append(":");
				char* strDest =new char[256];
				int target = CAccessible2::getRelationTarget(relationIndex,accRel);
				str.Append( itoa(target, strDest, 10));
			}
		}
		return (CComBSTR) str;
	}

	HRESULT CAccessible2::GetRelations(long maxRelations, 
		IAccessibleRelation** accRel, long* nRelations) const {
		HRESULT hr = E_FAIL ;
		if(m_spAccessible2){
			hr = m_spAccessible2->get_relations(maxRelations, accRel, nRelations);
		}
		return hr;
	}

	boolean CAccessible2::ScrollTo(long topLeft) const { 
		boolean bRes = false;
		if(m_spAccessible2){
			HRESULT hr= m_spAccessible2->scrollTo((IA2ScrollType)topLeft);
			if( SUCCEEDED(hr))
				bRes = true;
		}
		return bRes;
	}	


	boolean CAccessible2::ScrollToPoint(long coordinateType, long x, long y) const { 
		boolean bRes = false;
		if(m_spAccessible2){
			HRESULT hr= m_spAccessible2->scrollToPoint((IA2CoordinateType)coordinateType,
				x, y);
			if( SUCCEEDED(hr))
				bRes = true;
		}
		return bRes;
	}
				
	HRESULT CAccessible2::GetGroupPosition (long *groupLevel,
		long *similarItemsInGroup, long *positionInGroup) const { 
		HRESULT hr = E_FAIL ;
		if(m_spAccessible2){
			hr = m_spAccessible2->get_groupPosition(groupLevel, similarItemsInGroup, positionInGroup);
		}
		if(hr!=S_OK){
				putErrorCode(_T("accessibleGroupPosition"),getHRESULTString(hr), env, jobj);
		}	
		return hr;
	}		
				
	/* removed as per new idl
	CComBSTR CAccessible2::GetLocalizedRoleName() const {
		CComBSTR str;
		HRESULT hr ;
		if(m_spAccessible2){
			hr= m_spAccessible2->get_localizedRoleName(&str);
		}
		return str;
	}*/
				
	AccessibleStates CAccessible2::GetStates() const { 
		AccessibleStates states;
		long ct =0;
		HRESULT hr ;
		if(m_spAccessible2){
			hr = m_spAccessible2->get_states(&ct);
			if(hr!=S_OK){
					putErrorCode(_T("accessibleAccessibleStates"),getHRESULTString(hr), env, jobj);
			}	
			if(SUCCEEDED(hr)){
				return ct;
			}
		}
		return ct;
	}		
	/* removed as per new idl			
	HRESULT CAccessible2::GetLocalizedStateNames(long maxLocalizedStateNames,
		BSTR** strArray, long* nStates) const { 
		HRESULT hr ;
		if(m_spAccessible2 && maxLocalizedStateNames >0){
			hr = m_spAccessible2->get_localizedStateNames(maxLocalizedStateNames, strArray, nStates);
		}
		return hr;
	}			*/
				
	CComBSTR CAccessible2::GetExtendedRole() const { 
		CComBSTR str;
		HRESULT hr ;
		if(m_spAccessible2){
			hr = m_spAccessible2->get_extendedRole(&str);
			if(hr!=S_OK){
				putErrorCode(_T("accessibleExtendedRole"),getHRESULTString(hr), env, jobj);
			}	
		}
		return str;
	}		
				
	CComBSTR CAccessible2::GetLocalizedExtendedRole() const { 
		CComBSTR str;
		HRESULT hr ;
		if(m_spAccessible2){
			hr= m_spAccessible2->get_localizedExtendedRole(&str);
			if(hr!=S_OK){
				putErrorCode(_T("accessibleLocalizedExtendedRole"),getHRESULTString(hr), env, jobj);
			}
		}
		return str;
	}		
				
	long CAccessible2::GetExtendedStateCount() const { 
		long esCount =0;
		HRESULT hr ;
		if(m_spAccessible2){
			hr = m_spAccessible2->get_nExtendedStates(&esCount);
			if(hr == S_OK){
				return esCount;
			}else{
				putErrorCode(_T("accessibleExtendedStateCount"),getHRESULTString(hr), env, jobj);
			}
		}
		return 0;
	}		
				
	HRESULT CAccessible2::GetExtendedStates(long maxExtendedStates,
	BSTR** strArray, long* nStates) const {
		HRESULT hr = E_FAIL ;
		if(m_spAccessible2 && maxExtendedStates >0){
			hr = m_spAccessible2->get_extendedStates(maxExtendedStates, strArray, nStates);
		}
		if(hr!=S_OK){
			putErrorCode(_T("accessibleExtendedStates"),getHRESULTString(hr), env, jobj);
		}
		return hr;
	}			
				
	HRESULT CAccessible2::GetLocalizedExtendedStates(long maxLocalizedExtendedStates,
		BSTR** strArray, long* nStates) const {
		HRESULT hr = E_FAIL ;
		if(m_spAccessible2 && maxLocalizedExtendedStates >0){
			hr = m_spAccessible2->get_localizedExtendedStates(maxLocalizedExtendedStates, strArray, nStates);
			if(hr!=S_OK){
				putErrorCode(_T("accessibleLocalizedExtendedStates"),getHRESULTString(hr), env, jobj);
			}
		}
		return hr;
	}			
				
	long CAccessible2::GetUniqueID() const { 
		long uniqId =0;
		HRESULT hr ;
		if(m_spAccessible2){
			hr = m_spAccessible2->get_uniqueID(&uniqId);
			if(hr!=S_OK){
				putErrorCode(_T("uniqueID"),getHRESULTString(hr), env, jobj);
			}
		}
		return uniqId;
	}			
				
	HWND CAccessible2::GetWindowHandle() const{ 
		HWND hwnd =0;
		HRESULT hr ;
		if(m_spAccessible2){
			hr = m_spAccessible2->get_windowHandle(&hwnd);
		}
		return hwnd;
	}			
				
	long CAccessible2::GetIndexInParent() const { 
		long index =0;
		HRESULT hr ;
		if(m_spAccessible2){
			hr = m_spAccessible2->get_indexInParent(&index);
			if(hr!=S_OK){
				putErrorCode(_T("accessibleIndexInParent"),getHRESULTString(hr), env, jobj);
			}
		}
		return index;
	}				
				
	HRESULT CAccessible2::GetLocale(IA2Locale* loc) const { 
		HRESULT hr;
		if(m_spAccessible2){
		 hr = m_spAccessible2->get_locale(loc);
			if(hr!=S_OK){
				putErrorCode(_T("accessibleLocale"),getHRESULTString(hr), env, jobj);
			}
		}
		return hr;
	}		
				
	CComBSTR CAccessible2::GetAttributes() const{ 
		CComBSTR str;
		HRESULT hr;
		if(m_spAccessible2){
			hr= m_spAccessible2->get_attributes(&str);
			if(hr!=S_OK){
				putErrorCode(_T("accessibleAttributes"),getHRESULTString(hr), env, jobj);
			}
		}
		return str;
	}		

	/*
	LPACCDATA CAccessible2::BuildIa2Data(IAccessible2* pAcc, VARIANT varChild, DWORD evnt, DWORD dwmsEventTime){

	LPACCDATA accData = new ACCDATA;
	//name
	    CComBSTR bstrName;
		CComBSTR bstrData(_T(""));
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
					_ltow(rl, bRole,10);
				}
				else if(var.vt == VT_BSTR){
					lstrcpy(bRole,(LPCTSTR)var.bstrVal);
				}
			}
		}else{
			//rl = var.lVal;
			_ltow(rl, bRole,10);
		}

		//State
		pAcc->get_accState(varChild, &var);
		long bState = var.lVal;
		TCHAR st[256];
		_ltow(bState,st,10);

    // direct conversion from BSTR to LPCTSTR only works in Unicode
		lstrcpy(accData->name,(LPCTSTR)bstrName);
		lstrcpy(accData->role, bRole);
		lstrcpy(accData->state, st);
		accData->evnt = evnt;
		accData->milliseconds = dwmsEventTime;
		return accData;
}
	*/
	
IAccessible2* CAccessible2::getIA2FromIA(IAccessible *pAcc) {

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

IAccessible* CAccessible2::getIAFromIA2(IAccessible2 *pAcc2) {

		IAccessible *pAcc = NULL;
		IServiceProvider *pProv = NULL;
		HRESULT hr;
		if(pAcc2){ 
			hr = pAcc2->QueryInterface(IID_IServiceProvider, (void**) &pProv);
		}
	    
		if(SUCCEEDED(hr) && pProv){ 
			hr = pProv->QueryService(IID_IAccessible,IID_IAccessible,(void**)&pAcc); 
			pProv->Release();
			if(SUCCEEDED(hr) && pAcc){
				return pAcc;
			}
		}
		return pAcc;
}

IAccessible* CAccessible2::getMsaaFromIUnk(IUnknown *iUnk) {

		IAccessible *pAcc = NULL;
		IServiceProvider *pProv = NULL;
		HRESULT hr;
		if(iUnk){ 
			hr = iUnk->QueryInterface(IID_IServiceProvider, (void**) &pProv);
		}
	    
		if(SUCCEEDED(hr) && pProv){ 
			hr = pProv->QueryService(IID_IAccessible,IID_IAccessible,(void**)&pAcc); 
			pProv->Release();
			if(SUCCEEDED(hr) && pAcc){
				return pAcc;
			}
		}
		return pAcc;
}
IAccessible2* CAccessible2::getIA2FromIUnk(IUnknown *iUnk) {

		IAccessible2 *pAcc2 = NULL;
		IServiceProvider *pProv = NULL;
		HRESULT hr;
		if(iUnk){ 
			hr = iUnk->QueryInterface(IID_IServiceProvider, (void**) &pProv);
		}
	    
		if(SUCCEEDED(hr) && pProv){ 
			hr = pProv->QueryService(IID_IAccessible,IID_IAccessible2,(void**)&pAcc2); 
			if(SUCCEEDED(hr) && pAcc2){
				return pAcc2;
			}
		pProv->Release();
		}
		iUnk->Release();
		return pAcc2;
}

int CAccessible2::getRelationTarget(int index, IAccessibleRelation* accRel){

	IUnknown* iUnk = NULL;
	HRESULT res = accRel->get_target(index,&iUnk);
	IAccessible2* ia2Acc = NULL;
	
	if(SUCCEEDED(res) && iUnk)
	{
		ia2Acc = CAccessible2::getIA2FromIUnk(iUnk);
		if(ia2Acc)
			return (int) new CAccessible2(ia2Acc);
		else{
			IAccessible* iacc = CAccessible2::getMsaaFromIUnk(iUnk);
			if(iacc)
				return (int) new CAccessible(iacc);
		}
	}
	return 0;
}

HWND CAccessible2::GetIA2Hwnd() const { 
	HWND ia2handle=NULL;
	HRESULT hr = m_spAccessible2->get_windowHandle(&ia2handle);
	return ia2handle;
}

