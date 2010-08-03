package com.tms.threed.threedModel.server;

import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.imageModel.shared.Jpg;
import com.tms.threed.imageModel.shared.ImView;
import com.tms.threed.util.fileWalker.FileProcessor;
import com.tms.threed.util.fileWalker.FileWalker;
import com.tms.threed.util.lang.shared.Path;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JpgFallback {

    private Path jpgRootDir;
    private ImView view;

    //calculated fields
    private File jpgDirFile;
    private File fallbackDir;

    private Set<File> viewFallbackJpgs; //featureless - looks only at year/model/view
    private Set<FeatureFallbackJpg> featureJpgFallbacks;

    private File fallbackJpg;

    private static final Log log = LogFactory.getLog(JpgFallback.class);

    public JpgFallback(ImView v, Path jpgRootDir) {
        this.view = v;
        this.jpgRootDir = jpgRootDir;
        this.jpgDirFile = new File(jpgRootDir.toString());

        if (!jpgDirFile.canRead()) throw new IllegalStateException("Can't read jpgDir: [" + jpgDirFile + "]");
        log.error("jpgRootDir = [" + jpgDirFile + "]");

        fallbackDir = getFallbackDir();

        log.error("JPG fallbackDir = [" + fallbackDir + "]");

        if (fallbackDir == null || !fallbackDir.canRead()) {
            log.error("Can't read jpg fallbackDir: [" + fallbackDir + "]");
            fallbackJpg = new File(jpgDirFile, "fallback.jpg");
        } else {

            viewFallbackJpgs = getViewFallbackJpgs();

            log.error("viewFallbackJpgs (count=" + viewFallbackJpgs.size() + "): ");

            for (File viewFallbackJpg : viewFallbackJpgs) {
                log.debug("\t\t" + viewFallbackJpg);
            }

            featureJpgFallbacks = getFeatureJpgFallbacks();

            log.error("featureJpgFallbacks (count=" + featureJpgFallbacks.size() + "): ");
            for (FeatureFallbackJpg featureJpgFallback : featureJpgFallbacks) {
                log.debug("\t\t" + featureJpgFallback);
            }

        }
    }

    Set<File> getViewFallbackJpgs() {
        File[] a = fallbackDir.listFiles(new FileFilter() {
            @Override public boolean accept(File f) {
                return f.canRead() && f.getName().endsWith(".jpg");
            }
        });
        HashSet<File> set = new HashSet<File>();
        if (a == null) return set;
        set.addAll(Arrays.asList(a));
        return set;
    }

    File getFallbackDir() {
        Path path = view.getPath(jpgRootDir);
        File dir = new File(path.toString());
        return new File(dir, "__fallback");
    }

    Set<FeatureFallbackJpg> getFeatureJpgFallbacks() {
        final Set<FeatureFallbackJpg> set = new HashSet<FeatureFallbackJpg>();
        FileWalker w = new FileWalker(getFallbackDir());
        w.setFileFilter(new FileFilter() {
            @Override public boolean accept(File f) {
                if (!f.canRead()) return false;
                if (!f.getName().endsWith(".jpg")) return false;

                File dir1 = f.getParentFile();
                File dir2 = fallbackDir;
                if (dir1.equals(dir2)) return false;

                return true;
            }
        });

        w.setFileProcessor(new FileProcessor() {
            @Override public void processFile(File f) {
                set.add(new FeatureFallbackJpg(f));
            }
        });

        w.start();

        set.removeAll(viewFallbackJpgs);

        return set;
    }

    private File getViewFallback() {
        if (viewFallbackJpgs == null || viewFallbackJpgs.size() == 0) return null;
        return viewFallbackJpgs.iterator().next();
    }


    private File getFeatureFallback(Picks userPicks) {
        FeatureFallbackJpg candidateJpg = null;

        for (FeatureFallbackJpg jpg : featureJpgFallbacks) {
            final Set<String> features = jpg.getFeatureCodes();
            final boolean featureMatch = userPicks.containsAll(features);
            if (!featureMatch) continue;
            if (candidateJpg == null || jpg.hasMoreFeatures(candidateJpg)) {
                candidateJpg = jpg;
            }
        }
        if (candidateJpg != null) {
            return candidateJpg.jpgFile;
        } else {
            return null;
        }
    }

    public File getFallback(Picks userPicks) {
        if (fallbackJpg != null) return fallbackJpg;
        File fallback;
        File f = getFeatureFallback(userPicks);
        if (f != null) fallback = f;
        else fallback = getViewFallback();
        log.error("File Fallback JPG Result: [" + fallback + "]");
        return fallback;
    }

    public ImView getView() {
        return view;
    }

    private class FeatureFallbackJpg {

        File jpgFile;

        FeatureFallbackJpg(File jpgFile) {
            this.jpgFile = jpgFile;
        }

        String getFeatureSegmentOfPath() {
            File d = getFallbackDir();

            String jpgDirAbsolutePath = jpgFile.getParentFile().getAbsolutePath().replace('\\', '/');
            String fallbackDirAbsolutePath = d.getAbsolutePath().replace('\\', '/');
//            String simpleFileName = jpgFile.getName();

            int i1 = fallbackDirAbsolutePath.length();
            int i2 = jpgDirAbsolutePath.length();

            String featureSegment = jpgDirAbsolutePath.substring(i1, i2);

//            System.out.println(jpgFile);
//            System.out.println("featureSegment = [" + featureSegment + "]");
//            System.out.println();

            featureSegment = featureSegment.trim();
            return featureSegment;
        }

        Set<String> getFeatureCodes() {
            HashSet<String> set = new HashSet<String>();

            String s1 = getFeatureSegmentOfPath();
            if (s1.equals("")) return set;


            if (s1.charAt(0) == '/') s1 = s1.substring(1);
            if (s1.charAt(s1.length() - 1) == '/') s1 = s1.substring(0, s1.length() - 2);

            if (s1.contains("/")) {
                String[] a = s1.split("/");
                for (String featureCode : a) {
                    set.add(featureCode);
                }
                return set;
            } else {
                set.add(s1);
                return set;
            }

        }

        public boolean hasMoreFeatures(FeatureFallbackJpg jpg) {
            return this.getFeatureCodes().size() > jpg.getFeatureCodes().size();
        }

        @Override public String toString() {
            return jpgFile.getAbsolutePath();
        }
    }

    public static File getFallback(Path jpgRoot, Jpg jpg,  Picks picks) {
        JpgFallback m = new JpgFallback(jpg.getView(), jpgRoot);
        return m.getFallback(picks);
    }

    

}