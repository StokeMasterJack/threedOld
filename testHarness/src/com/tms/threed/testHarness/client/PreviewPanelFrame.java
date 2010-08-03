package com.tms.threed.testHarness.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.tms.threed.previewPanel.client.PreviewPanel;
import com.tms.threed.threedCore.shared.ViewSnap;

public class PreviewPanelFrame extends FlowPanel {

    public static final int PREFERRED_WIDTH_PX = PreviewPanel.PREFERRED_WIDTH_PX + 2;

    private final HeaderPanel headerPanel = new HeaderPanel();

    private final FooterPanel footerPanel = new FooterPanel();

    public PreviewPanelFrame(PreviewPanel previewPanel) {
        ensureDebugId("PreviewPanelFrame");
        add(headerPanel);
        add(previewPanel);
        add(footerPanel);

        getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        getElement().getStyle().setBorderColor("black");
        getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
        setVisible(false);


    }


    public void setPngMode(boolean pngMode) {
        String prefix = pngMode ? "PNG" : "JPG";
        String modeText = prefix + " Mode";
        this.headerPanel.setModeText(modeText);
        this.footerPanel.setModeText(modeText);
    }

    private class HeaderPanel extends FlowPanel {

        private Label label = new Label("PNG Mode");


        private HeaderPanel() {
            add(label);
            ensureDebugId("PreviewPanelFrame.HeaderPanel");


            Style lStyle = label.getElement().getStyle();
            lStyle.setFontWeight(Style.FontWeight.BOLD);
            lStyle.setPadding(.2, Style.Unit.EM);

        }

        public void setModeText(String mode) {
            label.setText(mode);
        }
    }

    private class FooterPanel extends FlowPanel {

        private FlexTable t = new FlexTable();

        private final Label viewValue = createValue("ViewName");
        private final Label angleValue = createValue("AngleValue");
        private final Label modeValue = createValue("ModeValue");

        private FooterPanel() {
            Style style = getElement().getStyle();
            style.setProperty("borderTop", "solid 1px black");
            style.setPadding(.5, Style.Unit.EM);

            add(t);

            t.setWidget(0, 0, createLabel("View"));
            t.setWidget(1, 0, createLabel("Angle"));
            t.setWidget(2, 0, createLabel("Mode"));

            t.setWidget(0, 1, viewValue);
            t.setWidget(1, 1, angleValue);
            t.setWidget(2, 1, modeValue);


//            getElement().getStyle().setBackgroundColor("red");
            ensureDebugId("PreviewPanelFrame.FooterPanel");
        }

        private Label createLabel(String text) {
            Label l = new Label(text + ": ");
            l.getElement().getStyle().setPadding(.2, Style.Unit.EM);
            l.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
            return l;
        }

        private Label createValue(String text) {
            Label l = new Label(text);
            l.getElement().getStyle().setPadding(.2, Style.Unit.EM);
            return l;
        }

        void setViewState(ViewSnap viewState) {
            viewValue.setText(viewState.getView().getLabel());
            angleValue.setText(viewState.getAnglePadded());
        }

        void setModeText(String mode) {
            modeValue.setText(mode);
        }

    }

    public void setViewState(ViewSnap viewState) {
        footerPanel.setViewState(viewState);
    }

}
