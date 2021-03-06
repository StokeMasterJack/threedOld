package com.tms.threed.imageModel.server;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.UnknownVarCodeException;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.imageModel.shared.ImFeature;
import com.tms.threed.imageModel.shared.ImFeatureOrPng;
import com.tms.threed.imageModel.shared.ImLayer;
import com.tms.threed.imageModel.shared.ImNodeType;
import com.tms.threed.imageModel.shared.ImPng;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.imageModel.shared.ImView;
import com.tms.threed.threedCore.shared.SeriesId;
import com.tms.threed.util.vnode.shared.VNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImNodeBuilder {

    private final FeatureModel featureModel;
    private final VNode seriesVDir;
    private final SeriesId seriesId;
    private final File seriesDir;

    public ImNodeBuilder(FeatureModel featureModel, VNode seriesVDir,File seriesDir) {
        this.featureModel = featureModel;
        this.seriesVDir = seriesVDir;
        this.seriesId = featureModel.getSeriesId();
        this.seriesDir = seriesDir;
    }

    public ImSeries buildImageModel() {
        List<VNode> viewDirs = seriesVDir.getChildNodes();
        assert viewDirs != null;
        assert viewDirs.size() > 0;
        List<ImView> imViews = createImViewsFromSeriesDir(seriesVDir);
        return new ImSeries(seriesVDir.getDepth(), seriesId, imViews);
    }

    private List<ImView> createImViewsFromSeriesDir(VNode seriesDir) {
        List<VNode> viewDirs = seriesDir.getChildNodes();

        assert viewDirs != null;
        List<ImView> imViews = new ArrayList<ImView>();
        for (VNode viewDir : viewDirs) {
            ImView view = createImViewFromViewDir(viewDir);
            imViews.add(view);
        }


        return imViews;
    }

    private ImView createImViewFromViewDir(VNode viewDir) {
        String name = viewDir.getName();
        List<ImLayer> imLayers = createImLayersFromViewDir(viewDir);
        return new ImView(viewDir.getDepth(), name, imLayers);
    }

    private List<ImLayer> createImLayersFromViewDir(VNode viewDir) {
        List<VNode> layerDirs = viewDir.getChildNodes();

        if (layerDirs == null) {
            throw new IllegalStateException(viewDir.getName() + " has no child dirs");
        }

        List<ImLayer> imLayers = new ArrayList<ImLayer>();
        for (VNode layerDir : layerDirs) {
            ImLayer layer = createImLayerFromLayerDir(layerDir);
            imLayers.add(layer);
        }
        return imLayers;
    }

    private ImLayer createImLayerFromLayerDir(@Nonnull VNode layerDir) {
        String layerName = layerDir.getName();
        List<ImFeatureOrPng> featuresAndOrPngs = createFeaturesAndOrPngsFromDir(ImNodeType.LAYER_DIR, layerDir);
        return new ImLayer(layerDir.getDepth(), layerName, featuresAndOrPngs);
    }

    /**
     *
     * dirNode could be any valid png or feature parent: i.e. LayerDir or FeatureDir
     * dirNode's children will either be FeatureDir or Png
     *
     * if dirNode has no children, an empty list is returned
     *
     * @param
     * @return a list including Pngs and or FeatureDirs
     */
    @Nonnull
    private List<ImFeatureOrPng> createFeaturesAndOrPngsFromDir(ImNodeType nodeType, VNode layerDirOrFeatureDir) {
        List<ImFeatureOrPng> featuresAndOrPngs = new ArrayList<ImFeatureOrPng>();
        if (!layerDirOrFeatureDir.hasChildNodes()) {
            return featuresAndOrPngs;
        }
        List<VNode> childNodes = layerDirOrFeatureDir.getChildNodes();
        for (VNode vNode : childNodes) {
            if (vNode.isDirectory()) {
                //must be a FeatureDir
                ImFeature imFeature = createImFeatureFromFeatureDir(vNode);
                featuresAndOrPngs.add(imFeature);
            } else {
                //must be a png
                ImPng imPng = createImPngFromPngFile(vNode);
                featuresAndOrPngs.add(imPng);
            }
        }
        return featuresAndOrPngs;
    }

    public static final String VERSION_PREFIX = "vr_1_";
    public static final String PNG_SUFFIX = ".png";

    /**
     * vr_1_04.png
     * 04.png
     * 04
     */
    private ImPng createImPngFromPngFile(VNode pngVFile) {
        String name = pngVFile.getName();
        name = name.replace(VERSION_PREFIX, "");
        String sAngle = name.replace(PNG_SUFFIX, "");

        int angle;
        try {
            angle = Integer.parseInt(sAngle);
        } catch (NumberFormatException e) {
            System.out.println(pngVFile.getName());
            System.out.println(pngVFile.getPath());
            throw new RuntimeException(e);
        }

        File pngFile = new File(seriesDir.getParent(),pngVFile.getPath().toString());

        String fingerprint;
        try {
            fingerprint = ImageUtil.getFingerprint(pngFile);  //todo
            fingerprint = "s";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ImPng png = new ImPng(pngVFile.getDepth(), angle);
        png.setFingerprint(fingerprint);
        return png;
    }

    private ImFeature createImFeatureFromFeatureDir(VNode featureDir) {
        String featureCode = featureDir.getName();
        Var var = null;
        try {
            var = featureModel.getVar(featureCode);
        } catch (UnknownVarCodeException e) {
            System.out.println("Bad Feature Code im png folders");
        }
        List<ImFeatureOrPng> childNodes = createFeaturesAndOrPngsFromDir(ImNodeType.FEATURE_DIR, featureDir);
        return new ImFeature(featureDir.getDepth(), var, childNodes);
    }

    private static final Log log = LogFactory.getLog(ImNodeBuilder.class);

}
