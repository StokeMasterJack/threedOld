package com.tms.threed.previewPanel.client.headerPanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.layout.client.Layout;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tms.threed.previewPanel.client.PreviewPanelStyles;
import com.tms.threed.previewPanel.client.chatPanel.ChatPanel;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.util.gwt.client.Console;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.tms.threed.util.lang.shared.Strings.isEmpty;

public class HeaderPanel extends LayoutPanel implements HeaderPanelDisplay {

    private static final int TOTAL_HEIGHT_PX = 57;

    private static final String FONT_COLOR = "white";
    private static final int FONT_SIZE_PX = 20;


    private final MsrpPanel msrpPanel = new MsrpPanel();

    private final YearSeriesLabel yearSeriesLabel = new YearSeriesLabel();

    public HeaderPanel(@Nullable SeriesKey seriesKey, String msrp, ChatPanel chatPanel) {

        HeaderTextBox textPanel = new HeaderTextBox();
        Widget background = createTransparentBackground();

        setSize("100%", TOTAL_HEIGHT_PX + "px");


        add(textPanel);
        setWidgetHorizontalPosition(textPanel, Layout.Alignment.BEGIN);
        setWidgetTopBottom(textPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);

        add(chatPanel);
        setWidgetHorizontalPosition(chatPanel, Layout.Alignment.END);
        setWidgetTopBottom(chatPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);

        add(background);
        PreviewPanelStyles.set(this);

        chatPanel.getElement().getStyle().setZIndex(100000);

        setSeriesKey(seriesKey);
        setMsrp(msrp);

    }

    public HeaderPanel(ChatPanel chatPanel) {
        this(null, null, chatPanel);
    }

    private class HeaderTextBox extends FlowPanel {

        private HeaderTextBox() {
            add(yearSeriesLabel);
            add(msrpPanel);

            Style style = getElement().getStyle();
            style.setPadding(0, Style.Unit.PX);
            style.setMargin(0, Style.Unit.PX);
            style.setZIndex(200);
            style.setColor(FONT_COLOR);
            style.setFontSize(FONT_SIZE_PX, Style.Unit.PX);
            style.setFontWeight(Style.FontWeight.BOLD);

            PreviewPanelStyles.set(this);

//            style.setBackgroundColor("yellow");

            style.setMarginLeft(5, Style.Unit.PX);

        }

    }

    private class YearSeriesLabel extends Label {

        private YearSeriesLabel() {
            this.setVisible(false);
            this.ensureDebugId("YearAndSeriesLabel");

            Style style = this.getElement().getStyle();
            style.setProperty("textTransform", "uppercase");

            style.setMargin(0, Style.Unit.PX);
            style.setPadding(0, Style.Unit.PX);
            style.setFontSize(FONT_SIZE_PX, Style.Unit.PX);

//        style.setBackgroundColor("pink");

        }

        void setSeriesKey(SeriesKey seriesKey) {
            if (seriesKey == null) {
                this.setText("YOUR 2047 OmpaLumpa");
                this.setVisible(false);
            } else {
                this.setText(seriesKey.getYourMessage());
                this.setVisible(true);
            }
        }
    }


    private class MsrpPanel extends FlowPanel {

        private final InlineLabel prefix = new InlineLabel("MRSP*");
        private final InlineLabel msrpValue = new InlineLabel("$XX,XXXX");

        private MsrpPanel() {
            add(prefix);
            add(new InlineHTML("&nbsp;"));
            add(new InlineHTML("&nbsp;"));
            add(msrpValue);

            Style prefixStyle = prefix.getElement().getStyle();
            prefixStyle.setFontSize((int) (FONT_SIZE_PX * .5), Style.Unit.PX);

            Style style = getElement().getStyle();
            style.setMargin(0, Style.Unit.PX);
            style.setPadding(0, Style.Unit.PX);

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

    private Widget createTransparentBackground() {
        HTML p = new HTML();
        p.ensureDebugId("TransparentBackground");
        p.setWidth("100%");
//        p.setHeight(TOTAL_HEIGHT_PX + "px");

        Style s = p.getElement().getStyle();
        s.setBackgroundColor("#000");
        s.setOpacity(.2);
        s.setProperty("filter", "alpha(opacity=20)");/* For IE6&7 */
//        s.setZIndex(100);

        return p;

    }

    @Override public void setMsrp(String msrp) {
        msrpPanel.setMsrp(msrp);
    }

    @Override public void setSeriesKey(SeriesKey seriesKey) {
        yearSeriesLabel.setSeriesKey(seriesKey);
    }


}