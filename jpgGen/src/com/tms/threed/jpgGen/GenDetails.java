package com.tms.threed.jpgGen;

import java.awt.Dimension;

public class GenDetails {

    private final int quality;
    private final boolean overwriteMode;
    private final Dimension jpgSize;

    /**
     *
     * @param quality
     * @param overwriteMode
     * @param jpgSize
     */
    public GenDetails(boolean overwriteMode, int quality, Dimension jpgSize) {
        if (quality < 1 || quality > 100)
            throw new IllegalArgumentException("quality must be between 1 and 100 exclusive");

        this.quality = quality;
        this.overwriteMode = overwriteMode;
        this.jpgSize = jpgSize;
    }

    public GenDetails(boolean overwriteMode, int quality) {
        this(overwriteMode, quality, null);
    }

    public GenDetails(boolean overwriteMode) {
        this(overwriteMode, 75);
    }

    /**
     * JPG compression quality.
     *
     * 100 is best
     * 0 is worst
     *
     * @return int between o and 100 inclusive
     */
    public int getQuality() {
        return quality;
    }

    /**
     * If this is true, then overwrite jpg files. The default should be false.
     * This is useful for 2 reasons: if one needs to restart jpg gen
     *
     */
    public boolean isOverwriteMode() {
        return overwriteMode;
    }

    /**
     * Used for scaling. If this returns null then no scaling (i.e.JPG image dims same as input png image dims)
     * @return The image height and width in pixels
     */
    public Dimension getJpgSize() {
        return jpgSize;
    }
}
