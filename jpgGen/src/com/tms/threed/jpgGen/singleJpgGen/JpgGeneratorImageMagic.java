package com.tms.threed.jpgGen.singleJpgGen;

import com.tms.threed.jpgGen.ezProcess.EzProcess;

import java.io.File;
import java.util.concurrent.ExecutorService;

public class JpgGeneratorImageMagic extends JpgGenerator {

    private final ExecutorService executorService;
    private final File imageMagicHome;

    public JpgGeneratorImageMagic(JpgSpec jpgSpec, ExecutorService executorService, File imageMagicHome) {
        super(jpgSpec);
        this.executorService = executorService;
        this.imageMagicHome = imageMagicHome;
    }

    @Override protected void doGen() {
        ImageMagicJpgCommandBuilder commandBuilder = new ImageMagicJpgCommandBuilder(imageMagicHome,spec);
        EzProcess p = new EzProcess(commandBuilder);
        p.executeSync(executorService);
    }

}
