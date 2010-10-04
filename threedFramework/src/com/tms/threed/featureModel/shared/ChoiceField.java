package com.tms.threed.featureModel.shared;

import com.tms.threed.featureModel.shared.boolExpr.BoolExpr;
import com.tms.threed.featureModel.shared.boolExpr.Xor;

import java.util.ArrayList;
import java.util.List;

public class ChoiceField extends FieldDef {

    private String name;
    private List<Var> vars;


    public ChoiceField(String name, List<Var> vars) {
        this.name = name;
        this.vars = vars;
    }

    public ChoiceField(String name) {
        this.name = name;
    }

    public ChoiceField(Xor xor) {
        this.name = xor.getName();
        for (BoolExpr expr : xor.getExprList()) {
            Var v = (Var) expr;
            add(v);
        }
    }

    public void add(Var var) {
        if (vars == null) vars = new ArrayList<Var>();
        this.vars.add(var);
    }

    public String getName() {
        return name;
    }

    public List<Var> getVars() {
        return vars;
    }

    @Override public String toString() {
        return "ChoiceField: " + getName() + " " + vars;
    }
}
