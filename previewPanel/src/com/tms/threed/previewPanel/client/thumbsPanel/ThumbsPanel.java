package com.tms.threed.previewPanel.client.thumbsPanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.tms.threed.previewPanel.client.TopImagePanel;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesInfoBuilder;
import com.tms.threed.util.gwt.client.Console;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.tms.threed.util.lang.shared.Strings.getSimpleName;
import static com.tms.threed.util.lang.shared.Strings.isEmpty;

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

    private final MsrpPanel msrpPanel = new MsrpPanel();

    public ThumbsPanel(String msrp) {
        this(new DefaultThumbPanelFactory(), msrp);
    }

    public ThumbsPanel(ThumbPanelFactory thumbPanelFactory, String msrp) {
        this.thumbPanelFactory = thumbPanelFactory;
        initWidget(layoutPanel);
        ensureDebugId(getSimpleName(this));
        setWidth("100%");
        setHeight("100%");
        allThumbPanels = createAllThumbPanels();

//        getElement().getStyle().setBackgroundColor("brown");

        refreshThumbCount();

        setMsrp(msrp);
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


        layoutPanel.add(msrpPanel);
        layoutPanel.setWidgetLeftWidth(msrpPanel, 0, Style.Unit.PX, 100, Style.Unit.PX);
    }

    private class MsrpPanel extends FlowPanel {

        private static final String FONT_COLOR = "white";
        private static final int FONT_SIZE_PX = 20;

        private final InlineLabel prefix = new InlineLabel("MRSP*");
        private final InlineLabel msrpValue = new InlineLabel("$XX,XXXX");

        private MsrpPanel() {
            add(new InlineHTML("&nbsp;"));
            add(msrpValue);
            add(prefix);

            Style prefixStyle = prefix.getElement().getStyle();
            prefixStyle.setFontSize((int) (FONT_SIZE_PX * .5), Style.Unit.PX);
            prefixStyle.setColor("#AAAAAA");

            Style style = getElement().getStyle();
            style.setMargin(0, Style.Unit.PX);
            style.setPadding(0, Style.Unit.PX);
            style.setPaddingTop(7, Style.Unit.PX);

            style.setFontSize(FONT_SIZE_PX, Style.Unit.PX);

//        style.setBackgroundColor("pink");

        }

        void setMsrp(String msrp) {
            if (isEmpty(msrp)) {
                msrpValue.setText("$99,999");
                this.setVisible(false);
            } else if (isMsrpValid(msrp)) {
                msrpValue.setText(msrp);
                this.setVisible(true);
            } else {
                msrpValue.setText("$99,999");
                this.setVisible(false);
                Console.log("Invalid MSRP[" + msrp + "]");
            }
        }
    }

    private boolean isMsrpValid(@Nonnull String msrp) {
        assert msrp != null;
        return msrp.length() > 3;
    }

    public void setMsrp(String msrp) {
        msrpPanel.setMsrp(msrp);
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
        double L = ((double) (totalWidth - thumbsWidth)) / 2.0;
        double Li = L + thumbIndex * thumbWidth;
        return (int) Li;
    }
}
