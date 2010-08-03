package com.tms.threed.jpgGen.jpgGen2.generate;

import java.util.Date;

public class LogEntry {
	protected long date = 0l;
	protected String message = null;
	
	public LogEntry(String message) {
		this.message = message;
		this.date = System.currentTimeMillis();
	}

	public Date getDate() {
		return new Date( date );
	}

	public String getMessage() {
		return message;
	}

}
