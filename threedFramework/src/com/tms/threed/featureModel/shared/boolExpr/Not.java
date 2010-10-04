package com.tms.threed.featureModel.shared.boolExpr;

import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.featureModel.shared.picks.PicksRO;

public class Not extends Unary {

    public Not(FeatureModel fm, BoolExpr expr) {
        super(fm, expr);
    }

    @Override public String getSymbol() {
        return "!";
    }

    @Override public boolean autoAssign(Picks picks,boolean value) {
        if (value) {
            return autoAssignTrue(picks);
        } else {
            return autoAssignFalse(picks);
        }
    }

    public boolean autoAssignTrue(Picks picks) {
        return expr.autoAssign(picks, false);
    }

    public boolean autoAssignFalse(Picks picks) {
        return expr.autoAssign(picks, true);
    }

    @Override public Bit eval(PicksRO picks) {
        Bit val = expr.eval(picks);
        if (val.isFalse()) return Bit.TRUE;
        if (val.isTrue()) return Bit.FALSE;
        else return Bit.UNASSIGNED;
    }
}
