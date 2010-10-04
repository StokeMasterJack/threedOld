package com.tms.threed.threedModelBuilders;

import com.tms.threed.featureModel.data.SampleFeatureSet;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedModel.server.JsonMarshaller;
import com.tms.threed.threedModel.server.Repos;
import com.tms.threed.threedModel.server.SeriesRepo;
import com.tms.threed.threedModel.shared.ThreedModel;
import junit.framework.TestCase;

import java.util.Set;

public class ModelBuilderTest2 extends TestCase {

    private ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();
//    private Path jpgRootDir = threedConfig.getJpgRootFileSystem();

    public void testXmlFeatureModelBuilder() throws Exception {
        FeatureModel fm = seriesRepo(SeriesKey.SIENNA_2011).createFeatureModel();
        fm.getRootVar().printVarTree();
    }

    private SeriesRepo seriesRepo(SeriesKey seriesKey) {
        return new SeriesRepo(seriesKey, threedConfig);
    }

    public void test_Fixup2() throws Exception {
        ThreedModel model = Repos.createModel(SeriesKey.VENZA_2009);

        System.out.println("Before Fix ups: ");
        Set<String> userPicks = new SampleFeatureSet("2810, VENZA, 2.7L 4-Cyl, FE, 1F7, FWD, 6-Speed Auto - 4-Cyl , FA12");
        System.out.println(userPicks);

        model.fixupPicks(userPicks);
        System.out.println("After Fix ups: ");
        System.out.println(userPicks);
        System.out.println();
    }


}