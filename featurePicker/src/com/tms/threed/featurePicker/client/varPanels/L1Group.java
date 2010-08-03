package com.tms.threed.featurePicker.client.varPanels;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featurePicker.client.VarPanel;
import com.tms.threed.featurePicker.client.VarPanelModel;

import javax.annotation.Nonnull;
import java.util.List;

public class L1Group extends VarPanel {

    public L1Group(@Nonnull VarPanelModel context, @Nonnull Var var) {
        super(context, var);
        List<Var> vars = var.getChildVars();

        FlowPanel flowPanel = new FlowPanel();
        for (Var childVar : vars) {
            if(childVar.isDerived() && context.hideDerived()) continue;
            VarPanel varPanel = context.getVarPanel(childVar);
            flowPanel.add(varPanel);
        }

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setWidget(flowPanel);

        initWidget(scrollPanel);

        getElement().getStyle().setPadding(.5, Style.Unit.EM);
    }

}