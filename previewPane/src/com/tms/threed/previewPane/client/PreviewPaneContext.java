package com.tms.threed.previewPane.client;

import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.featureModel.shared.picks.PicksChangeEvent;
import com.tms.threed.previewPane.client.externalState.picks.SeriesNotSetException;
import com.tms.threed.previewPane.client.series.SeriesContext;
import com.tms.threed.previewPanel.client.ChatInfo;
import com.tms.threed.previewPanel.client.PreviewPanel;
import com.tms.threed.previewPanel.shared.viewModel.AngleChangeHandler;
import com.tms.threed.previewPanel.shared.viewModel.ViewChangeHandler;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ViewSnap;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.gwt.client.MvcModel;

/**
 * One per App
 */
public class PreviewPaneContext extends MvcModel {

    private final PreviewPanelContext previewPanelContext;

    private SeriesContext seriesContext;

    public PreviewPaneContext() {
        previewPanelContext = new PreviewPanelContext();
    }

    public void setSeries(ThreedModel newThreedModel) {
        if (this.seriesContext == null) {
            seriesContext = new SeriesContext(previewPanelContext, newThreedModel);
        } else {
            SeriesKey oldSeries = seriesContext.getThreedModel().getSeriesKey();
            SeriesKey newSeries = newThreedModel.getSeriesKey();
            if (oldSeries.equals(newSeries)) return;

            seriesContext.close();
            seriesContext = new SeriesContext(previewPanelContext, newThreedModel);
        }
    }

    public void setChatInfo(ChatInfo chatInfo) {
        if (seriesContext == null) throw new SeriesNotSetException();
        seriesContext.setChatInfo(chatInfo);
    }

    public void setMsrp(String msrp) {
        if (seriesContext == null) throw new SeriesNotSetException();
        seriesContext.setMsrp(msrp);
    }

    public void setPicks(PicksChangeEvent picksChangeEvent) {
        if (seriesContext == null) throw new SeriesNotSetException();
        seriesContext.setPicks(picksChangeEvent);

        if (!picksChangeEvent.areNewPicksValid()) {
            previewPanelContext.hideButtonPanels();
        }
    }

    public SeriesContext getSeriesContext() {
        return seriesContext;
    }

    public PreviewPanel getPreviewPanel() {
        return previewPanelContext.getPreviewPanel();
    }

    public void setPngMode(boolean pngMode) {
        if (seriesContext == null) throw new SeriesNotSetException();
        seriesContext.setPngMode(pngMode);
    }

    public void refreshLayerVisibility() {
        seriesContext.refreshLayerVisibility();
    }

    public ViewSnap getViewState() {
        if (seriesContext == null) throw new SeriesNotSetException();
        return previewPanelContext.getViewSnap();
    }

    public HandlerRegistration addViewChangeHandler(ViewChangeHandler handler) {
        return previewPanelContext.addViewChangeHandler(handler);
    }

    public HandlerRegistration addAngleChangeHandler(AngleChangeHandler handler) {
        return previewPanelContext.addAngleChangeHandler(handler);
    }


}