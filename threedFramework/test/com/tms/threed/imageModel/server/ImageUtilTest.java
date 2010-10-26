package com.tms.threed.imageModel.server;

import com.google.common.io.Files;
import junit.framework.TestCase;

import java.io.File;
import java.util.Set;

public class ImageUtilTest extends TestCase {

    public void test() throws Exception {
        Set<String> messageDigests = ImageUtil.getMessageDigestAlgorithms();
        for (String digest : messageDigests) {
            System.out.println("digest = [" + digest + "]");
        }

    }


    public void testIsEmpty() throws Exception {
        String s = "/Users/dford/p-java/apache-tomcat-6.0.10/webapps/threed/pngs/2011/tacoma/exterior/21_Acc-CargoDiv/DI03/Regular/2WD/vr_1_01.png";
        File f = new File(s);
        boolean empty = ImageUtil.isEmpty(f);
        System.out.println("empty = [" + empty + "]");



//        Files.getDigest()

    }
}
