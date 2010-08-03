package com.tms.threed.featureModel.shared.picks;

import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.ProposePickResponse;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.boolExpr.BoolExpr;

import java.util.List;

public class PickTester {

    public ProposePickResponse proposePick(Picks picks, Var var, boolean proposedValue) {

        Picks picksCopy = picks.copyIgnoreFixupPicks();
        picksCopy.userAssign(var, proposedValue);
        try {
            picksCopy.fixup();
            return new ProposePickResponse(true);
        } catch (IllegalPicksStateException e) {
            return new ProposePickResponse(false);
        }

        //is this still necessary??
        //ProposePickResponse response = checkValid(picksCopy);

//        return response;
    }

    public ProposePickResponse checkValid(Picks picks) {
        FeatureModel fm = picks.getFeatureModel();


        BoolExpr rootConstraint = fm.getRootConstraint();
        List<BoolExpr> treeConstraint = fm.getTreeConstraint().getExprList();
        List<BoolExpr> cardConstraint = fm.getCardinalityConstraint().getExprList();
        List<BoolExpr> extraConstraint = fm.getExtraConstraint().getExprList();

        ProposePickResponse response = new ProposePickResponse();

        {

            Bit sat = rootConstraint.eval(picks);
            if (sat.isFalse()) {
                response.valid = false;
                return response;
            }
        }

        for (BoolExpr expr : treeConstraint) {
            Bit sat = expr.eval(picks);
            if (sat.isFalse()) {
                response.valid = false;
                return response;
            }
        }

        for (BoolExpr expr : cardConstraint) {
            Bit sat = expr.eval(picks);
            if (sat.isFalse()) {
                response.valid = false;
                return response;
            }
        }

        for (BoolExpr expr : extraConstraint) {
            Bit sat = expr.eval(picks);
            if (sat.isFalse()) {
                System.out.println(expr);
                response.valid = false;
                return response;
            }
        }

        response.valid = true;
        return response;
    }

}
