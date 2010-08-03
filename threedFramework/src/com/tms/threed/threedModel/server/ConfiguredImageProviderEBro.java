package com.tms.threed.threedModel.server;

import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.imageModel.shared.ImPng;
import com.tms.threed.imageModel.shared.ImView;
import com.tms.threed.imageModel.shared.ImageStack;
import com.tms.threed.imageModel.shared.Jpg;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedCore.shared.ViewKey;
import com.tms.threed.util.lang.shared.Path;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 *  This provides a bridge (an adapter) between the eBrochure cover page
 *  and the ConfiguredImage subsystem.
 */
public class ConfiguredImageProviderEBro {

    private final SThreedModel model;
    private final Picks picksFixed;

    public ConfiguredImageProviderEBro(String modelYear, String modelName, EBrochureRequest eBrochureRequest) {
        log.debug("ConfiguredImageProviderEBro init [" + modelYear + " " + modelName + "]");
        LinkedHashSet<String> picksRaw = eBrochureRequest.getFeatureCodes();

        log.debug("Getting ThreedModels");
        SThreedModels threedModels = SThreedModels.get();
        model = threedModels.getModel(modelYear, modelName);

        log.debug("User picks (featureSet) before fixUp: [" + picksRaw + "]");


        picksFixed = model.fixupPicks(picksRaw);

        log.debug("User picks (featureSet) after fixUp: [" + picksFixed + "]");
    }

    /**
     * @return URL of the image file representing the "as configured" exterior image
     */
    public URL getExteriorConfiguredImage() {
        Jpg jpg = model.getJpg(ViewKey.EXTERIOR, ViewKey.HERO_ANGLE, picksFixed);
        return getJpgUrlSafe(jpg);
    }

    public List<URL> getExteriorConfiguredImages() {
        return getConfiguredImages(ViewKey.EXTERIOR, ViewKey.HERO_ANGLE);
    }

    public List<URL> getInteriorConfiguredImages() {
        return getConfiguredImages(ViewKey.INTERIOR, ViewKey.DASH_ANGLE);
    }

    public List<URL> getConfiguredImages(String viewName, int angle) {

        Jpg jpg = model.getJpg(viewName, angle, picksFixed);

        ThreedConfig threedConfig = model.getThreedConfig();
        ImageStack imageStack = jpg.getImageStack();

        URL jpgUrl = getJpgUrlSafe(imageStack.getJpg());

        ArrayList<URL> urls = new ArrayList<URL>();
        urls.add(jpgUrl);

        for (ImPng png : imageStack.getPngs()) {
            Path pngPath = png.getPath(threedConfig.getPngRootFileSystem());
            File pngFile = new File(pngPath.toString());

            if (pngFile.canRead()) {
                log.debug(viewName + " configured zPng image found: [" + pngFile + "]");
            } else {
                log.error(viewName + " configured zPng image NOT found: [" + pngFile + "]");
                continue;
            }

            URI uri = pngFile.toURI();
            try {
                URL pngUrl = uri.toURL();
                urls.add(pngUrl);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        return urls;
    }

    /**
     * @return URL of the image file representing the "as configured" exterior image
     */
    public URL getInteriorConfiguredImage() {
        Jpg jpg = model.getJpg(ViewKey.INTERIOR, ViewKey.DASH_ANGLE, picksFixed);
        return getJpgUrlSafe(jpg);
    }

    public URL getJpgUrlSafe(Jpg jpg) {
        Path jpgRoot = model.getJpgRootFileSystem();
        ImView view = jpg.getView();
        String viewName = view.getName();

        log.debug(viewName + " configured image: [" + jpg.getPath(jpgRoot) + "]");

        Path jpgPath = jpg.getPath(jpgRoot);
        File jpgFile = new File(jpgPath.toString());

        if (jpgFile.canRead()) {
            log.debug(viewName + " configured image found.");
        } else {
            log.error(viewName + " configured image NOT image found. Trying fallback");
            jpgFile = JpgFallback.getFallback(jpgRoot, jpg, picksFixed);
            log.error(viewName + " falling back to jpg: [" + jpgFile + "]");
        }

        URI uri = jpgFile.toURI();
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    private static final Log log = LogFactory.getLog(ConfiguredImageProviderEBro.class);

}
