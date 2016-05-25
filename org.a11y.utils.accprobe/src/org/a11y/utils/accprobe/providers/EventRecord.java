/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.providers;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Event Record - construct for the Event Monitor table
 * @author annford
 *
 */

public class EventRecord {
	
	private String name = "";

	private String role = "";

	private String state = "";

	private String type = "";
	
	private long timestamp;
	
	private String miscData = "";


	String pattern = "H:mm:ss:SSS"; // 18:15:55:624

	public EventRecord (String name, String role, String state,
						String type, long timestamp) {
		
		this.name = name;
		this.role = role;
		this.state = state;
		this.type = type;
		this.timestamp = timestamp;
	}

	public EventRecord () {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	public String getFormattedTimestamp() {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern); 
		return formatter.format(new Date(timestamp));
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getMiscData() {
		return miscData;
	}

	public void setMiscData(String miscData) {
		this.miscData = miscData;
	}
	
	public String toString () {
		StringBuffer sb = new StringBuffer();
		String separator = " ";
		sb.append("time = " + getFormattedTimestamp());
		sb.append(separator);
		sb.append("type = " + type);
		sb.append(separator);
		sb.append("name = " + name);
		sb.append(separator);
		sb.append("role = " + role);
		sb.append(separator);
		sb.append("state = " + state);
		sb.append(separator);
		sb.append("miscData = " + miscData);
		return sb.toString(); 
	}
	
}