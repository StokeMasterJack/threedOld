package com.tms.threed.featureModel.shared.boolExpr;

import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.picks.IllegalPicksStateException;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.featureModel.shared.picks.PicksRO;

/**
 * AKA bi-implication
 *   same as (F => G) && (G => F)
 */
public class Iff extends Pair {

    public Iff(FeatureModel fm, BoolExpr expr1, BoolExpr expr2) {
        super(fm, expr1, expr2);
    }

    @Override public String getSymbol() {
        return "IFF";
    }

    public And toConjunctionOfImplications() {
        Implication imp1 = fm.imply(expr1, expr2);
        Implication imp2 = fm.imply(expr2, expr1);
        return fm.and(imp1, imp2);
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
        } else if (val1.isFalse() && val2.isUnassigned()) {
            return expr2.autoAssign(picks, false);
        } else if (val2.isTrue() && val1.isUnassigned()) {
            return expr1.autoAssign(picks, true);
        } else if (val2.isFalse() && val1.isUnassigned()) {
            return expr1.autoAssign(picks, false);
        } else if (val1.isTrue() && val2.isFalse()) {
            String s = this.toString();
            throw new IllegalPicksStateException("IFF: expr1 evals to true and expr2 evals to false [" + s +"]");
        } else if (val1.isFalse() && val2.isTrue()) {
            String s = this.toString();
            throw new IllegalPicksStateException("IFF: expr1 evals to false and expr2 evals to true [" + s +"]");
        } else {
            return false;
        }
        
    }

    public boolean autoAssignFalse(Picks picks) {
        throw new UnsupportedOperationException("Iff.autoAssignFalse[" + picks + "]");
    }

    @Override public Bit eval(PicksRO picks) {
        Bit val1 = expr1.eval(picks);
        Bit val2 = expr2.eval(picks);
        if (val1.isFalse() && val2.isFalse()) return Bit.TRUE;
        else if (val1.isTrue() && val2.isTrue()) return Bit.TRUE;
        else if (val1.isFalse() && val2.isTrue()) return Bit.FALSE;
        else if (val1.isTrue() && val2.isFalse()) return Bit.FALSE;
        else return Bit.UNASSIGNED;
    }


}