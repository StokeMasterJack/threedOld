package com.tms.threed.previewPanel.client;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.tms.threed.previewPanel.client.dragToSpin.ClearGif;
import com.tms.threed.previewPanel.client.dragToSpin.DragToSpin;
import com.tms.threed.previewPanel.client.headerPanel.HeaderPanel;

import static com.tms.threed.util.lang.shared.Strings.getSimpleName;

public class TopImagePanel extends AbsolutePanel {

    public static final int PREFERRED_WIDTH_PX = 599;
    public static final int PREFERRED_HEIGHT_PX = 366;

    public TopImagePanel(ThreedImagePanel threedImagePanel, BlinkOverlay blinkOverlay, DragToSpin<ClearGif> dragToSpin, HeaderPanel headerPanel, FooterPanel footerPanel) {
        this.setPixelSize(PREFERRED_WIDTH_PX, PREFERRED_HEIGHT_PX);
        ensureDebugId(getSimpleName(this));

        ClearGif dragDiv = new ClearGif();


        footerPanel.setPixelSize(PREFERRED_WIDTH_PX, footerPanel.getPreferredHeightPx());
        int footerPanelTop = PREFERRED_HEIGHT_PX - footerPanel.getPreferredHeightPx();

        add(threedImagePanel, 0, 0);
        add(blinkOverlay, 0, 0);
        add(headerPanel, 0, 0);
        add(dragDiv, 0, 0);
        add(footerPanel, 0, footerPanelTop);

//        image.addLoadHandler(new LoadHandler() {
//            @Override public void onLoad(LoadEvent event) {
//                refreshButtonPanels();
//            }
//        });

        PreviewPanelStyles.set(this);

        dragToSpin.attachToTarget(dragDiv);

    }

    public int getPanelIndex() {
        return 0;
    }

}

