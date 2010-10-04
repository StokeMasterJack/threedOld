package com.tms.threed.imageModel.shared.imfm;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.boolExpr.And;
import com.tms.threed.featureModel.shared.boolExpr.Xor;
import com.tms.threed.imageModel.shared.ImPng;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.imageModel.shared.ImView;

import java.util.List;
import java.util.Set;

public class ImFm {

    private ImSeries imSeries;
    private FeatureModel fm;

    public ImFm(ImSeries imSeries, FeatureModel fm) {
        this.imSeries = imSeries;
        this.fm = fm;
    }

    public void addImFeatures1() {
        Var imVar = fm.addVar(imSeries.getVarCode());
        List<ImView> imViews = imSeries.getViews();
        Xor xor = fm.xor();
        for (ImView imView : imViews) {
            Var viewVar = imVar.addVar(imView.getVarCode());
            xor.add(viewVar);

            int angleCount = imView.getViewKey().getAngleCount();
            for (int a = 1; a <= angleCount; a++) {
                Var angleVar = viewVar.addVar(imView.getVarCode(a));
                fm.addConstraint(fm.imply(angleVar, viewVar));
            }
        }
        fm.addConstraint(xor);


        addImFeatures2(imVar);
    }

    private void addImFeatures2(Var imVar) {
        Set<ImPng> imPngs = imSeries.getPngs();
        for (ImPng png : imPngs) {
            Var pngVar = imVar.addVar(png.getVarCode());
            Set<Var> features = png.getFeatures();
            And and = fm.and();
            for (Var feature : features) {
                and.add(feature);
            }
            ImView imView = png.getView();
            String angleVarCode = imView.getVarCode(png.getAngle());
            Var angleVar = fm.getVar(angleVarCode);
            and.add(angleVar);

            fm.addConstraint(fm.imply(pngVar, and));
        }

    }

}
