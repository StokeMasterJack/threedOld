package com.tms.threed.jpgGen.jpgGen2.generate.identification;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.featureModelJavaBdd.BddBuilder;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.imageModel.shared.ImView;
import com.tms.threed.imageModel.shared.Jpg;
import com.tms.threed.javabdd.BDD;
import com.tms.threed.javabdd.BDDVarSet;
import com.tms.threed.jpgGen.jpgGen2.PreAndPostProcessor;
import com.tms.threed.jpgGen.jpgGen2.generate.JpgGenerationProcess;
import com.tms.threed.jpgGen.jpgGen2.generate.ProcessCanceledException;
import com.tms.threed.jpgGen.jpgGen2.generate.ProcessStatus;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedCore.shared.ViewKey;

public class ProcessKickoff extends Thread {

	protected BlockingQueue<JpgGenerationProcess> work = null;
	protected BlockingQueue<JpgGenerationProcess> processQueue = null;
	protected ThreedConfig threedConfig = null;

	public ProcessKickoff(BlockingQueue<JpgGenerationProcess> work,
			BlockingQueue<JpgGenerationProcess> processQueue,
			List<JpgGenerationProcess> monitorList, ThreedConfig threedConfig) {
		this.work = work;
		this.processQueue = processQueue;
		this.threedConfig = threedConfig;
	}

	@Override
	public void run() {

		while (true) {
			try {

				JpgGenerationProcess jpgGenerationProcess = work.take();

				jpgGenerationProcess.setProcessStatus(ProcessStatus.Analyzing);
				
				JpgIdentifiedHandlerStrategy identifier = new ConfigFileGeneratingHandler(
						threedConfig, jpgGenerationProcess.getModel());

				FeatureModel featureModel = jpgGenerationProcess.getModel()
						.getFeatureModel();

				//long t1 = System.currentTimeMillis();
					quickRunning(jpgGenerationProcess, identifier, featureModel);
				

				//long t2 = System.currentTimeMillis();
				//System.out.println("quick = " + (t2 -t1));
				//	longRunning(jpgGenerationProcess, identifier, featureModel);
				
				//System.out.println("quick = " + (t2 -t1) + " slow" + (System.currentTimeMillis() - t2));
				

			} catch (Throwable t) {
				if( ! (t instanceof ProcessCanceledException))
					t.printStackTrace();
			}
		}

	}

	
	protected void quickRunning(JpgGenerationProcess jpgGenerationProcess,
			JpgIdentifiedHandlerStrategy identifier, FeatureModel featureModel)
			throws InterruptedException {
		BddBuilder bddBuilder = new BddBuilder(featureModel);
		bddBuilder.buildBddLeafOnly();
		
		BDD bdd = bddBuilder.getBdd();
		
		SeriesInfo seriesInfo = jpgGenerationProcess.getModel().getModel().getSeriesInfo();
		
		ViewKey[] viewsKeys = seriesInfo.getViewsKeys();
		
		
		int totalAnalysisWork = 0;
		for( ViewKey viewKey : viewsKeys )
		{
			for( int angle = 1; angle <= viewKey.getAngleCount(); angle++ )
			{
				totalAnalysisWork++;
			}
		}
		
		jpgGenerationProcess.getAnalysisProgress().setTotal(totalAnalysisWork);
		
		long totalResultingJpgs = 0;
		for( ViewKey viewKey : viewsKeys )
		{
			for( int angle = 1; angle <= viewKey.getAngleCount(); angle++ )
			{
				ImView imView = jpgGenerationProcess.getModel().getModel().getView(viewKey).copy(angle);

				BDDVarSet bddVarSet = bddBuilder.getBddVarSet(imView.getVarSet());

		        HashSet<Jpg> set = new HashSet<Jpg>();
		        BDD.BDDIterator it = bdd.iterator(bddVarSet);
		        while (it.hasNext()) {
		            boolean[] productAsBoolArray = it.next();
		            Picks picks = bddBuilder.boolArrayToPicks(productAsBoolArray);
		            Jpg jpg = imView.getJpg(picks, angle);
		            set.add(jpg);
		        }
		        
		        totalResultingJpgs += set.size();
		        jpgGenerationProcess.setJpgsToMake(totalResultingJpgs);
		        
				for( Jpg jpg : set)
				{
					identifier.jpgIdentified(jpg);
				}
				
				jpgGenerationProcess.getAnalysisProgress().addComplete();
		 
			}
		}

		
		if( totalResultingJpgs > 0 )
		{
			jpgGenerationProcess.setProcessStatus(ProcessStatus.Queueing);
			processQueue.put(jpgGenerationProcess);	
		}
		else
		{
			jpgGenerationProcess.setProcessStatus(ProcessStatus.Completed);
		}		        
	}
	
	
	protected void longRunning(JpgGenerationProcess jpgGenerationProcess,
			JpgIdentifiedHandlerStrategy identifier, FeatureModel featureModel)
			throws InterruptedException {
		BddBuilder bddBuilder = new BddBuilder(featureModel);
		bddBuilder.buildBdd();
		

		jpgGenerationProcess.getAnalysisProgress().setTotal(bddBuilder.satCount());

		ImSeries im = jpgGenerationProcess.getModel().getImageModel();
		
		BDD.BDDIterator it = bddBuilder.iterator();
		int counter = 0;
		
		Set<Jpg> jpgs = new HashSet<Jpg>();
		
		while (it.hasNext()) {
			boolean[] product = it.next();
		    Picks picks = new Picks(featureModel,product);

			for (ImView currentView : im.getViews()) {
				for (int currentAngle : currentView.getViewKey()
						.getAngleValues()) {
					Jpg jpg = currentView.getJpg(picks, currentAngle);
					jpgs.add(jpg);
					jpgGenerationProcess.setJpgsToMake(jpgs.size());
				}
			}
			counter++;
			jpgGenerationProcess.getAnalysisProgress().addComplete();
			
		}
		jpgGenerationProcess.setProcessStatus(ProcessStatus.Queueing);
		
		for( Jpg jpg : jpgs)
		{
			identifier.jpgIdentified(jpg);
		}
		
		if( jpgs.size() > 0 )
		{
			processQueue.put(jpgGenerationProcess);	
		}
		else
		{
			jpgGenerationProcess.setProcessStatus(ProcessStatus.Completed);
		}
	}
}
