/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.logging;

import java.io.PrintStream;


/**
 * general logger for writing to the console
*
 * @author Mike Squillace
 */
public class ConsoleErrorLogger extends AbstractErrorLogger
{

	private PrintStream _stream;

	/**
	 * create a console error logger that writes to <code>System.err</code>
	 * 
	 *
	 */
	public ConsoleErrorLogger () {
		this(System.err);
	}

	/**
	 * create an error logger for writing to the console
	 * 
	 * @param stream either <code>System.out</code> or <code>System.err</code>
	 */
	public ConsoleErrorLogger (PrintStream stream) {
		_stream = stream;
	}

	/** {@inheritDoc} */
	public void logError (String msg, Throwable t) {
		if (msg != null) {
			_stream.println(msg);
		}
		if (t != null) {
			_stream.println(t.getClass().getName() + " - " + t.toString());
			t.printStackTrace(_stream);
			while ((t = t.getCause()) != null) {
				_stream.println(t.getClass().getName() + " - " + t.toString());
				t.printStackTrace(_stream);
			}
		}
	}

}
