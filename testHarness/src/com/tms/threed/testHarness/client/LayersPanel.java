package com.tms.threed.testHarness.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.tms.threed.imageModel.shared.ImLayer;
import com.tms.threed.imageModel.shared.ImPng;

import java.util.Collections;
import java.util.List;

public class LayersPanel extends Composite {

    private FlowPanel buttonPanel = createButtonPanel();
    private FlowPanel layerPanel = createLayerPanel();
    private FlowPanel mainPanel = createMainPanel();

    private final LayersPanelModel model;

    public LayersPanel(LayersPanelModel model) {
        this.model = model;


//            t.setBorderWidth(1);

//            HTML title = new HTML();
//            title.setHTML("<div style='font-weight:bold;margin-left:.2em;text-align:center;'>Layers</div>");
//            vp.add(title);


//        refresh();




//            vp.setBorderWidth(1);

        initWidget(mainPanel);
    }

    private FlowPanel createMainPanel() {
        FlowPanel p = new FlowPanel();
        p.getElement().getStyle().setPaddingLeft(.5, Style.Unit.EM);
        p.getElement().getStyle().setPaddingRight(.5, Style.Unit.EM);
        p.getElement().getStyle().setPaddingTop(.2, Style.Unit.EM);
        p.getElement().getStyle().setPaddingBottom(.5, Style.Unit.EM);

        ScrollPanel scrollPanel = new ScrollPanel(layerPanel);
        scrollPanel.setHeight("100%");

        p.add(buttonPanel);
        p.add(scrollPanel);

//        p.setWidth("50%");
        return p;
    }

    private FlowPanel createButtonPanel() {
        FlowPanel p = new FlowPanel();

//        p.getElement().getStyle().setBackgroundColor("pink");

        Anchor selectAllLink = new Anchor("Select All");
        Anchor selectNoneLink = new Anchor("Select None");

        selectAllLink.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                DeferredCommand.addCommand(new Command() {
                    @Override public void execute() {
                        model.selectAll();
                    }
                });

            }
        });


        selectNoneLink.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                DeferredCommand.addCommand(new Command() {
                    @Override public void execute() {
                        model.selectNone();
                    }
                });
            }
        });


        selectAllLink.getElement().getStyle().setMarginLeft(.2, Style.Unit.EM);
        selectAllLink.getElement().getStyle().setMarginRight(.5, Style.Unit.EM);

        p.add(selectAllLink);
        p.add(selectNoneLink);

        return p;
    }

    private FlowPanel createLayerPanel() {
        FlowPanel p = new FlowPanel();
        return p;
    }


    public void refresh() {
        layerPanel.clear();

        FlowPanel g = new FlowPanel();

        List<ImLayer> layers = model.getLayers();

        Collections.sort(layers);

        for (final ImLayer layer : layers) {

            final ImPng png = model.getPngForLayer(layer);


            boolean hasPng = png != null;
            boolean isEmpty = hasPng && png.isEmpty();


            String htmlLabel = layer.getShortName();

            CheckBox checkBox = new CheckBox(htmlLabel);
            checkBox.setValue(layer.isVisible());
            checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override public void onValueChange(ValueChangeEvent<Boolean> e) {
                    DeferredCommand.addCommand(new Command() {
                        @Override public void execute() {
                            model.toggleLayer(layer);
                        }
                    });
                }
            });


            Style checkboxStyle = checkBox.getElement().getStyle();
            Style inputStyle = checkBox.getElement().getElementsByTagName("input").getItem(0).getStyle();
            Style labelStyle = checkBox.getElement().getElementsByTagName("label").getItem(0).getStyle();

            inputStyle.setMarginRight(1, Style.Unit.EM);


            String title;
            String color;
            if (hasPng) {
                if (isEmpty) {
                    color = "#CCCCCC";
                    title = "Empty PNG: " + png.getViewRelativePath() + "";
                } else {
                    color = "black";
                    title = png.getViewRelativePath() + "";
                }
            } else {
                color = "#DDDDDD";
                title = "No PNG";
            }
            checkBox.setTitle(title);

            checkboxStyle.setColor(color);
            checkboxStyle.setDisplay(Style.Display.BLOCK);


            g.add(checkBox);

            layerPanel.add(g);
        }

    }

}

