package com.tms.threed.jpgGen.jpgGen2.generate;

public class ProgressTracker {
	protected  volatile long complete = 0;
	protected volatile long total = 0;
	protected JpgGenerationProcess parent = null;
	
	public ProgressTracker( JpgGenerationProcess parent )
	{
		this.parent = parent;
	}
	
	public long getComplete() {
		return complete;
	}
	public synchronized long addComplete() {
		parent.pukeIfCanceled();
		return ++complete;
	}
	public void setTotal(long total) {
		parent.pukeIfCanceled();
		this.total = total;
	}
	
	public long getTotal() {
		return total;
	}
	public double getPercent()
	{
		return (100F) * (double) complete / (double) total;
	}
}
