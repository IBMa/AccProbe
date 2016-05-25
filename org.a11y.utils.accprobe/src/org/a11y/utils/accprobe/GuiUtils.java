/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;


public final class GuiUtils {

	protected static Activator plugin = Activator.getDefault();

	public static abstract class BaseSelectionAdapter extends SelectionAdapter {

		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	}

	private static List<Class<?>> ACCEPTABLE_TOSTRING_TYPES = new LinkedList<Class<?>>();
	static {
		ACCEPTABLE_TOSTRING_TYPES.add(java.util.Map.class);
		ACCEPTABLE_TOSTRING_TYPES.add(java.util.Set.class);
		ACCEPTABLE_TOSTRING_TYPES.add(java.awt.Rectangle.class);
		ACCEPTABLE_TOSTRING_TYPES.add(java.awt.Color.class);
		ACCEPTABLE_TOSTRING_TYPES.add(java.awt.Point.class);
	}
	

	protected GuiUtils() {
	}

	protected static boolean enableToolTips = true;

	public static boolean getEnableToolTips() {
		boolean res = enableToolTips;
		String s = System
				.getProperty("org.a11y.utils.accprobe.core.main.enableToolTips");
		if (s != null) {
			res = s.equalsIgnoreCase("true");
		}
		return res;
	}

	public static void setEnableToolTips(boolean enable) {
		enableToolTips = enable;
	}

	protected static void setValue(Widget c, String meth, Class<?> argType,
			Object value) {
		try {
			Method m = c.getClass().getMethod(meth, new Class<?>[] { argType });
			Object[] oa = (Object[]) Array.newInstance(argType, 1);
			oa[0] = value;
			m.invoke(c, oa);
		} catch (NoSuchMethodException nme) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static final Class<?>[] selType = { SelectionListener.class };

	public static final String ID_KEY = "org.a11y.validation.ui.Widget_ID";

	public static String getControlId(Widget c) {
		return (String) c.getData(ID_KEY);
	}

	public static void setControlId(Widget c, String id) {
		c.setData(ID_KEY, id);
	}

	/**
	 * Configures a Widget
	 * 
	 * @param c
	 *            Widget to configure
	 * @param id
	 *            id value to give to widget via setData; should be unique per
	 *            GUI (at a minimum, per Composite). Highly recommended. Uses
	 *            key "org.a11y.validation.ui.Widget_ID"
	 * @param ld
	 *            LayoutData sets the layout data the the control. Optional
	 * @param tooltip
	 *            Text for the tool tip; defaults to text if missing. Optional
	 * @param image
	 *            Image for the control. Optional.
	 * @param text
	 *            Text for the control. Optional.
	 * @param enabled
	 *            Sets the enabled state. Default true.
	 * 
	 */
	public static void configureControl(Widget c, String id, Object ld,
			String tooltip, Image image, String text, boolean enabled) {
		if (id != null) {
			setControlId(c, id);
		}
		if (ld != null) {
			if (c instanceof Control) {
				((Control) c).setLayoutData(ld);
			} else {
				throw new IllegalArgumentException(
						"only controls support layout data");
			}
		}
		if (text != null) {
			setValue(c, "setText", String.class, text);
			if (tooltip == null) {
				tooltip = text;
			}
		}
		if (image != null) {
			setValue(c, "setImage", Image.class, image);
		}
		if (enableToolTips) {
			if (tooltip != null) {
				while (tooltip.endsWith(" ")) {
					tooltip = tooltip.substring(0, tooltip.length() - 1);
				}
				if (tooltip.endsWith(":")) {
					tooltip = tooltip.substring(0, tooltip.length() - 1);
				}
				setValue(c, "setToolTipText", String.class, tooltip);
			}
		}
		setValue(c, "setEnabled", Boolean.class, enabled ? Boolean.TRUE
				: Boolean.FALSE);
	}

	public static void configureControl(Widget c, String id, Object ld,
			String tooltip, Image image, String text) {
		configureControl(c, id, ld, tooltip, image, text, true);
	}

	public static void configureControl(Widget c, String id, Object ld,
			String tooltip, String text) {
		configureControl(c, id, ld, tooltip, null, text);
	}

	public static void configureControl(Widget c, String id, Object ld,
			String tooltip, String text, boolean enabled) {
		configureControl(c, id, ld, tooltip, null, text, enabled);
	}

	public static void configureControl(Widget c, String id, Object ld,
			String tooltip, Image image) {
		configureControl(c, id, ld, tooltip, image, null);
	}

	public static void configureControl(Widget c, String id, Object ld,
			String tooltip, Image image, boolean enabled) {
		configureControl(c, id, ld, tooltip, image, null, enabled);
	}

	public static void configureControl(Widget c, String id, Object ld,
			String tooltip) {
		configureControl(c, id, ld, tooltip, null, null);
	}

	public static void configureControl(Widget c, String id, Object ld,
			String tooltip, boolean enabled) {
		configureControl(c, id, ld, tooltip, null, null, enabled);
	}

	public static void configureControl(Widget c, String id, String tooltip,
			Image image, String text) {
		configureControl(c, id, null, tooltip, image, text, true);
	}

	public static void configureControl(Widget c, String id, String tooltip,
			String text) {
		configureControl(c, id, null, tooltip, null, text);
	}

	public static void configureControl(Widget c, String id, String tooltip,
			String text, boolean enabled) {
		configureControl(c, id, null, tooltip, null, text, enabled);
	}

	public static void configureControl(Widget c, String id, String tooltip,
			Image image) {
		configureControl(c, id, null, tooltip, image, null);
	}

	public static void configureControl(Widget c, String id, String tooltip,
			Image image, boolean enabled) {
		configureControl(c, id, null, tooltip, image, null, enabled);
	}

	public static void configureControl(Widget c, String id, String tooltip) {
		configureControl(c, id, null, tooltip, null, null);
	}

	public static void configureControl(Widget c, String id, String tooltip,
			boolean enabled) {
		configureControl(c, id, null, tooltip, null, null, enabled);
	}

	public static void configureControl(Widget c, String text) {
		configureControl(c, (String) null, (String) null, text, true);
	}

	public static boolean isPrimitive (Class<?> type) {
		return String.class.isAssignableFrom(type) || Character.class.isAssignableFrom(type)
			|| Number.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)
			|| type.equals(Integer.TYPE)
			|| type.equals(Short.TYPE)
			|| type.equals(Long.TYPE)
			|| type.equals(Byte.TYPE)
			|| type.equals(Boolean.TYPE)
			|| type.equals(Float.TYPE)
			|| type.equals(Double.TYPE)
			|| type.equals(Character.TYPE);
	}

	public static boolean hasAcceptableToString (Class<?> type) {
		boolean found = ACCEPTABLE_TOSTRING_TYPES.contains(type);
		if (!found) {
			for (Iterator<Class<?>> iter = ACCEPTABLE_TOSTRING_TYPES.iterator(); !found & iter.hasNext(); ) {
				found = iter.next().isAssignableFrom(type);
			}
		}
		
		return found;
	}
	
	public static Map<String,?> filterNulls (Map<String,?> map) {
		Set<String> removeSet = new HashSet<String>();
		for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); ) {
			String key = iter.next();
			if (map.get(key) == null) {
				removeSet.add(key);
			}
		}
		for (Iterator<String> iter = removeSet.iterator(); iter.hasNext(); ) {
			map.remove(iter.next());
		}
		return map;
	}

}
