package com.tms.threed.featurePicker.client.varPanels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.tms.threed.featureModel.shared.ProposePickResponse;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.picks.PickTester;
import com.tms.threed.featurePicker.client.VarPanel;
import com.tms.threed.featurePicker.client.VarPanelModel;

import javax.annotation.Nonnull;

/**
 * AKA Boolean Field
 */
public class OptionLeaf extends VarPanel {

    private CheckBox checkBox;

    public OptionLeaf(@Nonnull VarPanelModel context, @Nonnull Var var) {
        super(context, var);
        checkBox = initCheckBox();
        initWidget(initCheckboxPanel());
    }

    private Panel initCheckboxPanel() {
        FlowPanel t = new FlowPanel();
        t.add(checkBox);
        return t;
    }

    private CheckBox initCheckBox() {
        final CheckBox cb = new CheckBox("<b>" + var.getCode() + "</b>: " + var.getName(), true);
        cb.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                DeferredCommand.addCommand(new Command() {

                    public void execute() {
                        picks.resetAutoAssignments();
                        if (cb.getValue()) {
                            picks.pick(var);
                        } else {
                            picks.unpick(var);
                        }
                        picks.fixup();
                        picks.firePicksChangeEvent();
                    }
                });
            }
        });
        return cb;
    }

    private PickTester pickTester = new PickTester();

    public void refresh() {
        ProposePickResponse response;
        if (picks.isTrue(var)) {
            response = pickTester.proposePick(picks, var, false);
        } else {
            response = pickTester.proposePick(picks, var, true);
        }

        if (response.valid) {
            getElement().getStyle().setColor("black");
        } else {
            getElement().getStyle().setColor("#BBBBBB");
            setTitle(response.toString());
        }
        checkBox.setValue(picks.isTrue(var));
    }


}
