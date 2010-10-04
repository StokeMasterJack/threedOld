package com.tms.threed.featureModel.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import static com.tms.threed.featureModel.shared.boolExpr.BoolExpr.isVar;

public final class JsBoolExpr extends JavaScriptObject {

    protected JsBoolExpr() {
    }

    public native String getType() /*-{
       return this.type;
    }-*/;

    public native String getSubtype() /*-{
       return this.subtype;
    }-*/;

    /**
     * Applies only to BoolExpr where type=Junction
     */
    public native JsArray<JsBoolExpr> getExprList() /*-{
       return this.exprList;
    }-*/;

    /**
     * Applies only to BoolExpr where type=Pair
     */
    public native JsBoolExpr getExpr1() /*-{
       return this.expr1;
    }-*/;

    /**
     * Applies only to BoolExpr where type=Pair
     */
    public native JsBoolExpr getExpr2() /*-{
       return this.expr2;
    }-*/;

    /**
     * Applies only to BoolExpr where type=Not
     */
    public native JsBoolExpr getExpr() /*-{
       return this.expr;
    }-*/;

     /**
     * Applies only to BoolExpr where type=Var
     */
    public native String getCode() /*-{
       return this.code;
    }-*/;


    public String describe() {
        String part1 = getType() + "/" + getSubtype();

        String part2 = "";
        if(isVar(getType())) part2 = "code[" + getCode() + "]";

        return part1 + " " + part2;
    }

    

}

