package com.tms.threed.featurePicker.client;

import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.featureModel.shared.picks.PicksChangeHandler;

public interface PicksInfo {

    boolean isValid();

    HandlerRegistration addPicksChangeHandler(PicksChangeHandler handler);
}
