package com.tms.threed.featureModel.server;

import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.util.lang.shared.Path;

public abstract class FeatureModelFactory {
    
    protected final Path pngRoot;

    public FeatureModelFactory(Path pngRoot) {this.pngRoot = pngRoot;}

    public abstract boolean canCreate(SeriesKey seriesKey);

    public abstract FeatureModel createFeatureModel(SeriesKey seriesKey);
}
