package com.tms.threed.featureModel.shared.exprParser;

import com.tms.threed.featureModel.data.Camry2011;
import com.tms.threed.featureModel.shared.ExprParser;
import junit.framework.TestCase;

/**
 * public Var iAcc = acc.addVar(InteriorAccessories, "Interior Accessories");
 public Var a28 = iAcc.addVar("28", "Shift Knob");
 public Var cf = iAcc.addVar("CF", "Floormats");
 public Var a2q = iAcc.addVar("2Q", "All Weather Mats");
 */
public class ExprParserTest extends TestCase {

    public void test() throws Exception {
        Camry2011 fm = new Camry2011();

        ExprParser p = new ExprParser(fm);

        System.out.println(p.parseImplication("28 CF+2Q"));
        System.out.println(p.parseImplication("28*CF+2Q"));
        System.out.println(p.parseImplication("28 CF 2Q"));
        System.out.println(p.parseImplication("28 !CF 2Q"));
        System.out.println(p.parseImplication("28 !CF+2Q"));
        System.out.println(p.parseImplication("28 !CF+2Q E5"));
        System.out.println(p.parseImplication("28 !CF + 2Q  E5 "));
        System.out.println(p.parseConflict("28 !CF * 2Q  E5 "));
    }
}
