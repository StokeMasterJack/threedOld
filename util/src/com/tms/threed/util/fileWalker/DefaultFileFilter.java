package com.tms.threed.util.fileWalker;

import java.io.File;
import java.io.FileFilter;

class DefaultFileFilter implements FileFilter {
    public boolean accept(File f) {
        return !f.isHidden() && !f.getName().startsWith(".");
    }
}
