package com.tms.threed.imageModel.shared;

import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.picks.PicksRO;
import com.tms.threed.threedCore.shared.Angle;
import com.tms.threed.util.lang.shared.Path;

import java.util.HashSet;
import java.util.Set;

public class ImPng extends ImChildBase implements ImFeatureOrPng, IsLeaf {

    private final int angle;
    private boolean visible = true; //for testHarness

    public static final String VERSION_PREFIX = "vr_1_";
    public static final String PNG_SUFFIX = ".png";
    public static final String BLINK_SEGMENT = "_w";
    public static final String BLINK_SUFFIX = "_w" + PNG_SUFFIX;


    @Override public String toString() {
        return super.toString();
    }

    public ImPng(int depth, int angle) {
        super(depth);
        this.angle = angle;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getAngle() {
        return angle;
    }

    @Override public String getName() {
        return getVersionPrefix() + getAnglePadded() + getPngSuffix();
    }

    public String getVarCode() {
        return toString().replace("/","").replace(".","").replace("_","X");
    }

    public static String getName(int angle) {
        return VERSION_PREFIX + Angle.getAnglePadded(angle) + PNG_SUFFIX;
    }

    public static Path getLocalPath(int angle) {
        return new Path(getName(angle));
    }

    public String getBlinkName() {
        return getVersionPrefix() + getAnglePadded() + getBlinkSegment() + getPngSuffix();
    }

    private String getBlinkSegment() {
        return BLINK_SEGMENT;
    }

    private String getAnglePadded() {return Angle.getAnglePadded(angle);}

    private String getVersionPrefix() {
        return VERSION_PREFIX;
    }

    public String getPngSuffix() {
        return PNG_SUFFIX;
    }

    public String getBlinkSuffix() {
        return PNG_SUFFIX;
    }

    @Override public boolean isFeature() {
        return false;
    }

    @Override public boolean isPng() {
        return true;
    }

    @Override public void getMatchingPngs(PngMatch bestMatch, PicksRO picks, int angle) {
        if (this.angle == angle) {
            bestMatch.add(this);
        }
    }

    public int indexOf(Var accessory) {
        IsParent p = parent;
        while (p.isFeature()) {
            ImFeature imFeature = (ImFeature) p;
            if (imFeature.is(accessory)) {
                return imFeature.getDepth();
            } else {
                p = imFeature.getParent();
            }
        }
        return -1;
    }

    public boolean hasFeature(Var accessory) {
        IsParent p = parent;
        while (p.isFeature()) {
            ImFeature imFeature = (ImFeature) p;
            if (imFeature.is(accessory)) return true;
            p = imFeature.getParent();
        }

        return false;
    }

    public Path getBlinkPath(Path root) {
        return getParent().getPath(root).append(getBlinkLocalPath());
    }

    public Path getBlinkPath() {
        return getBlinkPath(null);
    }

    public Path getBlinkLocalPath() {
        return new Path(getBlinkName());
    }

    public Set<Var> getFeatures() {
        HashSet<Var> vars = new HashSet<Var>();
        getFeatures(vars);
        return vars;
    }

    public void getFeatures(Set<Var> features) {
        IsParent p = parent;
        while (p.isFeature()) {
            ImFeature f = p.asFeature();
            features.add(f.getVar());
            p = p.getParent();
        }
    }

    public int getFeatureCount() {
        int fc = 0;
        IsParent p = parent;
        while (p.isFeature()) {
            fc++;
            p = p.getParent();
        }
        return fc;
    }

    public static boolean hasPngSuffix(String localName) {
        return localName.endsWith(PNG_SUFFIX);
    }

    public static boolean hasBlinkSuffix(String localName) {
        return localName.endsWith(BLINK_SUFFIX);
    }

    public static boolean canParseAngeFromLocalName(String localName) {
        try {
            getAngleFromPng(localName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int getAngleFromPng(String localName) {
        String s1 = localName.replace(ImPng.VERSION_PREFIX, "");
        String s2 = s1.replaceAll(ImPng.PNG_SUFFIX, "");
        return Integer.parseInt(s2);
    }

    public static boolean isValidLocalName(String localName) {
        return hasPngSuffix(localName) && !hasBlinkSuffix(localName) && canParseAngeFromLocalName(localName);
    }

    @Override public Path getPath() {
        Path p = super.getPath();
        return p;
    }

    @Override public boolean containsAngle(int angle) {
        return this.angle == angle;
    }

    public static ImPng bestMatch(ImPng png1, ImPng png2, Var accessory) {
        int featureIndex1 = png1.indexOf(accessory);
        int featureIndex2 = png2.indexOf(accessory);
        if (featureIndex1 < featureIndex2) return png1;
        else return png2;
    }

    public ImPng copy(int angle) {
        if (this.angle == angle) return new ImPng(depth, angle);
        else throw new IllegalStateException();
    }

    @Override public void getVarSet(Set<Var> varSet) {
       //intentionally blank
    }

    @Override public void getPngs(Set<ImPng> pngs) {
        pngs.add(this);
    }

    public ImView getView() {
        ImNode n = this;
        while (true) {
            n = n.getParent();
            if (n.isView()) return n.asView();
        }
    }

    public boolean isEmpty() {
        return false; //todo
    }

    public ImLayer getLayer() {
        ImNode n = this;
        while (true) {
            n = n.getParent();
            if (n.isLayer()) return n.asLayer();
        }
    }

    public Path getViewRelativePath()  {
        Path pngPath = getPath();
        Path layerPath = getView().getPath();
        return pngPath.leftTrim(layerPath);
    }

    public Path getLayerRelativePath()  {
        Path pngPath = getPath();
        Path layerPath = getLayer().getPath();
        return pngPath.leftTrim(layerPath);
    }

    public boolean isPartOfJpg() {
        return getLayer().isPartOfJpg();
    }

    public boolean isAccessory() {
        return getLayer().isAccessory();
    }
}
