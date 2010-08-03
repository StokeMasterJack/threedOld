package com.tms.threed.featureModel.shared.boolExpr;

import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.picks.IllegalPicksStateException;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.featureModel.shared.picks.PicksRO;

import java.util.ArrayList;
import java.util.List;

public class And extends Junction {

    public And(FeatureModel fm, List<BoolExpr> expressions) {
        super(fm, expressions);
    }

    public And(FeatureModel fm, BoolExpr... expressions) {
        super(fm, expressions);
    }

    public And(FeatureModel fm) {
        super(fm);
    }

    @Override public boolean autoAssign(Picks picks, boolean value) throws IllegalPicksStateException {
        if (value) {
            return autoAssignTrue(picks);
        } else {
            return autoAssignFalse(picks);
        }
    }

    private boolean autoAssignTrue(Picks picks) throws IllegalPicksStateException {
        boolean anyChange = false;

        for (int i = 0; i < exprList.size(); i++) {
            BoolExpr boolExpr = exprList.get(i);
            Bit value = boolExpr.eval(picks);
            if (value.isFalse()) {
                String s1 = boolExpr.toString();
                String s2 = this.toString();
                throw new IllegalPicksStateException("And: expr[" + i + "][" + s1 + "] of and expr [" + s2 + "] evals to false");
            }
        }

        for (int i = 0; i < exprList.size(); i++) {
            BoolExpr expr = exprList.get(i);
            boolean ch = expr.autoAssign(picks, true);
            if (ch) anyChange = true;
        }

        return anyChange;
    }

    private boolean autoAssignFalse(Picks picks) throws IllegalPicksStateException {
        Counts values = getValueCounts(picks);
        if (values.allTrueButOneUnassigned()) {
            return values.onlyUnassigned.autoAssign(picks, false);
        } else if (values.allTrue()) {
            throw new IllegalPicksStateException();
        } else {
            return false;
        }
    }


    @Override public String getSymbol() {
        return "AND";
    }


    @Override public List<Var> isConjunctionOfVars() {
        List<Var> vars = null;
        for (BoolExpr expr : exprList) {
            Var var = expr.asVar();
            if (var != null) {
                if (vars == null) vars = new ArrayList<Var>();
                vars.add(var);
            }
        }
        return vars;
    }

    @Override public Bit eval(PicksRO picks) {
        int unassignedCount = 0;
        for (BoolExpr expr : exprList) {
            Bit val = expr.eval(picks);
            if (val.isFalse()) return Bit.FALSE;
            if (val.isUnassigned()) unassignedCount++;
        }
        if (unassignedCount > 0) return Bit.UNASSIGNED;
        return Bit.TRUE;
    }


}
