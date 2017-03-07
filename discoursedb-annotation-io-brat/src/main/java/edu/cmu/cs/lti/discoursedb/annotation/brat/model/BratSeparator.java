package edu.cmu.cs.lti.discoursedb.annotation.brat.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BratSeparator {
	// Fill in with some context info about each contribution
	public static final String CONTRIB_SEPARATOR = "[****  USER: UUUUUUUUUUUU  DATE: DDDDDDDDDDDDDDDDDDD  ****]";
	public static final SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	public static final int length = CONTRIB_SEPARATOR.length();
	
	private String sep = "";
	public BratSeparator(int depth, String username, Date date) {
		if (depth > 99) { depth = 99; } if (depth < 0) { depth = 0; }
		username = username.replace("\n", "");
		if (username.length() > 12) { username = username.substring(0, 9) + "..."; }
		if (username.length() < 12) { username = String.format("%1$-12s",  username); } 
		
		String datestring = (date != null)?dt.format(date):"(unknown date)     ";
		
		sep = CONTRIB_SEPARATOR.
				replace("##", String.format("%02d", depth)).
				replace("DDDDDDDDDDDDDDDDDDD", datestring).
				replace("UUUUUUUUUUUU",  username);
		sep = String.format("%1$-" + CONTRIB_SEPARATOR.length() + "s",  sep);
		sep = sep.substring(0, length);
		
		assert sep.length() == length;
	}
	
	public String get() { return sep; }
		
}
