package com.tms.threed.imageModel.builders.server;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.imageModel.builders.shared.ImNodeBuilder;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.util.lang.shared.Path;
import com.tms.threed.util.vnode.shared.VNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImageModelBuilder {

    private final VNodeBuilder vNodeBuilder;
    private final ImNodeBuilder imNodeBuilder;

    public ImageModelBuilder(FeatureModel featureModel, SeriesInfo seriesInfo, Path pngRootFs) {
        vNodeBuilder = new VNodeBuilder(featureModel, seriesInfo, pngRootFs);
        imNodeBuilder = new ImNodeBuilder(featureModel, seriesInfo);
    }

    public ImSeries buildImageModel() {
        VNode seriesVNode = vNodeBuilder.buildSeriesVNode();
        assert seriesVNode.hasChildNodes();
        ImSeries series = imNodeBuilder.buildImageModel(seriesVNode);
        return series;
    }

    private static final Log log = LogFactory.getLog(ImageModelBuilder.class);

}
