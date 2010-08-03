package com.tms.threed.jpgGen.jpgGen2.generate;

import java.io.File;

public class FileForProcess {
	protected JpgGenerationProcess process = null;
	protected File file = null;
	protected int attemptCount = 0;
	protected Throwable throwable = null; 
	
	
	public FileForProcess(JpgGenerationProcess process, File file) {
		super();
		this.process = process;
		this.file = file;
	}
	public JpgGenerationProcess getProcess() {
		return process;
	}
	public File getFile() {
		return file;
	}
	public int getAttemptCount() {
		return attemptCount;
	}
	public void setAttemptCount(int attemptCount) {
		this.attemptCount = attemptCount;
	}
	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	
}
