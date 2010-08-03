package com.tms.threed.threedModelBuilders;

import com.tms.threed.imageModel.server.EmptyPngFinder;
import junit.framework.TestCase;

import java.io.File;

public class EmptyPngFinderTest extends TestCase {

    String s1 = "/Users/dford/Downloads/SiennaPngs/580_Int_Config_1-6";
    String ss = "/Users/dford/p-java/apache-tomcat-6.0.10/webapps/threed/pngs/2011/tacoma";
    String s2 = "/Users/dford/p-java/apache-tomcat-6.0.10/webapps/threed/pngs/2011/tacoma";
    String s3 = "/Users/dford/p-java/apache-tomcat-6.0.10/webapps/threed/pngs/2011/sienna/exterior/06_acc_01_RoofRack/3T";

    String startDirName = s2; //NOTE: be sure to set this dude!!!

    File startDir = new File(startDirName);

    /**
     * Run this guy first as a sanity check
     */
    public void test_ShowEmptyPngFiles() {
        EmptyPngFinder.showEmptyPngFiles(startDir);
    }

    /**
     * Then run this guy ti actually delete the files
     */
    public void test_deleteEmptyPngFiles() {
        EmptyPngFinder.deleteEmptyPngFiles(startDir);
    }


}
