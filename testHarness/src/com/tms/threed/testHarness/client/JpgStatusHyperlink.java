package com.tms.threed.testHarness.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;

public class JpgStatusHyperlink extends Anchor{

    public static String url = "/jpgGenerator/submit.jsp";

    public JpgStatusHyperlink() {
        super("JPG Gen Status");

        addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                Window.open(url, "jpgGenStatus", null);
            }
        });
    }


}