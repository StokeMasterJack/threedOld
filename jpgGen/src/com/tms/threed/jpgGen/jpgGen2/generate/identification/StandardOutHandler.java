package com.tms.threed.jpgGen.jpgGen2.generate.identification;

import com.tms.threed.imageModel.shared.Jpg;
import com.tms.threed.threedCore.shared.ThreedConfig;

public class StandardOutHandler implements JpgIdentifiedHandlerStrategy {

	protected ThreedConfig config = null;
	
	public StandardOutHandler(ThreedConfig config) {
		super();
		this.config = config;
	}

	@Override
	public void jpgIdentified(Jpg jpg) {
		System.out.println(jpg.getPath());
	}
}
