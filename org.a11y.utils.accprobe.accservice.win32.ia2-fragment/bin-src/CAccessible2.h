/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.
*
* Contributors:
*  Kavitha Teegala
*******************************************************************************/ 

#pragma once

#include "Accessible.h"
#include "ia2_api_all.h"

#if defined(__IAccessible2_INTERFACE_DEFINED__)
_COM_SMARTPTR_TYPEDEF(IAccessible2, __uuidof(IAccessible2));
#endif// #if defined(__IAccessible_INTERFACE_DEFINED__)


class CAccessible2: public CAccessible
{
public:

	/** Default CTOR, does not reference any IAccessible2 or window. */
	CAccessible2();

	/** Copy constructor */
	CAccessible2(const CAccessible2 &acc);

	/** 
	 * Create a CAccessible2 with child id set to CHILDID_SELF.
	 * @param pAccessible Accessible object of interest.
	 */
	CAccessible2(IAccessible2* pAccessible2);

	/** 
	 * Create a CAccessible2 with a particular child id.
	 * @param pAccessible Accessible object of interest.
	 * @param Child of interest.
	 */
	CAccessible2(IAccessible2* pAccessible, long lChildID) ;

	/** 
	 * Create a CAccessible2 from a window handle, use OBJID_WINDOW, then
	 * OBJID_CLIENT if needed.
	 *
	 * Internally, will attempt to obtain an IAccessible reference
	 * for the window.  Object will still instantiate even if no IAcc 
	 * interface available.
	 * @param hwnd Window of for which we want to construct a CAccessible2.
	 */
	CAccessible2(HWND hwnd);

	/** 
	 * Create a CAccessible2 from a window handle and child id, use OBJID_WINDOW, then
	 * OBJID_CLIENT if needed.
	 *
	 * Internally, will attempt to obtain an IAccessible reference
	 * for the window.  Object will still instantiate even if no IAcc 
	 * interface available.
	 * @param hwnd Window of for which we want to construct a CAccessible2.
	 * @param childID - child id
	 */
	CAccessible2(HWND hwnd, long lChildID);

	/** 
	 * Create a CAccessible2 from a screen location (uses MSAA 
	 * AccessibleObjectFromPoint API from platform SDK).
	 * @param rPtScreen Referece to point that is examined in screen coordinates.
	 * @returns Constructed object, will answer true to .IsNULL() on CTOR failure.
	 */
	CAccessible2(const POINT& rPtScreen);
		
	/** DTOR */
	virtual ~CAccessible2();

public:	// Operators

	/** Equality compare. */
	bool operator ==(const CAccessible2& rhs) const; 

	/** Unequality compare. */
	bool operator !=(const CAccessible2& rhs) const {return ! ((*this) == rhs);}

	/** Assignment. */
    CAccessible2& operator =(const CAccessible2& acc);

	/** Less than, to enable STL sort. */
	bool operator <(const CAccessible2& rhs) const;

	
	/**
	 * Get the CAccessible2 parent.
	 * @returns CAccessible2 parent.
	 */
	CAccessible2* GetAccParent() const;

	/**
	 * Get the number of children for this object.
	 * @returns number of children.
	 */
	
	long GetChildCount() const;

	bool InitFromObjSodcClient();
	bool InitFromObjClient();
		/*
	 * get all children
	 * @returns children
	 */
	CAccessible2** GetChildren(long* count);
	/*
	 * get the child at the specified index
	 * @param index
	 */
	CAccessible2* GetChild (long);

public: // Methods
	/** 
	 * Get the IAccessible2 pointer (not reference counted when returned)
	 * @returns pointer to IAccessible2
	 */
	IAccessible2* GetIAccessible2Ptr() const { return m_spAccessible2; }

	long GetAccessibleStateAsLong() const;

	CComBSTR GetAccessibleApplicationName() const;

	CComBSTR GetAccessibleApplicationVersion() const;

	CComBSTR GetAccessibleValueMin()  const;

	CComBSTR GetAccessibleValueMax()  const;

	IAccessibleTable* GetAccessibleTable()  const;

	IAccessibleTable2* GetAccessibleTable2()  const;

	IAccessibleTableCell* GetAccessibleTableCell()  const;

	IAccessibleText* GetAccessibleText()  const;

	IAccessibleImage* GetAccessibleImage()  const;

/////////////////////new methods//
	IAccessibleValue* GetAccessibleValue()  const;

	IAccessibleApplication* GetAccessibleApplication()  const;

	IAccessibleAction* GetAccessibleAction() const;

	IAccessibleComponent* GetAccessibleComponent() const;

	IAccessibleHyperlink* GetAccessibleHyperlink() const;

	IAccessibleHypertext* GetAccessibleHypertext() const;

	IAccessibleEditableText* GetAccessibleEditableText()  const;

	long GetNRelations() const;		

	CComBSTR GetRelation(long relationIndex) const;

	HRESULT GetRelations(long maxRelations,IAccessibleRelation** accRel, long* nRelations) const ;
			
	boolean ScrollTo(long x) const;			

	boolean ScrollToPoint(long coordinateType, long x, long y) const;
				
	HRESULT GetGroupPosition (long *groupLevel, long *similarItemsInGroup, long *positionInGroup) const;			
				
	//CComBSTR GetLocalizedRoleName() const;			
				
	AccessibleStates GetStates() const;			
				
	//HRESULT GetLocalizedStateNames(long maxLocalizedStateNames,
	//	BSTR** strArray, long* nStates) const;	
	long GetIA2Role() const;
	CComBSTR GetRoleAsString() const;
	CComBSTR GetExtendedRole() const;			
				
	CComBSTR GetLocalizedExtendedRole() const;			
				
	long GetExtendedStateCount() const;			
				
	HRESULT GetExtendedStates(long maxExtendedStates,
		BSTR** strArray, long* nStates) const;			
			
	HRESULT GetLocalizedExtendedStates(long maxLocalizedExtendedStates,
		BSTR** strArray, long* nStates) const;			
				
	long GetUniqueID() const;			
				
	HWND GetWindowHandle() const;			
				
	long GetIndexInParent() const;			
				
	HRESULT GetLocale(IA2Locale* loc) const;			
				
	CComBSTR GetAttributes() const;		
	
	HWND CAccessible2::GetIA2Hwnd() const;

	static IAccessible* getIAFromIA2(IAccessible2 *pAcc2);
	static IAccessible2* getIA2FromIA(IAccessible *pAcc);
	static IAccessible2* getIA2FromIUnk(IUnknown *iUnk);
	static IAccessible* getMsaaFromIUnk(IUnknown *iUnk);
//	static LPACCDATA BuildIa2Data(IAccessible2* pAcc,VARIANT varChild, DWORD evnt, DWORD dwmsEventTime);
	static int getRelationTarget(int index, IAccessibleRelation* accRel);

private: // Methods

	/** Set member IAccessible from HWND and vise-versa. */
	void Init();

private:
	IAccessible2Ptr m_spAccessible2;

};

void putErrorCode(CComBSTR key,CComBSTR val, JNIEnv* jenv, jobject obj);