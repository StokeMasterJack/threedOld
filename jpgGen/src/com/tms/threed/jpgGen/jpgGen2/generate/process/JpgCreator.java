package com.tms.threed.jpgGen.jpgGen2.generate.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.tms.threed.jpgGen.jpgGen2.generate.FileForProcess;
import com.tms.threed.jpgGen.jpgGen2.generate.ProcessCanceledException;
import com.tms.threed.jpgGen.jpgGen2.generate.ProcessStatus;
import com.tms.threed.jpgGen.singleJpgGen.JpgSpec;

public class JpgCreator extends Thread {

	protected FileGenerationStrategy strategy = null;
	protected BlockingQueue<FileForProcess> work = null;
	protected List<FileForProcess> dlq = null;
	
	public JpgCreator(FileGenerationStrategy strategy,
			BlockingQueue<FileForProcess> work,
			List<FileForProcess> dlq) {
		this.strategy = strategy;
		this.work = work;
		this.dlq = dlq;
	}


	@Override
	public void run() {

		while( true )
		{
			FileForProcess unitOfWork = null;
			try
			{
				unitOfWork = work.take();
				
				File inputFile = unitOfWork.getFile();
				
				unitOfWork.getProcess().pukeIfCanceled();
				
				JpgSpec jpgSpec = buildJpgSpec(unitOfWork, inputFile);
				
				strategy.processFileForProcess(unitOfWork, jpgSpec);
				unitOfWork.getProcess().getProgressTracker().addComplete();
				
				renameInputFile(unitOfWork, inputFile);
			}
			catch( Throwable t )
			{
				if( ! (t instanceof ProcessCanceledException))
				{				
					unitOfWork.setAttemptCount(unitOfWork.getAttemptCount() + 1);
					
					if( unitOfWork.getAttemptCount() < 5 )
					{
						System.out.println("requeueing");
						try{ work.put(unitOfWork); } catch( Exception e ) {}
					}
					else
					{
						unitOfWork.getProcess().setProcessStatus(ProcessStatus.FileHadError);
						System.out.println("THIS FILE IS BAD!!! : " + unitOfWork.getFile().getAbsolutePath());
						unitOfWork.setThrowable(t);
						dlq.add(unitOfWork);
					}
				}
			}
		}
	
	}



	private void renameInputFile(FileForProcess unitOfWork, File inputFile) {
		String newFileName = ".__DONE" + unitOfWork.getFile().getName();
		File newFile = new File(inputFile.getParentFile(), newFileName );
		inputFile.renameTo(newFile);
	}



	protected JpgSpec buildJpgSpec(FileForProcess unitOfWork, File inputFile)
			throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader( new FileReader( inputFile ));
		
		String line = reader.readLine();
		
		File jpgFile = convertJpgPathToFile( unitOfWork, line );
		
		List<File> inputPngs = new ArrayList<File>();
		
		while( (line = reader.readLine()) != null )
		{
			inputPngs.add(convertPngPathToFile(unitOfWork, line));
		}
		
		JpgSpec jpgSpec = new JpgSpec(inputPngs, jpgFile, false);
		return jpgSpec;
	}
	
	protected File convertJpgPathToFile( FileForProcess ffp, String path)
	{
		return new File(ffp.getProcess().getThreedConfig().getJpgRootFileSystem() + path);
	}
	protected File convertPngPathToFile( FileForProcess ffp, String path)
	{
		return new File(ffp.getProcess().getThreedConfig().getPngRootFileSystem() + path);
	}
	
}
