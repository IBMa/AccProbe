/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.locate;

import java.util.Properties;

import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;
import org.a11y.utils.accprobe.core.model.locate.AbstractNodeLocator;


/**
 * general purpose locator for all instances of <code>IAccessibleElement</code>
 * 
 * @author Mike Squillace
 *
 */
public class AccessibleNodeLocator extends AbstractNodeLocator
{

	public AccessibleNodeLocator () {
		super("accessibleName", "msaa", new AccessibleNodePointerFactory());
	}

	/**
	 * identifies the specified <code>IAccessibleElement</code> with its accessibleName properties. 
	 * Locator properties are not read from any model-specific configuration file.
	 * 
	 * @param element element to be identified
	 * @return identification string for element, usually its accessibleName
	 */
	public Properties describe (Object element) {
		String name = null;
		Properties properties = new Properties();
		
		if (element instanceof IAccessibleElement) {
			IAccessibleElement acc = (IAccessibleElement) element;
			try {
				name = acc.getAccessibleName();
			}catch (InvalidComponentException e) {
			}
		}
		
		if (name == null || name.length() == 0) {
			name = "<None>";
		}
		properties.setProperty("accessibleName", name);
		return properties;
		
	}

}