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

public class PickOneGroup extends VarPanel {

    private List<Var> childVars;
    private FlowPanel mainPanel;

    public PickOneGroup(@Nonnull VarPanelModel context, @Nonnull Var var) {
        super(context, var);
        assert var.isPickOneGroup();
        this.childVars = var.getChildVars();
        this.mainPanel = initMainPanel();
        initWidget(mainPanel);
        getElement().getStyle().setMarginBottom(1, Style.Unit.EM);
    }

    protected Widget buildLabel() {
        Label w = new Label(var.getName() + ": ");
        w.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        w.getElement().getStyle().setMarginBottom(.2, Style.Unit.EM);

        return w;
    }

    private FlowPanel initMainPanel() {
        FlowPanel p = new FlowPanel();
        p.add(buildLabel());
        for (Var childVar : childVars) {
            p.add(context.getVarPanel(childVar));
        }
        return p;
    }

//    public void refresh() {
//        for (int i = 0; i < mainPanel.getWidgetCount(); i++) {
//            Widget w = mainPanel.getWidget(i);
//            if (w instanceof PickOneLeaf) {
//                ((PickOneLeaf) w).refresh();
//            }
//        }
//    }


}

