package com.tms.threed.featureModel.shared;

//import com.tms.threed.config.server.ThreedConfigHelper;
//import com.tms.threed.config.shared.ThreedConfig;

import com.tms.threed.featureModel.data.Avalon2011;
import com.tms.threed.featureModel.data.Camry2011;
import com.tms.threed.featureModel.data.TrimColor;
import com.tms.threed.featureModel.server.FeatureModelBuilderXml;
import com.tms.threed.featureModel.server.SFeatureModel;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.util.lang.shared.Path;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class FeatureModelTest extends TestCase {

    Path pngRoot;

    public void test1() throws Exception {
        FeatureModel fm = new Camry2011();
        fm.test();
    }

    public void testVar() throws Exception {
        FeatureModel fm = new Camry2011();

        for (Var var : fm.getAccessories()) {
            System.out.println(var);
        }


    }

    public void testFixupNoBdd() throws Exception {

        FeatureModel fm = new Camry2011();

        Picks picks = fm.createPicks();
        picks.pick("Base", "040", "Ash", "6AT");

        System.out.println("Raw Picks: ");
        System.out.println("\t" + picks.getAllPicks2());
        System.out.println();

        long t1 = System.currentTimeMillis();

        picks.fixup();
        long t2 = System.currentTimeMillis();
        long delta = t2 - t1;

        System.out.println();
        System.out.println("FixedUp Picks: ");
        System.out.println("\t" + picks.getAllPicks2());

        System.out.println("Delta: " + delta);
        System.out.println();

        picks.printUnassignedVars();


//        fm.printAssignments();

//        fm.getRootVar().printVarTree();

    }


    /**
     * TODO
     */
    public void testCompareFeatureModelsCamry2011() throws Exception {
        FeatureModel fmA = new Camry2011();

        FeatureModelBuilderXml builder = new FeatureModelBuilderXml(pngRoot);
        FeatureModel fmB = builder.buildModel(SeriesKey.CAMRY_2011);

        SFeatureModel.compareFeatureModels(fmA, fmB);


//        fm.pick("Base", "040", "Ash", "6AT");
//        fixupPicksWithBdd(fm);
    }

    public void testCompareFeatureModelsAvalon2011() throws Exception {
        FeatureModel fmA = new Avalon2011();

        FeatureModelBuilderXml builder = new FeatureModelBuilderXml(pngRoot);
        FeatureModel fmB = builder.buildModel(SeriesKey.AVALON_2011);

        SFeatureModel.compareFeatureModels(fmA, fmB);
    }


    public void testPicksForCamry2011_A() throws Exception {
        FeatureModel fm = new Camry2011();
//        System.out.println("fm.satCount() = [" + fm.satCount() + "]");

        System.out.println("fm.getVarCount() = [" + fm.getVarCount() + "]");
        System.out.println("fm.getAllConstraintCount() = [" + fm.getAllConstraintCount() + "]");


        {
            long t1 = System.currentTimeMillis();
//            fm.getBddNode();
            long t2 = System.currentTimeMillis();
            long delta = t2 - t1;
            System.out.println("fm.getBdd() Delta: " + delta);
        }


    }


    public void test_PrintFeatureTree() throws Exception {
        TrimColor fm = new TrimColor();
        fm.printTree();

    }

    private void showRemainsPicks(FeatureModel fm) {
//        boolean v = fm.isPickValid("nv");
//        System.out.println("v = [" + v + "]");
//        Set<Var> vars = fm.getUnsetVars();

    }


    public void test() throws Exception {
        Camry2011 fm = new Camry2011();
        List<FieldDef> fields = fm.getFieldDefs("trim");
        for (FieldDef fd : fields) {
            System.out.println(fd);
        }
    }

    public static void printSize(Serializable o) throws IOException {
        System.out.println("byteCount: " + sizeOf(o));
    }

    public static int sizeOf(Serializable o) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);

        oos.writeObject(o);
        oos.flush();

        return os.size();

    }


}
