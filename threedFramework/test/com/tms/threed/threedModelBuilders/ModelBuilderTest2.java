package com.tms.threed.threedModelBuilders;

import com.tms.threed.featureModel.data.SampleFeatureSet;
import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedModel.server.ModelFactory;
import com.tms.threed.threedModel.server.SThreedModel;
import com.tms.threed.threedCore.shared.*;
import com.tms.threed.featureModel.server.FeatureModelBuilderXml;
import com.tms.threed.featureModel.shared.FeatureModel;
//import com.tms.threed.model.server.ModelFactory;
//import com.tms.threed.model.server.SModel;
import junit.framework.TestCase;

import java.util.Set;

public class ModelBuilderTest2 extends TestCase {

    private ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();
//    private Path jpgRootDir = threedConfig.getJpgRootFileSystem();

    public void testXmlFeatureModelBuilder() throws Exception {
        FeatureModelBuilderXml fmBuilder = new FeatureModelBuilderXml(threedConfig.getPngRootFileSystem());
        FeatureModel fm = fmBuilder.buildModel(SeriesKey.SIENNA_2011);
        fm.getRootVar().printVarTree();
    }

    public void test_Fixup2() throws Exception {
        ModelFactory modelBuilder = new ModelFactory(threedConfig);
        SThreedModel model = modelBuilder.createModel(SeriesKey.VENZA_2009);

        System.out.println("Before Fix ups: ");
        Set<String> userPicks = new SampleFeatureSet("2810, VENZA, 2.7L 4-Cyl, FE, 1F7, FWD, 6-Speed Auto - 4-Cyl , FA12");
        System.out.println(userPicks);

        model.fixupPicks(userPicks);
        System.out.println("After Fix ups: ");
        System.out.println(userPicks);
        System.out.println();
    }


}