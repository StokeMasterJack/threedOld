package com.tms.threed.jpgGen.jpgGen2.generate.process;

import java.io.File;
import java.util.concurrent.BlockingQueue;

import com.tms.threed.jpgGen.jpgGen2.generate.FileForProcess;
import com.tms.threed.jpgGen.jpgGen2.generate.JpgGenerationProcess;
import com.tms.threed.jpgGen.jpgGen2.generate.ProcessCanceledException;
import com.tms.threed.jpgGen.jpgGen2.generate.ProcessStatus;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.util.lang.shared.Path;

public class JpgQueuer extends Thread {

	protected ThreedConfig config = null;
	protected BlockingQueue<JpgGenerationProcess> processQueue = null;
	protected BlockingQueue<FileForProcess> workQueue = null;


	public JpgQueuer(ThreedConfig config,
			BlockingQueue<JpgGenerationProcess> processQueue,
			BlockingQueue<FileForProcess> workQueue) {
		this.config = config;
		this.processQueue = processQueue;
		this.workQueue = workQueue;
	}

	@Override
	public void run() {
	
		while( true )
		{
			try
			{
				JpgGenerationProcess process = processQueue.take();
				System.out.println("queueing up files: " + process.getSeriesKey() );
				
				File descriptorsFolder = getDescriptorsFolder( process );
				
				if( descriptorsFolder == null )
				{
					process.setProcessStatus(ProcessStatus.GenerationFilesNotFound);
					return;
				}
		
				File[] toProcess = descriptorsFolder.listFiles();
				
				if( toProcess.length == 0 )
				{
					process.setProcessStatus(ProcessStatus.GenerationFilesNotFound);
					return;
				}
				
				int totalWork = 0;
				
				for( File file : toProcess )
				{
					if( ! file.getName().startsWith(".__DONE"))
					{
						FileForProcess unitOfWork = new FileForProcess(process, file);
						workQueue.put(unitOfWork);
						
						totalWork++;	
					}
				}
				process.setProcessStatus(ProcessStatus.Generating);
				process.getProgressTracker().setTotal(totalWork);
					
			}
			catch( Throwable t )
			{
				if( ! (t instanceof ProcessCanceledException))
					t.printStackTrace();
			}

		}	
			
			
	
	}
	
	protected File getDescriptorsFolder( JpgGenerationProcess process )
	{
		Path jpgRootFileSystem = config.getJpgRootFileSystem();
		SeriesKey key = process.getModel().getSeriesKey();
		File f = new File(jpgRootFileSystem.toString(), new Integer(key
				.getYear()).toString());

		if (!f.isDirectory()) {
			return null;
		}

		f = new File(f, key.getName());

		if (!f.isDirectory()) {
			return null;
		}

		f = new File(f, "descriptors");

		if (!f.isDirectory()) {
			return null;
		}		
		return f;
	}
	
}
