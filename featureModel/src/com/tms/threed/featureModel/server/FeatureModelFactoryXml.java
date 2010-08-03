package com.tms.threed.featureModel.server;

import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.util.lang.shared.Path;

public class FeatureModelFactoryXml extends FeatureModelFactory {

    public FeatureModelFactoryXml(Path pngRoot) {
        super(pngRoot);
    }

    @Override public boolean canCreate(SeriesKey seriesKey) {
        return FeatureModelBuilderXml.checkModelXmlFile(pngRoot, seriesKey) != null;
    }

    @Override public FeatureModel createFeatureModel(SeriesKey seriesKey) {
        FeatureModelBuilderXml b = new FeatureModelBuilderXml(pngRoot);
        return b.buildModel(seriesKey);
    }

}
