package com.tms.threed.featureModel.client;

import com.google.gwt.core.client.JsArray;
import com.tms.threed.featureModel.shared.Cardinality;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.boolExpr.And;
import com.tms.threed.featureModel.shared.boolExpr.BoolExpr;
import com.tms.threed.featureModel.shared.boolExpr.Junction;
import com.tms.threed.threedCore.shared.*;

import java.util.List;

import static com.tms.threed.featureModel.shared.boolExpr.BoolExpr.*;

public final class JsonUnmarshaller {

    private FeatureModel fm;
    private final SeriesId seriesId;

    public JsonUnmarshaller(SeriesId seriesId) {
        this.seriesId = seriesId;
    }

    public FeatureModel createFeatureModelFromJson(JsFeatureModel jsFeatureModel) {
        fm = new FeatureModel(seriesId,jsFeatureModel.getDisplayName());
        mapVars(jsFeatureModel.getRootVar());
        mapConstraints(jsFeatureModel.getConstraints());
        return fm;
    }

    private void mapVars(JsVar jsRootVar) {
        mapVar(null, jsRootVar);
    }

    private void mapConstraints(JsBoolExpr rootConstraint) {
        BoolExpr boolExpr = map(rootConstraint);
        And and = (And) boolExpr;
        List<BoolExpr> exprList = and.getExprList();
        for (BoolExpr constraint : exprList) {
            fm.addConstraint(constraint);
        }
    }

    private void mapVar(Var parentVar, JsVar jsChildVar) {

        Var childVar;
        if (parentVar == null) {
            childVar = fm.getRootVar();
        } else {
            childVar = parentVar.addVar(jsChildVar.getCode(),jsChildVar.getName());
        }

        String sCardinality = jsChildVar.getCardinality();
        if (sCardinality != null) {
            Cardinality cardinality = Cardinality.valueOf(sCardinality);
            childVar.setCardinality(cardinality);
        }

        childVar.setDerived(jsChildVar.getDerived());
        childVar.setDefaultValue(jsChildVar.getDefaultValue());
        childVar.setManditory(jsChildVar.getManditory());

        JsArray<JsVar> jsChildNodes = jsChildVar.getChildNodes();
        if (jsChildNodes != null && jsChildNodes.length() > 0) {
            for (int i = 0; i < jsChildNodes.length(); i++) {
                mapVar(childVar, jsChildNodes.get(i));
            }
        }
    }

    public BoolExpr map(JsBoolExpr jsBoolExpr) {
        String type = jsBoolExpr.getType();
        if (isJunction(type)) {
            return mapJunction(jsBoolExpr);
        }
        else if (isPair(type)) {
            return mapPair(jsBoolExpr);
        }
        else if (isVar(type)) {
            return mapVar(jsBoolExpr);
        }
        else if (isNot(type)) {
            return mapNot(jsBoolExpr);
        }
        else{
            throw new IllegalStateException("Unknown type: [" + type + "]");
        }
    }

    private BoolExpr mapJunction(JsBoolExpr jsJunction) {
        String subtype = jsJunction.getSubtype();
        Junction junction;
        if (isAnd(subtype)) junction = fm.and();
        else if (isOr(subtype)) junction = fm.or();
        else if (isXor(subtype)) junction = fm.xor();
        else throw new IllegalStateException();

        JsArray<JsBoolExpr> jsExprList = jsJunction.getExprList();
        for (int i = 0; i < jsExprList.length(); i++) {
            junction.add(map(jsExprList.get(i)));
        }
        return junction;
    }

    private BoolExpr mapPair(JsBoolExpr jsPair) {
        String subtype = jsPair.getSubtype();

        JsBoolExpr jsExpr1 = jsPair.getExpr1();
        assert jsExpr1 != null;
        JsBoolExpr jsExpr2 = jsPair.getExpr2();
        assert jsExpr2 != null;

        BoolExpr expr1 = map(jsExpr1);
        assert expr1 != null : "map(" + jsExpr1.describe() + ") returned null";
        BoolExpr expr2 = map(jsExpr2);
        assert expr2 != null;

        if (isConflict(subtype)) return fm.conflict(expr1, expr2);
        else if (isIff(subtype)) return fm.iff(expr1, expr2);
        else if (isImplication(subtype)) return fm.imply(expr1, expr2);
        else if (isNand(subtype)) return fm.nand(expr1, expr2);
        else throw new IllegalStateException();
    }

    private BoolExpr mapNot(JsBoolExpr jsNot) {

        JsBoolExpr jsExpr = jsNot.getExpr();
        assert jsExpr != null;

        BoolExpr expr = map(jsExpr);
        assert expr != null : "map(" + jsExpr.describe() + ") returned null";

        return fm.not(expr);
        
    }

    private BoolExpr mapVar(JsBoolExpr jsVar) {
        String varCode = jsVar.getCode();
        Var var = fm.getVarOrNull(varCode);
        assert var != null : varCode + " was not in FeatureModel";
        return var;
    }


}