package com.tms.threed.imageModel.shared;

import com.tms.threed.featureModel.shared.ModelTypeFm;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.picks.PicksRO;
import com.tms.threed.threedCore.shared.Angle;
import com.tms.threed.threedCore.shared.ModelType;
import com.tms.threed.threedCore.shared.SeriesId;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesInfoBuilder;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.SeriesModel;
import com.tms.threed.threedCore.shared.ViewKey;
import com.tms.threed.threedCore.shared.ViewSnap;
import com.tms.threed.util.gwt.client.Console;
import com.tms.threed.util.lang.shared.Path;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImSeries extends ImNodeBase implements IsParent<ImView>, IsRoot , SeriesModel{

    private final SeriesId seriesId;
    private final SeriesInfo seriesInfo;
    private final List<ImView> imViews;

    public ImSeries(int depth, SeriesId seriesId, List<ImView> imViews) {
        super(depth);
        this.seriesId = seriesId;
        this.seriesInfo = SeriesInfoBuilder.createSeriesInfo(seriesId.getSeriesKey());
        this.imViews = imViews;

        for (ImView view : imViews) {
            view.initParent(this);
        }
    }

    @Override public String getName() {
        return seriesInfo.getSeriesName();
    }

    public SeriesInfo getSeriesInfo() {
        return seriesInfo;
    }

    private static final ModelType modelType = new ModelTypeIm();

    @Override public ModelType getModelType() {
        return modelType;
    }

    public SeriesId getSeriesId() {
        return seriesId;
    }

    public SeriesKey getSeriesKey() {
        return getSeriesId().getSeriesKey();
    }

    public ImView getView(ViewKey viewKey) {
        for (ImView view : imViews) {
            if (view.is(viewKey)) return view;
        }
        throw new IllegalArgumentException("No such view[" + viewKey + "]");
    }

    public List<ImView> getViews() {
        return imViews;
    }

    @Override public boolean isRoot() {
        return true;
    }

    @Override public boolean isSeries() {
        return true;
    }

    public List<ImPng> getPngs(ViewKey viewKey, int angle, PicksRO picks) {
        assert picks != null : "Picks is required";
        ImView view = getView(viewKey);
        return view.getPngs(picks, angle);
    }

    public Jpg getJpg(ViewKey viewKey, int angle, PicksRO picks) {
        assert picks != null : "Picks is required";
        ImView view = getView(viewKey);
        return view.getJpg(picks, angle);
    }

    public Jpg getJpg(String viewName, int angle, PicksRO picks) {
        assert picks != null : "Picks is required";
        ViewKey viewKey = seriesInfo.getViewKeyByName(viewName);
        return getJpg(viewKey, angle, picks);
    }

    public ImView getExteriorView() {
        ViewKey viewKey = seriesInfo.getExterior();
        return getView(viewKey);
    }

    public ImView getInteriorView() {
        ViewKey viewKey = seriesInfo.getInterior();
        return getView(viewKey);
    }

    @Override public Path getLocalPath() {
        String y = seriesInfo.getYear() + "";
        String n = seriesInfo.getSeriesName();
        Path localPath = new Path(y, n);
        return localPath;
    }

    @Override public List<ImView> getChildNodes() {
        return imViews;
    }

    @Override public boolean containsAngle(int angle) {
        List<ImView> views = getViews();
        if (views == null) return false;
        for (int i = 0; i < views.size(); i++) {
            if (views.get(i).containsAngle(angle)) return true;
        }
        return false;
    }

    public Set<Var> getVarSet() {
        HashSet<Var> vars = new HashSet<Var>();
        getVarSet(vars);
        return vars;
    }

    public void getVarSet(Set<Var> varSet) {
        for (int i = 0; i < imViews.size(); i++) {
            ImView imView = imViews.get(i);
            imView.getVarSet(varSet);
        }
    }

    public Set<ImPng> getPngs() {
        HashSet<ImPng> pngs = new HashSet<ImPng>();
        for (ImView imView : imViews) {
            imView.getPngs(pngs);
        }
        return pngs;
    }

    public String getVarCode() {
        return "imageModel";
    }

    public void printFeaturesPerAngleReport() {
        String seriesName = getName();
        System.out.println(seriesName);
        for (ImView imView : imViews) {
            System.out.println("\t" + imView.getName());
            int angleCount = imView.getAngleCount();
            for (int angle = 1; angle <= angleCount; angle++) {
                ImView aView = imView.copy(angle);
                Set<Var> aVarSet = aView.getVarSet();
                int aVarCount = aVarSet.size();
                String a = Angle.getAnglePadded(angle);
                System.out.println("\t\t a" + a + "(" + aVarCount + "): " + aVarSet);
            }
        }
    }

    public ViewSnap getInitialViewState() {
        return getSeriesInfo().getInitialViewState();
    }

    /**
     * For version 0: /2011/avalon
     * For version 1: /2011/avalon/versions/1
     * For version 2: /2011/avalon/versions/2
     * For version 3: /2011/avalon/versions/3
     */
    public Path getPath() {
        int v = getVersion();
        if (v == 0) {
            return super.getPath();
        } else {
            return super.getPath().append("versions/" + v);
        }
    }

    public int getVersion() {
        return getSeriesId().getVersion();
    }


}
