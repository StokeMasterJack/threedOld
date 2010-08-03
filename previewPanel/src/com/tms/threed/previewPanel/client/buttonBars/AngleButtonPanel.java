package com.tms.threed.previewPanel.client.buttonBars;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import static com.tms.threed.util.lang.shared.Strings.getSimpleName;

abstract public class AngleButtonPanel extends Composite {

    @Override protected void initWidget(Widget widget) {
        super.initWidget(widget);
        setVisible(false);
        ensureDebugId(getSimpleName(this));
    }

    public abstract int getPreferredHeightPx();

}

