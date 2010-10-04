package com.tms.threed.featureModel.data;

import com.tms.threed.featureModel.shared.Cardinality;
import com.tms.threed.featureModel.shared.Var;

public class TrimColor extends Trim {

    public Var color = addVar(Color);

    //exteriorColor
    public Var exteriorColor = color.addVar(ExteriorColor, "Exterior Color");
    public Var white = exteriorColor.addVar("040", "White");
    public Var silver = exteriorColor.addVar("1F7", "Silver");
    public Var gray = exteriorColor.addVar("1G3", "Gray");
    public Var black = exteriorColor.addVar("202", "Black");
    public Var red = exteriorColor.addVar("3R3", "Red");
    public Var beach = exteriorColor.addVar("4T8", "Beach");
    public Var spruce = exteriorColor.addVar("6V4", "Spruce");
    public Var green = exteriorColor.addVar("776", "Green");
    public Var blueRibbon = exteriorColor.addVar("8T5", "BlueRibbon");
    public Var blueWhisper = exteriorColor.addVar("8U8", "BlueWhisper");

    //interiorColor
    public Var interiorColor = color.addVar(InteriorColor, "Interior Color");
    public Var ash = interiorColor.addVar("Ash");
    public Var charcoal = interiorColor.addVar("Charcoal");
    public Var bisque = interiorColor.addVar("Bisque");

    //interiorColorCode
    public Var interiorColorCode = color.addVar(InteriorColorCode, "Interior Color Code");
    public Var ash13 = interiorColorCode.addVar("13");
    public Var ash14 = interiorColorCode.addVar("14");
    public Var charcoal15 = interiorColorCode.addVar("15");
    public Var bisque40 = interiorColorCode.addVar("40");

    //interiorMaterial
    public Var interiorMaterial = color.addVar(InteriorMaterial, "Interior Material");
    public Var fabric = interiorMaterial.addVar("Fabric");
    public Var leather = interiorMaterial.addVar("Leather");

    //fabricPlusColor
    public Var fabricPlusColor = color.addVar(FabricPlusColor);
    public Var fb13 = fabricPlusColor.addVar("FB13");
    public Var fb40 = fabricPlusColor.addVar("FB40");
    public Var fa13 = fabricPlusColor.addVar("FA13");
    public Var fa40 = fabricPlusColor.addVar("FA40");
    public Var fc14 = fabricPlusColor.addVar("FC14");
    public Var fc15 = fabricPlusColor.addVar("FC15");
    public Var la13 = fabricPlusColor.addVar("LA13");
    public Var la40 = fabricPlusColor.addVar("LA40");
    public Var lb14 = fabricPlusColor.addVar("LB14");
    public Var lb15 = fabricPlusColor.addVar("LB15");

    public TrimColor() {
        super();

        color.setManditory(true);
        color.setCardinality(Cardinality.AllGroup);

        fabricPlusColor.setCardinality(Cardinality.PickOneGroup);
        exteriorColor.setCardinality(Cardinality.PickOneGroup);
        interiorColor.setCardinality(Cardinality.PickOneGroup);
        interiorColorCode.setCardinality(Cardinality.PickOneGroup);
        interiorMaterial.setCardinality(Cardinality.PickOneGroup);

        interiorFabricColorGrade();
        exteriorColor();
        interiorColor();

        fabricPlusColor.setDerived(true);
    }

    protected void interiorFabricColorGrade() {
        addConstraint(iff(fa13, and(ash, ash13, fabric, or(hybrid, xle))));
        addConstraint(iff(fb13, and(ash, ash13, fabric, or(base, le))));
        addConstraint(iff(fc14, and(ash, ash14, fabric, se)));
        addConstraint(iff(la13, and(ash, ash13, leather, or(hybrid, xle))));
        addConstraint(iff(lb14, and(ash, ash14, leather, se)));

        addConstraint(iff(fa40, and(bisque, bisque40, fabric, or(hybrid, xle))));
        addConstraint(iff(fb40, and(bisque, bisque40, fabric, or(base, le))));
        addConstraint(iff(la40, and(bisque, bisque40, leather, or(hybrid, xle))));

        addConstraint(iff(fc15, and(charcoal, charcoal15, fabric, se)));
        addConstraint(iff(lb15, and(charcoal, charcoal15, leather, se)));
    }

    protected void interiorColor() {
        addConstraint(conflict(ash, beach));
        addConstraint(conflict(ash, green));
        addConstraint(conflict(bisque, silver));

        addConstraint(imply(ash13, ash));
        addConstraint(imply(ash14, ash));
        addConstraint(imply(ash, or(ash13, ash14)));
        addConstraint(iff(charcoal15, charcoal));
        addConstraint(iff(bisque40, bisque));
    }

    protected void exteriorColor() {
        addConstraint(conflict(base, or(gray, red, spruce, green, blueWhisper)));
        addConstraint(conflict(le, blueWhisper));
        addConstraint(conflict(se, or(beach, spruce, green, blueWhisper)));
        addConstraint(conflict(xle, blueWhisper));
    }


}
