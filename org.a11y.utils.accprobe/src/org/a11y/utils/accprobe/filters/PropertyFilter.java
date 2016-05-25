/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.filters;

import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import org.a11y.utils.accprobe.Activator;
import org.a11y.utils.accprobe.dialogs.PropertiesFilterDialog;
import org.a11y.utils.accprobe.providers.ExplorerViewNode;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


public class PropertyFilter extends ViewerFilter
{

	public static final Set<String> DEFAULT_INCLUDES = new HashSet<String>();
	static {
		//msaa defaults
		DEFAULT_INCLUDES.add("accName");
		DEFAULT_INCLUDES.add("accRole");
		DEFAULT_INCLUDES.add("accState");
		DEFAULT_INCLUDES.add("accValue");
		DEFAULT_INCLUDES.add("howFound");

		
		// ia2 defaults
		DEFAULT_INCLUDES.add("IAccessibleAction");
		DEFAULT_INCLUDES.add("IAccessibleApplication");
		DEFAULT_INCLUDES.add("IAccessibleComponent");
		DEFAULT_INCLUDES.add("IAccessibleEditableText");
		DEFAULT_INCLUDES.add("IAccessibleHyperlink");
		DEFAULT_INCLUDES.add("IAccessibleHypertext");
		DEFAULT_INCLUDES.add("IAccessibleImage");
		DEFAULT_INCLUDES.add("IAccessibleTable2");
		DEFAULT_INCLUDES.add("IAccessibleTableCell");
		DEFAULT_INCLUDES.add("IAccessibleText");
		DEFAULT_INCLUDES.add("IAccessibleValue");		
		DEFAULT_INCLUDES.add("role");
		DEFAULT_INCLUDES.add("states");
		
		//accservice defaults
		DEFAULT_INCLUDES.add("accessibleName");
		DEFAULT_INCLUDES.add("accessibleRole");
		DEFAULT_INCLUDES.add("accessibleState");
		DEFAULT_INCLUDES.add("accessibleValue");
		DEFAULT_INCLUDES.add("accessibleText");
		DEFAULT_INCLUDES.add("accessibleTable2");
		DEFAULT_INCLUDES.add("accessibleHowFound");
		
		//property group defaults (all of them)
		//DEFAULT_INCLUDES.add("accservice");
		DEFAULT_INCLUDES.add("msaa");
		DEFAULT_INCLUDES.add("ia2");
	}
	
	public PropertyFilter() {
	}

	public boolean select(Viewer viewer, Object parentElement, Object element) {
	
		Object obj = ((Entry)element).getValue();

		if ( (element instanceof Entry)){
			boolean result = true;
			String parentName = "";
			//filter is only applicable to property groups and their level 1 properties
			if(!isPropertyGroup((Entry)element)){
				if(parentElement instanceof Entry &&
						!isPropertyGroup((Entry) parentElement)){
					return true;
				}
			}
			IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(PropertiesFilterDialog.ID);;
			String name = (String) ((Entry) element).getKey();
			if (settings == null) {
				result = DEFAULT_INCLUDES.contains(name);

			} else if (parentElement != null && parentElement instanceof Entry) {
				parentName = (String) ((Entry) parentElement).getKey();
				result = settings.get(name)!=null && settings.getBoolean(name);

			} else if (parentElement != null && parentElement instanceof ExplorerViewNode) {
				result = settings.get(name)!=null && settings.getBoolean(name);
			}
			return result;
		}


		return true;
	}
	

	public static boolean isPropertyGroup(Entry e) {
		// TODO Auto-generated method stub
		String key = e.getKey().toString();
		return ( key.equals("msaa") || key.equals("ia2") || key.equals("accservice"));
	}

}
