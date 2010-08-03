package com.tms.threed.jpgGen.batchJpgGen;

import com.tms.threed.jpgGen.GenDetails;
import com.tms.threed.jpgGen.JpgGenContext;

public class JpgBatchJob {

    private final JpgGenContext context;
    private final JpgBatch jpgBatch;
    private final GenDetails genDetails;

    public JpgBatchJob(JpgGenContext context, JpgBatch jpgBatch, GenDetails genDetails) {
        this.context = context;
        this.jpgBatch = jpgBatch;
        this.genDetails = genDetails;
    }

    public JpgGenContext getContext() {
        return context;
    }

    public JpgBatch getJpgBatch() {
        return jpgBatch;
    }

    public GenDetails getGenDetails() {
        return genDetails;
    }
}
