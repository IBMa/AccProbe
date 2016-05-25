/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation.
*
*
*
 *
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.a11y.utils.accprobe.core.model.traverse.filters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.a11y.utils.accprobe.core.model.IModel;


/*
	 * public boolean isIgnorable(Object comp){
	 *   if (ignorableClassNamePatterns.isEmpty()) {
	 *   	String ids = getModelProperty(Configuration.MODEL_IGNOREIDS);
	 *   	String types = getModelProperty(Configuration.MODEL_IGNORENODENAMES);
	 *   	StringTokenizer regExpList = types == null ? null : new StringTokenizer(types);
	 *   	while (regExpList != null && regExpList.hasMoreTokens()) {
	 *   		ignorableClassNamePatterns.add(Pattern.compile(regExpList.nextToken()));
	 *   	}
	 * 	 	regExpList = ids == null ? null : new StringTokenizer(ids);
	 *   	while (regExpList != null && regExpList.hasMoreTokens()){
	 *      	ignorableIDPatterns.add(Pattern.compile(regExpList.nextToken()));
	 *      }
	 *   }
	 *   boolean matched = false;
	 *   if (comp != null) {
	 *   	Iterator iter = ignorableClassNamePatterns.iterator();
	 *    	String name = comp.getClass().getName();
	 *    	while (!matched & iter.hasNext()) {
	 *     		matched = ((Pattern) iter.next()).matcher(name).matches();
	 *    	}
	 *    	iter = ignorableIDPatterns.iterator();
	 *    	name = getNodeID(comp);
	 *    	while (!matched && iter.hasNext()) {
	 *     		matched = ((Pattern)iter.next()).matcher(name).matches();
	 *    	}
	 *   }
	 *  return matched;
	 * }
	 */
	 
	 
public class NodeTypeFilter implements INodeFilter {

	protected IModel model = null;
	protected List<Pattern> ignoreTypes = new ArrayList<Pattern>();

	public NodeTypeFilter(IModel model, String types) {
		this.model = model;
		StringTokenizer regExpList = types == null ? null : new StringTokenizer(types);
	   	while (regExpList != null && regExpList.hasMoreTokens()) {
			 ignoreTypes.add(Pattern.compile(regExpList.nextToken()));
		}
	}

	public boolean pass(Object node) {
		boolean matched = false;
		if (node != null && model != null) {
		   	Iterator<Pattern> iter = ignoreTypes.iterator();
		   	String name = model.getTypeName(model.getNodeName(node));
		   	while (!matched & iter.hasNext()) {
		      		matched = ((Pattern) iter.next()).matcher(name).matches();
		   	}
		 }
		 return !matched;
	}
	
}
