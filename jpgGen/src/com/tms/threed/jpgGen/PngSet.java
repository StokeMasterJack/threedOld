package com.tms.threed.jpgGen;

import com.tms.threed.threedCore.shared.*;
import com.tms.threed.jpgGen.batchJpgGen.JpgBatch;

public class PngSet {

    private final SeriesKey seriesKey;
    private final int version;

    private JpgBatch jpgBatch;

    public PngSet(SeriesKey seriesKey, int version) {
        this.seriesKey = seriesKey;
        this.version = version;
    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    public int getVersion() {
        return version;
    }

    public JpgBatch getJpgBatch() {
        if(jpgBatch == null){
            jpgBatch = createJpgBatch();
        }
        return jpgBatch;
    }

    private JpgBatch createJpgBatch() {
        return null;
    }
}
