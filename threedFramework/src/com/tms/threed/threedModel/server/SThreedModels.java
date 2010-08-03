package com.tms.threed.threedModel.server;

import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.imageModel.server.SImageModels;
import com.tms.threed.threedCore.shared.SeriesKey;

import java.util.HashMap;

public class SThreedModels {

    private HashMap<SeriesKey, SThreedModel> modelCache = new HashMap<SeriesKey, SThreedModel>();

    private final ThreedConfig threedConfig;
    private final ModelFactory modelBuilder;

    public SThreedModels(ThreedConfig threedConfig) {
        this.threedConfig = threedConfig;
        modelBuilder = new ModelFactory(threedConfig);
    }

    synchronized public SThreedModel getModel(SeriesKey seriesKey,boolean allowJavaFmConfig) {
        return getModelInternal(seriesKey,allowJavaFmConfig);
    }

    synchronized public SThreedModel getModel(SeriesKey seriesKey) {
        return getModelInternal(seriesKey,false);
    }

    synchronized public SThreedModel getModel(String modelYear, String seriesName) {
        return getModelInternal(new SeriesKey(modelYear, seriesName),false);
    }

    synchronized public SThreedModel getModel(int modelYear, String seriesName) {
        return getModelInternal(new SeriesKey(modelYear, seriesName),false);
    }

    private SThreedModel getModelInternal(SeriesKey seriesKey,boolean allowJavaFmConfig) {
        SThreedModel model = modelCache.get(seriesKey);
        if (model == null) {
            model = buildModel(seriesKey,allowJavaFmConfig);
            modelCache.put(seriesKey, model);
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

    private SThreedModel buildModel(SeriesKey seriesKey,boolean allowJavaFmConfig){
        return modelBuilder.createModel(seriesKey,allowJavaFmConfig);
    }

    public SImageModels getImageModels() {
        return new SImageModels(threedConfig);
    }


}
