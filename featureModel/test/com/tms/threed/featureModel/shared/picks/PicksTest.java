package com.tms.threed.featureModel.shared.picks;

import com.tms.threed.featureModel.data.SampleFeatureSet;
import com.tms.threed.featureModel.server.FeatureModelBuilderXml;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.ProposePickResponse;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import junit.framework.TestCase;

/**
 *  Venza
 *
 *  modelCode[2810] exteriorColor[1F7] interiorColor[FA12]
 */
public class PicksTest extends TestCase {

     public void testFixupVenza() throws Exception {

        ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();
        FeatureModelBuilderXml b = new FeatureModelBuilderXml(threedConfig);

        FeatureModel fm = b.buildModel(SeriesKey.VENZA_2010);
        Picks picks = fm.createPicks(new SampleFeatureSet("2810,1F7,FA12"));

        System.out.println("Current Picks:");
        System.out.println("\tRaw: " + picks);
        picks.fixup();
        System.out.println("\tFixed: " + picks);



    }

    public void testPicksTester() throws Exception {

        ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();
        FeatureModelBuilderXml b = new FeatureModelBuilderXml(threedConfig);

        FeatureModel fm = b.buildModel(SeriesKey.AVALON_2011);
        Picks picks = fm.getInitialVisiblePicks();

        System.out.println("Current Picks:");
        System.out.println("\tRaw: " + picks);
        picks.fixup();
        System.out.println("\tFixed: " + picks);

        System.out.println("---");

//        if(true) return;
        PickTester t = new PickTester();

        Var proposedPick = fm.getVar("LL02");
        System.out.println("proposedPick = [" + proposedPick + "=true]");

        ProposePickResponse response = t.proposePick(picks, proposedPick, true);
//
        System.out.println("\t " + (response.valid ? "Valid" : "Invalid"));


    }

}
