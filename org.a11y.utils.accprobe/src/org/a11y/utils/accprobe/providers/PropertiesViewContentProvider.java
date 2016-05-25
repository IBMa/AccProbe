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
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.a11y.utils.accprobe.GuiUtils;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2Accessible;
import org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2AccessibleElement;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessible;
import org.a11y.utils.accprobe.views.PropertiesView;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.TreeItem;



public class PropertiesViewContentProvider implements ITreeContentProvider
{

	
	/*
	 * Input to the table is assumed to be a ExplorerViewNode Need to get an
	 * array of PropertyDescriptors from the ExplorerViewNode and use these to
	 * form the rows in the table.
	 */
	public Object[] getElements(Object element) {
		HashMap map = new HashMap();

		PropertyGroup[] pgroups = null;
		if (element instanceof ExplorerViewNode) {
			pgroups = (PropertyGroup[]) ((ExplorerViewNode) element).getPropertyGroups();
			for (PropertyGroup p: pgroups){
				map.putAll(p.getProperties(element));
			}
			return map.entrySet().toArray();
		}else if (element instanceof IAccessibleElement){
			ExplorerViewNode elem = new ExplorerViewNode( element,null);
			pgroups = (PropertyGroup[]) (elem.getPropertyGroups());
			for (PropertyGroup p: pgroups){
				map.putAll(p.getProperties(elem));
			}
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
			Entry entry = (Entry) element;
			Object val = entry.getValue();
			String key = (String) entry.getKey();
			Class<?> type = val.getClass();
			
			if (type.isArray()) {
				Object[] oa = (Object[]) val;
				Map map = new HashMap();
				for (int i = 0; i < oa.length; ++i) {
					map.put("[" + i + "]", oa[i]);
				}
				result = new TreeMap(map).entrySet().toArray();
			} else if (Map.class.isAssignableFrom(type)) {
				result = ((Map) val).entrySet().toArray();
			} else if (!GuiUtils.isPrimitive(type) || !GuiUtils.hasAcceptableToString(type)) {
				try {
					Map errMap = new HashMap();
					Map pMap = new HashMap();
	
					Map resultMap = PropertyUtils.describe(val);
					resultMap.putAll(getIndexedProperties(val));
					resultMap.remove("class");
					resultMap = PropertiesView.FILTER_NULLS? GuiUtils.filterNulls(resultMap): resultMap;

					if(val instanceof IAccessibleElement && !key.equals("accservice") ){
						errMap = ((MsaaAccessible)val).errorCodeMap();
						if(key.equals("ia2")
						  ||(key.equals("accParent")&& val instanceof IA2Accessible)){
							pMap = IA2Accessible.propertyMap();
						}else if(key.equals("msaa")|| 
								(key.equals("accParent")&& val instanceof MsaaAccessible)){
							pMap = MsaaAccessible.propertyMap();
						}else if(val instanceof IA2Accessible){
							pMap =  IA2Accessible.propertyMap();
							pMap.putAll(MsaaAccessible.propertyMap());
						}else if(val instanceof MsaaAccessible){
							pMap = MsaaAccessible.propertyMap();
						}
					} else if (val instanceof IA2AccessibleElement){
						errMap = ((IA2AccessibleElement)val).errorCodeMap();
					}
					for(Object k: resultMap.keySet()){
						if(resultMap.get(k)==null){
							resultMap.put(k, errMap.get(k));
						}
					}
					if(!pMap.isEmpty()){
						Map newMap = new HashMap();
						Object[] kSet = pMap.keySet().toArray();
						for (int i=0; i< kSet.length; i++){
							newMap.put(pMap.get(kSet[i]), resultMap.get(kSet[i]));
						}
						newMap = PropertiesView.FILTER_NULLS? GuiUtils.filterNulls(newMap): newMap;
						result = new TreeMap(newMap).entrySet().toArray();
					}else{
						result = new TreeMap(resultMap).entrySet().toArray();
					}
				} catch (Exception e) {
					e.printStackTrace(System.err);
					result = new Object[0];
				}
			}
		}
		
		return result;
	}
	
	public Object getParent (Object element) {
		return null;
	}
	
	public boolean hasChildren (Object element) {
		boolean res = element instanceof ExplorerViewNode;
		if (!res && element instanceof Entry) {
			Object value = ((Entry) element).getValue();
			if(value!=null){
				Class<?> type = value.getClass();
				boolean isNonEmptyArray = type.isArray() && ((Object[]) value).length > 0;
				res = Map.class.isAssignableFrom(type) || isNonEmptyArray
					|| !(type.isArray() || GuiUtils.isPrimitive(type) || GuiUtils.hasAcceptableToString(type));
			}
		}else if( element instanceof TreeItem){
			res = ((TreeItem)element).getItemCount() >0;
		}
		
		return res;
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	//	viewer.setInput(newInput);
	}
	
	public Map getIndexedProperties(Object elem){
		Map iMap = new HashMap();
		//if(elem instanceof IAccessibleElement){
		//	return iMap;
		//}
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
}
