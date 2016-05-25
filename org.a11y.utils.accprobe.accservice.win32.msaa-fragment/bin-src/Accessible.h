/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.
* All rights reserved.

* Contributors:
* IBM Corporation
*******************************************************************************/ 

#pragma once
#define _WIN32_WINNT 0x0501
#define WIN32_LEAN_AND_MEAN		// Exclude rarely-used stuff from Windows headers
#include <windows.h>
#include <OleAcc.h>
#include <atlbase.h> // for CComVariant
#include <comdef.h>
#include <jni.h>
//#include <timeb.h>
//#include <time.h>
#include "psapi.h"
#pragma comment(lib, "Psapi") 

#include "ia2_api_all.h"
const int MAX_strlen = 256;
const int MAX_children = 1000;

/**
 * Some vendors define IAccesible "role" and "state" values which are 
 * outside the set of values defined by Microsoft.  Define some constants 
 * for those values.  Last ROLE value defined by Microsoft at this time
 * is ROLE_SYSTEM_OUTLINEBUTTON (0x40)
 */
#define ROLE_VENDOR_PDF_PAGE		(0x51)
#define ROLE_VENDOR_PDF_DIGITAL_SIG	(0x52)
#define OBJID_SODC_CLIENT ((LONG)0xFFFFFFEC)

#if defined(_WIN32) || defined (__WIN32__) || defined (WIN32)
#  ifdef CACCESSIBLE_EXPORTS
#    define CACCESSIBLE_API __declspec(dllexport)
#  else
#    define CACCESSIBLE_API __declspec(dllimport)
#  endif
#else
#  define PROJA_API
#endif

#define EVENT_DATA_READY_MSG _T("EVENT_DATA_READY_MSG")
#define BUF_SIZE 1024
#define _JNI_IMPLEMENTATION_

static TCHAR fileName[] = TEXT("Event_data_Mem_Map_File");

struct ACCDATA
{
    TCHAR name[256];
    TCHAR role[256];
    TCHAR state[256];
    DWORD evnt;
	DWORD milliseconds;
	TCHAR miscData[256];
};

typedef  ACCDATA *LPACCDATA;

class CACCESSIBLE_API CAccessible
{
public:

	/** Default CTOR, does not reference any IAccessible or window. */
	CAccessible();

	/** Copy constructor */
	CAccessible(const CAccessible &acc);

	/** 
	 * Create a CAccessible with child id set to CHILDID_SELF.
	 * @param pAccessible Accessible object of interest.
	 */
	CAccessible(IAccessible* pAccessible);

	/** 
	 * Create a CAccessible with a particular child id.
	 * @param pAccessible Accessible object of interest.
	 * @param Child of interest.
	 */
	CAccessible(IAccessible* pAccessible, long lChildID) ;

	/** 
	 * Create a CAccessible from a window handle, use OBJID_WINDOW, then
	 * OBJID_CLIENT if needed.
	 *
	 * Internally, will attempt to obtain an IAccessible reference
	 * for the window.  Object will still instantiate even if no IAcc 
	 * interface available.
	 * @param hwnd Window of for which we want to construct a CAccessible.
	 */
	CAccessible(HWND hwnd);

	/** 
	 * Create a CAccessible from a window handle and child id, use OBJID_WINDOW, then
	 * OBJID_CLIENT if needed.
	 *
	 * Internally, will attempt to obtain an IAccessible reference
	 * for the window.  Object will still instantiate even if no IAcc 
	 * interface available.
	 * @param hwnd Window of for which we want to construct a CAccessible.
	 * @param childID - child id
	 */
	CAccessible(HWND hwnd, long lChildID);

	/** 
	 * Create a CAccessible from a screen location (uses MSAA 
	 * AccessibleObjectFromPoint API from platform SDK).
	 * @param rPtScreen Referece to point that is examined in screen coordinates.
	 * @returns Constructed object, will answer true to .IsNULL() on CTOR failure.
	 */
	CAccessible(const POINT& rPtScreen);
		
