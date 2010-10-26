package com.tms.threed.jpgGen.jpgGen2.generate.identification;

import java.io.File;
import java.io.FileWriter;

import com.tms.threed.imageModel.shared.ImPng;
import com.tms.threed.imageModel.shared.Jpg;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.lang.shared.Path;

//this class will take a jpg which has been identified as needing to be generated and 
//create a file describing the jpg.  That file will be picked up later during generation
public class ConfigFileGeneratingHandler implements
		JpgIdentifiedHandlerStrategy {

	protected ThreedConfig config = null;
	protected ThreedModel model = null;

	protected static volatile long jpgNumber = 0;

	public ConfigFileGeneratingHandler(ThreedConfig threedConfig, ThreedModel model) {
		this.config = threedConfig;
		this.model = model;
	}

	protected synchronized static long getNextJpgNumber() {
		jpgNumber++;
		return jpgNumber;
	}

	@Override
	public void jpgIdentified(Jpg jpg) {

		try {
			File f = makeSureWeHaveARootDirectory();

			//String fileName = jpg.getSHAFingerPrint();
			String fileName = jpg.getPath().toString().replaceAll("/", "_");

			File descriptor = new File(f, fileName);

			if( ! descriptor.exists() )
			{
				FileWriter fw = null;
				try {
					fw = new FileWriter(descriptor);
	
					fw.write(jpg.getPath().toString());
					fw.write("\n");
	
					for (ImPng png : jpg.getPngs()) {
						fw.write(png.getPath().toString());
						fw.write("\n");
					}
				} finally {
					fw.close();
				}
			}

		} catch (Exception wrapItUp) {
			throw new RuntimeException(wrapItUp);
		}
	}


	private File makeSureWeHaveARootDirectory() {
		Path jpgRootFileSystem = config.getJpgRootFileSystem();

		SeriesKey key = model.getSeriesKey();

		File f = new File(jpgRootFileSystem.toString(), new Integer(key
				.getYear()).toString());

		if (!f.isDirectory()) {
			f.mkdir();
		}

		f = new File(f, key.getName());

		if (!f.isDirectory()) {
			f.mkdir();
		}

		f = new File(f, "descriptors");

		if (!f.isDirectory()) {
			f.mkdir();
		}
		
		return f;
	}
}
