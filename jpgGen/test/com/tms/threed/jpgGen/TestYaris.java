package com.tms.threed.jpgGen;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.featureModelJavaBdd.BddBuilder;
import com.tms.threed.imageModel.shared.ImPng;
import com.tms.threed.imageModel.shared.ImView;
import com.tms.threed.imageModel.shared.Jpg;
import com.tms.threed.javabdd.BDD;
import com.tms.threed.javabdd.BDDVarSet;
import com.tms.threed.jpgGen.ezProcess.EzProcess;
import com.tms.threed.jpgGen.singleJpgGen.ImageMagicHomes;
import com.tms.threed.jpgGen.singleJpgGen.JpgGenerator;
import com.tms.threed.jpgGen.singleJpgGen.JpgGeneratorImageMagic;
import com.tms.threed.jpgGen.singleJpgGen.JpgSpec;
import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedCore.shared.ViewKey;
import com.tms.threed.threedModel.server.SThreedModels;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.lang.shared.Path;
import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

public class TestYaris extends TestCase {

    ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();
    Path pngRoot = threedConfig.getPngRootFileSystem();
    Path jpgRoot = threedConfig.getJpgRootHttp();

    private FeatureModel fm;
    private BddBuilder bddBuilder;
    private SeriesKey seriesKey;
    private ThreedModel threedModel;
    private SeriesInfo seriesInfo;
    private BDD bdd;

    private ViewKey viewKey;
    private int angle;

    @Override protected void setUp() throws Exception {
        seriesKey = SeriesKey.SEQUOIA_2011;
        threedModel = SThreedModels.get().getModel(seriesKey).getModel();
        seriesInfo = threedModel.getSeriesInfo();

        fm = threedModel.getFeatureModel();
        bddBuilder = new BddBuilder(fm);
        bddBuilder.buildBddLeafOnly();

        bdd = bddBuilder.getBdd();
    }

    public void test() throws Exception {
        try {
            genAllJpgs();
        } catch (EzProcess.EzProcessExecException e) {
            EzProcess.ExecOutcome outcome = e.getExecOutcome();
            outcome.print();
        }
    }

    public void testPrintSummary() throws Exception {

        viewKey = seriesInfo.getExterior();
        angle = 2;

//        print("support: ", getSupportVarSet());
        print("view: ", getViewVarSet());
//        print("union: ", getUnionVarSet());
    }

    public void genAllJpgs() throws Exception {
//        ViewKey[] viewKeys = seriesInfo.getViewsKeys();
//        for (ViewKey viewKey : viewKeys) {
        this.viewKey = seriesInfo.getInterior();
        for (int angle = 1; angle <= viewKey.getAngleCount(); angle++) {
            this.angle = angle;
            genJpgsForCurrentViewState();
        }
//        }
    }

    public void genJpgsForCurrentViewState() throws Exception {
        System.out.println(viewKey + " " + angle);
        BDDVarSet varSet = getViewVarSet();
        System.out.println("\t varSet.size() = [" + varSet.size() + "]");
        Set<Jpg> jpgSet = doSatAll(getViewVarSet());
        genJpgs(jpgSet);
    }

    public ImView getImView() {
        return threedModel.getView(viewKey).copy(angle);
    }

    public Set<Jpg> doSatAll(BDDVarSet bddVarSet) throws Exception {
//        long satAllCount = satAllCount(bddVarSet);
        ImView imView = getImView();
        HashSet<Jpg> set = new HashSet<Jpg>();
        BDD.BDDIterator it = bdd.iterator(bddVarSet);
        int counter = 0;
        while (it.hasNext()) {
            boolean[] productAsBoolArray = it.next();
            Picks picks = bddBuilder.boolArrayToPicks(productAsBoolArray);
            assert picks != null;
            Jpg jpg = imView.getJpg(picks, angle);
            set.add(jpg);
            counter++;
//            if (satAllCount % counter == 10000)
//                System.out.println(counter + " of " + satAllCount + "  jpgCount[" + set.size() + "]");
        }

//        System.out.println("\t satAllCount = [" + counter + "]");
//        System.out.println("\t jpgCount: " + set.size());
        return set;
    }

    public void genJpgs(Set<Jpg> jpgs) {
        int jpgCount = jpgs.size();
        long counter = 0;
        for (Jpg jpg : jpgs) {
            genJpg(jpg);
            System.out.println("\t" + counter + " of " + jpgCount + "\t" + jpg);
            counter++;
        }
    }

    public void genJpg(Jpg jpg) {
        JpgSpec jpgSpec = createJpgSpec(jpg);
        JpgGenerator jpgGenerator = new JpgGeneratorImageMagic(jpgSpec, Executors.newCachedThreadPool(), ImageMagicHomes.DAVE_MAC);
        jpgGenerator.generate();
    }

    public JpgSpec createJpgSpec(Jpg jpg) {

        ArrayList<File> pngs = new ArrayList<File>();
        List<ImPng> imPngList = jpg.getPngs();
        for (ImPng imPng : imPngList) {
            Path pngPath = imPng.getPath(threedConfig.getPngRootFileSystem());
            File pngFile = new File(pngPath.toString());
            pngs.add(pngFile);
        }

        File jpgFile;
        Path jpgPath = jpg.getPath(threedConfig.getJpgRootFileSystem());
        jpgFile = new File(jpgPath.toString());
        return new JpgSpec(pngs, jpgFile, false);
    }

    public long satCount(BDDVarSet bddVarSet) {
        return (long) bdd.satCount(bddVarSet);
    }

    public BDD.BDDIterator satAll(BDDVarSet bddVarSet) {
        return bdd.iterator(bddVarSet);
    }

    public long satAllCount(BDDVarSet bddVarSet) {
        BDD.BDDIterator it = bdd.iterator(bddVarSet);
        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }
        return counter;
    }

    public void print(String title, BDDVarSet bddVarSet) {
        System.out.println(title);
        System.out.println("\tSatCount: " + (long) bdd.satCount());
//        System.out.println("\tvSatCount: " + satCount(bddVarSet));
//        System.out.println("\tvVarCount: " + bddVarSet.size());
//        System.out.println("\tvSatAllCount: " + satAllCount(bddVarSet));
    }

    public BDDVarSet getSupportVarSet() {
        return bdd.support();
    }

    public BDDVarSet getViewVarSet() {
        ImView imView = getImView();
        return bddBuilder.getBddVarSet(imView.getVarSet());
    }

    public BDDVarSet getUnionVarSet() {
        return getSupportVarSet().union(getViewVarSet());
    }


}