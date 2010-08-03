package com.tms.threed.jpgGen.singleJpgGen;

import com.tms.threed.jpgGen.ImageMagicCommandBuilderBase;
import com.tms.threed.jpgGen.ezProcess.Command;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageMagicJpgCommandBuilder extends ImageMagicCommandBuilderBase {

    private static final Log log = LogFactory.getLog(ImageMagicJpgCommandBuilder.class);

    private final JpgSpec jpgSpec;

    public ImageMagicJpgCommandBuilder(File imageMagicHome,JpgSpec jpgSpec) {
        super(imageMagicHome);
        this.jpgSpec = jpgSpec;
    }

    public Command buildCommand() {
        File jpgOutFile = jpgSpec.getOutputJpg();
        final List<String> command = buildCommandList(jpgOutFile);
        return new Command(command);
    }

    private List<String> buildCommandList(File jpgOutFile) {
        List<File> pngInFiles = jpgSpec.getInputPngs();

        ArrayList<String> commandList = new ArrayList<String>();

        commandList.add(getImageMagicExecutable().getPath());

        //first image should be background
        commandList.add(pngInFiles.get(0).toString());

        //overlay all additional images
        for (int i = 1; i < pngInFiles.size(); i++) {
            commandList.add(pngInFiles.get(i).toString());
            commandList.add("-composite");
        }

        //output result
        commandList.add("-quality");
        commandList.add("" + jpgSpec.getOutputQuality());
        commandList.add("-flatten");
        commandList.add(jpgOutFile.getAbsolutePath());

        return commandList;
    }

}
