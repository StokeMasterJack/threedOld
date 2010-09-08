package com.tms.threed.previewPane.client.externalState.picks;

import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.picks.IllegalPicksStateException;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.featureModel.shared.picks.PicksChangeEvent;
import com.tms.threed.featureModel.shared.picks.PicksChangeHandler;
import com.tms.threed.featureModel.shared.picks.PicksSnapshot;
import com.tms.threed.threedModel.client.RawPicksSnapshot;
import com.tms.threed.threedModel.client.VarPicksSnapshot;
import com.tms.threed.util.gwt.client.Console;
import com.tms.threed.util.gwt.client.MvcModel;
import com.tms.threed.util.lang.shared.Objects;

public class PicksChangeHandlers extends MvcModel {

    private final FeatureModel featureModel;

    public RawPicksSnapshot currentRawPicks;
    public VarPicksSnapshot currentVarPicks;
    public PicksSnapshot currentFixedPicks;

    public PicksChangeHandlers(FeatureModel featureModel) {
        this.featureModel = featureModel;
    }

    public void fire(RawPicksSnapshot newRawPicks) {
        assert newRawPicks != null;
        if (!rawPicksChanged(newRawPicks)) return;


        this.currentRawPicks = newRawPicks;
        VarPicksSnapshot newVarPicks = null;
        try {
            newVarPicks = VarPicksSnapshot.createVarPicksSnapshot(newRawPicks, featureModel);
        } catch (VarPicksSnapshot.UnknownVarCodeFromLeftSideException e) {
            Console.error("\t" + e);
            return;
        }
        if (!varPicksChanged(newVarPicks)) return;

        VarPicksSnapshot oldVarPicks = currentVarPicks;
        currentVarPicks = newVarPicks;

        PicksSnapshot oldFixedPicks = currentFixedPicks;
        Picks newPicks = newVarPicks.createPicks();

        try {
            newPicks.fixup();
            Console.log("\tPicks are valid");
        } catch (IllegalPicksStateException e) {
            Console.log("\t\t " + e);
            return;
        }

        currentFixedPicks = newPicks.createSnapshot();
        firePicksChangeEvent(newVarPicks, oldVarPicks, oldFixedPicks, currentFixedPicks);
    }

    private boolean varPicksChanged(VarPicksSnapshot newVarPicks) {
        return Objects.ne(currentVarPicks, newVarPicks);
    }

    private boolean rawPicksChanged(RawPicksSnapshot newRawPicks) {
        return Objects.ne(currentRawPicks, newRawPicks);
    }

    private void firePicksChangeEvent(
            VarPicksSnapshot newVarPicks,
            VarPicksSnapshot oldVarPicks,
            PicksSnapshot oldFixedPicks,
            PicksSnapshot newFixedPicks) {

        RawPicksChangeEvent rawPicksEvent = new RawPicksChangeEvent(oldVarPicks, newVarPicks, oldFixedPicks, newFixedPicks);

        PicksChangeEvent picksChangeEvent = new PicksChangeEvent(oldFixedPicks, newFixedPicks, rawPicksEvent.getBlinkAccessory());
        try {
            fireEvent(picksChangeEvent);
        } catch (Exception e) {
            Console.log("Unexpected exception dispatching PicksChangeEvent[" + e + "]");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public HandlerRegistration addPicksChangeHandler(PicksChangeHandler h) {
        return addHandler(PicksChangeEvent.TYPE, h);
    }

}

