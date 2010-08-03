package com.tms.threed.testHarness.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.tms.threed.threedCore.shared.SeriesKey;

public class JpgGenHyperlink extends Anchor{

    public static String jpgGenUrlTemplate = "/jpgGenerator/submit.jsp?seriesName=${seriesName}&seriesYear=${seriesYear}";

    private String url;

    public JpgGenHyperlink() {
        super("Generate JPG's");

        addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                if(url == null) Window.alert("Please pick a series first.");
                else Window.open(url, "jpgGen", null);
            }
        });
    }

    public void setSeries(SeriesKey seriesKey){
        this.url = jpgGenUrlTemplate.replace("${seriesName}", seriesKey.getName()).replace("${seriesYear}", seriesKey.getYear() + "");
        this.setText("Generate JPGs for " + seriesKey.getLabel());
    }


}
