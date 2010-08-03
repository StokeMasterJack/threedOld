package com.tms.threed.featureModel.shared;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.boolExpr.And;
import com.tms.threed.featureModel.shared.boolExpr.BoolExpr;
import com.tms.threed.featureModel.shared.boolExpr.Or;
import com.tms.threed.util.lang.shared.Strings;

import static com.tms.threed.util.lang.shared.Strings.containsWhitespace;
import static com.tms.threed.util.lang.shared.Strings.isEmpty;

/**
 *
 * For imply or bi-imply, replace spaces with +
 * For conflict, replace spaces with .
 *
 *
 *
 * X+Y+Z
 * X*Y*Z
 *
 *
 * implies="FF GG HH+XX
 *
 *
 */
public class ExprParser {

    private FeatureModel fm;

    public ExprParser(FeatureModel fm) {
        this.fm = fm;
    }


    public BoolExpr parseImplication(String sExpr) {
        sExpr = preProcess(sExpr);
        sExpr = sExpr.replaceAll(" ", "*");
        return parseOrs(sExpr);
    }

    public BoolExpr parseConflict(String sExpr) {
        sExpr = preProcess(sExpr);
        sExpr = sExpr.replaceAll(" ", "+");
        return parseOrs(sExpr);
    }

    String preProcess(String sExpr) {
        sExpr = sExpr.replaceAll("\\s+", " ");

        sExpr = sExpr.replace(" * ", "*");
        sExpr = sExpr.replace("* ", "*");
        sExpr = sExpr.replace(" *", "*");

        sExpr = sExpr.replace(" + ", "+");
        sExpr = sExpr.replace("+ ", "+");
        sExpr = sExpr.replace(" +", "+");

        return sExpr;
    }



    /**
     *
     * @param sExpr1 may contain and of the following:
     *      letters
     *      digits
     *      +
     *      .
     *      !
     * @return
     */
    BoolExpr parseOrs(String sExpr1) {
        if (isEmpty(sExpr1)) throw new IllegalArgumentException();
        if (containsWhitespace(sExpr1)) throw new IllegalArgumentException();

        for (int i = 0; i < sExpr1.length(); i++) {
            char c = sExpr1.charAt(i);
            boolean valid = Character.isLetterOrDigit(c) || c == '+' || c == '*' || c == '!';
            if (!valid) {
                throw new IllegalArgumentException("[" + sExpr1 + "] contains an invalid character: [" + c + "]");
            }
        }

        if (!sExpr1.contains("+")) {
            return parseAnds(sExpr1);
        } else {
            Or or = fm.or();
            for (String expr : sExpr1.split("\\+")) {
                or.add(parseAnds(expr));
            }
            return or;
        }

    }

    /**
     *
     * @param sExpr2 may contain and of the following:
     *      letters
     *      digits
     *      .
     *      !
     * @return
     */
    BoolExpr parseAnds(String sExpr2) {
        if (isEmpty(sExpr2)) throw new IllegalArgumentException();
        if (containsWhitespace(sExpr2)) throw new IllegalArgumentException();

        for (int i = 0; i < sExpr2.length(); i++) {
            char c = sExpr2.charAt(i);
            boolean valid = Character.isLetterOrDigit(c) || c == '*' || c == '!';
            if (!valid) {
                throw new IllegalArgumentException("[" + sExpr2 + "] contains an invalid character: [" + c + "]");
            }
        }

        if (!sExpr2.contains("*")) {
            return parse3(sExpr2);
        } else {
            And and = fm.and();
            for (String expr : sExpr2.split("\\*")) {
                and.add(parse3(expr));
            }
            return and;
        }

    }

    /**
     *
     * @param sExpr3  may contain a varCode or a varCode prefixed by a bang (!)
     * @return
     */
    BoolExpr parse3(String sExpr3) {
        if (isEmpty(sExpr3)) throw new IllegalArgumentException();
        if (containsWhitespace(sExpr3)) throw new IllegalArgumentException();

        if (sExpr3.startsWith("!")) {
            if (sExpr3.length() < 2) throw new IllegalArgumentException();
            return fm.not(parse4(sExpr3.substring(1)));
        } else {
            return parse4(sExpr3);
        }
    }

    BoolExpr parse4(String varCode) {
        if (isEmpty(varCode)) throw new IllegalArgumentException();
        if (containsWhitespace(varCode)) throw new IllegalArgumentException();
        if (Strings.containsNonwordChar(varCode)) throw new IllegalArgumentException();
        return fm.getVar(varCode);
    }


}
