package com.tms.threed.jpgGen.singleJpgGen;

import com.tms.threed.jpgGen.GenDetails;

import java.awt.Dimension;
import java.io.File;
import java.util.List;

public class JpgSpec {

    private final List<File> inputPngs;
    private final File outputJpg;

    private final GenDetails genDetails;

    public JpgSpec(List<File> inputPngs, File outputJpg, GenDetails genDetails) throws IllegalArgumentException {

        if (inputPngs == null) throw new IllegalArgumentException("inputPngs must not be nonnull");
        if (inputPngs.isEmpty()) throw new IllegalArgumentException("inputPngs must must not be empty list");
        if (outputJpg == null) throw new IllegalArgumentException("outputJpg must not be nonnull");


        this.inputPngs = inputPngs;
        this.outputJpg = outputJpg;

        this.genDetails = genDetails;
    }

    public JpgSpec(List<File> inputPngs, File outputJpg, boolean overwrightMode) throws IllegalArgumentException {
        this(inputPngs, outputJpg, new GenDetails(overwrightMode));
    }

    public GenDetails getGenDetails() {
        return genDetails;
    }

    public boolean jpgAlreadyCreated() {
        return outputJpg.exists();
    }

    public void prePreGen() throws CanNotReadPngException, CanNotCreateJpgFileException {
        checkCanReadPngs();
        createJpgParentDirsIfNecessary();
    }

    public void createJpgParentDirsIfNecessary() throws CanNotCreateJpgFileException {
        File parentDir = outputJpg.getParentFile();
        if (parentDir.exists()) return;
        boolean success = parentDir.mkdirs();
        if (!success) throw new CanNotCreateJpgFileException(outputJpg);
    }

    private void checkCanReadPngs() throws CanNotReadPngException {
        for (File png : inputPngs) {
            if (!png.canRead()) throw new CanNotReadPngException(png);
            if (!png.isFile()) throw new CanNotReadPngException(png);
        }
    }

    public List<File> getInputPngs() {
        return inputPngs;
    }

    public File getOutputJpg() {
        return outputJpg;
    }

    public boolean isOverwriteMode() {
        return genDetails.isOverwriteMode();
    }

    public int getOutputQuality() {
        return genDetails.getQuality();
    }

    public Dimension getOutputJpgSize() {
        return genDetails.getJpgSize();
    }
}
