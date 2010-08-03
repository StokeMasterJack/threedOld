package com.tms.threed.previewPane.client.summaryPane;

import com.tms.threed.featureModel.shared.picks.PicksChangeEvent;
import com.tms.threed.featureModel.shared.picks.PicksRO;
import com.tms.threed.imageModel.shared.ImageStack;
import com.tms.threed.imageModel.shared.Jpg;
import com.tms.threed.previewPanel.client.ThreedImagePanel;
import com.tms.threed.previewPanel.shared.viewModel.AngleChangeEvent;
import com.tms.threed.previewPanel.shared.viewModel.AngleChangeHandler;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedCore.shared.ViewSnap;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.gwt.client.Browser;
import com.tms.threed.util.lang.shared.Path;

import javax.annotation.Nonnull;
import java.util.List;

public class SummarySeriesContext {

    @Nonnull private final SummaryPanelContext previewPanel;
    @Nonnull private final ThreedModel threedModel;
    @Nonnull private final ThreedConfig threedConfig;
    @Nonnull private final Path jpgRootHttp;
    @Nonnull private final Path pngRootHttp;

    private PicksRO picks;

    public SummarySeriesContext(@Nonnull SummaryPanelContext previewPanel, @Nonnull ThreedModel threedModel) {
        assert previewPanel != null;
        assert threedModel != null;

        this.threedModel = threedModel;
        this.previewPanel = previewPanel;
        this.threedConfig = threedModel.getThreedConfig();
        this.jpgRootHttp = threedConfig.getJpgRootHttp();
        this.pngRootHttp = threedConfig.getPngRootHttp();

        this.previewPanel.setSeriesInfo(threedModel.getSeriesInfo());

        previewPanel.addAngleChangeHandler(new AngleChangeHandler() {
            @Override public void onChange(AngleChangeEvent e) {
                refreshExteriorImage();
            }
        });


    }

    public ThreedModel getThreedModel() {
        return threedModel;
    }

    public SeriesInfo getSeriesInfo() {
        return threedModel.getSeriesInfo();
    }

    public SeriesKey getSeriesKey() {
        return threedModel.getSeriesKey();
    }

    public void setPicks(PicksChangeEvent e) {
        this.picks = e.getNewPicks();
        refreshExteriorImage();
        refreshInteriorImage();
    }

    private void refreshImagePanel(ThreedImagePanel threedImagePanel, int panelIndex) {
        ViewSnap viewState = previewPanel.getViewSnapForPanel(panelIndex);
        Jpg jpg = getJpg(viewState);
        ImageStack imageStack = jpg.getImageStack();

        if (Browser.isIe6()) {
            imageStack.purgeZLayers();
        }

        List<Path> urls = imageStack.getPaths(pngRootHttp, jpgRootHttp);
        threedImagePanel.setImageUrls(urls);
    }

    private void refreshExteriorImage() {
        refreshImagePanel(previewPanel.getExteriorThreedImagePanel(), 0);
    }

    private void refreshInteriorImage() {
        refreshImagePanel(previewPanel.getInteriorThreedImagePanel(), 1);
    }

    public Jpg getJpg(ViewSnap viewState) {
        return threedModel.getJpg(viewState, picks);
    }

    public void close() {
        //release event handlers
    }


}