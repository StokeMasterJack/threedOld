package com.tms.threed.featureModel.data;

import com.tms.threed.featureModel.shared.CommonVarNames;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.threedCore.shared.SeriesKey;

public class Avalon2010 extends FeatureModel implements CommonVarNames {

    public Var avalon = addVar("avalon");

    public Var trim = avalon.addVar(Trim);

    public Var grade = trim.addVar(Grade);

    public Var base = grade.addVar(Base);
    public Var ltd = grade.addVar("LTD");

    public Var engine = trim.addVar(Engine);
    public Var v6 = engine.addVar("V6");

    public Var transmission = trim.addVar(Transmission);
    public Var at6 = transmission.addVar("6AT");

    public Var modelCode = trim.addVar(ModelCode);


    public Var t3544 = modelCode.addVar("3534");
    public Var t3554 = modelCode.addVar("3554");

    //start color

    public Var color = avalon.addVar(Color);

    public Var fabricPlusColor = color.addVar(FabricPlusColor);


    //interior fabric+color
    public Var LH02 = fabricPlusColor.addVar("LH02");
    public Var LH17 = fabricPlusColor.addVar("LH17");
    public Var LH20 = fabricPlusColor.addVar("LH20");

    public Var FE02 = fabricPlusColor.addVar("FE02");
    public Var FE17 = fabricPlusColor.addVar("FE17");
    public Var LL02 = fabricPlusColor.addVar("LL02");

    public Var LL17 = fabricPlusColor.addVar("LL17");
    public Var LL20 = fabricPlusColor.addVar("LL20");
    public Var LG20 = fabricPlusColor.addVar("LG20");

    public Var LN02 = fabricPlusColor.addVar("LN02");
    public Var LN17 = fabricPlusColor.addVar("LN17");
    public Var LN20 = fabricPlusColor.addVar("LN20");


    public Var exteriorColor = color.addVar(ExteriorColor, "Exterior Color");


    //exterior color
    public Var blizzardPearl = exteriorColor.addVar("070", "Blizzard Pearl");
    public Var silver = exteriorColor.addVar("1F7", "Silver");
    public Var gray = exteriorColor.addVar("1G3", "Gray");
    public Var black = exteriorColor.addVar("202", "Black");
    public Var red = exteriorColor.addVar("3R0", "Sizzling Crimson Mica");
    public Var beach = exteriorColor.addVar("4T8", "Beach");
    public Var bean = exteriorColor.addVar("4U5", "Cocoa Bean Metallic");
    public Var cypressPearl = exteriorColor.addVar("6T7", "Cypress Pearl");
    public Var blue = exteriorColor.addVar("8N0", "Zephyr Blue Metallic");

    public Var interiorColor = color.addVar(InteriorColor, "Interior Color");


    //interior color
    public Var Ivory = interiorColor.addVar("Ivory");
    public Var Gray = interiorColor.addVar("Gray");
    public Var Black = interiorColor.addVar("Black");

//    public Var ash13 = interiorColor.addVar("13");
//    public Var ash14 = interiorColor.addVar("14");
//    public Var charcoal15 = interiorColor.addVar("15");
//    public Var bisque40 = interiorColor.addVar("40");

    //interior material
    public Var interiorMaterial = color.addVar(InteriorMaterial, "Interior Material");

    public Var fabric = interiorMaterial.addVar("Fabric");
    public Var leather = interiorMaterial.addVar("Leather");

    public Var options = avalon.addVar(Options);

    public Var HD = options.addVar("HD");
    public Var HM = options.addVar("HM");
    public Var EJ = options.addVar("EJ");
    public Var NV = options.addVar("NV");
    public Var AF = options.addVar("AF");
    public Var LM = options.addVar("LM");

    public Var acc = avalon.addVar(Accessories, "Accessories Group");

    public Var eAcc = acc.addVar(ExteriorAccessories, "Exterior Accessories Group");
    public Var iAcc = acc.addVar(InteriorAccessories, "Interior Accessories Group");

    public Var CF = iAcc.addVar("CF", "Carpet Floor Mats");

    public Var WL = eAcc.addVar("WL", "Wheel locks (alloy)");

    public Var i9G = iAcc.addVar("9G", "Cargo Tote");

    public Var EF = eAcc.addVar("EF", "Rear Bumper Applique");


    public Avalon2010() {

        super(SeriesKey.AVALON_2011.v0(),"Avalon");

        addConstraint(xor(base, ltd));
        addConstraint(xor(t3544, t3554));
        addConstraint(v6);
        addConstraint(at6);
        addConstraint(xor(LH02,LH17,LH20,FE02,FE17,LL02,LL17,LL20,LG20,LN02,LN17,LN20));
        addConstraint(xor(blizzardPearl,silver,gray,black,red,beach,bean,cypressPearl,blue));
        addConstraint(xor(Ivory,Gray,Black));
        addConstraint(xor(leather,fabric));

        addConstraint(iff(t3544, and(base, v6, at6)));
        addConstraint(iff(t3554, and(ltd, v6, at6)));

        addConstraint(imply(ltd, and(HM, HD)));

        addConstraint(conflict(LH02, or(LM, AF, HM, silver, blue)));
        addConstraint(imply(LH02, and(leather, Ivory, base)));


        addConstraint(conflict(LH17, or(LM, AF, HM, beach, bean)));
        addConstraint(imply(LH17, and(leather, Gray, base)));

        addConstraint(conflict(LH20, or(LM, AF, HM, beach, cypressPearl)));
        addConstraint(imply(LH20, and(leather, Black, base)));

        addConstraint(iff(FE02, and(fabric, Ivory, base, AF)));
        addConstraint(conflict(FE02, or(blizzardPearl, silver, blue)));


        addConstraint(iff(FE02, and(fabric, Ivory, base, AF)));
        addConstraint(conflict(FE02, or(blizzardPearl, silver, blue)));

        addConstraint(iff(LL02, and(leather, Ivory, base, HM)));
        addConstraint(conflict(LL02, or(silver, blue)));

        addConstraint(iff(LL17, and(leather, Gray, base, HM)));
        addConstraint(conflict(LL17, or(beach, bean)));

        addConstraint(iff(LL20, and(leather, Black, base, HM)));
        addConstraint(conflict(LL20, or(cypressPearl, beach)));

        addConstraint(iff(LG20, and(leather, Black, base, LM)));

        addConstraint(iff(LN02, and(leather, Ivory, ltd)));
        addConstraint(conflict(LN02, or(silver, blue)));

        addConstraint(iff(LN17, and(leather, Gray, ltd)));
        addConstraint(conflict(LN17, or(beach, bean)));

        addConstraint(iff(LN20, and(leather, Black, ltd)));
        addConstraint(conflict(LN20, or(beach, cypressPearl, blue)));


        addConstraint(conflict(fabric, black));


        addConstraint(imply(HM, HD));

        addConstraint(conflict(NV, or(AF, LM, EJ)));


        addConstraint(iff(AF, fabric));
        addConstraint(conflict(AF, or(HD, HM, LM, blizzardPearl, Black, ltd)));


        addConstraint(imply(LM, black));
        addConstraint(conflict(LM, or(HD, HM, Ivory, Gray, ltd)));
    }


    public void printTree() {
        getRootVar().print();
    }
}