package com.tms.threed.imageModel.shared;

import com.tms.threed.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *  This class represents a stack of images such that:
 *
 *  1.  Each image in the stack has the same x,y,width,height but different zIndex
 *  2.  The bottom image is always a JPG all others are PNGs 
 */
public class ImageStack {

    private final Jpg jpg;
    private final List<ImPng> pngs;

    public ImageStack(Jpg jpg, List<ImPng> pngs) {
        this.jpg = jpg;
        this.pngs = pngs;
    }

    public Jpg getJpg() {
        return jpg;
    }

    public List<ImPng> getPngs() {
        return pngs;
    }

    public List<Path> getPaths(Path pngRootHttp, Path jpgRootHttp) {
        ArrayList<Path> list = new ArrayList<Path>();
        list.add(jpg.getPath(jpgRootHttp));

        for (int i = 0; i < pngs.size(); i++) {
            ImPng png = pngs.get(i);
            list.add(png.getPath(pngRootHttp));
        }

        return list;
    }

    public void print(){
        System.out.println("ImageStack:");
        System.out.println("\t jpg:");
        System.out.println("\t\t" + jpg);
        System.out.println("\t pngs: ");
        for (ImPng png : pngs) {
            System.out.println("\t\t" +  png);
        }
        System.out.println();
    }

    public void purgeZLayers() {
        pngs.clear();
    }
}
