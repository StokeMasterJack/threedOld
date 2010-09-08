package com.tms.threed.previewPanel.shared.viewModel;

import com.google.gwt.event.shared.GwtEvent;
import com.tms.threed.threedCore.shared.ViewSnap;

import static com.tms.threed.util.lang.shared.Strings.getSimpleName;

/**
 * Angle and View changed
 */
public class AngleAndViewChangeEvent extends GwtEvent<AngleAndViewChangeHandler> {

    private ViewSnap newViewSnap;

    public AngleAndViewChangeEvent(ViewSnap newViewSnap) {
        this.newViewSnap = newViewSnap;
    }

    public static final Type<AngleAndViewChangeHandler> TYPE = new Type<AngleAndViewChangeHandler>();

    @Override public Type<AngleAndViewChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override protected void dispatch(AngleAndViewChangeHandler handler) {
        handler.onChange(this);
    }

    public ViewSnap getNewViewSnapAngle() {
        return newViewSnap;
    }

    @Override public String toString() {
        return getSimpleName(this) + " [" + newViewSnap + "]";
    }

}
