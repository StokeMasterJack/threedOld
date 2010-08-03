package com.tms.threed.featureModel.shared.picks;

import com.google.gwt.event.shared.GwtEvent;
import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.Var;

import javax.annotation.Nonnull;
import java.util.List;

import static com.tms.threed.util.lang.shared.Strings.getSimpleName;

public class PicksChangeEvent extends GwtEvent<PicksChangeHandler> {

    @Nonnull private final PicksSnapshot oldPicks;
    @Nonnull private final PicksSnapshot newPicks;
    @Nonnull private final Var mostRecentSinglePick;

    public PicksChangeEvent(PicksSnapshot oldPicks, PicksSnapshot newPicks) {
        this.oldPicks = oldPicks;
        this.newPicks = newPicks;
        this.mostRecentSinglePick = null;
    }

    public PicksChangeEvent(PicksSnapshot oldPicks, PicksSnapshot newPicks, Var mostRecentSinglePick) {
        this.oldPicks = oldPicks;
        this.newPicks = newPicks;
        this.mostRecentSinglePick = mostRecentSinglePick;
    }

    public boolean isBlinkEvent() {
        if (mostRecentSinglePick == null) return false;
        return mostRecentSinglePick.isAccessory();
    }

    public Var getBlinkAccessory() {
        if (mostRecentSinglePick == null) return null;
        if (mostRecentSinglePick.isAccessory()) return mostRecentSinglePick;
        return null;
    }

    public static final Type<PicksChangeHandler> TYPE = new Type<PicksChangeHandler>();

    @Override public Type<PicksChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override protected void dispatch(PicksChangeHandler handler) {
        handler.onPicksChange(this);
    }

    public PicksSnapshot getOldPicks() {
        return oldPicks;
    }

    public PicksSnapshot getNewPicks() {
        return newPicks;
    }

    @Override public String toString() {
        return getSimpleName(this) + " [" + newPicks + "]";
    }

    public boolean isDirty(Var var) {
        if (var.isLeaf()) {
            Bit oldValue = oldPicks.get(var);
            Bit newValue = newPicks.get(var);
            return !oldValue.equals(newValue);
        } else {
            List<Var> childVars = var.getChildVars();
            for (Var childVar : childVars) {
                boolean childDirty = isDirty(childVar);
                if (childDirty) return true;
            }
            return false;
        }

    }


}