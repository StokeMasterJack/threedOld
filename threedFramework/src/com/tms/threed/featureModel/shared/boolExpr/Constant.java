package com.tms.threed.featureModel.shared.boolExpr;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.picks.Picks;

public abstract class Constant extends BoolExpr {

    public Constant(FeatureModel fm) {
        super(fm);
        throw new UnsupportedOperationException("Constant constructor");
    }

    public void accept(BoolExprVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null) return false;
        return this.getClass() == that.getClass();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override public boolean autoAssign(Picks picks,boolean value) {
        throw new UnsupportedOperationException("Constant.autoAssign[" + picks + "]");
    }




}