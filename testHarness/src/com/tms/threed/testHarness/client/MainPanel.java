package com.tms.threed.testHarness.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;

import static com.tms.threed.util.lang.shared.Strings.getSimpleName;

public class MainPanel extends ResizeComposite {

    private FeaturePickerPanel featurePickerPanel;

    //    private LayoutPanel outer = new LayoutPanel();
    private DockLayoutPanel outer = new DockLayoutPanel(Style.Unit.EM);
    private DockLayoutPanel big = new DockLayoutPanel(Style.Unit.PX);

    private PreviewPanelFrame previewPanelFrame;

    private MenuBar mainMenuBar;

    private FlowPanel headerPanel;

    private LayersPanel layersPanel;

    public MainPanel(PreviewPanelFrame previewPanelFrame, LayersPanel layersPanel, FeaturePickerPanel featurePickerPanel, MenuBar mainMenuBar) {
        this.featurePickerPanel = featurePickerPanel;
        this.previewPanelFrame = previewPanelFrame;
        this.mainMenuBar = mainMenuBar;
        this.headerPanel = createHeaderPanel();
        this.layersPanel = layersPanel;

        StackLayoutPanel stackLayoutPanel = new StackLayoutPanel(Style.Unit.EM);
        stackLayoutPanel.add(layersPanel, "Layers", 2.2);

        outer.addNorth(headerPanel, 2);
        outer.add(big);

        big.getElement().getStyle().setMarginTop(.3, Style.Unit.EM);
        big.getElement().getStyle().setMarginBottom(.8, Style.Unit.EM);
        big.getElement().getStyle().setMarginLeft(.8, Style.Unit.EM);
        big.getElement().getStyle().setMarginRight(.8, Style.Unit.EM);

        SplitLayoutPanel splitLayoutPanel = new SplitLayoutPanel();
        splitLayoutPanel.addWest(featurePickerPanel, 300);
        splitLayoutPanel.add(stackLayoutPanel);
        splitLayoutPanel.getElement().getStyle().setMarginRight(1, Style.Unit.EM);

        big.addEast(previewPanelFrame, PreviewPanelFrame.PREFERRED_WIDTH_PX);
        big.add(splitLayoutPanel);

        initWidget(outer);
        ensureDebugId(getSimpleName(this));

        big.setVisible(false);
    }

    private FlowPanel createHeaderPanel() {
        FlowPanel p = new FlowPanel();
        p.ensureDebugId("headerPanel");
        Style style = p.getElement().getStyle();
//        style.setBackgroundColor("orange");
//        style.setPaddingTop(.4, Style.Unit.EM);
//        style.setPaddingBottom(.1, Style.Unit.EM);
//        style.setPaddingLeft(1, Style.Unit.EM);
//        style.setPaddingRight(1, Style.Unit.EM);
        p.add(mainMenuBar);
        return p;
    }

    public void showSeriesPanel() {
        getPreviewPanelFrame().setVisible(true);
        big.setVisible(true);
    }

    public PreviewPanelFrame getPreviewPanelFrame() {
        return previewPanelFrame;
    }
}