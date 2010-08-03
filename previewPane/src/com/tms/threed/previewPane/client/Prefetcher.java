package com.tms.threed.previewPane.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.ui.Image;
import com.tms.threed.previewPanel.shared.viewModel.ViewStates;
import com.tms.threed.util.lang.shared.Path;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Prefetcher {

    private final PrefetchStrategy strategy;

    private static Set<Path> prefetchedUrls = new HashSet<Path>();

    public Prefetcher(PrefetchStrategy strategy) {
        this.strategy = strategy;
    }

    public void prefetch() {
        DeferredCommand.addPause();
        DeferredCommand.addPause();
        DeferredCommand.addPause();
        DeferredCommand.addCommand(new Command() {
            @Override public void execute() {
                List<Path> urls = strategy.getPrefetchUrls();
                DeferredCommand.addCommand(new PrefetchNextCommand(urls));
            }
        });
    }

    private class PrefetchNextCommand implements IncrementalCommand {

        private List<Path> urlsToPrefetch;

        private PrefetchNextCommand(List<Path> urlsToPrefetch) {
            this.urlsToPrefetch = urlsToPrefetch;
        }

        @Override public boolean execute() {
            Path urlToPrefetch = urlsToPrefetch.remove(0);

            boolean retValue = urlsToPrefetch.size() != 0;

            if (prefetchedUrls.contains(urlToPrefetch)) return retValue;

//            System.out.println("prefetching [" + urlToPrefetch + "]");

            Image.prefetch(urlToPrefetch.toString());
            prefetchedUrls.add(urlToPrefetch);

            return retValue;
        }
    }

}