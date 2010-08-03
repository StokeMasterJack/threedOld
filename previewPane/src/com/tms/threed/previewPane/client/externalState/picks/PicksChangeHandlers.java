package com.tms.threed.previewPane.client.externalState.picks;

import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.featureModel.shared.FeatureModel;
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

    public void processPicksChange(RawPicksSnapshot newRawPicks) {
        assert newRawPicks != null;
        if (!rawPicksChanged(newRawPicks)) return;

        Console.log("PicksChanged: " + newRawPicks.toString());

        this.currentRawPicks = newRawPicks;
        VarPicksSnapshot newVarPicks = VarPicksSnapshot.createVarPicksSnapshot(newRawPicks, featureModel);
        if (!varPicksChanged(newVarPicks)) return;

        VarPicksSnapshot oldVarPicks = currentVarPicks;
        currentVarPicks = newVarPicks;

        PicksSnapshot oldFixedPicks = currentFixedPicks;
        Picks newPicks = newVarPicks.createPicks();
        newPicks.fixup();

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
        fireEvent(picksChangeEvent);
    }

    public HandlerRegistration addPicksChangeHandler(PicksChangeHandler h) {
        return addHandler(PicksChangeEvent.TYPE, h);
    }

}

