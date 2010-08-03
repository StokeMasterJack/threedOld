package com.tms.threed.featureModel.server;

import com.google.common.collect.Sets;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.boolExpr.BoolExpr;

import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SFeatureModel  {

    private FeatureModel featureModel;

    public SFeatureModel(FeatureModel featureModel) {
        this.featureModel = featureModel;
    }

    public FeatureModel getFeatureModel() {
        return featureModel;
    }

    public ObjectNode toJsonNode() {
        JsonMarshaller mapper = new JsonMarshaller();
        return mapper.mapFeatureModel(featureModel);
    }

    public String toJsonString() {
        return toJsonNode().toString();
    }

    public void prettyPrintJson() throws IOException {
        JsonMarshaller.prettyPrint(toJsonNode());
    }

    public static void compareFeatureModels(FeatureModel fmA, FeatureModel fmB) throws Exception {
        Set aVarSet = new HashSet(fmA.getVars());
        Set<BoolExpr> aEcExprSet = fmA.getExtraConstraint().getExprSet();
        Set<BoolExpr> aTcExprSet = fmA.getTreeConstraint().getExprSet();


        System.out.println("FM A: ");
//        System.out.println("\tSatCount: " + fmA.satCount());
        System.out.println("\tTreeConstraintCount: " + aTcExprSet.size());
        System.out.println("\tExtraConstraintCount: " + aEcExprSet.size());
        System.out.println();


        Set bVarSet = new HashSet(fmB.getVars());
        Set<BoolExpr> bEcExprSet = fmB.getExtraConstraint().getExprSet();
        Set<BoolExpr> bTcExprSet = fmB.getTreeConstraint().getExprSet();

        System.out.println("FM B: ");
//        System.out.println("\tSatCount: " + fmB.satCount());
        System.out.println("\tTreeConstraintCount: " + bTcExprSet.size());
        System.out.println("\tExtraConstraintCount: " + bEcExprSet.size());

        System.out.println();
        System.out.println("Diffs:");

        System.out.println("\tVar Diff: A-B: " + Sets.difference(aVarSet, bVarSet));
        System.out.println("\tVar B-A: " + Sets.difference(bVarSet, aVarSet));

        System.out.println("\tEC A-B: " + Sets.difference(aEcExprSet, bEcExprSet));
        System.out.println("\tEC B-A: " + Sets.difference(bEcExprSet, aEcExprSet));

        System.out.println("\tTC A-B: " + Sets.difference(aTcExprSet, bTcExprSet));
        System.out.println("\tTC B-A: " + Sets.difference(bTcExprSet, aTcExprSet));
    }




}
