/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.model;

/**
 * implementations retrieve the desired IModel based upon the
 * raven:model attribute value of the &lt;rulebase&gt; element.
 *
 * @author Mike Squillace
 */
public interface IModelFactory
{

	/**
	 * retrieve the desired IModel for the given model type.
	 *
	 * @param model -- model type
	 * @return model appropriate for the given type
	 */
	public IModel resolveModel (String model);
	
} // IModelFactory
