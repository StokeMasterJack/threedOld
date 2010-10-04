package com.tms.threed.jpgGen.blink;

import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.imageModel.shared.ImFeature;
import com.tms.threed.imageModel.shared.ImFeatureOrPng;
import com.tms.threed.imageModel.shared.ImLayer;
import com.tms.threed.imageModel.shared.ImPng;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.imageModel.shared.ImView;
import com.tms.threed.jpgGen.ezProcess.AbstractCommandBuilder;
import com.tms.threed.jpgGen.ezProcess.EzProcess;
import com.tms.threed.threedModel.server.JsonMarshaller;
import com.tms.threed.threedModel.server.SThreedModels;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.lang.shared.Path;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlinkPngMaker {

    private ThreedConfig threedConfig;
    private Path pngRootPath;
    private File imageMagicHome;

    public BlinkPngMaker(ThreedConfig threedConfig, File imageMagicHome) {
        this.threedConfig = threedConfig;
        pngRootPath = threedConfig.getPngRootFileSystem();
        this.imageMagicHome = imageMagicHome;
    }

    public void generateBlinkPngs(SeriesKey seriesKey) throws Exception {
        SThreedModels models = SThreedModels.get();
        ThreedModel model = models.getModel(seriesKey);
        ImSeries imageModel = model.getImageModel();
        List<ImView> views = imageModel.getViews();
        for (ImView view : views) {
            generateBlinkPngs(view);
        }
    }

    public void generateBlinkPngs(ImView view) {
        System.out.println(view.getName());
        List<ImLayer> layers = view.getLayers();
        for (ImLayer layer : layers) {
            if (layer.isAccessory()) {
                generateBlinkPngs(layer);
            }
        }
    }

    public void generateBlinkPngs(ImLayer layer) {
        System.out.println("\t" + layer.getName());
        List<ImFeatureOrPng> featureOrPngList = layer.getChildNodes();
        for (ImFeatureOrPng node : featureOrPngList) {
            generateBlinkPngOrPngs(node);
        }
    }

    public void generateBlinkPngOrPngs(ImFeatureOrPng node) {
        System.out.println("\t\t" + node.getPath());
        if(node.isPng()) {
            generateBlinkPng((ImPng) node);
        }
        else if(node.isFeature()){
            ImFeature imFeature = (ImFeature) node;
            for (ImFeatureOrPng featureOrPng : imFeature.getChildNodes()) {
                generateBlinkPngOrPngs(node);
            }

        }
    }

    public void generateBlinkPng(ImPng png) throws EzProcess.EzProcessExecException {
        AbstractCommandBuilder commandBuilder = new ImageMagicBlinkCommandBuilder(imageMagicHome, threedConfig, png);
        log.debug("Command line to create blink:");
        log.debug("\t" + commandBuilder.commandToString());

        EzProcess ezProcess = new EzProcess(commandBuilder);
        ezProcess.executeSync(executorService);
    }

    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private static final Log log = LogFactory.getLog(BlinkPngMaker.class);

}
