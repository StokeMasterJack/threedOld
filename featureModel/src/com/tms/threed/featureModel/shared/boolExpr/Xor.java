package com.tms.threed.featureModel.shared.boolExpr;

import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.ChoiceField;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.FieldDef;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.featureModel.shared.picks.PicksRO;

import java.util.List;

/**
 * Must support a single child
 */
public class Xor extends Junction {

    private String name;

    public Xor(FeatureModel fm, String name, List<BoolExpr> expressions) {
        super(fm, expressions);
        this.name = name;
    }

    public Xor(FeatureModel fm, String name, BoolExpr... expressions) {
        super(fm, expressions);
        this.name = name;
    }

    public Xor(FeatureModel fm, String name) {
        super(fm);
        this.name = name;
    }

    public Xor(FeatureModel fm, List<BoolExpr> expressions) {
        super(fm, expressions);
    }

    public Xor(FeatureModel fm, BoolExpr... expressions) {
        super(fm, expressions);
    }

    public Xor(FeatureModel fm) {
        super(fm);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Or getOrPart() {
        return fm.or(exprList);
    }

    public And getNandsPart() {
        List<Nand> nandList1 = toPairList(Nand.class);
        List<BoolExpr> nandList2 = upCastList(nandList1);
        return fm.and(nandList2);
    }

    public And toOrAndNandPairs() {
        Or or = getOrPart();
        And and = getNandsPart();
        return fm.and(or, and);
    }


    @Override public String getSymbol() {
        return "XOR";
    }

    private ChoiceField fieldDef;

    public FieldDef isFieldDef(String className) {
        if (!this.hasClass(className)) return null;
        if (!this.containsOnlyVars()) return null;
        if (fieldDef == null) {
            fieldDef = new ChoiceField(this);
        }
        return fieldDef;
    }

    public FieldDef isFieldDef() {
        if (containsOnlyVars()) {
            return new ChoiceField(this);
        } else {
            return null;
        }
    }

    public boolean isXorPickOneGroup() {
        assert !exprList.isEmpty();
        BoolExpr expr = exprList.get(0);
        if (expr instanceof Var) {
            Var v = (Var) expr;
            return v.isPickOneChild();
        } else {
            return false;
        }
    }

    @Override public boolean autoAssign(Picks picks, boolean value) {
        if (value) {
            return autoAssignTrue(picks);
        } else {
            return autoAssignFalse(picks);
        }
    }

    private boolean autoAssignFalse(Picks picks) {
        throw new UnsupportedOperationException("Xor.autoAssignFalse[" + picks + "]");
    }

    private boolean autoAssignTrue(Picks picks) {
        Counts values = getValueCounts(picks);

        if (values.trueCount == 1) {
            boolean anyChange = false;
            for (BoolExpr expr : exprList) {
                Bit val = expr.eval(picks);
                if (val.isUnassigned()) {
                    boolean ch = expr.autoAssign(picks, false);
                    if (ch) anyChange = true;
                }
            }
            return anyChange;
        } else if (values.allFalseButOneUnassigned()) {
            return values.onlyUnassigned.autoAssign(picks, true);
        } else {
            return false;
        }
    }

    @Override public Bit eval(PicksRO picks) {
        if (getExpressionCount() == 0) {
            return Bit.TRUE;
        } else if (getExpressionCount() == 1) {
            BoolExpr onlyExpr = exprList.get(0);
            return onlyExpr.eval(picks);
        } else {
            int trueCount = 0;
            for (BoolExpr boolExpr : exprList) {
                Bit value = boolExpr.eval(picks);
                if (value.isUnassigned()) return Bit.UNASSIGNED;
                if (value.isTrue()) trueCount++;
            }
            if (trueCount == 1) {
                return Bit.TRUE;
            }
            return Bit.FALSE;
        }
    }

}