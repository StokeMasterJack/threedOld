package com.tms.threed.featureModel.shared.boolExpr;

import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.picks.PicksRO;

public class True extends Constant {


    public True(FeatureModel fm) {
        super(fm);
    }


    @Override public String toString() {
        return "True";
    }

    @Override public Bit eval(PicksRO picks) {
        return Bit.TRUE;
    }


}
