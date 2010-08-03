package com.tms.threed.jpgGen.singleJpgGen;


import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Takes multiple pngs and turns them into a single jpg
 */
public class JpgGeneratorPureJava extends JpgGenerator {

    public JpgGeneratorPureJava(JpgSpec spec) {
        super(spec);
    }

    @Override protected void doGen() {
        if (spec.jpgAlreadyCreated() && !spec.isOverwriteMode()) return;

        List<File> pngFiles = spec.getInputPngs();
        File bgFile = pngFiles.get(0);
        BufferedImage bgImage = readImage(bgFile);
        Graphics2D bgGraphics = bgImage.createGraphics();

        for (int i = 1; i < pngFiles.size(); i++) {
            final File file = pngFiles.get(i);
            BufferedImage image = readImage(file);
            bgGraphics.drawImage(image, 0, 0, null);
        }
        bgGraphics.dispose();
        createJpg(bgImage);
    }

    private static BufferedImage readImage(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void writeJpg(BufferedImage input) {
//        try {
//            ImageIO.write(input, "jpg", jpgFile);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        if(true)   return;
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("JPG");
        if (!iter.hasNext()) throw new IllegalStateException();

        ImageWriter writer = iter.next();
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        float q = (float)spec.getOutputQuality() * .01F;
        iwp.setCompressionQuality(q);
        FileImageOutputStream output = newFileImageOs(spec.getOutputJpg());
        writer.setOutput(output);
        IIOImage image = new IIOImage(input, null, null);
        writeImage(writer, iwp, image);


    }


    private void createJpg(BufferedImage png) {
        BufferedImage bufferedImage;
        bufferedImage = new BufferedImage(png.getWidth(null), png.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();
        //Color.WHITE estes the background to white. You can use any other color
        g.drawImage(png, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), Color.WHITE, null);
        writeJpg(bufferedImage);

    }


    private static FileImageOutputStream newFileImageOs(File outFile) {
        try {
            return new FileImageOutputStream(outFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeImage(ImageWriter writer, ImageWriteParam iwp, IIOImage image) {
        try {
            writer.write(null, image, iwp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
