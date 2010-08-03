package com.tms.threed.jpgGen.singleJpgGen;

import junit.framework.TestCase;

import java.io.File;
import java.util.concurrent.Executors;

public class SingleJpgGeneratorTest extends TestCase {

    public void testPureJava() throws Exception {
        JpgSpecBuilder jpgSpecBuilder = new JpgSpecBuilder("pureJava");
        JpgSpec jpgSpec = jpgSpecBuilder.buildJpgSpec();
        JpgGenerator jpgGenerator = new JpgGeneratorPureJava(jpgSpec);
        jpgGenerator.generate();
    }

    public void testImageMagic() throws Exception {
        JpgSpecBuilder jpgSpecBuilder = new JpgSpecBuilder("imageMagic");
        File imHome = ImageMagicHomes.DAVE_MAC;
        JpgSpec jpgSpec = jpgSpecBuilder.buildJpgSpec();
        JpgGenerator jpgGenerator = new JpgGeneratorImageMagic(jpgSpec, Executors.newCachedThreadPool(), imHome);
        jpgGenerator.generate();
    }

}
