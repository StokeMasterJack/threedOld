package com.tms.threed.imageModel.shared.imfm;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.boolExpr.And;
import com.tms.threed.imageModel.shared.ImPng;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.imageModel.shared.ImView;

import java.util.Set;

public class ImFm2 {

    private ImSeries imSeries;
    private FeatureModel fm;

//    private ImView imView;
//    private int angle;

    public ImFm2(ImSeries imSeries) {
        this.imSeries = imSeries;
        this.fm = new FeatureModel();

        ImView imView = imSeries.getExteriorView().copy(1);

        System.out.println("imView.getVarSet().size() = [" + imView.getVarSet().size() + "]");

        Set<Var> varSet = imView.getVarSet();
        for (Var var : varSet) {
            fm.addVar(var.getCode());
        }


        Set<ImPng> imPngs = imView.getPngs();
        System.out.println("imPngs.size() = [" + imPngs.size() + "]");
        for (ImPng png : imPngs) {
            Var pngVar = fm.addVar(png.getVarCode());
            Set<Var> features = png.getFeatures();

            if (features.size() == 0) {
                //fm.addConstraint(pngVar);
            } else {
                And and = fm.and();
                for (Var f : features) {
                    Var var = fm.getVar(f.getCode());
                    and.add(var);
                }

                fm.addConstraint(fm.imply(pngVar, and));

            }
        }

    }

    public FeatureModel getFm() {
        return fm;
    }
}