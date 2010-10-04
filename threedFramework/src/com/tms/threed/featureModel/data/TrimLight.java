package com.tms.threed.featureModel.data;

import com.tms.threed.featureModel.shared.Cardinality;
import com.tms.threed.featureModel.shared.CommonVarNames;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.threedCore.shared.SeriesKey;

public class TrimLight extends FeatureModel implements CommonVarNames {

    public Var camry = addVar("camry");

    public Var trim = camry.addVar(Trim);

    public Var grade = trim.addVar(Grade);
    public Var base = grade.addVar(Base);     //done
    public Var le = grade.addVar("LE");         //done
//    public Var se = grade.addVar("SE");
//    public Var xle = grade.addVar("XLE");
//    public Var hybrid = grade.addVar("Hyb");

    public Var engine = trim.addVar(Engine);
    public Var l4 = engine.addVar("L4");
    public Var v6 = engine.addVar("V6");
//    public Var hybridEngine = engine.addVar("Hybrid");

//    public Var transmission = trim.addVar(Transmission);
//    public Var mt6 = transmission.addVar("6MT");
    //    public Var at6 = transmission.addVar("6AT");
//    public Var ecvt = transmission.addVar("ECVT");
//
    public Var modelCode = trim.addVar(ModelCode);
    //
    public Var t2513 = modelCode.addVar("2513");
    public Var t2514 = modelCode.addVar("2514");
    //
    public Var t2531 = modelCode.addVar("2531");
    public Var t2532 = modelCode.addVar("2532");
//    public Var t2552 = modelCode.addVar("2552");
//
//    public Var t2540 = modelCode.addVar("2540");
//    public Var t2554 = modelCode.addVar("2554");
//
//    public Var t2545 = modelCode.addVar("2545");
//    public Var t2546 = modelCode.addVar("2546");
//    public Var t2550 = modelCode.addVar("2550");
//
//    public Var t2560 = modelCode.addVar("2560");

    public TrimLight() {
        super(SeriesKey.CAMRY_2011.v0(),"Camry");

        grade.setCardinality(Cardinality.PickOneGroup);
        engine.setCardinality(Cardinality.PickOneGroup);
//        transmission.setCardinality(Cardinality.PickOneGroup);
        modelCode.setCardinality(Cardinality.PickOneGroup);

        trim.setCardinality(Cardinality.AllGroup);
        trim.setManditory(true);

//        addExtraConstraint(iff(t2513, and(base, l4, mt6)));
//        addExtraConstraint(iff(t2513, and(base, l4, mt6)));
        addConstraint(iff(t2513, and(base, l4)));
        addConstraint(iff(t2514, and(base, v6)));
//
        addConstraint(iff(t2531, and(le, l4)));
        addConstraint(iff(t2532, and(le, v6)));
//        addExtraConstraint(iff(t2552, and(le, v6, at6)));
//
//        addExtraConstraint(iff(t2540, and(xle, l4, at6)));
//        addExtraConstraint(iff(t2554, and(xle, v6, at6)));
//
//        addExtraConstraint(iff(t2545, and(se, l4, mt6)));
//        addExtraConstraint(iff(t2546, and(se, l4, at6)));
//        addExtraConstraint(iff(t2550, and(se, v6, at6)));
//
//        addExtraConstraint(iff(t2560, and(hybrid, hybridEngine, ecvt)));


    }


    public void printTree() {
        getRootVar().print();
    }
}