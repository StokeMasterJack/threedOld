package com.tms.threed.jpgGen.blink;

import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.imageModel.shared.ImPng;
import com.tms.threed.jpgGen.ImageMagicCommandBuilderBase;
import com.tms.threed.jpgGen.ezProcess.Command;
import com.tms.threed.util.lang.shared.Path;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;


/**
 * original=/Users/dford/p-java/apache-tomcat-6.0.10/webapps/threed/pngs/2011/camry/exterior/08_Acc-BodyMolding/BM/040/vr_1_02.png
 white=/Users/dford/p-java/apache-tomcat-6.0.10/webapps/threed/pngs/2011/camry/exterior/08_Acc-BodyMolding/BM/040/vr_1_02_w.png


 #convert originalImage.png -background none -white-threshold 0 whiteImge.png
 convert $original -background none -white-threshold 0 $white
 */
public class ImageMagicBlinkCommandBuilder extends ImageMagicCommandBuilderBase {

    private static final Log log = LogFactory.getLog(ImageMagicBlinkCommandBuilder.class);

    private final Path pngRoot;

    private ImPng png;

    public ImageMagicBlinkCommandBuilder(File imageMagicHome, ThreedConfig threedConfig, ImPng png) {
        super(imageMagicHome);
        this.pngRoot = threedConfig.getPngRootFileSystem();
        this.png = png;
    }

    public Command buildCommand() {
        ArrayList<String> a = new ArrayList<String>();

        Path originalPng = png.getPath(pngRoot);
        Path whitePng = png.getBlinkLocalPath();

        a.add(getImageMagicExecutable().getPath());

        assert originalExists();

        a.add(originalPng.toString());
        a.add("-background");
        a.add("none");
        a.add("-white-threshold");
        a.add("0");
        a.add(whitePng.toString());

        return new Command(a);
    }

    private boolean originalExists() {
        Path path = png.getPath(pngRoot);
        String sPath = path.toString();
        File fPath = new File(sPath);
        return fPath.exists() && fPath.isFile() && fPath.canRead();
    }

    private boolean whitePngExists() {
        Path path = png.getBlinkPath(pngRoot);
        String sPath = path.toString();
        File fPath = new File(sPath);
        return fPath.exists();
    }


}