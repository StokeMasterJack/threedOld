package com.tms.threed.featureModel.shared.boolExpr;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Var;

import java.util.Set;

public abstract class Pair extends BoolExpr {

    protected BoolExpr expr1;
    protected BoolExpr expr2;

    public Pair(FeatureModel fm, BoolExpr expr1, BoolExpr expr2) {
        super(fm);
        assert expr1 != null;
        assert expr2 != null;
        this.expr1 = expr1.simplify();
        this.expr2 = expr2.simplify();
    }

    public void accept(BoolExprVisitor visitor) {
        visitor.visit(this);
    }

    public abstract String getSymbol();

    @Override public String toString() {
        return "(" + expr1 + " " + getSymbol() + " " + expr2 + ")";
    }

    public BoolExpr getExpr1() {
        return expr1;
    }

    public BoolExpr getExpr2() {
        return expr2;
    }

    @Override
    public int hashCode() {
        int h1 = expr1.hashCode();
        int h2 = expr2.hashCode();
        int hh1 = Math.min(h1, h2);
        int hh2 = Math.max(h1, h2);
        return 31 * hh1 + hh2;
    }

    @Override public boolean equals(Object o) {
        if (o.getClass() == this.getClass()) {
            Pair that = (Pair) o;
            if (expr1.equals(that.expr1) && expr2.equals(that.expr2)) return true;
            else if (expr1.equals(that.expr2) && expr2.equals(that.expr1)) return true;
            else return false;
        } else {
            return false;
        }
    }


}