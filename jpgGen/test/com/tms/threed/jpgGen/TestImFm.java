package com.tms.threed.jpgGen;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModelJavaBdd.BddBuilder;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.imageModel.shared.imfm.ImFm2;
import com.tms.threed.javabdd.BDD;
import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedModel.server.SThreedModels;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.lang.shared.Path;
import junit.framework.TestCase;

public class TestImFm extends TestCase {

    ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();
    Path pngRoot = threedConfig.getPngRootFileSystem();
    Path jpgRoot = threedConfig.getJpgRootHttp();
    private ImSeries im;
    private FeatureModel fm;
    private ThreedModel threedModel;
    private BddBuilder bddBuilder;


    @Override protected void setUp() throws Exception {
        SeriesKey seriesKey = SeriesKey.TACOMA_2011;
        threedModel = SThreedModels.get().getModel(seriesKey);


    }

    public void test() throws Exception {
        im = threedModel.getImageModel();
        ImFm2 imFm = new ImFm2(im);
        bddBuilder = new BddBuilder(imFm.getFm());
        bddBuilder.buildBddLeafOnly();
        BDD bdd = bddBuilder.getBdd();

        System.out.println(bdd.satCount());

//        BDD.BDDIterator it = bdd.iterator(im.get);
//
//        int counter = 0;
//        while (it.hasNext()) {
//            boolean[] product = it.next();
//            counter++;
//        }
//
//        System.out.println("counter = [" + counter + "]");

    }



}