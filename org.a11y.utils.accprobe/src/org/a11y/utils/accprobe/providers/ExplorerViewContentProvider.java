/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.providers;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.a11y.utils.accprobe.Activator;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.core.model.DefaultModelFactory;
import org.a11y.utils.accprobe.core.model.IModel;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;
import org.a11y.utils.accprobe.core.model.traverse.ITreeNodeWalker;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

public class ExplorerViewContentProvider implements ILazyTreeContentProvider
{

	protected TreeViewer treeViewer;

	public ExplorerViewContentProvider() {
	}

	public void updateElement (Object parent, int index) {
				Object element = null;
				ExplorerViewNode node = null, newNode = null;

		if (parent instanceof ExplorerViewNode) {
			node = (ExplorerViewNode) parent;
			element = node.getUnderlyingComponent();
			if (element != null) {
				ITreeNodeWalker tw = getTreeWalker(element);
				Logger logger = Logger.getLogger(Activator.PLUGIN_ID);
				try {
					Object child = tw.getChild(element, index);
					if (child != null) {
						newNode = new ExplorerViewNode(child, node);
						treeViewer.replace(parent, index, newNode);
						// TODO should add getChildCount to IModel interface
						if (child instanceof IAccessibleElement) {
							int count =  ((IAccessibleElement) child).getAccessibleChildCount();
							if ( count >1000){
								count =0;
							}
							treeViewer.setChildCount(newNode, count);
						}
					}
				} catch (InvalidComponentException e) {
					logger.log(Level.WARNING, "Component " + element + " is invalid - no children available");
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Unable to retrieve child at index " + index + " for component", e);
				}
			}
		} else if (parent instanceof ArrayList<?>) {
			ArrayList<?> inputList = (ArrayList<?>) parent;
			Object root = ((ExplorerViewNode) inputList.get(index)).getUnderlyingComponent();
			newNode = new ExplorerViewNode(root, null);
			treeViewer.replace(parent, index, newNode);
			if (root instanceof IAccessibleElement) {
				try {
					treeViewer.setChildCount(newNode, ((IAccessibleElement) root).getAccessibleChildCount());
				} catch (InvalidComponentException e) {
				}
			}
		}
	}

	public void updateChildCount (Object element, int currentChildCount) {
		int count = 0;
		if (element instanceof ArrayList<?>) {
			count = ((ArrayList<?>) element).size();
		} else if (element instanceof ExplorerViewNode) {
			Object elem = ((ExplorerViewNode) element).getUnderlyingComponent();
			if (elem instanceof IAccessibleElement) {
				try {
					count = ((IAccessibleElement) elem).getAccessibleChildCount();
					if( count > 1000){
						count =0;
					}
				} catch (InvalidComponentException e) {
				}
			}
		}
		treeViewer.setChildCount(element, count);
	}
	
	public Object getParent(Object comp) {
		return comp instanceof ExplorerViewNode ? ((ExplorerViewNode) comp).getParent() : null;
	}

	private ITreeNodeWalker getTreeWalker(Object element) {
		ITreeNodeWalker tw = null;
		IModel model = DefaultModelFactory.getInstance()
				.resolveModel(element.getClass());
		
		if (model != null) {
			tw = (ITreeNodeWalker) model.getNodeWalker();
		}
		
		return tw;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		treeViewer = (TreeViewer) viewer;
	}
	
}
