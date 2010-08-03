package com.tms.threed.testHarness.client;

import com.google.gwt.user.client.ui.SimplePanel;
import com.tms.threed.featurePicker.client.VarPanel;

import static com.tms.threed.util.lang.shared.Strings.getSimpleName;

public class FeaturePickerPanel extends SimplePanel{

    private VarPanel varPanel;

    public FeaturePickerPanel() {
        setSize("100%","100%");
//        getElement().getStyle().setBackgroundColor("blue");

        ensureDebugId(getSimpleName(this));
    }

    public void setVarPanel(VarPanel varPanel) {
        this.varPanel = varPanel;
        setWidget(varPanel);
//        varPanel.getElement().getStyle().setBackgroundColor("green");
        varPanel.setSize("100%","99%");
    }
}
