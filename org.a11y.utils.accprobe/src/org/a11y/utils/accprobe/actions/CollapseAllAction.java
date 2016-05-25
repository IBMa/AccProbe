/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.actions;

import org.a11y.utils.accprobe.providers.ExplorerViewNode;
import org.a11y.utils.accprobe.views.ExplorerView;
import org.a11y.utils.accprobe.views.PropertiesView;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.TreeItem;

public class CollapseAllAction extends AbstractAction {

	public CollapseAllAction() {
		super();
	}

	public void run(IAction action) {
		if (view instanceof ExplorerView) {
			ExplorerView ev = (ExplorerView) view;
			TreeItem[] items = ev.getTreeViewer().getTree().getItems();
			for (int i=0;i<items.length;i++){
				Object obj = items[i].getData();
				if(obj instanceof ExplorerViewNode){
					ev.getTreeViewer().collapseToLevel(((ExplorerViewNode)obj).getRoot(), 3);
				}
			}
		} else if (view instanceof PropertiesView) {
			((PropertiesView) view).getTreeViewer().collapseAll();
		}
	}

}
