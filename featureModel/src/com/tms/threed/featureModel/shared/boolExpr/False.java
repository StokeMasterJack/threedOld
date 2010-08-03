package com.tms.threed.featureModel.shared.boolExpr;

import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.picks.PicksRO;

public class False extends Constant {

    public False(FeatureModel fm) {
        super(fm);
    }


    @Override public String toString() {
        return "False";
    }

    @Override public Bit eval(PicksRO picks) {
        return Bit.FALSE;
    }

}