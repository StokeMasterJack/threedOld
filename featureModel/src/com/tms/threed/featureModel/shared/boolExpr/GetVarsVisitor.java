package com.tms.threed.featureModel.shared.boolExpr;

import com.tms.threed.featureModel.shared.Var;

import java.util.HashSet;
import java.util.Set;

public class GetVarsVisitor extends BoolExprVisitor {

    private Set<Var> vars = new HashSet<Var>();

    @Override protected void visitImpl(Junction junction) {
        for (BoolExpr expr : junction.exprList) {
            expr.accept(this);
        }
    }

    @Override protected void visitImpl(Pair pair) {
        pair.getExpr1().accept(this);
        pair.getExpr2().accept(this);
    }

    @Override protected void visitImpl(Unary unary) {
        unary.getExpr().accept(this);
    }

    @Override protected void visitImpl(Constant constant) {

    }

    @Override protected void visitImpl(Var var) {
        vars.add(var);
    }

    public Set<Var> getVars() {
        return vars;
    }
}