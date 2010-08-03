package com.tms.jpggenerator.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.tms.threed.jpgGen.jpgGen2.generate.FileForProcess;
import com.tms.threed.jpgGen.jpgGen2.generate.JpgGenerationProcess;
import com.tms.threed.jpgGen.jpgGen2.generate.identification.ProcessKickoff;
import com.tms.threed.jpgGen.jpgGen2.generate.process.JavaIoProcessor;
import com.tms.threed.jpgGen.jpgGen2.generate.process.JpgCreator;
import com.tms.threed.jpgGen.jpgGen2.generate.process.JpgQueuer;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.util.lang.shared.Path;

public class ContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
 	   ThreedConfig threedConfig = getThreedConfig(arg0);

       List<JpgGenerationProcess> monitorList = new ArrayList<JpgGenerationProcess>();
       BlockingQueue<JpgGenerationProcess> analysisQueue = new LinkedBlockingQueue<JpgGenerationProcess>();
       List<FileForProcess> dlq = new ArrayList<FileForProcess>();
       BlockingQueue<FileForProcess> workQueue = new LinkedBlockingQueue<FileForProcess>();
       BlockingQueue<JpgGenerationProcess> processQueue = new LinkedBlockingQueue<JpgGenerationProcess>();
       
       
       arg0.getServletContext().setAttribute("monitorList", monitorList);
       arg0.getServletContext().setAttribute("analysisQueue", analysisQueue);
       arg0.getServletContext().setAttribute("dlq", dlq);
       arg0.getServletContext().setAttribute("threedConfig", threedConfig);
       arg0.getServletContext().setAttribute("workQueue", workQueue);
       arg0.getServletContext().setAttribute("processQueue", processQueue);
       
       
       ProcessKickoff processKickoff = new ProcessKickoff(analysisQueue, processQueue, monitorList, threedConfig);
       processKickoff.setDaemon(true);
       processKickoff.start();
       
       processKickoff = new ProcessKickoff(analysisQueue, processQueue, monitorList, threedConfig);
       processKickoff.setDaemon(true);
       processKickoff.start();       
       
       JpgQueuer queuer = new JpgQueuer(threedConfig, processQueue, workQueue);
       queuer.setDaemon(true);
       queuer.start();
       
       queuer = new JpgQueuer(threedConfig, processQueue, workQueue);
       queuer.setDaemon(true);
       queuer.start();
       
       
       addProcessors(arg0, workQueue, dlq);

	}

	private ThreedConfig getThreedConfig(ServletContextEvent arg0) {
		String fileSystem = arg0.getServletContext().getInitParameter(
				"threedRootFileSystem");
		String http = arg0.getServletContext().getInitParameter(
				"threedRootHttp");

		return new ThreedConfig(new Path(http), new Path(fileSystem), null,
				null);
	}

	protected void addProcessors(ServletContextEvent arg0,
			BlockingQueue<FileForProcess> workQueue,
			List<FileForProcess> dlq) {
		String workersString = arg0.getServletContext().getInitParameter("workers");
		   int workers = 5;
		   try
		   {
			   workers = Integer.parseInt(workersString);
		   }
		   catch( Exception nothingWillWork )
		   {
			   System.out.println("\"workers\", if provided to the jpg generator as an init parameter in web.xml, must be an integer.  Defaults to 5");
			   throw new RuntimeException(nothingWillWork);
		   }
		   
		   
		   for( int i = 0; i < workers; i++ )
		   {
			   JpgCreator jpgCreator = new JpgCreator(  new JavaIoProcessor(), workQueue, dlq );
			   jpgCreator.setDaemon(true);
			   jpgCreator.start();
		   }
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}
	
}
