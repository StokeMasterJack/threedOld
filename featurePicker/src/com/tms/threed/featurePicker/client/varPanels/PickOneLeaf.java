package com.tms.threed.featurePicker.client.varPanels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.tms.threed.featureModel.shared.ProposePickResponse;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.picks.PickTester;
import com.tms.threed.featurePicker.client.VarPanel;
import com.tms.threed.featurePicker.client.VarPanelModel;
import com.tms.threed.util.gwt.client.Console;

import javax.annotation.Nonnull;

public class PickOneLeaf extends VarPanel {

    private final Var groupVar;

    private RadioButton radioButton;

    public PickOneLeaf(@Nonnull final VarPanelModel context, @Nonnull final Var var) {
        super(context, var);
        assert var.isPickOneChild();
        groupVar = var.getParent();

        String radioGroupId = groupVar.getCode();
        String radioLabel = "<b>" + var.getCode() + "</b>: " + var.getName();
        radioButton = new RadioButton(radioGroupId, radioLabel, true);

        FlowPanel mainPanel = new FlowPanel();
        mainPanel.add(radioButton);

        initWidget(mainPanel);

        radioButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        picks.resetAutoAssignments();
                        picks.pick(var);
                        picks.fixup();
                        picks.firePicksChangeEvent();
                    }
                });

            }
        });
    }

    private PickTester pickTester = new PickTester();
    
    public void refresh() {
        ProposePickResponse response = pickTester.proposePick(picks, var, true);

        if (response.valid) {
            getElement().getStyle().setColor("black");
        } else {
            getElement().getStyle().setColor("#BBBBBB");
            String responseText = response.toString();
            setTitle(responseText);
        }
        Boolean value = picks.isTrue(var);
        radioButton.setValue(value);
    }
}
