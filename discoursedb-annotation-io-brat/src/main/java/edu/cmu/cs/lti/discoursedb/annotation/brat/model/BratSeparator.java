package edu.cmu.cs.lti.discoursedb.annotation.brat.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BratSeparator {
	// Fill in with some context info about each contribution
	public static final String CONTRIB_SEPARATOR = "[****  USER: UUUUUUUUUUUU DATE: DDDDDDDDDDDDDDDDDDD TTTTTTTTTTTTTTTTTTTTTTTTT ****]";
	public static final SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	public static final int length = CONTRIB_SEPARATOR.length();
	
	static String fix_length(String item, int length) {
		if (item == null) { item = ""; }
		item = item.replace("\n", "");
		if (item.length() > length) { item = item.substring(0, length-3) + "..."; }
		if (item.length() < length) { item = String.format("%1$-" + length + "s",  item); } 
		return item;
	}
	private String sep = "";
	public BratSeparator(int depth, String username, String title, Date date) {
		if (depth > 99) { depth = 99; } if (depth < 0) { depth = 0; }
		username = fix_length(username, 12);
		
		String datestring = (date != null)?dt.format(date):"(unknown date)     ";
		title = fix_length(title, 25);
		
		sep = CONTRIB_SEPARATOR.
				replace("##", String.format("%02d", depth)).
				replace("DDDDDDDDDDDDDDDDDDD", datestring).
				replace("TTTTTTTTTTTTTTTTTTTTTTTTT", title).
				replace("UUUUUUUUUUUU",  username);
		sep = String.format("%1$-" + CONTRIB_SEPARATOR.length() + "s",  sep);
		sep = sep.substring(0, length);
		
		assert sep.length() == length;
	}
	
	public String get() { return sep; }
		
}
