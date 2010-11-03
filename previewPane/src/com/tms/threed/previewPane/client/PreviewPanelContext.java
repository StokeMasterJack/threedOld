package com.tms.threed.previewPane.client;

import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.tms.threed.previewPanel.client.BlinkOverlay;
import com.tms.threed.previewPanel.client.ChatInfo;
import com.tms.threed.previewPanel.client.FooterPanel;
import com.tms.threed.previewPanel.client.PreviewPanel;
import com.tms.threed.previewPanel.client.ThreedImagePanel;
import com.tms.threed.previewPanel.client.TopImagePanel;
import com.tms.threed.previewPanel.client.buttonBars.exterior.ExteriorButtonHandler;
import com.tms.threed.previewPanel.client.buttonBars.exterior.ExteriorButtonPanel;
import com.tms.threed.previewPanel.client.buttonBars.interior.InteriorButtonHandler;
import com.tms.threed.previewPanel.client.buttonBars.interior.InteriorButtonPanel;
import com.tms.threed.previewPanel.client.chatPanel.ChatPanel;
import com.tms.threed.previewPanel.client.dragToSpin.ClearGif;
import com.tms.threed.previewPanel.client.dragToSpin.DragToSpin;
import com.tms.threed.previewPanel.client.headerPanel.HeaderPanel;
import com.tms.threed.previewPanel.client.thumbsPanel.ThumbClickEvent;
import com.tms.threed.previewPanel.client.thumbsPanel.ThumbClickHandler;
import com.tms.threed.previewPanel.client.thumbsPanel.ThumbPanel;
import com.tms.threed.previewPanel.client.thumbsPanel.ThumbsPanel;
import com.tms.threed.previewPanel.shared.viewModel.AngleAndViewChangeEvent;
import com.tms.threed.previewPanel.shared.viewModel.AngleAndViewChangeHandler;
import com.tms.threed.previewPanel.shared.viewModel.AngleChangeEvent;
import com.tms.threed.previewPanel.shared.viewModel.AngleChangeHandler;
import com.tms.threed.previewPanel.shared.viewModel.ViewChangeEvent;
import com.tms.threed.previewPanel.shared.viewModel.ViewChangeHandler;
import com.tms.threed.previewPanel.shared.viewModel.ViewStates;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ViewKey;
import com.tms.threed.threedCore.shared.ViewSnap;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.lang.shared.Path;

import javax.annotation.Nonnull;
import java.util.List;

public class PreviewPanelContext {

    private final HandlerManager bus = new HandlerManager(this);

    private final BlinkOverlay blinkOverlay;
    private final ThreedImagePanel mainThreedImagePanel;
    private final TopImagePanel topImagePanel;
    private final FooterPanel footerPanel;
    private final ChatPanel chatPanel;
    private final HeaderPanel headerPanel;

    private final ThumbsPanel thumbsPanel;
    private final PreviewPanel previewPanel;
    private final ExteriorButtonPanel exteriorButtonPanel;
    private final InteriorButtonPanel interiorButtonPanel;
    private final DragToSpin<ClearGif> dragToSpin;

    private final DefaultAngleButtonHandler angleButtonHandler;
    private final ThumbClickHandler thumbClickHandler;

    private ThreedModel threedModel;
    private SeriesKey seriesKey;
    private SeriesInfo seriesInfo;
    private ViewStates viewStates;

