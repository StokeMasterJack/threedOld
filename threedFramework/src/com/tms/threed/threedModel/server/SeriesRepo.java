package com.tms.threed.threedModel.server;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.threedCore.shared.SeriesId;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.lang.shared.Path;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.tms.threed.util.lang.shared.Strings.isEmpty;

public class SeriesRepo {

    private final SeriesKey seriesKey;
    private final ThreedConfig threedConfig;
    private final Path pngRoot;

    public static final String VERSIONS_DIR_NAME = "versions";
    public static final String CURRENT_VERSION_FILE_NAME = "currentVersion.txt";

    public SeriesRepo(@Nonnull SeriesKey seriesKey, @Nonnull ThreedConfig threedConfig) {
        assert seriesKey != null;
        assert threedConfig != null;

        this.seriesKey = seriesKey;
        this.threedConfig = threedConfig;
        this.pngRoot = threedConfig.getPngRootFileSystem();

    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    public File getRepoDir() {
        Path seriesPngRoot = seriesKey.getSeriesPngRoot(pngRoot);
        return new File(seriesPngRoot.toString());
    }

    public SeriesDir getVersion(int version) {
        return new SeriesDir(getSeriesDir(version), getSeriesId(version));
    }

    public SeriesDir getVersion() {
        return getVersion(getCurrentVersion());
    }

    public SeriesId getSeriesId(int version) {
        return new SeriesId(getSeriesKey(), version);
    }

    public SeriesId getSeriesId() {
        return new SeriesId(getSeriesKey(), getCurrentVersion());
    }

    public File getSeriesDir() {
        return getSeriesDir(getCurrentVersion());
    }

    public File getSeriesDir(int version) {
        if (version == 0) {
            return getRepoDir();
        } else {
            return new File(getVersionsDir(), version + "");
        }
    }

    private File getCurrentVersionFile() {
        return new File(getVersionsDir(), CURRENT_VERSION_FILE_NAME);
    }

    private File getVersionsDir() {
        return new File(getRepoDir(), VERSIONS_DIR_NAME);
    }

    /**
     * @return 0 for initial version
     */
    public int getCurrentVersion() {
        File vDir = getVersionsDir();
        if (!vDir.exists()) return 0;
        if (!vDir.isDirectory() || !vDir.canRead())
            throw new IllegalStateException(VERSIONS_DIR_NAME + " dir must be a readable directory");

        File vFile = getCurrentVersionFile();
        if (!vFile.exists()) return 0;
        if (!vFile.isFile() || !vFile.canRead())
            throw new IllegalStateException(CURRENT_VERSION_FILE_NAME + " file must be a readable file");

        try {
            String vString = Files.readFirstLine(vFile, Charset.defaultCharset());
            if (Strings.isNullOrEmpty(vString))
                throw new IllegalStateException(CURRENT_VERSION_FILE_NAME + " must not be empty");
            try {
                return Integer.parseInt(vString.trim());
            } catch (NumberFormatException e) {
                throw new IllegalStateException(CURRENT_VERSION_FILE_NAME + " must contain a valid int");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public SeriesId getCurrentSeriesId() {
        return new SeriesId(seriesKey, getCurrentVersion());
    }

    public FeatureModel createFeatureModel() {
        return createFeatureModel(getCurrentVersion());
    }

    public FeatureModel createFeatureModel(int version) {
        return getVersion(version).createFeatureModel();
    }

    public ImSeries createImageModel(int version, FeatureModel fm) {
        return getVersion(version).createImageModel(fm);
    }

    public ImSeries createImageModel(FeatureModel fm) {
        return getVersion().createImageModel(fm);
    }

    public ThreedModel createThreedModel(int version, Path httpImageRoot) {
        return getVersion(version).createThreedModel(httpImageRoot);
    }

    public ThreedModel createThreedModel(Path httpImageRoot) {
        return createThreedModel(getCurrentVersion(), httpImageRoot);
    }

    public ThreedModel createModel(int version, @Nonnull Path httpImageRoot) {
        assert httpImageRoot != null;
        assert !isEmpty(httpImageRoot.toString());
        return getVersion(version).createThreedModel(httpImageRoot);
    }

    public ThreedModel createModel(Path httpImageRoot) {
        return getVersion().createThreedModel(httpImageRoot);
    }

    public boolean useNewConfigurator() {
        if (!getRepoDir().isDirectory()) return false;
        File modelXmlFile = getVersion().getModelXmlFile();
        if (!modelXmlFile.isFile()) return false;
        File testFile = new File(getRepoDir(), "useFlashConfigurator");
        return !testFile.isFile();
    }


}
