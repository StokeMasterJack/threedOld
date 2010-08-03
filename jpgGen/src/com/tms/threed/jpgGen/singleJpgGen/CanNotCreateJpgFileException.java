package com.tms.threed.jpgGen.singleJpgGen;

import javax.annotation.Nonnull;
import java.io.File;

public class CanNotCreateJpgFileException extends RuntimeException {

    private final File jpgFile;

    public CanNotCreateJpgFileException(@Nonnull File jpgFile) {
        assert jpgFile != null;
        this.jpgFile = jpgFile;
    }

    public File getJpgFile() {
        return jpgFile;
    }


    @Override public String getMessage() {
        return jpgFile.toString();
    }
}