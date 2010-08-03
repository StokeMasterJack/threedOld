package com.tms.threed.jpgGen;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.featureModelJavaBdd.BddBuilder;
import com.tms.threed.imageModel.shared.ImView;
import com.tms.threed.imageModel.shared.Jpg;
import com.tms.threed.javabdd.BDD;
import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedCore.shared.ViewKey;
import com.tms.threed.threedModel.server.SThreedModels;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.lang.shared.Path;
import junit.framework.TestCase;

import java.util.HashSet;

public class Test extends TestCase {

    ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();
    Path pngRoot = threedConfig.getPngRootFileSystem();
    Path jpgRoot = threedConfig.getJpgRootHttp();

    public void testSatAllWithPicksAndJpgsRav() throws Exception {

        SeriesKey seriesKey = SeriesKey.YARIS_2010;
        ThreedModel threedModel = SThreedModels.get().getModel(seriesKey).getModel();
        SeriesInfo seriesInfo = threedModel.getSeriesInfo();

        FeatureModel fm = threedModel.getFeatureModel();
        BddBuilder bddBuilder = new BddBuilder(fm);
        bddBuilder.buildBdd();

        long satCount = bddBuilder.satCount();
        System.out.println("satCount: " + satCount);

        ImView imView = threedModel.getView(seriesInfo.getExterior());

        imView = imView.copy(2);

        HashSet<String> set = new HashSet<String>();

        BDD.BDDIterator it = bddBuilder.iterator();
        int counter = 0;
        while (it.hasNext()) {
            boolean[] productAsBoolArray = it.next();
            Picks picks = new Picks(fm, productAsBoolArray);
            Jpg jpg = imView.getJpg(picks, ViewKey.HERO_ANGLE);
            set.add(jpg.getPath().toString());
            counter++;
            if(satCount % counter == 10000) System.out.println(counter + " of " + satCount  + "  jpgCount[" + set.size() + "]" );
        }

        System.out.println("Jpg count - exterior angle 2");


    }

    
    public void testSatAllWithPicksAndJpgsCamry() throws Exception {

        SeriesKey seriesKey = SeriesKey.CAMRY_2011;
        ThreedModel threedModel = SThreedModels.get().getModel(seriesKey).getModel();
        SeriesInfo seriesInfo = threedModel.getSeriesInfo();

        FeatureModel fm = threedModel.getFeatureModel();
        BddBuilder bddBuilder = new BddBuilder(fm);
        bddBuilder.buildBdd();

        long satCount = bddBuilder.satCount();
        System.out.println("satCount: " + satCount);

        ImView imView = threedModel.getView(seriesInfo.getExterior());

        imView = imView.copy(2);

        HashSet<String> set = new HashSet<String>();

        BDD.BDDIterator it = bddBuilder.iterator();
        int counter = 0;
        while (it.hasNext()) {
            boolean[] productAsBoolArray = it.next();
            Picks picks = new Picks(fm, productAsBoolArray);
            Jpg jpg = imView.getJpg(picks, ViewKey.HERO_ANGLE);
            set.add(jpg.getPath().toString());
            counter++;
            if(satCount % counter == 10000) System.out.println(counter + " of " + satCount  + "  jpgCount[" + set.size() + "]" ); 
        }

        System.out.println("Jpg count - exterior angle 2");


    }

    public void testSatAllWithPicksAndJpgsYaris() throws Exception {

        SeriesKey seriesKey = SeriesKey.YARIS_2010;
        ThreedModel threedModel = SThreedModels.get().getModel(seriesKey, true).getModel();
        SeriesInfo seriesInfo = threedModel.getSeriesInfo();

        FeatureModel fm = threedModel.getFeatureModel();
        BddBuilder bddBuilder = new BddBuilder(fm);
        bddBuilder.buildBdd();

        ImView imView = threedModel.getView(seriesInfo.getExterior());

        imView = imView.copy(ViewKey.HERO_ANGLE);

        HashSet<String> set = new HashSet<String>();

        System.out.println("bddBuilder.satCount() = [" + bddBuilder.satCount() + "]");
        System.out.println("bddBuilder.satAllCount() = [" + bddBuilder.iteratorCount() + "]");

        BDD.BDDIterator it = bddBuilder.iterator();
        while (it.hasNext()) {
            Picks picks = new Picks(fm,it.next());
            Jpg jpg = imView.getJpg(picks, ViewKey.HERO_ANGLE);
            set.add(jpg.getPath().toString());
        }

        System.out.println("jpgCount = [" + set.size() + "]");


    }
}
