package com.tms.threed.jpgGen.singleJpgGen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.tms.threed.imageModel.shared.ImPng;
import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.util.lang.shared.Path;

public class JpgSpecBuilder {

    private static final ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();

    SeriesKey seriesKey = SeriesKey.VENZA_2009;
    String viewName = "exterior";
    int angle = 4;
    int version = 1;
    final String outputJpgLocalName;


    String[] subLayers = new String[]{
            "/01_Background",
            "/02_Standard",
            "/03_Roof/1F7",
            "/04_Paint/1F7",
            "/05_Wheels/19W",
            "/06_Exhaust/SingleExhaust",
            "/10_RoofRack/3T",
            "/11_BikeSkiCombo/DIO1"
    };

    Path pngRoot = threedConfig.getPngRootFileSystem();
    Path modelPngRoot = seriesKey.getSeriesPngRoot(pngRoot);
    Path viewPngRoot = modelPngRoot.append(viewName);

    File outputJpgRoot = new File("/temp");

    File outputJpg;


    public JpgSpecBuilder(String outputJpgLocalName) {
        this.outputJpgLocalName = outputJpgLocalName;

        outputJpg = new File(outputJpgRoot, outputJpgLocalName + ".jpg");
    }

    public void print() {
        System.out.println("pngRoot = [" + pngRoot + "]");
        System.out.println("modelPngRoot = [" + modelPngRoot + "]");
        System.out.println("viewPngRoot = [" + viewPngRoot + "]");

        Path pSubLayer = new Path(subLayers[0]);

        System.out.println("getPngFile(subLayers[0]) = [" + getPngFile(pSubLayer) + "]");
        System.out.println();
    }

    private Path getLocalPngFile() {
        return ImPng.getLocalPath(this.angle);
    }

    private Path getPngFile(Path subLayer) {
        return viewPngRoot.append(subLayer).append(getLocalPngFile());
    }

    public List<File> getPngs() {
        ArrayList<File> a = new ArrayList();
        for (String subLayer : subLayers) {
            Path p = getPngFile(new Path(subLayer));
            a.add(new File(p.toString()));
        }
        return a;
    }

    public File getOutputJpg() {
        return outputJpg;
    }

    public JpgSpec buildJpgSpec() {
        return new JpgSpec(getPngs(), outputJpg, true);
    }
}
