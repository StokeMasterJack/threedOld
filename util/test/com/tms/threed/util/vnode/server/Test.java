package com.tms.threed.util.vnode.server;

import com.tms.threed.util.vnode.shared.VNode;
import junit.framework.TestCase;

import java.io.File;

public class Test extends TestCase {

    File threedRoot = new File("/Users/dford/p-java/apache-tomcat-6.0.10/webapps/threed");
    File seriesPngRoot = new File("/Users/dford/p-java/apache-tomcat-6.0.10/webapps/threed/pngs/2011/camry");

    //vNode.getTotalNodeCount() = [2928]

    public void test() throws Exception {
        VNodeBuilder vNodeBuilder = new VNodeBuilder(seriesPngRoot);

        vNodeBuilder.setVFileFilter(ff);
        VNode vNode = vNodeBuilder.buildVNode();

        System.out.println("vNode.getTotalNodeCount() = [" + vNode.getTotalNodeCount() + "]");

//        vNode.printTree();
    }

    VFileFilter ff = new VFileFilter() {
        @Override public boolean accept(File file, File root, int depth) {
            if (depth == 1 && !file.isDirectory()) {
                System.out.println("Skipping: " + file);
                return false;
            }
            if (depth == 2 && !file.isDirectory()) {
                System.out.println("Skipping: " + file);
                return false;
            }
            return true;
        }
    };
}