    public PreviewPanelContext() {

        chatPanel = new ChatPanel();
        headerPanel = new HeaderPanel(chatPanel);

        angleButtonHandler = new DefaultAngleButtonHandler();
        thumbClickHandler = new DefaultThumbClickHandler();

        exteriorButtonPanel = new ExteriorButtonPanel();
        exteriorButtonPanel.setButtonHandler(angleButtonHandler);

        interiorButtonPanel = new InteriorButtonPanel();
        interiorButtonPanel.setButtonHandler(angleButtonHandler);

        footerPanel = new FooterPanel(interiorButtonPanel, exteriorButtonPanel);

        mainThreedImagePanel = new ThreedImagePanel(0, TopImagePanel.PREFERRED_WIDTH_PX, TopImagePanel.PREFERRED_HEIGHT_PX);

        dragToSpin = new DragToSpin<ClearGif>();
        dragToSpin.setExteriorButtonHandler(angleButtonHandler);

        blinkOverlay = new BlinkOverlay();

        topImagePanel = new TopImagePanel(mainThreedImagePanel, blinkOverlay, dragToSpin, headerPanel, footerPanel);

        thumbsPanel = new ThumbsPanel("$00000");
        thumbsPanel.addThumbClickHandler(thumbClickHandler);


        previewPanel = new PreviewPanel(topImagePanel, thumbsPanel);

        hideButtonPanels();

        mainThreedImagePanel.addBaseJpgLoadHandler(new LoadHandler() {
            @Override public void onLoad(LoadEvent event) {
                refreshButtonPanels();
            }
        });

        mainThreedImagePanel.addBaseJpgErrorHandler(new ErrorHandler() {
            @Override public void onError(ErrorEvent event) {
                hideButtonPanels();
            }
        });

    }

    public void setThreedModel(ThreedModel threedModel) {
        if (threedModel == null) {
            threedModel = null;
            this.seriesKey = null;
            this.seriesInfo = null;
            this.viewStates = null;
        } else {
            this.threedModel = threedModel;
            this.seriesKey = threedModel.getSeriesKey();
            this.seriesInfo = threedModel.getSeriesInfo();
            this.viewStates = new ViewStates(seriesInfo);

            assert seriesKey != null;
            assert seriesInfo != null;

            refreshAfterSeriesUpdate();
        }
    }

    public void setCurrentViewAndAngle(@Nonnull ViewSnap newViewSnap) {
        ViewSnap oldViewSnap = viewStates.getCurrentViewSnap();
        if(newViewSnap.equals(oldViewSnap)) return;
        boolean angleChanged = newViewSnap.getAngle() != oldViewSnap.getAngle();
        boolean viewChanged = !newViewSnap.getView().equals(oldViewSnap.getView());


        if(angleChanged && viewChanged){
            viewStates.setCurrentViewAndAngle(newViewSnap);
            fireAngleAndViewChangeEvent(newViewSnap);
        }else if(angleChanged){
            viewStates.setCurrentAngle(newViewSnap.getAngle());
            fireAngleChangeEvent(newViewSnap.getAngle());
        }
        else if(viewChanged){
            viewStates.setCurrentView(newViewSnap.getView());
            fireViewChangeEvent(newViewSnap.getView());
        }

    }

    public void setCurrentViewAndAngle(int orientation) {
        ViewSnap viewSnap = seriesInfo.getViewSnapFromOrientation(orientation);
        setCurrentViewAndAngle(viewSnap);
    }

    public ViewStates getViewStatesCopy() {
        if (viewStates == null) return null;
        return new ViewStates(viewStates);
    }

    @Nonnull
    public ThreedImagePanel getMainThreedImagePanel() {
        assert mainThreedImagePanel != null;
        return mainThreedImagePanel;
    }

    @Nonnull
    public PreviewPanel getPreviewPanel() {
        assert previewPanel != null;
        return previewPanel;
    }

    @Nonnull
    public HeaderPanel getHeaderPanel() {
        assert headerPanel != null;
        return headerPanel;
    }

    @Nonnull
    public ChatPanel getChatPanel() {
        assert chatPanel != null;
        return chatPanel;
    }

    @Nonnull
    public ExteriorButtonPanel getExteriorButtonPanel() {
        assert exteriorButtonPanel != null;
        return exteriorButtonPanel;
    }

    @Nonnull
    public InteriorButtonPanel getInteriorButtonPanel() {
        assert interiorButtonPanel != null;
        return interiorButtonPanel;
    }


    @Nonnull
    public DragToSpin<ClearGif> getDragToSpin() {
        assert dragToSpin != null;
        return dragToSpin;
    }


    @Nonnull
    public BlinkOverlay getBlinkOverlay() {
        assert blinkOverlay != null;
        return blinkOverlay;
    }


    @Nonnull
    public ThumbsPanel getThumbsPanel() {
        return thumbsPanel;
    }

    public List<ThumbPanel> getThumbPanels() {
        return thumbsPanel.getThumbPanels();
    }

