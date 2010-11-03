package com.tms.threed.threedModel.shared;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.featureModel.shared.picks.PicksRO;
import com.tms.threed.imageModel.shared.ImPng;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.imageModel.shared.ImView;
import com.tms.threed.imageModel.shared.Jpg;
import com.tms.threed.threedCore.shared.SeriesId;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ViewKey;
import com.tms.threed.threedCore.shared.ViewSnap;
import com.tms.threed.util.lang.shared.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

import static com.tms.threed.util.lang.shared.Strings.isEmpty;

public class ThreedModel  {

    private final SeriesId seriesId;
    private final FeatureModel featureModel;
    private final ImSeries imageModel;
    private final Path httpImageRoot;
    private final Path httpJpgRoot;
    private final Path httpPngRoot;

    public ThreedModel(@Nonnull FeatureModel featureModel, @Nonnull ImSeries imageModel, @Nonnull Path httpImageRoot) {
        assert featureModel != null;
        assert imageModel != null;
        assert httpImageRoot != null;
        assert !isEmpty(httpImageRoot.toString());

        this.featureModel = featureModel;
        this.imageModel = imageModel;
        this.httpImageRoot = httpImageRoot;

        this.seriesId = featureModel.getSeriesId();
        this.httpJpgRoot = httpImageRoot.append("jpgs");
        this.httpPngRoot = httpImageRoot.append("pngs");

    }

    public SeriesInfo getSeriesInfo() {
        return imageModel.getSeriesInfo();
    }

    public SeriesId getSeriesId() {
        return seriesId;
    }

    public FeatureModel getFeatureModel() {
        return featureModel;
    }

    public ImSeries getImageModel() {
        return imageModel;
    }

    public Picks fixupPicks(Set<String> picksRaw) {
        return featureModel.fixupPicks(picksRaw);
    }

//    public Path getJpg(ViewKey viewKey, Angle angle, Set<String> rawPicks) {
//        Picks picks = fixupPicks(rawPicks);
//        return imageModel.getJpg(viewKey, angle, picks);
//    }
//
//    public Path getJpg(ViewKey viewKey, Angle angle, VarPicksSnapshot picksRaw) {
//        Picks picks = fixupPicks(picksRaw.getFeatureSet());
//        return imageModel.getJpg(viewKey, angle, picks);
//    }

    public Jpg getJpg(String viewName, int angleValue, PicksRO picks) {
        assert picks != null : "Picks is required";
        return imageModel.getJpg(viewName, angleValue, picks);
    }

    public Jpg getJpg(ViewSnap viewState, PicksRO picks) {
        assert picks != null : "Picks is required";
        assert viewState != null;
        return imageModel.getJpg(viewState.getView(), viewState.getAngle(), picks);
    }

    public Path getJpgUrl(ViewSnap viewState, PicksRO picks) {
        assert picks != null : "Picks is required";
        assert viewState != null;
        return this.getJpg(viewState, picks).getPath(httpJpgRoot);
    }

    @Nullable
    public Path getBlinkPngUrl(ViewSnap viewState, PicksRO picks, Var blinkFeature) {
        assert picks != null : "Picks is required";
        assert viewState != null;
        assert blinkFeature != null;

        ImView view = getView(viewState.getView());
        ImPng png = view.getAccessoryPng(viewState.getAngle(), picks, blinkFeature);
        if (png == null) return null;

        return png.getBlinkPath(httpPngRoot);

    }


    public Jpg getJpg(ViewKey viewKey, int angle, PicksRO picks) {
        assert picks != null;
        return imageModel.getJpg(viewKey, angle, picks);
    }

    public Path getJpgUrl(ViewKey viewKey, int angle, PicksRO picks) {
        assert picks != null;
        Jpg jpg = getJpg(viewKey, angle, picks);
        return jpg.getPath(httpJpgRoot);
    }

    public ImView getView(ViewKey viewKey) {
        return imageModel.getView(viewKey);
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj.getClass() != ThreedModel.class) return false;
        ThreedModel that = (ThreedModel) obj;
        return getSeriesId().equals(that.getSeriesId());
    }

    public ViewSnap getInitialViewState() {
        return getImageModel().getInitialViewState();
    }

    public SeriesKey getSeriesKey() {
        return getSeriesId().getSeriesKey();
    }

    public Path getHttpImageRoot() {
        assert httpImageRoot != null;
        assert !isEmpty(httpImageRoot.toString());
        return httpImageRoot;
    }

    public Path getHttpJpgRoot() {
        return httpJpgRoot;
    }

    public Path getHttpPngRoot() {
        return httpPngRoot;
    }

}
