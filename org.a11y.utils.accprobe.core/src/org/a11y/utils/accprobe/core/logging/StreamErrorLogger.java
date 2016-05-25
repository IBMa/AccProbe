/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.logging;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


/**
 * an error logger for logging errors to an <code>java.io.OutputStream</code>
*
 * @author Mike Squillace
 */
public class StreamErrorLogger extends AbstractErrorLogger
{

	private PrintWriter _writer;

	private String _filename;

	/**
	 * create an error logger for writing to a stream
	 * 
	 * @param stream - stream to which errors are to be logged
	 */
	public StreamErrorLogger (OutputStream stream) {
		_writer = new PrintWriter(new OutputStreamWriter(stream), true);
	}

	/** {@inheritDoc} */
	public void logError (String msg, Throwable t) {
		if (msg != null) {
			_writer.println(msg);
		}
		if (t != null) {
			_writer.println(t.getClass().getName() + " - " + t.toString());
			t.printStackTrace();
			while ((t = t.getCause()) != null) {
				_writer.println(t.toString());
				t.printStackTrace();
			}
		}
	}

	/**
	 * return the filename associated with the stream
	 * 
	 * @return filename associated with stream or <code>null</code> if
	 * a filename was not set
	 * @see #setFilename(String)
	 */
	public String getFilename () {
		return _filename;
	}

	/**
	 * asociate a filename with the underlying stream with which this
	 * error logger was instantiated
	 * 
	 * @param name - filename
	 */
	public void setFilename (String name) {
		_filename = name;
	}

	protected void finalize () throws Throwable {
		_writer.close();
	}

	public String toString () {
		return getClass().getName() + "["
				+ (_filename == null ? "" : _filename) + "]";
	}
}
