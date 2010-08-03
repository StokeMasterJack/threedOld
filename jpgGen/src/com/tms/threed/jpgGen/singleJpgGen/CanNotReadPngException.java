package com.tms.threed.jpgGen.singleJpgGen;

import javax.annotation.Nonnull;
import java.io.File;

public class CanNotReadPngException extends RuntimeException {

    private final File pngFile;

    public CanNotReadPngException(@Nonnull File pngFile) {
        assert pngFile != null;
        this.pngFile = pngFile;
    }

    public File getPngFile() {
        return pngFile;
    }


    @Override public String getMessage() {
        return pngFile.toString();
    }
}
