/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.providers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.a11y.utils.accprobe.Activator;
import org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2Accessible;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessible;
import org.a11y.utils.accprobe.core.model.DefaultModelFactory;


public class ExplorerViewNode
{

	private Object element = null;
	private ExplorerViewNode parent = null;
	private ExplorerViewNode root = null;
	private String modelType = null;
	

	/**
	 * This class provides a generic wrapper class for all GUI components
	 * displayed in the Explorer View
	 * 
	 * @param element --
	 *            the gui component to be wrapped
	 */
	public ExplorerViewNode(Object element, ExplorerViewNode parent) {
		if (element != null) {
			this.element = element;
			this.parent = parent;
			if(parent==null){
				setRoot(this);
			}else{
				setRoot(parent.root);
			}
			try {
				modelType = DefaultModelFactory.getInstance()
					.resolveModel(element.getClass()).getName();
				} catch (IllegalArgumentException iae) {
				Logger.getLogger(Activator.PLUGIN_ID).log(Level.SEVERE, iae.getMessage(), iae);
			}
		} else {
			throw new NullPointerException("ExplorerViewNode constructor element cannot be null");
		}
	}

	public Object getUnderlyingComponent() {
		return element;
	}
	
	public ExplorerViewNode getParent () {
		return parent;
	}

	/**
	 * 
	 * @return one of the pre-defined model types (Configuration.<modeltype>_MODEL
	 */
	public String getModelType() {
		return modelType;
	}
	
	public boolean equals (Object o) {
		boolean result = o instanceof ExplorerViewNode;
		if (result) {
			result = this.getUnderlyingComponent()
				.equals(((ExplorerViewNode) o).getUnderlyingComponent());
		}
		return result;
	}
	
	public int hashCode() {
		return getUnderlyingComponent().hashCode();
	}

	public ExplorerViewNode getRoot() {
		return root;
	}

	public void setRoot(ExplorerViewNode root) {
		this.root = root;
	}
	
	public PropertyGroup[] getAllPropertyGroups (){
		PropertyGroup[] pGroup =  {	new PropertyGroup("accservice"),
				new PropertyGroup("msaa"),
				new PropertyGroup("ia2")};
			return pGroup;
	}
	
	public PropertyGroup[] getPropertyGroups (){		
		if(element instanceof IA2Accessible ){	
			PropertyGroup[] pGroup =  {	new PropertyGroup("accservice"),
				new PropertyGroup("msaa"),
				new PropertyGroup("ia2")};
			return pGroup;
		}
		else
		if(element instanceof MsaaAccessible ){	
			PropertyGroup[] pGroup =  {	new PropertyGroup("accservice"),
					new PropertyGroup("msaa")};
				return pGroup;
		}
		return null;
	}
}

