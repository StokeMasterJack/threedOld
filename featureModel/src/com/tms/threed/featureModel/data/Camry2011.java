package com.tms.threed.featureModel.data;

import com.tms.threed.featureModel.shared.Var;

import java.util.Set;

public class Camry2011 extends TrimColorOption {

    public Var acc = addVar(Accessories, "Accessories");

    public Var iAcc = acc.addVar(InteriorAccessories, "Interior Accessories");
    public Var a28 = iAcc.addVar("28", "Shift Knob");
    public Var cf = iAcc.addVar("CF", "Floormats");
    public Var a2q = iAcc.addVar("2Q", "All Weather Mats");

    public Var eAcc = acc.addVar(ExteriorAccessories, "Exterior Accessories");
    public Var e5 = eAcc.addVar("E5", "Exhaust Tip");
    public Var wb = eAcc.addVar("WB", "Alloy Wheel");
    public Var r7 = eAcc.addVar("R7");
    public Var bm = eAcc.addVar("BM", "Body Side Molding");
    public Var dio3 = eAcc.addVar("DIO3", "Mudguards");

    public Camry2011() {
        addConstraint(imply(up, wb));
        addConstraint(imply(ut, wb));
        addConstraint(imply(t2540, e5));
        addConstraint(conflict(a28, mt6));
        addConstraint(xor(a2q, cf));
        a2q.setDefaultValue(true);
        addConstraint(imply(qd, and(r7, sr, se)));
        addConstraint(imply(r7, se));
    }

    public Set<Var> getSamplePicks() {
        SampleFeatureSet picks = new SampleFeatureSet();
        return getVars(picks);
    }


  
}
