package com.tms.threed.previewPane.client;

import com.tms.threed.imageModel.shared.ImageStack;
import com.tms.threed.previewPane.client.previewPanelModel.ImageUrlProvider;
import com.tms.threed.previewPanel.shared.viewModel.ViewStates;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedCore.shared.ViewSnap;
import com.tms.threed.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.List;

public class PrefetchStrategy1 implements PrefetchStrategy{

    private final ImageUrlProvider imageUrlProvider;
    private final ViewStates viewStates;
    private final ThreedConfig threedConfig;

    public PrefetchStrategy1(ImageUrlProvider imageUrlProvider, ViewStates viewStates, ThreedConfig threedConfig) {
        this.imageUrlProvider = imageUrlProvider;
        this.viewStates = viewStates;
        this.threedConfig = threedConfig;
    }

    public List<Path> getPrefetchUrls() {
        UrlListBuilder urlListBuilder = new UrlListBuilder(viewStates);
        return urlListBuilder.getUrls();
    }


    private class UrlListBuilder {

        private final List<Path> urls = new ArrayList<Path>();

        private final ViewStates viewStatesCopy;

        private UrlListBuilder(ViewStates viewStates) {

            this.viewStatesCopy = new ViewStates(viewStates);

            int span = 6;
            for (int i = 0; i < span; i++) {
                viewStatesCopy.previousAngle();
                addUrl();
            }

            for (int i = 0; i < span; i++) {
                viewStatesCopy.nextAngle();
            }

            for (int i = 0; i < span; i++) {
                viewStatesCopy.nextAngle();
                addUrl();
            }

            viewStatesCopy.nextView();
            addUrl();

        }

        public List<Path> getUrls() {
            return urls;
        }

        private void addUrl() {
            ViewSnap state = viewStatesCopy.getCurrentViewSnap();
            ImageStack imageStack = imageUrlProvider.getImageUrl(state);

            List<Path> urls = imageStack.getPaths(threedConfig.getPngRootHttp(), threedConfig.getJpgRootHttp());
            for (int i = 0; i < urls.size(); i++) {
                Path url = urls.get(i);
                this.urls.add(url);
            }

        }


    }

}