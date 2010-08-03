package com.tms.threed.featureModel.shared;

public class UnsatException extends RuntimeException {

    private String varCode;

    public UnsatException(Var unassignedVar) {
        super(unassignedVar.getName() + " all values for var [" + unassignedVar + "] make the constraint unsat");
        this.varCode = unassignedVar.getCode();
    }

    public String getVarCode() {
        return varCode;
    }
}
