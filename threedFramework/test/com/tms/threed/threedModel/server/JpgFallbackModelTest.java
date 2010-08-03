package com.tms.threed.threedModel.server;

import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.imageModel.shared.ImView;
import com.tms.threed.util.lang.shared.Path;
import junit.framework.TestCase;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JpgFallbackModelTest extends TestCase {

    String VENZA = "venza";

    Set<String> featureSet1 = parse("3R3,Base,XXX");
    Set<String> featureSet2 = parse("202,Base,XXX");
    Set<String> featureSet3 = parse("XXX,Base,XXX");
    Set<String> featureSet4 = parse("XXX,YYY,ZZZ");



    private ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();
    private Path jpgRoot = threedConfig.getJpgRootFileSystem();
    private SThreedModel model;
    private FeatureModel fm;

    @Override protected void setUp() throws Exception {


    }

    public void test() throws Exception {
        ModelFactory modelBuilder = new ModelFactory(threedConfig);
        model = modelBuilder.createModel(2009, VENZA);
        fm = model.getFeatureModel();
        ImSeries imageModel = model.getImageModel();
        ImView exteriorView = imageModel.getExteriorView();
        ImView interiorView = imageModel.getInteriorView();

        fallbackFor(exteriorView, featureSet1);
        fallbackFor(exteriorView, featureSet2);
        fallbackFor(exteriorView, featureSet3);
        fallbackFor(exteriorView, featureSet4);

        fallbackFor(interiorView, featureSet1);
        fallbackFor(interiorView, featureSet2);
        fallbackFor(interiorView, featureSet3);
        fallbackFor(interiorView, featureSet4);

    }


    public void fallbackFor(ImView view, Set<String> picks) throws Exception {
         fallbackFor(view, fm.createPicks(picks));
    }
    public void fallbackFor(ImView view, Picks picks) throws Exception {

        JpgFallback jpgFallbackModel = new JpgFallback(view, jpgRoot);
        File fallback = jpgFallbackModel.getFallback(picks);

        System.out.println("Fallback JPG for:");
        System.out.println("\tView: " + view);
        System.out.println("\tPicks: " + picks);
        System.out.println("\tJPG Fallback: " + fallback);
    }

    public static Set<String> parse(String commaDelimited) {
        HashSet<String> set = new HashSet<String>();
        String[] a = commaDelimited.split(",");
        set.addAll(Arrays.asList(a));
        return set;
    }


}
