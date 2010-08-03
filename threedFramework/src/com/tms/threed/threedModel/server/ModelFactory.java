package com.tms.threed.threedModel.server;

import com.tms.threed.featureModel.server.FeatureModelFactory;
import com.tms.threed.featureModel.server.FeatureModelFactoryJava;
import com.tms.threed.featureModel.server.FeatureModelFactoryXml;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.imageModel.builders.server.ImageModelBuilder;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesInfoBuilder;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.lang.shared.Path;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ModelFactory {

    private final ThreedConfig threedConfig;

    private final FeatureModelFactoryJava jFeatureModelFactory;
    private final FeatureModelFactory xFeatureModelFactory;

    public ModelFactory(ThreedConfig threedConfig) {
        this.threedConfig = threedConfig;
        jFeatureModelFactory = new FeatureModelFactoryJava(threedConfig.getPngRootFileSystem());
        xFeatureModelFactory = new FeatureModelFactoryXml(threedConfig.getPngRootFileSystem());
    }

    public SThreedModel createModel(int year, String name) {
        return createModel(year, name, false);
    }

    public SThreedModel createModel(int year, String name, boolean allowJavaFmConfig) {
        return createModel(new SeriesKey(year, name), allowJavaFmConfig);
    }

    public SThreedModel createModel(SeriesKey seriesKey) {
        return createModel(seriesKey, false);
    }

    public SThreedModel createModel(SeriesKey seriesKey, boolean useJavaFmConfig) {

        FeatureModel featureModel;

        /*
         *
         * This is a short-lived hack. We know for certain
         * that (as of 7/12/2011) the
         * currently deployed Avalon and
         * Camry *java-based* feature models are
         * consistent with the
         * currently deployed PNGs and JPGs.
         *
         * When we redo the PNGs/JPGs for Avalon/Camry
         * that would be the correct time
         * to ensure the XML-based FMs are consistent with
         * the PNGs and JPGs and discontinue use of
         * the Java-based FMs.
         *
         */
        if (seriesKey.isa(SeriesKey.CAMRY) || seriesKey.isa(SeriesKey.AVALON)) {
            useJavaFmConfig = true;
        }

        if (useJavaFmConfig && jFeatureModelFactory.canCreate(seriesKey)) {
            log.warn("Using JFeatureModel for " + seriesKey);
            featureModel = jFeatureModelFactory.createFeatureModel(seriesKey);
        } else if (xFeatureModelFactory.canCreate(seriesKey)) {
            featureModel = xFeatureModelFactory.createFeatureModel(seriesKey);
        } else {
            throw new IllegalStateException("Unable to create FeatureModel for [" + seriesKey + "]");
        }

        SeriesInfo seriesInfo = SeriesInfoBuilder.createSeriesInfo(seriesKey);

        Path pngRootFs = threedConfig.getPngRootFileSystem();
        ImageModelBuilder imageModelFactory = new ImageModelBuilder(featureModel, seriesInfo, pngRootFs);

        ImSeries imageModel = imageModelFactory.buildImageModel();

        ThreedModel model = new ThreedModel(threedConfig, featureModel, imageModel);
        return new SThreedModel(model);
    }

    private static final Log log = LogFactory.getLog(ModelFactory.class);

}