	/** DTOR */
	virtual ~CAccessible();

public:	// Operators

	/** Equality compare. */
	bool operator ==(const CAccessible& rhs) const; 

	/** Unequality compare. */
	bool operator !=(const CAccessible& rhs) const {return ! ((*this) == rhs);}

	/** Assignment. */
    CAccessible& operator =(const CAccessible& acc);

	/** Less than, to enable STL sort. */
	bool operator <(const CAccessible& rhs) const;

public: // Methods
	/** 
	 * Get the IAccessible pointer (not reference counted when returned)
	 * @returns pointer to IAccessible
	 */
	IAccessible* GetIAccessiblePtr() const { return m_spAccessible; }

	/** @returns the ChildID value for this accessible object. */
	long GetChildID() const { return m_lChildID; }

	/**
	 * Invoke DoDefaultAction against underlying IAccessible object.
	 */
	void DoDefaultAction() const;

	/**
	 * Get the CAccessible parent.
	 * @returns CAccessible parent.
	 */
	CAccessible* GetParent() const;

	/**
	 * Get the number of children for this object.
	 * @returns number of children.
	 */
	long GetChildCount() const;

	/*
	 * get all children
	 * @returns children
	 */
	CAccessible** GetChildren(long* obtained);
	/*
	 * get the child at the specified index
	 * @param index
	 */
	CAccessible* GetChild (long);

	/**
	 * Get a localized string that describes the default action for the specified object.
     * @returns string that describes default action for object ("" if this object has no default action).
	 * 
	 */
	CComBSTR GetDefaultAction() const;

	/** Get a localized string that describes the specified object (or "" if none available).
	 * @returns string that describes the object
	 * 
	 */
	CComBSTR GetDescription() const;
	
    /** Get information about which object has focus.
	 * @returns CComVariant containing information about which object has focus.  
	 */
	CComVariant GetFocus() const;

	/** Get a localized string that contains the help information
	 * @returns localized string that contains help information
	 */
	CComBSTR GetHelp() const;

		/** Get a localized string that contains the help Topic information
	 * @returns localized string that contains help information
	 */
	CComBSTR GetHelpTopic() const;

	/** Get a string with the full path of the WinHelp file associated with the specified object
	 * @returns string with full path of WinHelp file
	 * @param lIDTopic - variable that identifies the Help file topic associated with the specified object
	 */
	CComBSTR GetHelpTopic(long *plIDTopic) const;

	/** Get a localized string that identifies the keyboard shortcut.
	 * @returns string that identifies the keyboard shortcut ("" if none).
	 * 
	 */
    CComBSTR GetKeyboardShortcut() const;

	/** Get a string that contains the specified object's name.  
	 * @returns string that contains object's name
	 * 
	 */
	CComBSTR GetName() const;

	/** Get the object role constant.  
	 * @returns role constant
	 */
	long GetRole() const;

	CComBSTR CAccessible::GetRoleAsString() const;

	/**
	 * Get rectangle for this accessible.
	 * 
	 * Wrapper for IAccessible::accLocation, or GetWindowsInfo if we have hwnd 
	 * but no IAccessible.
	 * @returns Bounding rectangle (screen location) for this object.  Sets all
	 * coord's in RECT to (-1) on failure.
	 */
	RECT GetLocation() const;

	/** 
	 * Gets the current selection of a selectable object if there is only a 
	 * single item selected.
	 * <p>
	 * This is not a complete implementation of a general get selection function.
	 * The underlying MSAA api provides for both single and multiple item selection,
	 * but we haven't done the work to implement multi-select.
	 * ### BUG multi-select use cases are not implemented (they appear as none selected).
	 * @returns A non-NULL CAccessible object if a single selection, a NULL CAccessible
	 * if either no selection or multiple selection.
	 */
	//CAccessible* GetSelection() const;

	CComVariant  GetSelection() const;

	/** Get the object's state constant.  
	 * @returns state constant
	 */
	long GetState() const;

