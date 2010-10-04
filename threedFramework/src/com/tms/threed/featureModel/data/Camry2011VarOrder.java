package com.tms.threed.featureModel.data;

import com.tms.threed.featureModel.shared.Var;

import java.util.Set;

public class Camry2011VarOrder extends TrimColorOption {

    public Var acc = addVar(Accessories, "Accessories Group");

    public Var eAcc = acc.addVar(ExteriorAccessories, "Exterior Accessories Group");
    public Var iAcc = acc.addVar(InteriorAccessories, "Interior Accessories Group");

    public Var a28 = iAcc.addVar("28", "Shift Knob");
    public Var cf = iAcc.addVar("CF", "Floormats");
    public Var a2q = iAcc.addVar("2Q", "All Weather Mats");

    public Var e5 = eAcc.addVar("E5", "Exhaust Tip");
    public Var wb = eAcc.addVar("WB", "Alloy Wheel");
    public Var bm = eAcc.addVar("BM", "Body Side Molding");
    public Var dio3 = eAcc.addVar("DIO3", "Mudguards");
    public Var r7 = eAcc.addVar("R7");

    public Camry2011VarOrder() {

        addConstraint(imply(up, wb));
        addConstraint(imply(ut, wb));
        addConstraint(imply(t2540, e5));
        addConstraint(conflict(a28, mt6));
        addConstraint(xor(a2q, cf));

        addConstraint(imply(qd, and(r7, sr, se)));
        addConstraint(imply(r7, se));

//        addPickOneGroupXorsIfNeeded();

    }

    public Set<Var> getSamplePicks() {
        SampleFeatureSet picks = new SampleFeatureSet();
        return getVars(picks);
    }


}