    private void refreshAfterSeriesUpdate() {
        assert assertNonNullSeries();
        headerPanel.setFeatureModel(threedModel.getFeatureModel());
        chatPanel.setSeriesKey(seriesKey);
        thumbsPanel.setThumbCount(seriesInfo.getViewCount() - 1);

        refreshAfterViewUpdate();
    }

    private boolean assertNonNullSeries() {
        assert seriesKey != null;
        assert seriesInfo != null;
        assert viewStates != null;
        return true;
    }

    private void refreshAfterViewUpdate() {
        assertNonNullSeries();
        hideButtonPanels();

        for (ThumbPanel thumbPanel : getThumbPanels()) {
            int panelIndex = thumbPanel.getPanelIndex();
            ViewKey viewForPanel = viewStates.getCurrentViewForPanel(panelIndex);
            thumbPanel.setViewKey(viewForPanel);
        }
    }

    public void hideButtonPanels() {
        dragToSpin.setEnabled(false);
        exteriorButtonPanel.setVisible(false);
        interiorButtonPanel.setVisible(false);
    }

    public void refreshButtonPanels() {
        dragToSpin.setEnabled(viewStates.isExterior());
        exteriorButtonPanel.setVisible(viewStates.isExterior());
        interiorButtonPanel.setVisible(viewStates.isInterior());
    }

    public ViewSnap getViewSnap() {
        if (seriesInfo == null) return null;
        return viewStates.getCurrentViewSnap();
    }

    public ViewSnap getViewSnapForPanel(int panelIndex) {
        if (seriesInfo == null) return null;
        return viewStates.getViewSnapForPanel(panelIndex);
    }

    public void setMsrp(String msrp) {
        getThumbsPanel().setMsrp(msrp);
    }

    public void setChatInfo(ChatInfo chatInfo) {
        getChatPanel().setChatInfo(chatInfo);
    }

    public void doFeatureBlink(Path png, Command postCommand) {
        assert png != null;
        getBlinkOverlay().doFeatureBlink(png, postCommand);
    }

    private class DefaultAngleButtonHandler implements ExteriorButtonHandler, InteriorButtonHandler {
        @Override public void onPrevious() {
            if (viewStates == null) return;
            viewStates.previousAngle();
            fireAngleChangeEvent(viewStates.getCurrentAngle());
        }

        @Override public void onNext() {
            if (viewStates == null) return;
            viewStates.nextAngle();
            fireAngleChangeEvent(viewStates.getCurrentAngle());
        }

        @Override public void onSelection(int clickedAngle) {
            if (viewStates == null) return;
            if (clickedAngle == viewStates.getCurrentAngle()) return;
            viewStates.setCurrentAngle(clickedAngle);
            fireAngleChangeEvent(clickedAngle);
        }
    }

    private class DefaultThumbClickHandler implements ThumbClickHandler {
        @Override public void onThumbClick(ThumbClickEvent event) {
            if (viewStates == null) return;
            int thumbIndex = event.getThumbIndex();
            viewStates.thumbClicked(thumbIndex);
            refreshAfterViewUpdate();
            fireViewChangeEvent(viewStates.getCurrentView());
        }
    }

    private void fireViewChangeEvent(ViewKey newViewKey) {
        bus.fireEvent(new ViewChangeEvent(newViewKey));
    }


    private void fireAngleChangeEvent(int newAngle) {
        bus.fireEvent(new AngleChangeEvent(newAngle));
    }

    private void fireAngleAndViewChangeEvent(ViewSnap newViewSnap) {
        bus.fireEvent(new AngleAndViewChangeEvent(newViewSnap));
    }

    public HandlerRegistration addViewChangeHandler(ViewChangeHandler handler) {
        return bus.addHandler(ViewChangeEvent.TYPE, handler);
    }

    public HandlerRegistration addAngleChangeHandler(AngleChangeHandler handler) {
        return bus.addHandler(AngleChangeEvent.TYPE, handler);
    }

    public HandlerRegistration addAngleAndViewChangeHandler(AngleAndViewChangeHandler handler) {
        return bus.addHandler(AngleAndViewChangeEvent.TYPE, handler);
    }


}