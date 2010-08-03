package com.tms.threed.previewPane.client.summaryPane;

import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.featureModel.shared.picks.PicksChangeEvent;
import com.tms.threed.previewPane.client.externalState.picks.SeriesNotSetException;
import com.tms.threed.previewPanel.client.summaryPanel.SummaryPanel;
import com.tms.threed.previewPanel.shared.viewModel.AngleChangeHandler;
import com.tms.threed.previewPanel.shared.viewModel.ViewChangeHandler;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ViewSnap;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.gwt.client.MvcModel;

/**
 * One per App
 */
public class SummaryPaneContext extends MvcModel {

    private final SummaryPanelContext summaryPanelContext;

    private SummarySeriesContext seriesContext;

    public SummaryPaneContext() {
        summaryPanelContext = new SummaryPanelContext();
    }

    public void setSeries(ThreedModel newThreedModel) {
        if (this.seriesContext == null) {
            seriesContext = new SummarySeriesContext(summaryPanelContext, newThreedModel);
        } else {
            SeriesKey oldSeries = seriesContext.getThreedModel().getSeriesKey();
            SeriesKey newSeries = newThreedModel.getSeriesKey();
            if (oldSeries.equals(newSeries)) return;

            seriesContext.close();
            seriesContext = new SummarySeriesContext(summaryPanelContext, newThreedModel);
        }
    }

    public void setPicks(PicksChangeEvent picksChangeEvent) {
        if (seriesContext == null) throw new SeriesNotSetException();
        seriesContext.setPicks(picksChangeEvent);
    }

    public SummarySeriesContext getSeriesContext() {
        return seriesContext;
    }

    public SummaryPanel getSummaryPanel() {
        return summaryPanelContext.getSummaryPanel();
    }

    public ViewSnap getViewState() {
        if (seriesContext == null) throw new SeriesNotSetException();
        return summaryPanelContext.getViewState();
    }

     public HandlerRegistration addViewChangeHandler(ViewChangeHandler handler) {
        return summaryPanelContext.addViewChangeHandler(handler);
    }

    public HandlerRegistration addAngleChangeHandler(AngleChangeHandler handler) {
        return summaryPanelContext.addAngleChangeHandler(handler);
    }


}