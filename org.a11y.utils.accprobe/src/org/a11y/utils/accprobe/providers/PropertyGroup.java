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
package org.a11y.utils.accprobe.providers;

import java.util.HashMap;
import java.util.Map;

import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2Accessible;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessible;

public class PropertyGroup {
	
	private String propGroupName =null;

	public PropertyGroup (String name){
		this.propGroupName = name;
	}
	
	public String getName (){
		return propGroupName;
	}
	
	public Map<String, IAccessibleElement> getProperties(Object element){
		HashMap<String, IAccessibleElement> pMap = new HashMap<String, IAccessibleElement>();
		if(element instanceof ExplorerViewNode){
			if(getName().equals("ia2"))	{
				pMap.put("ia2",(IA2Accessible)((ExplorerViewNode) element).getUnderlyingComponent());
			}else if(getName().equals("msaa"))	{
				pMap.put("msaa",(MsaaAccessible)((ExplorerViewNode) element).getUnderlyingComponent());
			}else if(getName().equals("accservice"))	{
				pMap.put("accservice",(IAccessibleElement) ((ExplorerViewNode) element).getUnderlyingComponent());
			}
		}
		return pMap;
	}
	
}
