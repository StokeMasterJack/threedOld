package com.tms.threed.featureModel.shared.picks;

import com.tms.threed.featureModel.shared.ProposePickResponse;
import com.tms.threed.featureModel.shared.Var;

public class PickTester {

    public ProposePickResponse proposePick(Picks picks, Var var, boolean proposedValue) {
        Picks picksCopy = picks.copyIgnoreFixupPicks();
        picksCopy.userAssign(var, proposedValue);
        picks.errorMessage = null;
        picks.valid = null;
        try {
            picksCopy.fixup();
            return new ProposePickResponse(true, null);
        } catch (IllegalPicksStateException e) {
            return new ProposePickResponse(false, e.getMessage());
        }

    }

}
