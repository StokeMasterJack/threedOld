package com.tms.threed.featureModel.shared.boolExpr;

import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.CardinalityGuesser;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.FieldDef;
import com.tms.threed.featureModel.shared.picks.IllegalPicksStateException;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.featureModel.shared.picks.PicksRO;
import com.tms.threed.featureModel.shared.Var;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.tms.threed.util.lang.shared.Strings.getSimpleName;

public abstract class BoolExpr {

    public static final CardinalityGuesser VarTemplates = new CardinalityGuesser();

    protected Set<String> classes;
    protected FeatureModel fm;

    protected BoolExpr() {}

    protected BoolExpr(FeatureModel fm) {
        this.fm = fm;
//        this.bddFactory = fm.getBddFactory();
    }

    public FeatureModel getFeatureModel() {
        return fm;
    }

    abstract public void accept(BoolExprVisitor visitor);

    abstract public boolean autoAssign(Picks picks,boolean value) throws IllegalPicksStateException;


//    public True getTrue() {
//        return fm.getTrue();
//    }
//
//    public False getFalse() {
//        return fm.getFalse();
//    }

    public static List<BoolExpr> upCastList(List<? extends BoolExpr> inList) {
        List<BoolExpr> outList = new ArrayList();
        for (BoolExpr expr : inList) {
            outList.add(expr);
        }
        return outList;
    }

    public Var asVar() {
        return null;
    }

    public boolean isVar() {
        return this instanceof Var;
    }

    public List<Var> isConjunctionOfVars() {
        return null;
    }

    @Nonnull
    public Bit eval(PicksRO picks) {
        throw new UnsupportedOperationException("eval is unsupported for [" + getClass().getName() + "]");
    }

    public FieldDef isFieldDef(String className) {
        return null;
    }

    public FieldDef isFieldDef() {
        return null;
    }

    public Set<String> getClasses() {
        return classes;
    }

    /**
     * Similar in concept to css classes
     */
    public void addClass(String className) {
        if (classes == null) classes = new HashSet();
        classes.add(className);
    }

    public boolean hasClass(String className) {
        if (classes == null) return false;
        return classes.contains(className);
    }

    /**
     * Compares to expressions ignoring the owning FeatureModel.
     * This allows us to compare boolExpr from 2 different fm's
     */
    public boolean equals(Object that) {
        throw new UnsupportedOperationException("BoolExpr.equals");
    }

    public BoolExpr simplify() {


        if (this instanceof Junction) {
            Junction a = (Junction) this;
            List<BoolExpr> exprList = a.getExprList();
            if (exprList.size() == 1) return exprList.get(0);
            else return this;
        } else {
            return this;
        }
    }

    public static boolean isJunction(String type) {
        return type.equals(TYPE_JUNCTION);
    }

    public static boolean isPair(String type) {
        return type.equals(TYPE_PAIR);
    }

    public static boolean isVar(String type) {
        return type.equals(TYPE_VAR);
    }

    public static boolean isNot(String type) {
        return type.equals(TYPE_NOT);
    }

    public static boolean isAnd(String subtype) {
        return subtype.equals(JUNCTION_SUBTYPE_AND);
    }

    public static boolean isOr(String subtype) {
        return subtype.equals(JUNCTION_SUBTYPE_OR);
    }

    public static boolean isXor(String subtype) {
        return subtype.equals(JUNCTION_SUBTYPE_XOR);
    }

    public static boolean isImplication(String subtype) {
        return subtype.equals(PAIR_SUBTYPE_IMPLICATION);
    }

    public static boolean isIff(String subtype) {
        return subtype.equals(PAIR_SUBTYPE_IFF);
    }

    public static boolean isConflict(String subtype) {
        return subtype.equals(PAIR_SUBTYPE_CONFLICT);
    }

    public static boolean isNand(String subtype) {
        return subtype.equals(PAIR_SUBTYPE_NAND);
    }

    public Set<Var> getVars() {
        GetVarsVisitor getVars = new GetVarsVisitor();
        accept(getVars);
        return getVars.getVars();
    }

    public Set<Var> getLeafVars() {
        GetLeafVarsVisitor visitor = new GetLeafVarsVisitor();
        accept(visitor);
        return visitor.getLeafVars();
    }

    public boolean referencesAnyNonLeafVars() {
        CheckForNonLeafReferenceVisitor visitor = new CheckForNonLeafReferenceVisitor();
        accept(visitor);
        return visitor.getResponse();
    }

    public boolean dependsOn(Var var) {
        DependsOnVisitor visitor = new DependsOnVisitor(var);
        accept(visitor);
        return visitor.getResponse();
    }

    private static final String TYPE_JUNCTION = getSimpleName(Junction.class);
    private static final String TYPE_PAIR = getSimpleName(Pair.class);
    private static final String TYPE_VAR = getSimpleName(Var.class);
    private static final String TYPE_NOT = getSimpleName(Not.class);

    private static final String JUNCTION_SUBTYPE_AND = getSimpleName(And.class);
    private static final String JUNCTION_SUBTYPE_OR = getSimpleName(Or.class);
    private static final String JUNCTION_SUBTYPE_XOR = getSimpleName(Xor.class);

    private static final String PAIR_SUBTYPE_IMPLICATION = getSimpleName(Implication.class);
    private static final String PAIR_SUBTYPE_IFF = getSimpleName(Iff.class);
    private static final String PAIR_SUBTYPE_CONFLICT = getSimpleName(Conflict.class);
    private static final String PAIR_SUBTYPE_NAND = getSimpleName(Nand.class);


}
