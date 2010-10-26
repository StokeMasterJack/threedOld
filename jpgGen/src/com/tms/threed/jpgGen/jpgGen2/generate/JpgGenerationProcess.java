package com.tms.threed.jpgGen.jpgGen2.generate;

import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedModel.server.Repos;
import com.tms.threed.threedModel.shared.ThreedModel;

public class JpgGenerationProcess {

	protected ProcessStatus processStatus = ProcessStatus.AwaitingAnalysis;
	protected ProgressTracker analysisProgress = new ProgressTracker(this);
	protected ProgressTracker progressTracker = new ProgressTracker(this);
	protected long creationTime = System.currentTimeMillis();

	protected ThreedModel model = null;
    protected ThreedConfig threedConfig = null;
    protected SeriesKey seriesKey = null;
    protected long jpgsToMake = 0;


    
    public JpgGenerationProcess( ThreedConfig threedConfig, SeriesKey seriesKey )
    {
    	this.seriesKey = seriesKey;
        this.threedConfig = threedConfig;
        model =  Repos.createModel(seriesKey);
    }

	public ProcessStatus getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(ProcessStatus processStatus) {
		pukeIfCanceled();
		this.processStatus = processStatus;
	}


	public ThreedModel getModel() {
		pukeIfCanceled();
		return model;
	}


	public ProgressTracker getProgressTracker() {
		return progressTracker;
	}


	public void setProgressTracker(ProgressTracker progressTracker) {
		this.progressTracker = progressTracker;
	}

	public ThreedConfig getThreedConfig() {
		pukeIfCanceled();
		return threedConfig;
	}

	public SeriesKey getSeriesKey() {
		return seriesKey;
	}

	
	public long getJpgsToMake() {
		return jpgsToMake;
	}

	public void setJpgsToMake(long jpgsToMake) {
		pukeIfCanceled();
		this.jpgsToMake = jpgsToMake;
	}

	public ProgressTracker getAnalysisProgress() {
		return analysisProgress;
	}

	public void setAnalysisProgress(ProgressTracker analysisProgress) {
		pukeIfCanceled();
		this.analysisProgress = analysisProgress;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getSeriesKey()).append("\t");
		sb.append(getProcessStatus().name()).append("\t");
		sb.append(getAnalysisProgress().getComplete()).append("/");
		sb.append(getAnalysisProgress().getTotal()).append("=");		
		sb.append(getAnalysisProgress().getPercent()).append(" ");
		sb.append(getProgressTracker().getComplete()).append("/");
		sb.append(getProgressTracker().getTotal()).append("=");		
		sb.append(getProgressTracker().getPercent()).append(" (").append(getJpgsToMake() ).append(" jpgs)\n");
		return sb.toString();
	}

	public void pukeIfCanceled()
	{
		if( processStatus.equals(ProcessStatus.Canceled ))
			throw new ProcessCanceledException();
	}
	

}
	
