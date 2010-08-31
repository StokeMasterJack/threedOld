package com.tms.threed.featureModel.shared;

public class ProposePickResponse {

    public boolean valid;
    public String errorMessage;

    public ProposePickResponse(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    @Override public String toString() {
        if(valid) return "Valid";
        else return errorMessage;
    }
}
