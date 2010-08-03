package com.tms.threed.featureModel.shared.boolExpr;

import com.tms.threed.featureModel.shared.FeatureModel;

public abstract class Unary extends BoolExpr {

    protected BoolExpr expr;

    public Unary(FeatureModel fm, BoolExpr expr) {
        super(fm);
        assert expr != null;
        this.expr = expr.simplify();
    }

    public void accept(BoolExprVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unary that = (Unary) o;
        return expr.equals(that.expr);
    }



    abstract public String getSymbol();

    @Override
    public int hashCode() {
        return expr.hashCode();
    }

    public BoolExpr getExpr() {
        return expr;
    }

    @Override public String toString() {
        return "(" + getSymbol() + expr + ")";
    }

}
