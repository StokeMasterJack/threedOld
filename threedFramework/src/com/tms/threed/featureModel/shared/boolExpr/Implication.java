package com.tms.threed.featureModel.shared.boolExpr;

import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.picks.IllegalPicksStateException;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.featureModel.shared.picks.PicksRO;

public class Implication extends Pair {

    public Implication(FeatureModel fm, BoolExpr expr1, BoolExpr expr2) {
        super(fm, expr1, expr2);
    }

    @Override public String getSymbol() {
        return "=>";
    }

//    @Override public boolean fixupPicks() {
//        return fm.implicationFixupPicks(this, expr1, expr2);
//    }


    public BoolExpr toNotOrForm() {
        return fm.or(fm.not(expr1), expr2);
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
            return expr2.autoAssign(picks, true);
        } else if (val2.isFalse() && val1.isUnassigned()) {
            return expr1.autoAssign(picks, false);
        } else if (val1.isTrue() && val2.isFalse()) {
            throw new IllegalPicksStateException();
        } else {
            return false;
        }
    }

    public boolean autoAssignFalse(Picks picks) {
        Bit val1 = expr1.eval(picks);
        Bit val2 = expr2.eval(picks);

        if (val1.isFalse()) {
            throw new IllegalPicksStateException("Implication.autoAssignFalse: expr1[" + expr1 + "]  expr2[" + expr2 + "]");
        } else if (val1.isTrue() && val2.isFalse()) {
            //already correctly assigned - do nothing
            return false;
        } else if (val1.isTrue() && val2.isTrue()) {
            throw new IllegalPicksStateException("Implication.autoAssignFalse: expr1[" + expr1 + "]  expr2[" + expr2 + "]");
        } else if (val1.isTrue() && val2.isUnassigned()) {
            return expr2.autoAssign(picks, false);
        } else if (val1.isUnassigned() && val2.isFalse()) {
            return expr1.autoAssign(picks, true);
        } else {
            return false;
        }

    }

    @Override public Bit eval(PicksRO picks) {
        Bit val1 = expr1.eval(picks);
        Bit val2 = expr2.eval(picks);
        if (val1.isFalse()) return Bit.TRUE;
        else if (val2.isTrue()) return Bit.TRUE;
        else if (val1.isTrue() && val2.isFalse()) return Bit.FALSE;
        else {
//            System.out.println("Implication: val1[" + val1 + "]   val2[" + val2 + "]  - Leave Unassigned");
            return Bit.UNASSIGNED;
        }
    }
}