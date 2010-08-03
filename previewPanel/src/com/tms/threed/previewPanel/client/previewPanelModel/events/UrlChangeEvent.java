package com.tms.threed.previewPanel.client.previewPanelModel.events;

import com.tms.threed.util.gwt.client.events2.SimpleEvent;
import com.tms.threed.util.lang.shared.Path;

public class UrlChangeEvent extends SimpleEvent {

    private final Path newUrl;

    public UrlChangeEvent(Path newUrl) {
        this.newUrl = newUrl;
    }

    public Path getNewUrl() {
        return newUrl;
    }
}
