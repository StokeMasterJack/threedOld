package com.tms.threed.threedModel.server;

import com.tms.threed.imageModel.server.ImageModels;
import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.SeriesId;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.lang.shared.Path;

import java.util.HashMap;

public class SThreedModels {

    private HashMap<SeriesId, ThreedModel> modelCache = new HashMap<SeriesId, ThreedModel>();

    private final ThreedConfig threedConfig;
    private final Path httpImageRoot;

    public SThreedModels(ThreedConfig threedConfig) {
        this.threedConfig = threedConfig;
        this.httpImageRoot = threedConfig.getThreedRootHttp();
    }

    synchronized public ThreedModel getModel(SeriesId seriesId) {
        return getModelInternal(seriesId);
    }

    synchronized public ThreedModel getModel(SeriesKey seriesKey, int version) {
        return getModelInternal(new SeriesId(seriesKey, version));
    }

    synchronized public ThreedModel getModel(int year, String seriesName, int version) {
        return getModelInternal(new SeriesId(year, seriesName, version));
    }

    synchronized public ThreedModel getModel(SeriesKey seriesKey) {
        return getModelInternal(currentId(seriesKey));
    }

    synchronized public ThreedModel getModel(int year, String seriesName) {
        return getModel(new SeriesKey(year, seriesName));
    }

    synchronized public ThreedModel getModel(String year, String seriesName) {
        return getModel(new SeriesKey(year, seriesName));
    }

    private SeriesRepo seriesRepo(SeriesKey seriesKey) {
        return new SeriesRepo(seriesKey, threedConfig);
    }

    private SeriesId currentId(SeriesKey seriesKey) {
        return seriesRepo(seriesKey).getCurrentSeriesId();
    }

    private ThreedModel getModelInternal(SeriesId seriesId) {
        ThreedModel model = modelCache.get(seriesId);
        if (model == null) {
            model = buildModel(seriesId);
            modelCache.put(seriesId, model);
        }
        return model;
    }

    public void resetCache() {
        modelCache.clear();
    }

    public ThreedConfig getThreedConfig() {
        return threedConfig;
    }

    private static SThreedModels instance;

    synchronized public static SThreedModels get() {
        if (instance == null) {
            ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();
            instance = new SThreedModels(threedConfig);
        }
        return instance;
    }

    private ThreedModel buildModel(SeriesKey seriesKey) {
        return new SeriesRepo(seriesKey, threedConfig).createModel(httpImageRoot);
    }

    private ThreedModel buildModel(SeriesId seriesId) {
        return buildModel(seriesId.getSeriesKey(), seriesId.getVersion());
    }

    private ThreedModel buildModel(SeriesKey seriesKey, int version) {
        return new SeriesRepo(seriesKey, threedConfig).createModel(version,httpImageRoot);
    }

    public ImageModels getImageModels() {
        return new ImageModels(threedConfig);
    }


}
