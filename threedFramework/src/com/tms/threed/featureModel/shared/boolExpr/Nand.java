package com.tms.threed.featureModel.shared.boolExpr;

import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.featureModel.shared.picks.PicksRO;

public class Nand extends Pair {

    public Nand(FeatureModel fm, BoolExpr expr1, BoolExpr expr2) {
        super(fm, expr1, expr2);
    }

    public Or toOr() {
        return fm.or(fm.not(expr1), fm.not(expr2));
    }

    @Override public String getSymbol() {
        return "NAND";
    }

    @Override public boolean autoAssign(Picks picks, boolean value) {
        if (value) {
            return autoAssignTrue(picks);
        } else {
            return autoAssignFalse(picks);
        }
    }

    public boolean autoAssignTrue(Picks picks) {
        Bit val1 = expr1.eval(picks);
        Bit val2 = expr2.eval(picks);
        if (val1.isTrue() && val2.isUnassigned()) {
            return expr2.autoAssign(picks, false);
        } else if (val2.isTrue() && val1.isUnassigned()) {
            return expr1.autoAssign(picks, true);
        } else {
            return false;
        }
    }

    public boolean autoAssignFalse(Picks picks) {
        throw new UnsupportedOperationException("Nand.autoAssignFalse[" + picks + "]");
    }

    @Override public Bit eval(PicksRO picks) {
        Bit val1 = expr1.eval(picks);
        Bit val2 = expr2.eval(picks);
        if (val1.isFalse()) return Bit.TRUE;
        else if (val2.isFalse()) return Bit.TRUE;
        else if (val1.isTrue() && val2.isTrue()) return Bit.FALSE;
        else return Bit.UNASSIGNED;
    }

}