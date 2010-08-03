package com.tms.threed.featurePicker.client.varPanels;

import com.google.gwt.user.client.ui.HTML;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featurePicker.client.VarPanel;
import com.tms.threed.featurePicker.client.VarPanelModel;

import javax.annotation.Nonnull;

public class MandatoryLeaf extends VarPanel {

    public MandatoryLeaf(@Nonnull VarPanelModel context, @Nonnull Var var) {
        super(context, var);
        initWidget(new HTML("X <b>" + var.getCode() + "</b>: " + var.getName()));
    }

}