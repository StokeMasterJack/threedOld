package com.tms.threed.previewPanel.client.thumbsPanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.tms.threed.previewPanel.client.TopImagePanel;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesInfoBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.tms.threed.util.lang.shared.Strings.getSimpleName;

public class ThumbsPanel extends Composite {

    private final HandlerManager handlers = new HandlerManager(this);

    private static SeriesInfo dummySeriesInfo = SeriesInfoBuilder.createDummySeriesInfo();

    private final ThumbPanelFactory thumbPanelFactory;

    private final ArrayList<ThumbPanel> allThumbPanels;
    private final ArrayList<ThumbPanel> activeThumbPanels = new ArrayList<ThumbPanel>();

    private int thumbCount = 2;

    private final LayoutPanel layoutPanel = new LayoutPanel();

    private static final int PREFERRED_WIDTH_PX = TopImagePanel.PREFERRED_WIDTH_PX;
    public static final int PREFERRED_HEIGHT_PX = ThumbPanel.PREFERRED_HEIGHT_PX;

    public ThumbsPanel() {
        this(new DefaultThumbPanelFactory());
    }

    public ThumbsPanel(ThumbPanelFactory thumbPanelFactory) {
        this.thumbPanelFactory = thumbPanelFactory;
        initWidget(layoutPanel);
        ensureDebugId(getSimpleName(this));
        setWidth("100%");
        setHeight("100%");
        allThumbPanels = createAllThumbPanels();

//        getElement().getStyle().setBackgroundColor("brown");

        refreshThumbCount();
    }

    public HandlerRegistration addThumbClickHandler(ThumbClickHandler handler) {
        return handlers.addHandler(ThumbClickEvent.TYPE, handler);
    }

    private ArrayList<ThumbPanel> createAllThumbPanels() {
        ArrayList<ThumbPanel> a = new ArrayList<ThumbPanel>();
        for (int ti = 1; ti <= 4; ti++) {
            ThumbPanel thumbPanel = thumbPanelFactory.createThumbPanel(ti);
            a.add(thumbPanel);
            final int thumbIndex = ti;
            thumbPanel.addClickHandler(new ClickHandler() {
                @Override public void onClick(ClickEvent event) {
                    onThumbClick(thumbIndex);
                }
            });
        }
        return a;
    }

    private void onThumbClick(int thumbIndex) {
        if (thumbIndex > thumbCount) return;
        handlers.fireEvent(new ThumbClickEvent(thumbIndex));
    }

    public void setThumbCount(int thumbCount) {
        if (this.thumbCount == thumbCount) return;
        this.thumbCount = thumbCount;
        refreshThumbCount();
    }

    private void refreshThumbCount() {
        resetActiveThumbPanels();
        redrawThumbs();
    }

    private void resetActiveThumbPanels() {
        activeThumbPanels.clear();
        for (int i = 0; i < thumbCount; i++) {
            activeThumbPanels.add(allThumbPanels.get(i));
        }
    }

    private void redrawThumbs() {
        layoutPanel.clear();

        for (int col = 0; col < thumbCount; col++) {
            ThumbPanel thumbPanel = activeThumbPanels.get(col);
            int Li = getThumbLeft(col, thumbCount);
            layoutPanel.add(thumbPanel);
            layoutPanel.setWidgetLeftWidth(thumbPanel, Li, Style.Unit.PX, ThumbPanel.PREFERRED_WIDTH_PX, Style.Unit.PX);
        }
    }


    public List<ThumbPanel> getThumbPanels() {
        return activeThumbPanels;
    }

    private static class DefaultThumbPanelFactory implements ThumbPanelFactory {
        @Override public ThumbPanel createThumbPanel(int thumbIndex) {
            ThumbPanel tp = new ThumbPanel(thumbIndex);
//            tp.getElement().getStyle().setBackgroundColor("#DDDDDD");
            return tp;
        }
    }

    private static int getThumbLeft(int thumbIndex, int thumbCount) {
        int totalWidth = ThumbsPanel.PREFERRED_WIDTH_PX;
        int thumbWidth = ThumbPanel.PREFERRED_WIDTH_PX;

        int thumbsWidth = thumbCount * thumbWidth;
        double L = ((double)(totalWidth - thumbsWidth)) / 2.0;
        double Li = L + thumbIndex * thumbWidth;
        return (int) Li;
    }
}
