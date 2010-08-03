package com.tms.threed.jpgGen.jpgGen2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.tms.threed.jpgGen.jpgGen2.generate.FileForProcess;
import com.tms.threed.jpgGen.jpgGen2.generate.JpgGenerationProcess;
import com.tms.threed.jpgGen.jpgGen2.generate.identification.ProcessKickoff;
import com.tms.threed.jpgGen.jpgGen2.generate.process.JavaIoProcessor;
import com.tms.threed.jpgGen.jpgGen2.generate.process.JpgCreator;
import com.tms.threed.jpgGen.jpgGen2.generate.process.JpgQueuer;
import com.tms.threed.jpgGen.jpgGen2.generate.process.ProcessMonitor;
import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;

public class JpgGenTest {


    public static void main(String[] args) {
    	   ThreedConfigHelper helper = ThreedConfigHelper.get();
           ThreedConfig threedConfig = helper.getThreedConfig();

           List<JpgGenerationProcess> monitorList = new ArrayList<JpgGenerationProcess>();
           BlockingQueue<JpgGenerationProcess> analyzeQueue = new LinkedBlockingQueue<JpgGenerationProcess>();
           BlockingQueue<JpgGenerationProcess> processQueue = new LinkedBlockingQueue<JpgGenerationProcess>();
           BlockingQueue<FileForProcess> workQueue = new LinkedBlockingQueue<FileForProcess>();
           List<FileForProcess> dlq = new ArrayList<FileForProcess>();
           //ProgressTracker progressTracker = new ProgressTracker();
           
           ProcessKickoff processKickoff = new ProcessKickoff(analyzeQueue, processQueue, monitorList, threedConfig);
           processKickoff.start();
           
           ProcessMonitor monitor = new ProcessMonitor(monitorList);
           monitor.start();
           
           JpgQueuer queuer = new JpgQueuer(threedConfig, processQueue, workQueue);
           queuer.start();
           
           //generates jpgs using imagemagick
           
           SeriesKey seriesKey = SeriesKey.AVALON_2011;
           
           if( args.length > 1 )
           {
        	   seriesKey = new SeriesKey(Integer.parseInt(args[1]), args[0]);
           }
           
           int processors = 5;
           
           if( args.length > 2 )
        	   processors = Integer.parseInt(args[2]);
           
           
           for( int i = 0; i < processors; i++ )
        	   addProcessor(workQueue, dlq);
           


           JpgGenerationProcess jpgGenerationProcess = new JpgGenerationProcess( threedConfig, seriesKey);
			
			monitorList.add(jpgGenerationProcess);
			analyzeQueue.add(jpgGenerationProcess);           
           
	}

	protected static void addProcessor(
			BlockingQueue<FileForProcess> workQueue, List<FileForProcess> dlq) {
		JpgCreator processor = new JpgCreator(  new JavaIoProcessor(), workQueue, dlq );
           processor.start();
	}


}