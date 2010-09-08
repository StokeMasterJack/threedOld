package com.tms.threed.previewPanel.shared.viewModel;

import com.tms.threed.previewPanel.client.ViewState;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.ViewKey;
import com.tms.threed.threedCore.shared.ViewSnap;

public class ViewStates {

    private final ViewState[] viewStates; //maps viewIndex to viewState

    private final ViewState[] panelStates;

    private SeriesInfo seriesInfo;
    private ViewKey currentView;


    public ViewStates(SeriesInfo seriesInfo) {
        this.seriesInfo = seriesInfo;

        this.currentView = this.seriesInfo.getInitialView();
        ViewKey[] viewKeys = seriesInfo.getViewsKeys();

        viewStates = new ViewStateByViewKey[viewKeys.length];
        for (int i = 0; i < viewKeys.length; i++) {
            viewStates[i] = new ViewStateByViewKey(this, viewKeys[i]);
        }

        panelStates = new ViewState[viewKeys.length];
        for (int i = 0; i < viewKeys.length; i++) {
            panelStates[i] = new ViewStateByPanel(i, this);
        }

    }


    /**
     * Copy
     */
    public ViewStates(ViewStates source) {
        this.seriesInfo = source.seriesInfo;
        this.currentView = source.currentView;

        int L = source.viewStates.length;

        this.viewStates = new ViewStateByViewKey[L];
        for (int i = 0; i < L; i++) {
            ViewKey vk = seriesInfo.getViewKey(i);
            viewStates[i] = new ViewStateByViewKey(this, vk);
        }

        this.panelStates = new ViewStateByPanel[L];
        for (int i = 0; i < L; i++) {
            ViewKey vk = seriesInfo.getViewKey(i);
            panelStates[i] = new ViewStateByPanel(i, this);
        }
    }


    public ViewKey getCurrentView() {
        return currentView;
    }

    public int getCurrentAngle(ViewKey viewKey) {
        return viewStates[viewKey.index].getCurrentAngle();
    }

    public void setCurrentView(ViewKey newViewKey) {
        this.currentView = newViewKey;
    }

    public void thumbClicked(int panelIndex) {
        ViewKey vk = getCurrentViewForPanel(panelIndex);
        setCurrentView(vk);
    }

    public void nextView() {
        ViewKey nextViewKey = seriesInfo.nextView(currentView);
        setCurrentView(nextViewKey);
    }

    public void setCurrentAngle(ViewKey viewKey, int newAngle) {
        viewStates[viewKey.index].setCurrentAngle(newAngle);
    }

    public void previousAngle(ViewKey viewKey) {
        int currentAngle = getCurrentAngle(viewKey);
        int nextAngle = viewKey.getPrevious(currentAngle);
        setCurrentAngle(viewKey, nextAngle);
    }

    public void nextAngle(ViewKey viewKey) {
        int currentAngle = getCurrentAngle(viewKey);
        int nextAngle = viewKey.getNext(currentAngle);
        setCurrentAngle(viewKey, nextAngle);
    }

    public void nextAngle() {
        nextAngle(currentView);
    }

    public void previousAngle() {
        previousAngle(currentView);
    }

    public int getCurrentAngle() {
        return this.getCurrentAngle(currentView);
    }

    public void setCurrentAngle(int newAngle) {
        setCurrentAngle(currentView, newAngle);
    }

    public ViewSnap getCurrentViewSnap() {
        return new ViewSnap(getCurrentView(), getCurrentAngle());
    }

    public SeriesInfo getSeriesInfo() {
        return seriesInfo;
    }

    public int getViewCount() {
        return seriesInfo.getViewCount();
    }

    public ViewKey getCurrentViewForPanel(int panelIndex) {
        int viewCount = seriesInfo.getViewCount();
        ViewKey currentMainView = getCurrentView();
        int panelViewIndex = (currentMainView.index + panelIndex) % viewCount;
        return seriesInfo.getViewKey(panelViewIndex);
    }

    public ViewState getExteriorViewState() {
        ViewKey viewKey = seriesInfo.getExterior();
        return viewStates[viewKey.index];
    }

    public ViewState getInteriorViewState() {
        ViewKey viewKey = seriesInfo.getInterior();
        return viewStates[viewKey.index];
    }

    public ViewState getViewStateForViewKey(ViewKey viewKey) {
        return viewStates[viewKey.index];
    }

    public ViewState getViewStateForPanel(int panelIndex) {
        return panelStates[panelIndex];
    }

    public ViewSnap getViewSnapForPanel(int panelIndex) {
        ViewState viewState = getViewStateForPanel(panelIndex);
        return new ViewSnap(viewState.getCurrentView(), viewState.getCurrentAngle());
    }

    public int getThumbCount() {
        return getViewCount() - 1;
    }

    public ViewState[] getAll() {
        return viewStates;
    }

    public int getViewForPanel(int panel) {
        return (currentView.index + panel) % getViewCount();
    }

    public int getPanelForView(int view) {
        return ((view - currentView.index) + getViewCount()) % getViewCount();
    }

    public int getCurrentPanelForView(ViewKey viewKey) {
        return getPanelForView(viewKey.index);
    }

    public boolean isExterior() {
        return getCurrentView().isExterior();
    }

    public boolean isInterior() {
        return getCurrentView().isInterior();
    }

    public void setCurrentViewAndAngle(ViewSnap viewSnap) {
        setCurrentView(viewSnap.getView());
        setCurrentAngle(viewSnap.getAngle());
    }
}