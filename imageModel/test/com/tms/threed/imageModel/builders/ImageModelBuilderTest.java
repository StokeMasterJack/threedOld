package com.tms.threed.imageModel.builders;

import com.tms.threed.featureModel.data.Camry2011;
import com.tms.threed.featureModel.server.FeatureModelBuilderXml;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.imageModel.builders.server.ImageModelBuilder;
import com.tms.threed.imageModel.builders.server.VNodeBuilder;
import com.tms.threed.imageModel.shared.ImFeature;
import com.tms.threed.imageModel.shared.ImFeatureOrPng;
import com.tms.threed.imageModel.shared.ImLayer;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.imageModel.shared.ImView;
import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesInfoBuilder;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.util.lang.shared.Path;
import com.tms.threed.util.vnode.shared.VNode;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageModelBuilderTest extends TestCase {

    ThreedConfig threedConfig;
    Path pngRoot;
    Camry2011 featureModel;
    SeriesInfo seriesInfo;

    @Override protected void setUp() throws Exception {
        threedConfig = ThreedConfigHelper.get().getThreedConfig();
        pngRoot = threedConfig.getPngRootFileSystem();
        featureModel = new Camry2011();
        seriesInfo = SeriesInfoBuilder.createSeriesInfo(SeriesKey.CAMRY_2011);
    }

    public void test_VNodeBuilder() throws Exception {
        VNodeBuilder builder = new VNodeBuilder(featureModel, seriesInfo, pngRoot);
        VNode seriesNode = builder.buildSeriesVNode();
        Assert.assertEquals(0, seriesNode.getDepth());
    }

    public void test_ImNodeBuilder() throws Exception {
        SeriesInfo seriesInfo = SeriesInfoBuilder.createSeriesInfo(SeriesKey.CAMRY_2011);
        ImageModelBuilder imageModelBuilder = new ImageModelBuilder(featureModel, seriesInfo, pngRoot);
        ImSeries imSeries = imageModelBuilder.buildImageModel();
    }

    public void testPrintFeaturesPerAngleReport() throws Exception {
        SeriesKey seriesKey = SeriesKey.TACOMA_2011;
        FeatureModel fm = new FeatureModelBuilderXml(pngRoot).buildModel(seriesKey);
        SeriesInfo seriesInfo = SeriesInfoBuilder.createSeriesInfo(seriesKey);
        ImageModelBuilder imageModelBuilder = new ImageModelBuilder(fm, seriesInfo, pngRoot);
        ImSeries imSeries = imageModelBuilder.buildImageModel();
        imSeries.printFeaturesPerAngleReport();

//        imSeries.printTree();
    }

    void displayTree(ImSeries imSeries) {

        List<ImView> views = imSeries.getViews();
        for (ImView view : views) {
            System.out.println(view.getDepth() + ":" + view.getName());
            List<ImLayer> layers = view.getLayers();
            for (ImLayer layer : layers) {
                System.out.println("\t" + layer.getDepth() + ":" + layer.getName());
                List<ImFeatureOrPng> imFeatureOrPngList = layer.getChildNodes();
                for (ImFeatureOrPng fp : imFeatureOrPngList) {
                    System.out.println("\t\t" + fp.getDepth() + ":" + fp.getName());

                    if (fp.isFeature()) {
                        ImFeature f = (ImFeature) fp;
                        List<ImFeatureOrPng> childNodes = f.getChildNodes();
                        for (ImFeatureOrPng node : childNodes) {
                            System.out.println("\t\t\t" + node.getDepth() + ":" + node.getName());
                        }
                    }
                }

            }
        }
    }

    private static final Log log = LogFactory.getLog(ImageModelBuilderTest.class);


}
