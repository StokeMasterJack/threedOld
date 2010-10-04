package com.tms.threed.imageModel.server;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.util.vnode.shared.VNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

public class ImageModelBuilder {

    private final FeatureModel featureModel;
    private final File seriesDir;

    public ImageModelBuilder(FeatureModel featureModel, File seriesDir) {
        this.featureModel = featureModel;
        this.seriesDir = seriesDir;
    }

    public ImSeries buildImageModel() {
        VNodeBuilder vNodeBuilder = new VNodeBuilder(featureModel, seriesDir);
        VNode seriesVDir = vNodeBuilder.buildSeriesVNode();
        assert seriesVDir.hasChildNodes();
        ImNodeBuilder imNodeBuilder = new ImNodeBuilder(featureModel, seriesVDir,seriesDir);
        return imNodeBuilder.buildImageModel();
    }

    private static final Log log = LogFactory.getLog(ImageModelBuilder.class);

}
