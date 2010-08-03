package com.tms.threed.jpgGen.jpgGen2.generate;

public enum ProcessStatus {
	AwaitingAnalysis, Analyzing, Analyzed, Queueing, Generating, Completed, GenerationFilesNotFound, FileHadError, Canceled;
	
	
	public boolean isTerminal()
	{
		return this.equals(ProcessStatus.Completed) 
		|| this.equals(ProcessStatus.FileHadError)
		|| this.equals(ProcessStatus.Canceled)
		|| this.equals(ProcessStatus.GenerationFilesNotFound);
	}
}
