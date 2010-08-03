package com.tms.threed.previewPane.client;

import com.tms.threed.imageModel.shared.ImageStack;
import com.tms.threed.previewPane.client.previewPanelModel.ImageUrlProvider;
import com.tms.threed.previewPanel.shared.viewModel.ViewStates;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedCore.shared.ViewKey;
import com.tms.threed.threedCore.shared.ViewSnap;
import com.tms.threed.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.List;

public class PrefetchStrategy2 implements PrefetchStrategy{

    private final ImageUrlProvider imageUrlProvider;
    private final SeriesInfo seriesInfo;
    private final ThreedConfig threedConfig;

    public PrefetchStrategy2(ImageUrlProvider imageUrlProvider, ViewStates viewStates, ThreedConfig threedConfig) {
        this.imageUrlProvider = imageUrlProvider;
        this.seriesInfo = viewStates.getSeriesInfo();
        this.threedConfig = threedConfig;
    }

    public List<Path> getPrefetchUrls() {
        UrlListBuilder urlListBuilder = new UrlListBuilder(seriesInfo);
        return urlListBuilder.getUrls();
    }


    private class UrlListBuilder {

        private final List<Path> urls = new ArrayList<Path>();

        private UrlListBuilder(SeriesInfo seriesInfo) {

            ViewKey[] viewsKeys = seriesInfo.getViewsKeys();
            for (int i = 0; i < viewsKeys.length; i++) {
                ViewKey viewsKey = viewsKeys[i];
                for (int angle = 1; angle <= viewsKey.getAngleCount(); angle++) {
                    ViewSnap viewSnap = new ViewSnap(viewsKey, angle);
                    addUrls(viewSnap);
                }
            }

        }

        public List<Path> getUrls() {
            return urls;
        }

        private void addUrls(ViewSnap viewSnap) {
            ImageStack imageStack = imageUrlProvider.getImageUrl(viewSnap);

            List<Path> urls = imageStack.getPaths(threedConfig.getPngRootHttp(), threedConfig.getJpgRootHttp());
            for (int i = 0; i < urls.size(); i++) {
                Path url = urls.get(i);
                this.urls.add(url);
            }

        }

    }

}