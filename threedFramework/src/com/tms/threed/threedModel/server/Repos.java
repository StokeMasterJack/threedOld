package com.tms.threed.threedModel.server;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.SeriesId;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedModel.shared.ThreedModel;

import java.io.File;

public class Repos {

    private static ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();

    public static SeriesRepo getSeriesRepo(SeriesKey seriesKey) {
        return new SeriesRepo(seriesKey, threedConfig);
    }

    public static File getSeriesDir(SeriesKey seriesKey) {
        return getSeriesRepo(seriesKey).getRepoDir();
    }

    public static int getCurrentVersion(SeriesKey seriesKey) {
        return getSeriesRepo(seriesKey).getCurrentVersion();
    }

    public static SeriesId getCurrent(SeriesKey seriesKey) {
        return getSeriesRepo(seriesKey).getCurrentSeriesId();
    }

    public static FeatureModel createFeatureModel(SeriesKey seriesKey) {
        return getSeriesRepo(seriesKey).createFeatureModel();
    }

    public static ImSeries createImageModel(SeriesKey seriesKey, FeatureModel fm) {
        return getSeriesRepo(seriesKey).createImageModel(fm);
    }

    public static ThreedModel createModel(SeriesKey seriesKey) {
        return getSeriesRepo(seriesKey).createModel(threedConfig.getThreedRootHttp());
    }

    public static ThreedModel createModel(SeriesKey seriesKey, int version) {
        return getSeriesRepo(seriesKey).createModel(version, threedConfig.getThreedRootHttp());
    }

    public static boolean useNewConfigurator(SeriesKey seriesKey) {
        return getSeriesRepo(seriesKey).useNewConfigurator();
    }

}
