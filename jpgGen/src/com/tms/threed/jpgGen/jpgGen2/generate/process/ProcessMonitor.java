package com.tms.threed.jpgGen.jpgGen2.generate.process;

import java.util.List;

import com.tms.threed.jpgGen.jpgGen2.generate.JpgGenerationProcess;

public class ProcessMonitor extends Thread {

	protected List<JpgGenerationProcess> monitorList = null;

	public ProcessMonitor(List<JpgGenerationProcess> monitorList) {
		this.monitorList = monitorList;
	}

	@Override
	public void run() {
	
		while( true )
		{
			try
			{
				for( JpgGenerationProcess process : monitorList )
				{
					System.out.println(process.toString());
				}
				
				sleep( 5000L );
			}
			catch( Throwable t )
			{
				t.printStackTrace();
				throw new RuntimeException(t);
			}

		}	
			
			
	
	}
	
}
