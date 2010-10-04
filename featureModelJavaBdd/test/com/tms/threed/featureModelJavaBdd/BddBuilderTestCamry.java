package com.tms.threed.featureModelJavaBdd;

import com.tms.threed.featureModel.data.Camry2011;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.imageModel.shared.ImView;
import com.tms.threed.javabdd.BDD;
import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesInfoBuilder;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.util.lang.shared.Path;
import junit.framework.TestCase;

import java.util.List;
import java.util.Set;

public class BddBuilderTestCamry extends TestCase {

    ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();
    Path pngRoot = threedConfig.getPngRootFileSystem();

    SeriesKey seriesKey;
    SeriesInfo seriesInfo;
    FeatureModel fm;
    ImSeries im;

    @Override protected void setUp() throws Exception {
        seriesKey = SeriesKey.CAMRY_2011;
        seriesInfo = SeriesInfoBuilder.createSeriesInfo(seriesKey);
        fm = new Camry2011();
//        im = new ImageModelBuilder(fm, seriesInfo, pngRoot).buildImageModel();
    }

    public void testSatCountNotUsingAnyCareSet() throws Exception {
        BddBuilder bddBuilder = new BddBuilder(fm);
        bddBuilder.buildBdd();
        assertEquals(2154486, bddBuilder.satCount());
        assertEquals(2154486, bddBuilder.iteratorCount());

        System.out.println("satCount (no care set) = [" + bddBuilder.satCount() + "]");
    }

    public void testWithImageFeatures() throws Exception {
        System.out.println("SatCounts taking image features");
        BddBuilder bddBuilder = new BddBuilder(fm);
        bddBuilder.buildBdd();

        Set<Var> careSetAllVies = im.getVarSet();
        System.out.println(seriesKey + ": " + bddBuilder.iteratorCount(careSetAllVies));

        //loop through all views
        List<ImView> imViews = im.getViews();
        for (ImView imView : imViews) {
            Set<Var> careSetOneView = imView.getVarSet();
            System.out.println("\t " + imView.getName() + ": " + bddBuilder.iteratorCount(careSetOneView));
            int angleCount = imView.getAngleCount();
            for (int angle = 1; angle <= angleCount; angle++) {
                ImView imViewForOneAngle = imView.copy(angle);
                Set<Var> careSetOneAngle = imViewForOneAngle.getVarSet();
                System.out.println("\t\t " + angle + ": " + bddBuilder.iteratorCount(careSetOneAngle));
            }

        }
    }

    public void testSatAllWithPicks() throws Exception {

        BddBuilder bddBuilder = new BddBuilder(fm);
        bddBuilder.buildBdd();
        BDD.BDDIterator it = bddBuilder.iterator();
        int counter = 0;
        while (it.hasNext()) {
            boolean[] productAsBoolArray = it.next();
            Picks picks = bddBuilder.boolArrayToPicks(productAsBoolArray);
            counter++;
        }
        assertEquals(2154486, counter);
    }

    public <T> T getFoo() {
        return null;
    }

    public void test() throws Exception {
        if (getFoo() instanceof Integer) {
            System.out.println("fo");
        }
    }


}