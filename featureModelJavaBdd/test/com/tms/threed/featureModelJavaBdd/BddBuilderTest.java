package com.tms.threed.featureModelJavaBdd;

import com.tms.threed.featureModel.server.FeatureModelBuilderXml;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.imageModel.builders.server.ImageModelBuilder;
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

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

public class BddBuilderTest extends TestCase {

    ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();
    Path pngRoot = threedConfig.getPngRootFileSystem();

    SeriesKey seriesKey;
    SeriesInfo seriesInfo;
    FeatureModel fm;
    ImSeries im;
    BddBuilder bddBuilder;

    @Override protected void setUp() throws Exception {
        seriesKey = SeriesKey.SEQUOIA_2011;

        seriesInfo = SeriesInfoBuilder.createSeriesInfo(seriesKey);

        FeatureModelBuilderXml fmBuilder = new FeatureModelBuilderXml(threedConfig);
        fm = fmBuilder.buildModel(seriesKey);
//        fm = new Camry2011();

        im = new ImageModelBuilder(fm, seriesInfo, pngRoot).buildImageModel();

        bddBuilder = new BddBuilder(fm);
        bddBuilder.buildBdd();
    }

    public void testSatCountWithAllFmVars() throws Exception {
        long satCount = bddBuilder.satCount();
        System.out.println("satCount(allFmVars): " + satCount + "]");
    }

    public void testIteratorCountWithAllFmVars() throws Exception {
        long iteratorCount = bddBuilder.iteratorCount();
        System.out.println("iteratorCount(allFmVars): " + iteratorCount);
    }

    /**
     * [7916, 040, Graphite, Fabric, 2Q, 2WD, SR5, V8, 6AT, FG13]
     *
     * User Picks: 7916, 040, Graphite, Fabric, 2Q,
     * Implied Picks: 2WD, SR5, V8, 6AT, FG13
     *
     * Fixed up [7916, 040, Graphite, Fabric, 2Q, 2WD, SR5, V8, 6AT, FG13]
     */
    public void testIteratorWithAllFmVars() throws Exception {
        File satAllSequoia = new File("/temp/satAllSequoia.txt");

        PrintWriter out = new PrintWriter(satAllSequoia);

        BDD.BDDIterator it = bddBuilder.iterator();
        while (it.hasNext()) {
            boolean[] product = it.next();
            Picks picks = new Picks(fm, product);
            String sPicks = picks.toString();
            if (
                //user picks
                    sPicks.contains("2Q") &&
                            sPicks.contains("7916") &&
                            sPicks.contains("Fabric") &&
                            sPicks.contains("040") &&
                            sPicks.contains("Graphite") &&

                            //implied picks (i.e. derived, i.e. fixed)
                            sPicks.contains("2WD") &&
                            sPicks.contains("SR5") &&
                            sPicks.contains("V8") &&
                            sPicks.contains("6AT") &&
                            sPicks.contains("FG13")
                    ) {
                out.println(picks);
            }
        }

        //2WD, SR5, V8, 6AT, FG13

        out.close();
    }

    boolean matchesShit(Picks picks) {
        String sPicks = picks.toString();
        return
                //user picks
                sPicks.contains("2Q") &&
                sPicks.contains("7916") &&
                sPicks.contains("Fabric") &&
                sPicks.contains("040") &&
                sPicks.contains("Graphite") &&

                //implied picks (i.e. derived, i.e. fixed)
                sPicks.contains("2WD") &&
                sPicks.contains("SR5") &&
                sPicks.contains("V8") &&
                sPicks.contains("6AT") &&
                sPicks.contains("FG13");
    }

    public void testWithImageFeatures() throws Exception {
        System.out.println("satCounts");

        Set<Var> careSetAllViews = im.getVarSet();

        System.out.println(seriesKey + ": " + bddBuilder.iteratorCount(careSetAllViews));

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

        BDD.BDDIterator it = bddBuilder.iterator();
        int counter = 0;
        while (it.hasNext()) {
            boolean[] productAsBoolArray = it.next();
            Picks picks = bddBuilder.boolArrayToPicks(productAsBoolArray);
            counter++;
        }

        System.out.println("counter = [" + counter + "]");
    }


}
