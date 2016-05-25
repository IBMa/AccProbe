/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.providers;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.a11y.utils.accprobe.GuiUtils;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2Accessible;
//import org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2AccessibleElement;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessible;
import org.a11y.utils.accprobe.views.PropertiesView;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
//import org.eclipse.swt.widgets.TreeItem;



public class PropertiesFilterContentProvider implements ITreeContentProvider
{

	
	/*
	 * Input to the table is assumed to be a ExplorerViewNode Need to get an
	 * array of PropertyDescriptors from the ExplorerViewNode and use these to
	 * form the rows in the table.
	 */
	public Object[] getElements(Object element) {
		HashMap<String, IAccessibleElement> map = new HashMap<String, IAccessibleElement>();

		PropertyGroup[] pgroups = null;
		if (element instanceof ExplorerViewNode) {
			pgroups = (PropertyGroup[]) ((ExplorerViewNode) element).getPropertyGroups();
			for (PropertyGroup p: pgroups){
				map.putAll(p.getProperties(element));
			}
			return map.entrySet().toArray();
		}else if (element instanceof IAccessibleElement){
			IAccessibleElement iaElement = (IAccessibleElement)element;
			ExplorerViewNode elem = new ExplorerViewNode( element,null);
			pgroups = (PropertyGroup[]) (elem.getAllPropertyGroups());
			for (PropertyGroup p: pgroups){
				if(p.getName().equals("ia2"))	{
					map.put("ia2",iaElement);
				}else if(p.getName().equals("msaa"))	{
					map.put("msaa",iaElement);
				}else if(p.getName().equals("accservice"))	{
					map.put("accservice",iaElement);
				}			}
			return map.entrySet().toArray();
		}
		return null;
		
	}

	public void dispose () {
	}
	
	public Object[] getChildren (Object element) {
		Object[] result = new Object[0];

		if (element instanceof ExplorerViewNode) {
			result = getElements(element);
		} else if (element instanceof Entry) {
			@SuppressWarnings("unchecked")
			Entry<String, ?> entry = (Entry<String, ?>) element;
			Object val = entry.getValue();
			String key = (String) entry.getKey();
			Class<?> type = val.getClass();

			if (!GuiUtils.isPrimitive(type) || !GuiUtils.hasAcceptableToString(type)) {
				if(key.equals("ia2")
						||key.equals("accservice")
						||key.equals("msaa")){
					try {
						Map<String, String> pMap = new HashMap<String, String>();

						@SuppressWarnings("unchecked")
						Map<String,String> resultMap = PropertyUtils.describe(val);
						resultMap.putAll(getIndexedProperties(val));
						if(val instanceof IAccessibleElement ){
							if(key.equals("ia2")){
								pMap = IA2Accessible.propertyMap();
							}else if(key.equals("msaa")){
								pMap = MsaaAccessible.propertyMap();
							}
						} 
						if(!pMap.isEmpty()){
							Map<String, String> newMap = new HashMap<String, String>();
							String[] kSet = pMap.values().toArray(new String[0]);
							for (int i=0; i< kSet.length; i++){
								newMap.put(kSet[i],null);
							}
							return newMap.entrySet().toArray();
						}
						result = resultMap.entrySet().toArray();
					} catch (Exception e) {
						e.printStackTrace(System.err);
						result = new Object[0];
					}
				}
			}
		}

		return result;
	}
	
	public Object getParent (Object element) {
		return null;
	}
	
	public boolean hasChildren (Object element) {
		boolean res = false;
		if (element instanceof Entry) {
			String key = ((Entry<?,?>) element).getKey().toString();			
			if(key.equals("ia2")||key.equals("msaa")||key.equals("accservice")){
				res = true;
			}
		}
		
		return res;
	}
		
	public Map<String, String> getIndexedProperties(Object elem){
		Map<String, String> iMap = new HashMap<String, String>();
		if(elem instanceof IAccessibleElement){
			return iMap;
		}
		Method[] ms = elem.getClass().getMethods();
		for ( Method m: ms){

				Class<?>[] ca = m.getParameterTypes();
				if(  m.getDeclaringClass()!=Object.class && ca.length >0 ){
					int modifier = m.getModifiers();
					if(Modifier.isPublic(modifier)){
						String argProp = m.getName();
						if(!argProp.equals("get") && argProp.startsWith("get")){
							argProp = argProp.replaceFirst("get", "");
							argProp = argProp.replaceFirst(argProp.substring(0, 1),argProp.substring(0, 1).toLowerCase());
						}
						iMap.put(argProp, new String(PropertiesView.ENTER_PARAMETERS));
					}
			}
		}
		return iMap;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}
}
