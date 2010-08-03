package com.tms.threed.imageModel.shared;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.threedCore.shared.Angle;
import com.tms.threed.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  Unified JPG URL:
 *      Pattern: {view.jpgRoot}/{feature1.path}/{feature2.path}/{featureN.path}/vr_{jpg.version}_{jpg.angle}.jpg
 *      Example: {jpgRoot}/2009/Venza/Interior/Seats-Fabric/IntColor-LightGray/ExtColor-Blue/RoofRack/vr_1_01.jpg
 *
 *      Where:
 *          view.jpgRoot: {config.jpgRoot}/2009/Venza/Interior
 *          feature1.path: Seats-Fabric
 *          feature2.path: IntColor-LightGray
 *          feature3.path: ExtColor-Blue
 *          feature4.path: RoofRack
 *          jpg.version: 1
 *          angle: 01
 */
public class Jpg {

    private final ImView view;
    private final List<ImPng> pngs;
    private final int angle;

    private List<Var> featureList;
    private Path localPath;

    public Jpg(ImView view, List<ImPng> pngs, int angle) {

        assert pngs.size() != 0;

        this.view = view;
        this.angle = angle;
        this.pngs = pngs;

        //cache
        this.featureList = initFeatureList();
        this.localPath = initLocalPath();


    }

    public List<Var> initFeatureList() {
        Set<Var> f = new HashSet<Var>();
        for (ImPng png : pngs) {
            png.getFeatures(f);
        }
        List<Var> a = new ArrayList<Var>(f);
        Collections.sort(a);
        return a;
    }


    public List<Var> getFeatureList() {
        return featureList;
    }

    /**
     * @return Ex: 19W/1F7
     */
    public Path getFeaturePath() {
        return FeatureModel.createFeaturePath(this.featureList);
    }

    /**
     * @return Ex: /2009/venza/exterior/007B/065B/19W/202/3T/DIO1/vr_1_02.jpg
     */
    public Path getPath() {
        Path viewPath = getView().getPath();
        Path myPath = getFeaturePath().append(getLocalPath());
        return viewPath.append(myPath);
    }

    /**
     * @return Ex: vr_1_02.jpg
     */
    public Path getLocalPath() {
        return localPath;
    }

    private Path initLocalPath() {
        return new Path("vr_1_" + Angle.getAnglePadded(angle) + ".jpg");
    }

    public Path getPath(Path jpgRoot) {
        return jpgRoot.append(getPath());
    }

    public ImView getView() {
        return view;
    }

    public int getAngle() {
        return angle;
    }

    public List<ImPng> getPngs() {
        return pngs;
    }

    public ImageStack getImageStack() {
        ArrayList<ImPng> basePngs = new ArrayList<ImPng>();

        ArrayList<ImPng> topPngs = new ArrayList<ImPng>();
        for (int i = 0; i < pngs.size(); i++) {
            ImPng png = pngs.get(i);
            if (png.isPartOfJpg()) {
                topPngs.add(png);
            } else {
                basePngs.add(png);
            }
        }
        Jpg baseJpg = new Jpg(view, basePngs, angle);
        return new ImageStack(baseJpg, topPngs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jpg that = (Jpg) o;
        return this.localPath.equals(that.localPath);

    }

    @Override public String toString() {
        return getPath().toString();
    }

    private transient int hash = -1;

    @Override
    public int hashCode() {
        if (hash == -1) {
            this.hash = getPath().toString().hashCode();
        }
        return hash;
    }


}