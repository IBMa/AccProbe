/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.logging;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * All parameters for the trace stream and level are also set
 * and maintained by this class
 *
 * @author Mike Squillace
 */
public final class LoggingUtil
{

	
	/**
	 * Logger names for the main plugins
	 */
	public static final String ACCSERVICE_LOGGER_NAME = "org.a11y.utils.accprobe.accservice";
	public static final String A11Y_CORE_LOGGER_NAME = "org.a11y.utils.accprobe.core";
	public static final String A11Y_VALIDATION_LOGGER_NAME = "org.a11y.validation";
	public static final String A11Y_JAVACO_LOGGER_NAME = "org.a11y.javaco";
	public static final String A11Y_WEBELO_LOGGER_NAME = "org.a11y.webelo";
	public static final String A11Y_UI_LOGGER_NAME = "org.a11y.ui";

	protected LoggingUtil () {
	}


	
	/**
	 * print the members of the given array.
	 * This method is typically used in the trace by methods that wish to
	 * output the list of parameters given to a constructor
	 * or method.
	 *
	 * @param logger -- the Logger instance to use
	 * @param level -- level at which to print
	 * @param params -- parameters to be printed
	 */
	public static void printParams(Logger logger, Object[] params) {
		if (logger != null) {
			logger.log(Level.FINE, "With parameters:");
			for (int p = 0; params != null && p < params.length; ++p) {
				logger
						.log(Level.FINE, (params[p] == null ? "" : params[p]
								.getClass().getName())
								+ ":"
								+ params[p]
								+ (p < params.length - 1 ? ", " : ""));
			}
		}

	} // printParams

	/**
	 * converts the first character of the given string to upper case
	 *
	 * @param str -- a string
	 * @return str with its first character converted to upper case
	 */
	public static String firstCharToUpper (String str) {
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	public static String arrayAsString (String[] str) {
		StringBuffer sb = new StringBuffer();
		for (int a = 0; a < str.length; ++a) {
			sb.append(str[a]);
			if (a < str.length - 1) {
				sb.append('\n');
			}
		}
		return sb.toString();
	}

	/**
	 * retrieve the root exception of the given Throwable
	 * @param t - throwable
	 * @return root exception of t or t if t has no cause
	 */
	public static Throwable getRootException (Throwable t) {
		Throwable root = t;
		while ((t = t.getCause()) != null) {
			root = t;
		}
		return root;
	}

	/**
	 * Replace XML specific characters with entities.
	 * @param s text to fix
	 */
	public static String escape (String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\"') {
				sb.append("&quote;");
			}else if (c == '\'') {
				sb.append("&apos;");
			}else if (c == '&') {
				sb.append("&amp;");
			}else if (c == '<') {
				sb.append("&lt;");
			}else if (c == '>') {
				sb.append("&gt;");
			}else if (c < ' ' || c > 0x7E) {
				sb.append("&#");
				String xi = Integer.toHexString(c);
				if (xi.length() % 2 != 0) {
					sb.append("0");
				}
				sb.append(xi);
				sb.append(";");
			}else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
} // Utils
