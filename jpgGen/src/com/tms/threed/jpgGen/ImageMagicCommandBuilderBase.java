package com.tms.threed.jpgGen;

import com.tms.threed.jpgGen.ezProcess.AbstractCommandBuilder;

import java.io.File;

public abstract class ImageMagicCommandBuilderBase extends AbstractCommandBuilder {

    private final File imageMagicHome;
    private final File imageMagicExecutable;

    public ImageMagicCommandBuilderBase(File imageMagicHome) {
        this.imageMagicHome = imageMagicHome;

        if (imageMagicHome == null) {
            this.imageMagicExecutable = new File("convert");
        } else {
            this.imageMagicExecutable = new File(imageMagicHome, "convert");
        }
    }

    public File getImageMagicHome() {
        return imageMagicHome;
    }

    public File getImageMagicExecutable() {
        return imageMagicExecutable;
    }

}
