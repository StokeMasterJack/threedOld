package com.tms.threed.previewPane.client.series;

import com.google.gwt.event.shared.EventHandler;

public interface SeriesChangeHandler extends EventHandler {
    void onChange(SeriesChangeEvent e);
}