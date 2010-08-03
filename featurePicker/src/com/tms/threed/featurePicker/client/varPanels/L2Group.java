package com.tms.threed.featurePicker.client.varPanels;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featurePicker.client.VarPanel;
import com.tms.threed.featurePicker.client.VarPanelModel;

import javax.annotation.Nonnull;
import java.util.List;

public class L2Group extends VarPanel {

    private final FlowPanel flowPanel = new FlowPanel();

    public L2Group(@Nonnull VarPanelModel context, @Nonnull Var var) {
        super(context, var);
        List<Var> vars = var.getChildVars();

        flowPanel.add(buildLabel(var.getName()));
        for (Var childVar : vars) {
            if(childVar.isDerived() && context.hideDerived()) continue;
            VarPanel varPanel = context.getVarPanel(childVar);
            flowPanel.add(varPanel);
        }

        initWidget(flowPanel);
        getElement().getStyle().setMarginBottom(1, Style.Unit.EM);
    }

    protected Widget buildLabel(String labelText) {
        Label w = new Label(labelText + ": ");
        w.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        w.getElement().getStyle().setMarginBottom(.2, Style.Unit.EM);

        return w;
    }


}