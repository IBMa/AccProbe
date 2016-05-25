/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation.
*
*
*
 *
 *
 * Contributors:
 *  IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.a11y.utils.accprobe.accservice.core.win32.ia2;

import java.util.HashMap;
import java.util.Map;

public class IA2AccessibleElement {

	protected Map errorCodeReturnMap = new HashMap();
	public static String errString = "HRESULT = ";

	protected void putErrorCode(String key, String value){
		if(errorCodeReturnMap!=null){
			if(!errorCodeReturnMap.containsKey(key))
				errorCodeReturnMap.put(key, errString.concat(value) );
			}
	}
	public Map errorCodeMap(){
		return errorCodeReturnMap;
	}
}
