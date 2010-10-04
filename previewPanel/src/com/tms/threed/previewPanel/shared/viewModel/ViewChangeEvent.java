package com.tms.threed.previewPanel.shared.viewModel;

import com.google.gwt.event.shared.GwtEvent;
import com.tms.threed.threedCore.shared.ViewKey;

import static com.tms.threed.util.lang.shared.Strings.getSimpleName;

/**
 * The current view has changed
 */
public class ViewChangeEvent extends GwtEvent<ViewChangeHandler> {

    private final ViewKey newValue;

    public ViewChangeEvent(ViewKey newValue) {
        this.newValue = newValue;
    }

    public ViewKey getNewValue() {
        return newValue;
    }

    public static final GwtEvent.Type<ViewChangeHandler> TYPE = new GwtEvent.Type<ViewChangeHandler>();

    @Override
    public GwtEvent.Type<ViewChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override protected void dispatch(ViewChangeHandler handler) {
        handler.onChange(this);
    }

    @Override public String toString() {
        return getSimpleName(this) + " [" + newValue + "]";
    }
}
