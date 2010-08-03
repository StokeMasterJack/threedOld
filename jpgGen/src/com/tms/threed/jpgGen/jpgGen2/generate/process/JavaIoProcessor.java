package com.tms.threed.jpgGen.jpgGen2.generate.process;

import com.tms.threed.jpgGen.jpgGen2.generate.FileForProcess;
import com.tms.threed.jpgGen.singleJpgGen.JpgGeneratorPureJava;
import com.tms.threed.jpgGen.singleJpgGen.JpgSpec;

public class JavaIoProcessor implements FileGenerationStrategy {
	
	@Override
	public void processFileForProcess(FileForProcess ffp, JpgSpec jpgSpec) {
		 JpgGeneratorPureJava jpgGeneratorPureJava = new JpgGeneratorPureJava(jpgSpec);
		 jpgGeneratorPureJava.generate();		
	}
}
