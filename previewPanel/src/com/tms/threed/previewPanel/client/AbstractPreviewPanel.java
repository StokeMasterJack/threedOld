package com.tms.threed.previewPanel.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.tms.threed.util.lang.shared.Path;

public abstract class AbstractPreviewPanel extends Composite {

    public abstract int getPreferredWidthPx();

    public abstract int getPreferredHeightPx();
}
