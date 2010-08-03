package com.tms.threed.jpgGen;

import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.util.lang.shared.Path;

import java.util.List;

public class Global {

    public static JpgGenContext jpgGenContext;

    public static JpgGenContext getJpgGenContext() {
        if (jpgGenContext == null) {
            jpgGenContext = createJpgGenContext();
        }
        return jpgGenContext;
    }

    private static JpgGenContext createJpgGenContext() {
        return null;
    }

    private static class JpgGenContextImpl implements JpgGenContext {

        private ThreedConfig threedConfig;

        private JpgGenContextImpl(ThreedConfig threedConfig) {
            this.threedConfig = threedConfig;
        }

        @Override public List<SeriesKey> getSeriesKeys() {
            return SeriesKey.getAll();
        }

        @Override public Path getJpgRootFileSystem() {
            return threedConfig.getJpgRootFileSystem();
        }

        @Override public Path getPngRootFileSystem() {
            return threedConfig.getPngRootFileSystem();
        }

        @Override public List<PngSet> getPngSets(SeriesKey seriesKey) {
            return null;
        }
    }
}
