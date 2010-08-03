package com.tms.threed.jpgGen.singleJpgGen;

import com.tms.threed.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * The spec to build a single JPG file from an ordered list of PNG files (aka layers)
 */
public class Jpg {

    private final List<Path> pngs;
    private final Path jpg;

    public Jpg(List<Path> pngs, Path jpg) {
        if (pngs == null) throw new IllegalArgumentException("inputPngs must not be nonnull");
        if (pngs.isEmpty()) throw new IllegalArgumentException("inputPngs must must not be empty list");
        if (jpg == null) throw new IllegalArgumentException("outputJpg must not be nonnull");

        this.pngs = pngs;
        this.jpg = jpg;
    }

    /**
     * Returns a z-index ordered list of PNG files (relative to pngRoot input folder) to be turned into a single jpg file.
     * These files should already be present before generating the JPG
     *
     * @return A z-index ordered list of PNG files (relative to pngRoot input folder) to be turned into a single jpg file
     */
    public List<Path> getPngPaths() {
        return pngs;
    }

    public List<Path> getPngPaths(Path pngRoot) {
        List<Path> a = new ArrayList<Path>();
        for (Path png : pngs) {
            a.add(pngRoot.append(png));
        }
        return a;
    }

    /**
     * Returns the file name (relative to jpgRoot output folder) of the JPG to be generated.
     *
     */
    public Path getJpgPath() {
        return jpg;
    }

    public Path getJpgPath(Path jpgRoot) {
        return jpgRoot.append(jpg);
    }


}
