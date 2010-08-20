package com.tms.threed.imageModel.shared;

import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.picks.PicksRO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ImLayer extends ImChildBase implements ImLayerOrFeature, IsChild, IsParent<ImFeatureOrPng>, Comparable<ImLayer> {

    private final String name;
    private final ArrayList<ImFeatureOrPng> childNodes;

    private boolean visible = true; //for testHarness

    public ImLayer(int depth, String name, List<ImFeatureOrPng> childNodes) {
        super(depth);
        this.name = name;
        this.childNodes = (ArrayList<ImFeatureOrPng>) childNodes;

        for (ImFeatureOrPng node : childNodes) {
            node.initParent(this);
        }
    }

    public String getName() {
        return name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public List<ImFeatureOrPng> getChildNodes() {
        return childNodes;
    }

    @Override public boolean isLayer() {
        return true;
    }

    public boolean isFeatureless() {
        if (childNodes == null) return true;
        for (ImFeatureOrPng node : childNodes) {
            if (node.isFeature()) return false;
        }
        return true;
    }

    public ImPng getPng(PicksRO picks, int angle) {
        assert picks != null;
//        if(name.equals("03_FloorMats")){
//            System.out.println("QQQQQ");
//        }
        PngMatch bestMatch = new PngMatch();
        for (ImFeatureOrPng featureOrPng : childNodes) {
            featureOrPng.getMatchingPngs(bestMatch, picks, angle);
        }
        return bestMatch.getPng();
    }

    /**
     * @return true if this layer represents an accessory
     */
    public boolean isAccessory() {
        String n = getName().toLowerCase();
        if (n.contains("_acc_")) return true;
        if (n.contains("-acc-")) return true;
        if (n.contains("_acc-")) return true;
        if (n.contains("-acc_")) return true;
        if (n.contains("_zacc")) return true;
        if (n.contains("-zacc")) return true;
        return false;
    }

    public boolean isPartOfJpg() {
        String n = getName();
        return n.charAt(3) == 'z';
    }

    public boolean isBlinkPngRequired() {
        return isAccessory();
    }

    @Override public boolean containsAngle(int angle) {
        if (childNodes == null) return false;
        for (int i = 0; i < childNodes.size(); i++) {
            if (childNodes.get(i).containsAngle(angle)) return true;
        }
        return false;
    }

//    public ImLayer copy(int angle) {
//        if (!containsAngle(angle)) return null;
//        else return new ImLayer(this.depth, this.name, );
//    }

    public ImLayer copy(int angle) {
        ArrayList<ImFeatureOrPng> a;
        if (childNodes == null) {
            a = null;
        } else {
            a = new ArrayList<ImFeatureOrPng>();
            for (ImFeatureOrPng childNode : childNodes) {
                if (childNode.containsAngle(angle)) a.add(childNode.copy(angle));
            }
        }
        return new ImLayer(depth, name, a);
    }


    public void getVarSet(Set<Var> varSet) {
        for (int i = 0; i < childNodes.size(); i++) {
            ImFeatureOrPng featureOrPng = childNodes.get(i);
            featureOrPng.getVarSet(varSet);
        }
    }

    public void getPngs(Set<ImPng> pngs) {
        for (ImFeatureOrPng fp : childNodes) {
            fp.getPngs(pngs);
        }
    }

    public String getShortName() {
        String[] a = name.split("_");
        int L = a.length;
        return a[L - 1];
    }

    public void toggleVisibility() {
        visible = !visible;
    }

    @Override public int compareTo(ImLayer that) {
        return this.name.compareTo(that.name);
    }


}
