package com.tms.threed.featureModel.shared.boolExpr;

import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.picks.IllegalPicksStateException;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.featureModel.shared.picks.PicksRO;

import java.util.List;

public class Or extends Junction {


    public Or(FeatureModel fm, List<BoolExpr> expressions) {
        super(fm, expressions);
    }

    public Or(FeatureModel fm, BoolExpr... expressions) {
        super(fm, expressions);
    }

    public Or(FeatureModel fm) {
        super(fm);
    }

    public void add(BoolExpr e) {
        exprList.add(e);
    }

    @Override public String getSymbol() {
        return "OR";
    }

    @Override public boolean autoAssign(Picks picks, boolean value) {
        if (value) {
            return autoAssignTrue(picks);
        } else {
            return autoAssignFalse(picks);
        }
    }

    private boolean autoAssignTrue(Picks picks) {

        int exprCount = exprList.size();

        int falseCount = 0;
        int trueCount = 0;
        int nullCount = 0;
        BoolExpr nullExpr = null;

        for (int i = 0; i < exprCount; i++) {
            BoolExpr expr = exprList.get(i);
            Bit value = expr.eval(picks);
            switch (value) {
                case TRUE:
                    trueCount++;
                    break;
                case FALSE:
                    falseCount++;
                    break;
                case UNASSIGNED:
                    nullCount++;
                    nullExpr = expr;
                    break;
                default:
                    throw new IllegalStateException();
            }
        }

        if (falseCount == (exprCount - 1) && nullCount == 1) {
            assert nullExpr != null;
            return nullExpr.autoAssign(picks, true);
        } else if (falseCount == exprCount) {
            throw new IllegalPicksStateException();
        } else {
            return false;
        }

    }

    private boolean autoAssignFalse(Picks picks) {

        int exprCount = exprList.size();

        int trueCount = 0;
        int falseCount = 0;
        int nullCount = 0;
        BoolExpr nullExpr = null;

        for (int i = 0; i < exprCount; i++) {
            BoolExpr expr = exprList.get(i);
            Bit value = expr.eval(picks);
            switch (value) {
                case TRUE:
                    trueCount++;
                    break;
                case FALSE:
                    falseCount++;
                    break;
                case UNASSIGNED:
                    nullCount++;
                    nullExpr = expr;
                    break;
                default:
                    throw new IllegalStateException();
            }
        }

        if (trueCount == (exprCount - 1) && nullCount == 1) {
            assert nullExpr != null;
            return nullExpr.autoAssign(picks, false);
        } else if (trueCount == exprCount) {
            throw new IllegalPicksStateException();
        } else {
            return false;
        }


    }

    @Override public Bit eval(PicksRO picks) {
        int unassignedCount = 0;
        for (BoolExpr expr : exprList) {
            Bit val = expr.eval(picks);
            if (val.isTrue()) return Bit.TRUE;
            if (val.isUnassigned()) unassignedCount++;
        }
        if (unassignedCount > 0) return Bit.UNASSIGNED;
        return Bit.FALSE;
    }

}