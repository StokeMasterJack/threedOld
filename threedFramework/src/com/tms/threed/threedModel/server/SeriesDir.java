package com.tms.threed.threedModel.server;

import com.tms.threed.featureModel.server.FeatureModelBuilderXml;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.imageModel.server.ImageModelBuilder;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.threedCore.shared.SeriesId;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.lang.shared.Path;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import java.io.File;

import static com.tms.threed.util.lang.shared.Strings.isEmpty;

/**
 * Represents a particular version
 */
public class SeriesDir {

    private final File seriesDir;
    private final SeriesId seriesId;

    public SeriesDir(File seriesDir, SeriesId seriesId) {
        this.seriesDir = seriesDir;
        this.seriesId = seriesId;
        check();
    }

    public File getSeriesDir() {
        return seriesDir;
    }

    public SeriesId getSeriesId() {
        return seriesId;
    }

    public int getVersion() {
        return seriesId.getVersion();
    }

    public File getModelXmlFile() {
        return new File(seriesDir, "model.xml");
    }

    public FeatureModel createFeatureModel() {
        FeatureModelBuilderXml b = new FeatureModelBuilderXml(seriesId, getModelXmlFile());
        return b.buildModel();
    }

    public ImSeries createImageModel(FeatureModel fm) {
        ImageModelBuilder imageModelFactory = new ImageModelBuilder(fm, seriesDir);
        return imageModelFactory.buildImageModel();
    }

    public ThreedModel createThreedModel(@Nonnull Path httpImageRoot) {
        assert httpImageRoot != null;
        assert !isEmpty(httpImageRoot.toString());
        FeatureModel featureModel = createFeatureModel();
        ImSeries imageModel = createImageModel(featureModel);
        return new ThreedModel(featureModel, imageModel, httpImageRoot);
    }

    private void check() throws IllegalStateException {
        log.debug("Checking seriesDir: [" + seriesDir + "]");
        if (!seriesDir.canRead() || !seriesDir.isDirectory()) {
            throw new IllegalStateException("Unable to read seriesDir: [" + seriesDir + "]");
        }
    }

    private static final Log log = LogFactory.getLog(SeriesDir.class);

}
