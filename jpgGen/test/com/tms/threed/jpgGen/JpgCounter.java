package com.tms.threed.jpgGen;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.featureModelJavaBdd.BddBuilder;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.imageModel.shared.ImView;
import com.tms.threed.imageModel.shared.Jpg;
import com.tms.threed.javabdd.BDD;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedCore.shared.ViewSnap;
import com.tms.threed.threedModel.server.SThreedModels;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.lang.shared.Path;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpgCounter extends TestCase {

    ThreedModel threedModel;

    ThreedConfig threedConfig;
    Path pngRoot;

    SeriesKey seriesKey;
    SeriesInfo seriesInfo;
    FeatureModel fm;
    ImSeries im;

    @Override protected void setUp() throws Exception {

        seriesKey = SeriesKey.TACOMA_2011;
        threedModel = SThreedModels.get().getModel(seriesKey,true).getModel();
        seriesInfo = threedModel.getSeriesInfo();

        threedConfig = threedModel.getThreedConfig();
        pngRoot = threedConfig.getPngRootFileSystem();

        fm = threedModel.getFeatureModel();
        im = threedModel.getImageModel();

    }

    public void testWithImageFeatures() throws Exception {
        BddBuilder bddBuilder = new BddBuilder(fm);
        bddBuilder.buildBdd();

        //loop through all views
        List<ImView> imViews = im.getViews();
        for (ImView imView : imViews) {
            System.out.println("\t " + imView.getName());
            int angleCount = imView.getAngleCount();
            for (int angle = 1; angle <= angleCount; angle++) {
                ImView imViewForOneAngle = imView.copy(angle);
                Set<Var> careSetOneAngle = imViewForOneAngle.getVarSet();
                BDD.BDDIterator it = bddBuilder.iterator(careSetOneAngle);
                ViewSnap viewState = new ViewSnap(imView.getViewKey(), angle);

                HashSet<String> set = new HashSet<String>();
                while (it.hasNext()) {
                    boolean[] aPicks = it.next();
                    Picks picks = bddBuilder.boolArrayToPicks(aPicks);
                    Jpg jpg = threedModel.getJpg(viewState, picks);
                    set.add(jpg.toString());
                }

                System.out.println("\t\t " + angle + ": " + set.size());
            }

        }
    }


}
