package com.tms.threed.previewPanel.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tms.threed.util.gwt.client.Console;
import com.tms.threed.util.lang.shared.Path;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ThreedImagePanel extends Composite {

    private final HandlerManager baseJpgLoadHandlers = new HandlerManager(this);
    private final HandlerManager baseJpgErrorHandlers = new HandlerManager(this);

    public final int widthPx;// = 599;
    public final int heightPx;// = 366;

    private final AbsolutePanel p = new AbsolutePanel();

    private final int panelIndex;

    private final List<Image> images = new ArrayList<Image>();

    public ThreedImagePanel(int panelIndex, int widthPx, int heightPx) {
        this(panelIndex, widthPx, heightPx, false);
    }

    public ThreedImagePanel(int panelIndex, int widthPx, int heightPx, boolean showDefaultPlaceHolder) {
        this.panelIndex = panelIndex;
        this.widthPx = widthPx;
        this.heightPx = heightPx;
        initWidget(p);
        setPixelSize(widthPx, heightPx);
        ensureDebugId("ThreedImagePanel");

        if (showDefaultPlaceHolder) setPlaceHolder("Loading vehicle image...");

    }

    public HandlerRegistration addBaseJpgLoadHandler(LoadHandler loadHandler) {
        return baseJpgLoadHandlers.addHandler(LoadEvent.getType(), loadHandler);
    }

    public HandlerRegistration addBaseJpgErrorHandler(ErrorHandler errorHandler) {
        return baseJpgErrorHandlers.addHandler(ErrorEvent.getType(), errorHandler);
    }

    public void setPlaceHolder() {
        setPlaceHolder("Place Holder", "#CCCCCC");
    }

    public void setPlaceHolder(String text) {
        setPlaceHolder(text, null);
    }

    public void setPlaceHolder(String text, String backgroundColor) {
        Grid grid = new Grid(1, 1);
        if (text != null) grid.setText(0, 0, text);
        grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
        Style style = grid.getElement().getStyle();
        if (backgroundColor != null) style.setBackgroundColor(backgroundColor);
        setPlaceHolder(grid);
    }

    public void setPlaceHolder(Widget widget) {
        p.clear();
        widget.setSize("100%", "100%");
        p.add(widget, 0, 0);
    }


    public void setImageUrls(@Nonnull List<Path> urls) {
        assert urls != null;
        assert assertAllNonNull(urls);

        if (urls == null) return;

        Console.log("ThreedImagePanel[" + panelIndex + "].setImageUrls[" + urls.get(0) + "]");

        final List<Image> oldImages = new ArrayList<Image>(images);

        images.clear();
        for (int i = 0; i < urls.size(); i++) {
            final Path url = urls.get(i);
            final Image image = new Image();
            images.add(image);


            final boolean isFirst = (i == 0);
            image.addLoadHandler(new LoadHandler() {
                @Override public void onLoad(LoadEvent event) {
                    if (isFirst) {
                        Console.log("\t ThreedImagePanel[" + panelIndex + "] base image loaded[" + url + "]");
                        for (int j = 0; j < oldImages.size(); j++) {
                            Image oldImage = oldImages.get(j);
                            p.remove(oldImage);
                        }
                        oldImages.clear();
                        baseJpgLoadHandlers.fireEvent(event);
                    }

                    image.setVisible(true);
                }
            });

            image.addErrorHandler(new ErrorHandler() {
                @Override public void onError(ErrorEvent event) {
                    if (isFirst) {
                        showMessage("404", image.getUrl(), "#dddddd");
                        baseJpgErrorHandlers.fireEvent(event);
                    }
                    Console.log("Unable to load image[" + image.getUrl() + "]");
                }
            });

            image.setPixelSize(widthPx, heightPx);
            image.setUrl(url.toString());
            image.setVisible(false);
            p.add(image, 0, 0);


        }
    }

    public void showMessage(String shortMessage, final String longMessage, String color) {
        final InlineLabel label = new InlineLabel(shortMessage);
        label.getElement().getStyle().setPadding(.2, Style.Unit.EM);
        label.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        label.getElement().getStyle().setBackgroundColor("yellow");
        final PopupPanel pp = new PopupPanel(true);
        pp.setWidget(new Label(longMessage));

        label.addClickHandler(new ClickHandler() {


            @Override public void onClick(ClickEvent event) {
                pp.showRelativeTo(label);
                event.preventDefault();
                event.stopPropagation();
            }
        });

        Grid grid = new Grid(1, 1);
        grid.setWidget(0, 0, label);
        grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
        Style style = grid.getElement().getStyle();
        style.setBackgroundColor(color);
        this.setPlaceHolder(grid);
    }


    private static boolean assertAllNonNull(List<Path> urls) {
        for (Path url : urls) {
            if (url == null) return false;
        }
        return true;
    }

    public void clearImage() {
        p.clear();
    }

}
