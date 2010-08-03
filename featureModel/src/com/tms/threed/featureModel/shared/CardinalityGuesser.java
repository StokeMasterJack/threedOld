package com.tms.threed.featureModel.shared;

import java.io.Serializable;

public class CardinalityGuesser implements Serializable, CommonVarNames {

    public Cardinality getCard(Var var) {

        if (var.isRoot()) return Cardinality.AllGroup;
        if (var.getCode().equalsIgnoreCase(Trim)) return Cardinality.AllGroup;
        if (var.getCode().equalsIgnoreCase(Color)) return Cardinality.AllGroup;
        if (var.getCode().equalsIgnoreCase(Options)) return Cardinality.ZeroOrMoreGroup;
        if (var.getCode().equalsIgnoreCase(Accessories)) return Cardinality.ZeroOrMoreGroup;
        if (var.getCode().equalsIgnoreCase(ExteriorAccessories)) return Cardinality.ZeroOrMoreGroup;
        if (var.getCode().equalsIgnoreCase(InteriorAccessories)) return Cardinality.ZeroOrMoreGroup;

        if (var.getChildVars() == null) return Cardinality.Leaf;


        return null;
    }


}
