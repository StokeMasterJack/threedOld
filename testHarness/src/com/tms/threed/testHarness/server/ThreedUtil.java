package com.tms.threed.testHarness.server;

import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.util.lang.shared.Path;
import com.tms.threed.util.lang.shared.Strings;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import static com.tms.threed.util.lang.shared.Strings.isInt;

public class ThreedUtil {

    public static List<SeriesKey> getSeriesKeys(Path pngRootFs) {

        ArrayList<SeriesKey> seriesKeys = new ArrayList<SeriesKey>();

        File pngRoot = new File(pngRootFs.toString());

        File[] yearDirs = pngRoot.listFiles(new FileFilter() {
            @Override public boolean accept(File f) {
                if (f.isHidden()) return false;
                if (f.getName().startsWith(".")) return false;
                if (!f.isDirectory()) return false;
                if (f.getName().length() != 4) return false;
                if (!isInt(f.getName())) return false;
                return true;
            }
        });

        for (File yearDir : yearDirs) {
            int year = Integer.parseInt(yearDir.getName());

            File[] seriesDirs = yearDir.listFiles(new FileFilter() {
                @Override public boolean accept(File f) {
                    if (f.isHidden()) return false;
                    if (f.getName().startsWith(".")) return false;
                    if (!f.isDirectory()) return false;
                    String name = f.getName();
                    if (Strings.containsWhitespace(name)) return false;
                    if (Strings.containsNonwordChar(name)) return false;
                    return true;
                }
            });

            for (File seriesDir : seriesDirs) {
                String seriesName = seriesDir.getName();
                SeriesKey seriesKey = new SeriesKey(year, seriesName);
                seriesKeys.add(seriesKey);
            }

        }

        return seriesKeys;
    }

}
