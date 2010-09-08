package com.tms.threed.previewPane.client.series;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.tms.threed.featureModel.shared.picks.PicksChangeEvent;
import com.tms.threed.featureModel.shared.picks.PicksRO;
import com.tms.threed.imageModel.shared.ImLayer;
import com.tms.threed.imageModel.shared.ImPng;
import com.tms.threed.imageModel.shared.ImageStack;
import com.tms.threed.imageModel.shared.Jpg;
import com.tms.threed.previewPane.client.PrefetchStrategy;
import com.tms.threed.previewPane.client.PrefetchStrategy2;
import com.tms.threed.previewPane.client.Prefetcher;
import com.tms.threed.previewPane.client.PreviewPanelContext;
import com.tms.threed.previewPane.client.previewPanelModel.ImageUrlProvider;
import com.tms.threed.previewPanel.client.ChatInfo;
import com.tms.threed.previewPanel.client.ThreedImagePanel;
import com.tms.threed.previewPanel.client.thumbsPanel.ThumbPanel;
import com.tms.threed.previewPanel.shared.viewModel.AngleAndViewChangeEvent;
import com.tms.threed.previewPanel.shared.viewModel.AngleAndViewChangeHandler;
import com.tms.threed.previewPanel.shared.viewModel.AngleChangeEvent;
import com.tms.threed.previewPanel.shared.viewModel.AngleChangeHandler;
import com.tms.threed.previewPanel.shared.viewModel.ViewChangeEvent;
import com.tms.threed.previewPanel.shared.viewModel.ViewChangeHandler;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedCore.shared.ViewSnap;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.gwt.client.Browser;
import com.tms.threed.util.lang.shared.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SeriesContext {

    @Nonnull private final PreviewPanelContext previewPanel;
    @Nonnull private final ThreedModel threedModel;
    @Nonnull private final ThreedConfig threedConfig;
    @Nonnull private final Path jpgRootHttp;
    @Nonnull private final Path pngRootHttp;

    private PrefetchStrategy prefetchStrategy;
    private Prefetcher prefetcher;

    private PicksRO picks;

    private boolean pngMode = false;

    public SeriesContext(@Nonnull PreviewPanelContext previewPanel, @Nonnull ThreedModel threedModel) {
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
                refreshImagePanels();
            }
        });

        previewPanel.addViewChangeHandler(new ViewChangeHandler() {
            @Override public void onChange(ViewChangeEvent e) {
                refreshImagePanels();
            }
        });

        previewPanel.addAngleAndViewChangeHandler(new AngleAndViewChangeHandler() {
            @Override public void onChange(AngleAndViewChangeEvent e) {
                refreshImagePanels();
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

    private ImageUrlProvider imageUrlProvider = new ImageUrlProvider() {
        @Override public ImageStack getImageUrl(ViewSnap viewState) {
            Jpg jpg = getJpg(viewState);
            return jpg.getImageStack();
        }
    };

    public boolean isPngMode() {
        return pngMode;
    }

    public void setPngMode(boolean pngMode) {
        this.pngMode = pngMode;
        refreshImagePanels();
    }

    public void refreshLayerVisibility() {
        refreshMainImage();
    }

    public PrefetchStrategy getPrefetchStrategy() {
        if (prefetchStrategy == null) {
            prefetchStrategy = new PrefetchStrategy2(imageUrlProvider, previewPanel.getViewStatesCopy(), threedConfig);
        }
        return prefetchStrategy;
    }


    public Prefetcher getPrefetcher() {
        if (prefetcher == null) {
            prefetcher = new Prefetcher(getPrefetchStrategy());

        }
        return prefetcher;
    }

    private void refreshImagePanels() {
        refreshMainImage();
        DeferredCommand.addPause();
        DeferredCommand.addPause();
        DeferredCommand.addPause();
        DeferredCommand.addCommand(new Command() {
            @Override public void execute() {
                prefetch();
                refreshThumbImages();
            }
        });
    }

    private void refreshImagePanelsWithBlink(PicksChangeEvent e) {
        refreshMainImage();

        doBlink(e, new Command() {
            @Override public void execute() {
                refreshThumbImages();
                prefetch();
            }
        });
    }

    private boolean isBlinkSeries() {
        return getSeriesKey().isa(SeriesKey.FOUR_RUNNER) || getSeriesKey().isa(SeriesKey.SIENNA);
    }

    public void setPicks(PicksChangeEvent e) {
        this.picks = e.getNewPicks();
        if (isBlinkSeries() && e.isBlinkEvent() && !Browser.isIe6()) {
            refreshImagePanelsWithBlink(e);
        } else {
            refreshImagePanels();
        }
    }

    public void setChatInfo(ChatInfo chatInfo) {
        previewPanel.setChatInfo(chatInfo);
    }

    public void setMsrp(String msrp) {
        previewPanel.setMsrp(msrp);
    }

    private void refreshImagePanel(ThreedImagePanel threedImagePanel, int panelIndex) {
        ViewSnap viewState = previewPanel.getViewSnapForPanel(panelIndex);

        if (!picks.isValid()) {
            refreshImagePanelBadPicks(threedImagePanel);
        } else {
            Jpg jpg = getJpg(viewState);
            if (pngMode) {
                refreshImagePanelPngMode(threedImagePanel, jpg);
            } else {
                refreshImagePanelJpgMode(threedImagePanel, jpg);
            }
        }
    }

    private void refreshImagePanelBadPicks(ThreedImagePanel threedImagePanel) {
        threedImagePanel.showMessage("Invalid Build", picks.getErrorMessage(), "#ffdddd");
        previewPanel.hideButtonPanels();
    }

    private void refreshImagePanelPngMode(ThreedImagePanel threedImagePanel, Jpg jpg) {
        List<ImPng> pngs = jpg.getPngs();
        ArrayList<Path> a = new ArrayList<Path>();
        for (int i = 0; i < pngs.size(); i++) {
            ImPng png = pngs.get(i);
            if (!png.isVisible()) continue;
            ImLayer layer = png.getLayer();
            if (!layer.isVisible()) continue;
            Path url = png.getPath(pngRootHttp);
            a.add(url);
        }
        threedImagePanel.setImageUrls(a);
    }

    private void refreshImagePanelJpgMode(ThreedImagePanel threedImagePanel, Jpg jpg) {
        ImageStack imageStack = jpg.getImageStack();
        if (Browser.isIe6()) {
            imageStack.purgeZLayers();
        }
        List<Path> urls = imageStack.getPaths(pngRootHttp, jpgRootHttp);
        threedImagePanel.setImageUrls(urls);
    }

    private void refreshMainImage() {
        ThreedImagePanel mainImagePanel = previewPanel.getMainThreedImagePanel();
        refreshImagePanel(mainImagePanel, 0);
    }

    private void refreshThumbImages() {
        List<ThumbPanel> thumbPanels = previewPanel.getThumbPanels();
        for (ThumbPanel p : thumbPanels) {
            int panelIndex = p.getPanelIndex();
            ThreedImagePanel threedImagePanel = p.getThreedImagePanel();
            refreshImagePanel(threedImagePanel, panelIndex);
        }
    }

    public Jpg getJpg(ViewSnap viewState) {
        return threedModel.getJpg(viewState, picks);
    }

    private void prefetch() {
        if (picks.isValid()) {
            getPrefetcher().prefetch();
        }
    }

    @Nullable
    public Path getBlinkPngUrl(PicksChangeEvent e) {
        return threedModel.getBlinkPngUrl(previewPanel.getViewSnap(), e.getNewPicks(), e.getBlinkAccessory());
    }


    private void doBlink(PicksChangeEvent e, Command postCommand) {
        Path blinkUrl = getBlinkPngUrl(e);
        //null blinkUrl means that accessory is not visible at the current angle
        if (blinkUrl == null || !picks.isValid()) {
            DeferredCommand.addCommand(postCommand);
        } else {
            previewPanel.doFeatureBlink(blinkUrl, postCommand);
        }
    }

    public void close() {
        //release event handlers
    }


    public void setViewAndAngle(int orientation) {
        previewPanel.setCurrentViewAndAngle(orientation);
    }
}