	/** Get a localized string that contains the object's current value.
	 * @returns string that contains object's value
	 */
	CComBSTR GetValue() const;

	/** Wrapper for the MSAA IAccessible::put_accValue() method. */
	void PutValue(CComBSTR ss);

	/** Get a localized string that contains the object's role text.
	 * @returns string that contains object's role text
	 */
	CComBSTR GetRoleText() const;

	/** Static version of GetRoleText(). */
	static CComBSTR GetRoleText(long accRole);

	/** 
	 * Get window handle associated with this Accessible object. 
	 * @returns Window handle if available;  NULL handle if any problem or no window.
	 */
	HWND GetHwnd() const { return m_hwnd; }

	/** 
	 * Get window handle associated with this Accessible object. 
	 * @returns Window class name if available;  empty string otherwise.
	 */
	CComBSTR GetWndClassName() const;

	/** 
	 * Wrapper function for Win32 GetWindowText() API. 
	 * @returns Window text (title, caption, control text content) if available;
	 *  empty string otherwise.  See Win32 documentation for API.
	 */
	CComBSTR GetWndText() const { return GetWndText(m_hwnd); }

	/** 
	 * Wrapper function for Win32 GetWindowText() API. 
	 * @returns Window text (title, caption, control text content) if available;  
	 *  empty string otherwise.  See Win32 documentation for API.
	 */
	static CComBSTR GetWndText(HWND hwnd);

	/**
	 * Utility function to retrieve window class name from HWND.
	 * @param hwnd Window of interest.
	 * @returns Class name, empty on error.
	 */
	static CComBSTR GetClassNameFromHwnd(HWND hwnd);

	/** Returns true if two RECT's describe same rectangle (utility function).  */
	static 
	bool IsEqualRectangles(const RECT& rc1, const RECT& rc2);

	/** Returns true if this object is a link, false otherwise. */
	bool IsLink() const;

	/** Returns true if this object is a control, false otherwise. */
	bool IsControl() const;

	/** Returns true if this object contains a non-NULL IAccessible ptr.  */
	bool IsNotNULL() const { return m_spAccessible != NULL; }

	/** Returns true if this object contains a NULL IAccessible ptr.  */
	bool IsNULL() const { return m_spAccessible == NULL; }

	/** Returns true if this object has focus, false otherwise. */
    bool IsFocused() const;

	/** Returns true if this object is contained by the object passed in by parameter. */
    bool IsInsideOf(CAccessible& rAcc2) const;

	/** Returns true if object is visible. */
	bool IsVisible() const;

	/** Returns true if value field (possibly empty) is available. E.g.,
		password fields of text edit boxes are not available - error return code. */
	bool IsValueAvailable() const;
	static LPACCDATA BuildData(IAccessible* pAcc,VARIANT varChild, DWORD evnt, 
								 HWND hwnd, LONG idObject, LONG idChild,DWORD dwEventThread, DWORD dwmsEventTime);
	static LPACCDATA BuildIa2Data(IAccessible2* pAcc,VARIANT varChild, DWORD evnt, 
								 HWND hwnd, LONG idObject, LONG idChild,DWORD dwEventThread, DWORD dwmsEventTime);
	static CComBSTR CreateMiscData(HWND hwnd, LONG idObject, LONG idChild, DWORD threadId);

	static bool writeFileMap(LPACCDATA name);
	static IAccessible2* getIA2FromMsaa(IAccessible *pAcc);
	static CComBSTR getHRESULTString(HRESULT hr);
	static void putErrorCode(CComBSTR key,CComBSTR val, JNIEnv* jenv, jobject obj);
	void setJNI(JNIEnv* env,jobject jobj);
	
private: // Methods

	/** Set member IAccessible from HWND and vise-versa. */
	void Init();


protected:
	IAccessiblePtr m_spAccessible;
	long m_lChildID;
	HWND m_hwnd;
	JNIEnv* env;
	jobject jobj;
};
