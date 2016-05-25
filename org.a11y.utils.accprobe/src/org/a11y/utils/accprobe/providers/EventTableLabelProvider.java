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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.a11y.utils.accprobe.views.EventMonitorView;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;

public class EventTableLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	protected Table t;

	private SimpleDateFormat formatter;

	private String pattern = "H:mm:ss:SSS"; // 18:15:55:624

	public EventTableLabelProvider(Table t) {
		this.t = t;
		formatter = new SimpleDateFormat(pattern);
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		if (element instanceof EventRecord) {
			EventRecord record = (EventRecord) element;
			String columnHeader = t.getColumn(columnIndex).getText();
			if (columnHeader != null
					&& columnHeader.equals(EventMonitorView.NAME_COLUMN)) {
				result = record.getName();
			} else if (columnHeader != null
					&& columnHeader.equals(EventMonitorView.ROLE_COLUMN)) {
				result = record.getRole();
			} else if (columnHeader != null
					&& columnHeader.equals(EventMonitorView.STATE_COLUMN)) {
				result = record.getState();
			} else if (columnHeader != null
					&& columnHeader.equals(EventMonitorView.TYPE_COLUMN)) {
				result = record.getType();
			} else if (columnHeader != null
					&& columnHeader.equals(EventMonitorView.TIMESTAMP_COLUMN)) {
				long time = record.getTimestamp();
				result = formatter.format(new Date(time));
			} else if (columnHeader != null
					&& columnHeader.equals(EventMonitorView.MISC_DATA_COLUMN)) {
				result  = record.getMiscData();				
			}

		}
		return result;
	}
}
