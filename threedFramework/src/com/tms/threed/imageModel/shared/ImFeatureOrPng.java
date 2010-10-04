package com.tms.threed.imageModel.shared;

import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.picks.PicksRO;

import java.util.Set;

public interface ImFeatureOrPng extends IsChild{

    void getMatchingPngs(PngMatch bestMatch, PicksRO picks, int angle);

    ImFeatureOrPng copy(int angle);

    void getVarSet(Set<Var> varSet);

    void getPngs(Set<ImPng> pngs);
}
