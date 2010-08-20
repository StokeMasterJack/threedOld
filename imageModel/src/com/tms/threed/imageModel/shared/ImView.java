package com.tms.threed.imageModel.shared;

import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.picks.PicksRO;
import com.tms.threed.threedCore.shared.ViewKey;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImView extends ImChildBase implements IsParent<ImLayer> {

    private final String name;
    private final List<ImLayer> layers;

    transient private ViewKey viewKey;

    public ImView(int depth, String name, List<ImLayer> layers) {
        super(depth);
        this.name = name;
        Collections.sort(layers);
        this.layers = layers;

        for (ImLayer layer : layers) {
            layer.initParent(this);
        }
    }

    @Override public void initParent(IsParent parent) {
        super.initParent(parent);
        viewKey = initViewKey();
    }

    private ViewKey initViewKey() {
        return ((ImSeries) parent).getSeriesInfo().getViewKeyByName(name);
    }

    public ViewKey getViewKey() {
        return viewKey;
    }

    @Override public boolean isView() {
        return true;
    }

    @Override public boolean containsAngle(int angle) {
        if (layers == null) return false;
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).containsAngle(angle)) return true;
        }
        return false;
    }

    @Override public String getName() {
        return name;
    }

    public List<ImLayer> getLayers() {
        return layers;
    }

    public List<ImPng> getPngs(PicksRO picks, int angle) {
        assert picks != null;
        ArrayList<ImPng> a = new ArrayList<ImPng>();
        for (ImLayer layer : layers) {
            ImPng png = layer.getPng(picks, angle);
            if (png != null) {
                a.add(png);
            }
        }
        return a;
    }

    public boolean is(ViewKey viewKey) {
        return this.viewKey.equals(viewKey);
    }

    public Jpg getJpg(PicksRO picks, int angle) {
        assert picks != null;
        List<ImPng> pngs = getPngs(picks, angle);
        if (pngs == null) return null;
        assert pngs.size() != 0;
        return new Jpg(this, pngs, angle);
    }

//    public Jpg getJpg(Picks picks, int angle) {
//        final List<Png> pngs = getPngs(picks, angle);
//        if (pngs == null) return null;
//        int version = 1; //todo fix this: "version" shouldn't be hard-coded
//
//        assert pngs.size() != 0;
////        return new Jpg(this, pngs, angle);
//        return null;
//    }

    public int getLayerCount() {
        if (layers == null) return 0;
        return layers.size();
    }

    @Override public List<ImLayer> getChildNodes() {
        return layers;
    }

    @Nullable
    public ImPng getAccessoryPng(int angle, PicksRO picks, Var accessory) {
        assert picks != null;
        assert accessory != null;

        List<ImPng> pngs = getPngs(picks, angle);

        class FeaturePng {
            ImPng png;
            int featureIndex;

            FeaturePng(ImPng png, int featureIndex) {
                this.png = png;
                this.featureIndex = featureIndex;
            }
        }

        FeaturePng bestMatch = null;

        for (ImPng png : pngs) {
            int featureIndex = png.indexOf(accessory);
            if (featureIndex == -1) continue;

            if (bestMatch == null) {
                bestMatch = new FeaturePng(png, featureIndex);
            } else if (featureIndex < bestMatch.featureIndex) {
                bestMatch.featureIndex = featureIndex;
                bestMatch.png = png;
            }
        }

        if (bestMatch == null) {
            //accessory not visible for this angle
            return null;
        } else {
            return bestMatch.png;
        }

    }

    public ImView copy(int angle) {
        ArrayList<ImLayer> a;
        if (layers == null) {
            a = null;
        } else {
            a = new ArrayList<ImLayer>();
            for (ImLayer childNode : layers) {
                if (childNode.containsAngle(angle)) a.add(childNode.copy(angle));
            }
        }
        ImView copy = new ImView(depth, name, a);
        copy.parent = parent;
        return copy;
    }

    public Set<Var> getVarSet() {
        HashSet<Var> vars = new HashSet<Var>();
        getVarSet(vars);
        return vars;
    }

    public void getVarSet(Set<Var> varSet) {
        for (int i = 0; i < layers.size(); i++) {
            ImLayer imLayer = layers.get(i);
            imLayer.getVarSet(varSet);
        }
    }

    public Set<ImPng> getPngs() {
        HashSet<ImPng> set = new HashSet<ImPng>();
        getPngs(set);
        return set;
    }

    public void getPngs(Set<ImPng> pngs) {
        for (ImLayer imLayer : layers) {
            imLayer.getPngs(pngs);
        }
    }

    public String getVarCode() {
        return getName() + "View";
    }

    public String getVarCode(int angle) {
        return getVarCode() + "_" + angle;
    }

    public int getAngleCount() {
        return getViewKey().getAngleCount();
    }

    /**
     * For use in TestHarness
     */
    public void selectAll() {
        for (ImLayer layer : layers) {
            layer.setVisible(true);
        }
    }

    /**
     * For use in TestHarness
     */
    public void selectNone() {
        for (ImLayer layer : layers) {
            layer.setVisible(false);
        }
    }
}
