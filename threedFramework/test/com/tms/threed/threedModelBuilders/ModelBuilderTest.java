package com.tms.threed.threedModelBuilders;

import com.tms.threed.featureModel.data.SampleFeatureSet;
import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.imageModel.shared.ImPng;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.imageModel.shared.ImView;
import com.tms.threed.imageModel.shared.Jpg;
import com.tms.threed.threedModel.server.ModelFactory;
import com.tms.threed.threedModel.server.SThreedModel;
import com.tms.threed.threedCore.shared.Angle;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesInfoBuilder;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ViewKey;
import com.tms.threed.util.lang.shared.Path;
import junit.framework.TestCase;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelBuilderTest extends TestCase {

    private static ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();
    Path jpgRootDir = threedConfig.getJpgRootFileSystem();


    private SeriesKey seriesKey = SeriesKey.SIENNA_2011;
    private SeriesInfo seriesInfo = SeriesInfoBuilder.createSeriesInfo(seriesKey);

    private Angle HERO = seriesInfo.getExterior().getHeroAngle();


    public void test_GetPngsExterior() throws Exception {
        ModelFactory modelBuilder = new ModelFactory(threedConfig);
        SThreedModel model = modelBuilder.createModel(seriesKey);

        ImSeries imageModel = model.getImageModel();
        FeatureModel fm = model.getFeatureModel();
        ImView view = imageModel.getExteriorView();

        System.out.println("Input: ");

//        Set<String> userPicks = new SampleFeatureSet();
        Set<String> userPicks = new SampleFeatureSet("Base, FE, 6-Speed Automatic, 17W5, 3.5-Liter V6, 7PSGR, FA14, Pole, StdRearViewMirror, 040, 2WD, FWD, Sienna 7-Passenger, 5328, V6");


        System.out.println("\tUser Picks: " + userPicks);

        ViewKey viewKey = view.getViewKey();
        Angle angle = viewKey.getHeroAngle();
        System.out.println("\tAngle: " + angle);
        System.out.println();


        System.out.println("Intermediate: ");
        Picks picks = model.fixupPicks(userPicks);
        System.out.println("\tUser Picks (After FixUp):  " + userPicks);


        for (String pick : userPicks) {
            System.out.println("pick = [" + pick + "]");
        }
        System.out.println();

        final List<ImPng> pngs = view.getPngs(picks, angle.angleValue);
        System.out.println("Output: ");

        System.out.println("\tPNG Layer Files:");
        int i = 0;
        for (ImPng png : pngs) {
            System.out.println("\t\t" + i + ": " + png);
            i++;
        }

        System.out.println("\tJPG File:");
        Jpg jpg = view.getJpg(picks, angle.angleValue);
        System.out.println("\t\t" + jpg);
        System.out.println(exists(jpg.getPath(jpgRootDir)));

    }

    public void test_GetPngsExterior2() throws Exception {
        ModelFactory modelBuilder = new ModelFactory(threedConfig);
        SThreedModel model = modelBuilder.createModel(2011, "Sienna");


        ImView view = model.getImageModel().getExteriorView();
        System.out.println("LC: " + view.getLayerCount());

        System.out.println("Input: ");
        Set<String> userPicks = new SampleFeatureSet("Base,42,FC,040,17W,2WD,FF14,L4, 5302, 6AT");
        System.out.println("\tUser Picks: " + userPicks);

        System.out.println("\tAngle: " + HERO);
        System.out.println();


        System.out.println("Intermediate: ");
        Picks picks = model.fixupPicks(userPicks);
        System.out.println("\tUser Picks (After FixUp):  " + userPicks);
        System.out.println();

        final List<ImPng> pngs = view.getPngs(picks, HERO.angleValue);
        System.out.println("Output: ");

        System.out.println("\tPNG Layer Files:");
        int i = 0;
        for (ImPng png : pngs) {
            System.out.println("\t\t" + i + ": " + png);
            i++;
        }

        System.out.println("\tJPG File:");
        Jpg jpg = view.getJpg(picks, HERO.angleValue);
        System.out.println("\t\t" + jpg);
        System.out.println(exists(jpg.getPath(jpgRootDir)));

    }


    public void test_GetPngsExterior3() throws Exception {
        ModelFactory modelBuilder = new ModelFactory(threedConfig);
        SThreedModel model = modelBuilder.createModel(SeriesKey.CAMRY_2011);


        FeatureModel fm = model.getFeatureModel();

        ImSeries imageModel = model.getImageModel();
        ImView view = imageModel.getExteriorView();


//        if(true) return;
        System.out.println("Input: ");

        //"2513", "040", "13", "CF", "WB"
        Set<String> picksRaw = new SampleFeatureSet("2513, 13, 040, CF,WB");

        System.out.println("\t picksRaw: " + picksRaw);

        ViewKey viewKey = view.getViewKey();
        Angle angle = viewKey.getHeroAngle();
        System.out.println("\tAngle: " + angle.angleValue);
        System.out.println();


        System.out.println("Intermediate: ");
        Picks picksFixed = model.fixupPicks(picksRaw);
        System.out.println("\t picksFixed:  " + picksFixed);


        final List<ImPng> pngs = view.getPngs(picksFixed, angle.angleValue);
        System.out.println("Output: ");

        System.out.println("\tPNG Layer Files:");
        int i = 0;
        for (ImPng png : pngs) {
            System.out.println("\t\t" + i + ": " + png);
            i++;
        }

        System.out.println("\tJPG File:");
        Jpg jpg = view.getJpg(picksFixed, angle.angleValue);
        Path jpgPath = jpg.getPath(jpgRootDir);
        System.out.println(jpgPath);

        System.out.println("Exists: " + exists(jpgPath));

    }

    private boolean exists(Path mp) {
        File f = new File(mp.toString());
        return f.exists();
    }


    public void test_GetPngsInterior() throws Exception {
        ModelFactory modelBuilder = new ModelFactory(threedConfig);
        SThreedModel model = modelBuilder.createModel(2009, "Venza");

        ImView view = model.getImageModel().getInteriorView();

        System.out.println("Input: ");
        final HashSet<String> userPicks = new SampleFeatureSet();
        System.out.println("\tUser Picks: " + userPicks);

        System.out.println("\tAngle: " + HERO);
        System.out.println();


        System.out.println("Intermediate: ");
        Picks picks = model.fixupPicks(userPicks);
        System.out.println("\tUser Picks (After FixUp):  " + userPicks);
        System.out.println();

        final List<ImPng> pngs = view.getPngs(picks, HERO.angleValue);
        System.out.println("Output: ");

        System.out.println("\tPNG Layer Files:");
        int i = 0;
        for (ImPng png : pngs) {
            System.out.println("\t\t" + i + ": " + png);
            i++;
        }

        System.out.println("\tJPG File:");
        System.out.println("\t\t" + view.getJpg(picks, HERO.angleValue));

    }


}