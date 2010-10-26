package com.tms.threed.featureModel.shared;

import com.tms.threed.featureModel.shared.boolExpr.*;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.threedCore.shared.ModelType;
import com.tms.threed.threedCore.shared.SeriesId;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.SeriesModel;
import com.tms.threed.util.lang.shared.Path;
import com.tms.threed.util.lang.shared.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.tms.threed.util.lang.shared.Strings.isEmpty;

public class FeatureModel implements SeriesModel {

    private static final String ROOT_VAR_CODE = "Root";

    private final SeriesId seriesId;
    private final String displayName;
    private final Var rootVar;

    private final VarCollection vars = new VarCollection(); //0th element is always the root-var

    //extraConstraints should never reference (directly or indirectly) non-leaf vars
    private final And extraConstraints = new And(this);



//    public final True TRUE = new True(this);
//    public final False FALSE = new False(this);

    public FeatureModel(SeriesId seriesId,String displayName) {
        this.seriesId = seriesId;
        this.displayName = displayName;
        rootVar = new Var(this, null, ROOT_VAR_CODE, ROOT_VAR_CODE);
        vars.add(rootVar);
    }

    private static final ModelType modelType = new ModelTypeFm();

    @Override public ModelType getModelType() {
        return modelType;
    }

    public SeriesId getSeriesId() {
        return seriesId;
    }

    public SeriesKey getSeriesKey() {
        return seriesId.getSeriesKey();
    }

    public Var addVar(String code, String name) {
        return rootVar.addVar(code, name);
    }

    public Var addVar(String code) {
        return rootVar.addVar(code);
    }

    public And getCardinalityConstraint() {
        return getCardinalityConstraint(false);
    }

    private BoolExpr masterConstraint;

    public BoolExpr getMasterConstraint() {
        if (masterConstraint == null) {
            masterConstraint = and(getRootConstraint(), getTreeConstraint(), getExtraConstraint(), getCardinalityConstraint(true));
        }
        return masterConstraint;
    }

    public And getCardinalityConstraint(boolean leafOnly) {
        And cardConstraints = and();
        for (Var var : vars) {

            if (var.isManditory() || var.isAllGroupChild()) {
                if (!leafOnly) {
                    cardConstraints.add(imply(var.getParent(), var));
                } else if (var.isLeaf()) {
                    cardConstraints.add(var);
                }
            }

            if (var.isPickOneGroup()) {
                if (!leafOnly) {
                    cardConstraints.add(imply(var.getParent(), var.getChildNodesAsXor()));
                } else {
                    boolean b = var.hasAnyNonLeafChildNodes();
                    if (!b) {
                        cardConstraints.add(var.getChildNodesAsXor());
                    } else {
                        System.out.println("Not adding isPickOneGroup's xor children: " + var);
                    }
                }
            }

        }
        return cardConstraints;
    }


    public List<Var> getLeafVars() {
        return vars.getLeafVars();
    }

    public List<Var> getLeafVars2() {
        return getRootVar().getSelfPlusDescendants(true);
    }

    private boolean sorted;

    public void sortChildVarsByLeafRelationCount() {
        if (!sorted) {
            initRelationCount();
            getRootVar().sortDescendantVarsByLeafRelationCount();
            sorted = true;
        }
    }

    private void initRelationCount() {getRootVar().initLeafRelationCount();}

    public Var getVar(int index) {
        return vars.get(index);
    }

    public Var getVarOrNull(String code) {
        return vars.get(code);
    }

    public Var getVar(String code) throws UnknownVarCodeException {
        Var var = vars.get(code);

        if (var == null) {
            throw new UnknownVarCodeException(code, seriesId);
        }
        return var;
    }

    public boolean containsCode(String code) {
        return vars.containsCode(code);
    }

    public void printSolution(boolean[] solution) {
        System.out.println(solution.length);
        int varCount = vars.size();

        for (int i = 0; i < varCount; i++) {
            if (solution[i]) {
                Var var = (Var) vars.get(i);
                System.out.print(var.getCode());
                System.out.print(',');
            }
        }
        System.out.println();
    }


    public void clearExtraConstraints() {
        extraConstraints.clear();
    }

    public int getVarCount() {
        return vars.size();
    }

//    public Set<String> getFeatureCodes() {
//        return vars.toCodeSet();
//    }

    public Var[] getFieldNames() {
        return this.vars.toVarArray();
    }

    public List<Var> getVars() {
        return vars.getVarList();
    }

    VarCollection getVarCollection() {
        return vars;
    }

    public Set<Var> getVars(Collection<String> varCodes) {
        Set<Var> set = new HashSet<Var>();
        for (String code : varCodes) {
            Var var = getVarOrNull(code);
            if (var == null) {
                System.out.println("[" + code + "] not in FeatureModel");
            } else {
                set.add(var);
            }
        }
        return set;
    }

//    public List<String> toCodeList() {
//        return vars.toCodeList();
//    }

    public And getVarsAsConjunction() {
        return and(upCastList(getVars()));
    }

    public BoolExpr getRootConstraint() {
        return getRootVar();
    }

    public And getTreeConstraint() {
        And a = this.and();
        List<Var> varList = vars.getVarList();
        for (Var var : varList) {
            if (var.isRoot()) continue;
            Implication tcImp = this.imply(var, var.getParent());
            a.add(tcImp);
        }
        return a;
    }

    public And getExtraConstraint() {
        return extraConstraints;
    }

    public And getAllConstraints() {
        return and(getRootConstraint(), getTreeConstraint(), getCardinalityConstraint(), getExtraConstraint());
    }

    public int getExtraConstraintCount() {
        return extraConstraints.getExpressionCount();
    }

    public int getRootConstraintCount() {
        return 1;
    }

    public int getTreeConstraintCount() {
        return getTreeConstraint().getExpressionCount();
    }

    public int getCardinalityConstraintCount() {
        return getCardinalityConstraint().getExpressionCount();
    }

    public int getAllConstraintCount() {
        return getRootConstraintCount() + getTreeConstraintCount() + getCardinalityConstraintCount() + getExtraConstraintCount();
    }

    public Var getRootVar() {
        return rootVar;
    }

    /**
     * Add extra-constraint
     *
     * @param expr  must not reference (directly or indirectly) any non-leaf vars
     */
    public void addConstraint(BoolExpr expr) {
        extraConstraints.add(expr);
    }

    public void printLeafVarRelations() {
        long t1 = System.currentTimeMillis();


        long t2 = System.currentTimeMillis();
        long delta = t2 - t1;
        System.out.println("getLeafVarRelations Delta: " + delta);
    }


    public boolean assertExtraConstraintsReferenceOnlyLeafVars() {

        long t1 = System.currentTimeMillis();

        boolean retVal = extraConstraints.referencesAnyNonLeafVars();

        long t2 = System.currentTimeMillis();
        long delta = t2 - t1;
        System.out.println("assertExtraConstraintsReferenceOnlyLeafVars Delta: " + delta);

        return !retVal;
    }

    public And and(List<BoolExpr> expressions) {
        return new And(this, expressions);
    }

    public And and(BoolExpr... expressions) {
        return new And(this, Arrays.asList(expressions));
    }

    public And and() {
        return new And(this);
    }

    public Or or(List<BoolExpr> exprList) {
        return new Or(this, exprList);
    }

    public Or or(BoolExpr... expressions) {
        return new Or(this, Arrays.asList(expressions));
    }

    public Or or() {
        return new Or(this);
    }

    public Not not(BoolExpr expr) {
        return new Not(this, expr);
    }


    public Xor xor(String name, List<BoolExpr> expressions) {
        return new Xor(this, name, expressions);
    }

    public Xor xor(String name, BoolExpr... expressions) {
        return new Xor(this, name, Arrays.asList(expressions));
    }

    public Xor xor(String name) {
        return new Xor(this, name);
    }

    public Xor xor(List<BoolExpr> expressions) {
        return new Xor(this, expressions);
    }

    public Xor xor(BoolExpr... expressions) {
        return new Xor(this, Arrays.asList(expressions));
    }

    public Xor xor() {
        return new Xor(this);
    }

    public Conflict conflict(BoolExpr e1, BoolExpr e2) {
        assert e1 != null;
        assert e2 != null;
        return new Conflict(this, e1, e2);
    }

    public Iff iff(BoolExpr e1, BoolExpr e2) {
        assert e1 != null;
        assert e2 != null;
        return new Iff(this, e1, e2);
    }

    public Nand nand(BoolExpr e1, BoolExpr e2) {
        assert e1 != null;
        assert e2 != null;
        return new Nand(this, e1, e2);
    }

    public Implication imply(BoolExpr e1, BoolExpr e2) {
        assert e1 != null;
        assert e2 != null;
        return new Implication(this, e1, e2);
    }


//    public True getTrue() {
//        return TRUE;
//    }
//
//    public False getFalse() {
//        return FALSE;
//    }

    public void printSummary() {
        System.out.println("Var Count: " + getVarCount());
        System.out.println("Constraint Count: " + getTreeConstraintCount() + "/" + getExtraConstraintCount());
    }

    public void printDetails() {
        System.out.println("Var Count: " + getVarCount());
        System.out.println("Tree Constraint Count: " + getTreeConstraintCount());
        System.out.println("Extra Constraint Count: " + getExtraConstraintCount());

        System.out.println("Features:");
        for (Var var : vars.getVarList()) {
            System.out.println("\t" + var.getCode() + ": " + var.getName());
        }

        System.out.println("Constraints:");
        for (BoolExpr expr : extraConstraints.getExprList()) {
            System.out.println("\t" + expr);
        }
    }

    public <T extends Pair> T createPair(Class<T> pairImplClass, BoolExpr expr1, BoolExpr expr2) {
        //Note: Intentionally avoided reflection here for the benefit of GWT compatibility
        if (pairImplClass == Implication.class) return (T) new Implication(this, expr1, expr2);
        if (pairImplClass == Conflict.class) return (T) new Conflict(this, expr1, expr2);
        if (pairImplClass == Iff.class) return (T) new Iff(this, expr1, expr2);
        if (pairImplClass == Nand.class) return (T) new Nand(this, expr1, expr2);
        throw new IllegalArgumentException();
    }

    public boolean isPickValid(Var var) {
        return isAssignmentValid(var, Bit.TRUE);
    }

    public boolean isAssignmentValid(Var var, Bit value) {
//        BDD testBdd = restrict(f, var, value);
//        return !testBdd.isZero();
        return true; //todo
    }

    public Set<String> varsToCodes(Set<Var> picks) {
        HashSet<String> set = new HashSet<String>();
        for (Var var : picks) {
            set.add(var.getCode());
        }
        return set;
    }

    public boolean isLeaf(String varCode) {
        final Var var = getVarOrNull(varCode);
        return var.isLeaf();
    }

    public void printRootConstraint() {
        System.out.println("RootConstraint: ");
        System.out.println("\t" + getRootConstraint());
    }

    public void printAllConstraints() {
        printRootConstraint();
        printTreeConstraints();
        printExtraConstraints();
    }

    public void printExtraConstraints() {
        System.out.println("ExtraConstraints: ");
        List<BoolExpr> list = getExtraConstraint().getExprList();
        for (BoolExpr boolExpr : list) {
            System.out.println("\t" + boolExpr);
        }
    }

    public void printTreeConstraints() {
        System.out.println("TreeConstraints: ");
        List<BoolExpr> list = getTreeConstraint().getExprList();
        for (BoolExpr boolExpr : list) {
            System.out.println("\t" + boolExpr);
        }
    }

//    public Map<Integer, String> getVarNames(boolean oneBased) {
//        return vars.getVarNames(oneBased);
//    }


    public List<Var> getPickOneGroups() {
        List<Var> pickOneGroups = new ArrayList<Var>();
        for (Var var : getVars()) {
            if (var.isPickOneGroup()) {
                pickOneGroups.add(var);
            }
        }
        return pickOneGroups;
    }

    public static Path createFeaturePath(List<Var> featureList) {
        if (featureList == null || featureList.size() == 0) return new Path();
        Path p = new Path();
        for (Var var : featureList) {
            p = p.append(var.getCode());
        }
        return p;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getYear() {
        return seriesId.getYear();
    }

    public String getYourMessage() {
        return "your " + getYear() + " " + getDisplayName();
    }

    public static interface VarFilter {
        boolean accept(Var var);
    }

    public Set<String> toFeatureSet(boolean[] product, VarFilter filter) {
        HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < product.length; i++) {
            Var var = vars.get(i);
            if (product[i] && filter.accept(var)) {
                set.add(var.getCode());
            }
        }
        return set;
    }

    public Collection<Var> getVarsByClass(final String className) {
        return vars.getByClass(className);
    }

    public static List<BoolExpr> upCastList(List<? extends BoolExpr> inList) {
        return BoolExpr.upCastList(inList);
    }

    public Picks fixupPicks(Set<String> picksRaw) {
        Picks picks = this.createPicks();
        picks.pick(picksRaw);
        picks.fixup();
        return picks;
    }

    public Set<String> fixupPicks1(Set<String> picksRaw) {
        Picks picks = this.createPicks();
        picks.pick(picksRaw);
        picks.fixup();
        return fixupPicks(picksRaw).getAllPicks2();
    }

    public Picks createPicks() {
        return new Picks(this);
    }

    public Picks createPicks(Set<String> picks) {
        Picks p = new Picks(this);
        p.pick(picks);
        return p;
    }

    public long satCount(Collection<String> careSet) {
        throw new UnsupportedOperationException("Need to put back");
    }


    public String getSeriesName() {
        return rootVar.getCode();
    }

    private boolean isBaseGrade(String featureCode) {
        if (featureCode.equalsIgnoreCase(getSeriesName())) return true;
        if (featureCode.equalsIgnoreCase("base")) return true;
        return false;
    }

    /**
     * Not needed for NonFlash Config
     * Maybe needed for eBro
     */
    public void preFixup(Set<String> userPicks) {

        HashSet<String> toBeAdded = new HashSet<String>();
        Iterator<String> it = userPicks.iterator();

        while (it.hasNext()) {
            String featureCode = it.next();

            if (featureCode == null) it.remove();

            int L = featureCode.length();
            featureCode = featureCode.trim();
            if (featureCode.equals("")) it.remove();

            if (featureCode.length() != L) {
                it.remove();
                toBeAdded.add(featureCode);
            }

            if (isBaseGrade(featureCode)) {
                it.remove();
                toBeAdded.add(featureCode);
            }

        }

        userPicks.addAll(toBeAdded);

    }

    public Picks getInitialVisiblePicks() {
        Picks picks = new Picks(this);
        picks.initVisibleDefaults();
//        picks.printPicks();
        return picks;
    }


    public List<FieldDef> getFieldDefs(String filter) {
        List<FieldDef> fields = new ArrayList<FieldDef>();
        And a = getExtraConstraint();
        List<BoolExpr> list = a.getExprList();
        for (BoolExpr expr : list) {
            FieldDef fieldDef = expr.isFieldDef(filter);
            if (fieldDef != null) {
                fields.add(fieldDef);
            }
        }
        return fields;
    }

//    private IndexToVarMapper intToVarMapper = new IndexToVarMapper() {
//        @Override public Var indexToVar(int varIndex) {
//            return getVar(varIndex);
//        }
//    };
//
//    private IndexToVarCodeMapper indexToVarCodeMapper = new IndexToVarCodeMapper() {
//        @Override public String indexToVar(int varIndex) {
//            return getVar(varIndex).getCode();
//        }
//    };
//
//    public IndexToVarCodeMapper getIndexToVarCodeMapper() {
//        return indexToVarCodeMapper;
//    }

    public ProductDef getProductDef() {
        Var[] fieldNames = getFieldNames();
        return new ProductDef("fmProduct", fieldNames);
    }


    public boolean hasNoGrades() {
        Var var = this.getVarOrNull(StdVarNames.Grade);
        if (var == null) return true;
        List<Var> childNodes = var.getChildVars();
        if (childNodes == null) return true;
        if (childNodes.isEmpty()) return true;

        return false;
    }

    public List<Var> getGrades() {
        final Var grade = this.getVarOrNull(StdVarNames.Grade);
        if (grade == null) {
            return null;
        } else if (grade.getChildVars() == null) {
            return null;
        } else {
            List<Var> list = grade.getChildVars();
            if (list.size() == 0) return null;
            else return grade.getChildVars();
        }
    }

    public List<Var> getExteriorColors() {
        final Var ec = this.getVarOrNull(StdVarNames.ExteriorColor);
        return ec.getChildVars();
    }

    public interface StdVarNames {
        String Grade = "Grade";
        String ExteriorColor = "ExteriorColor";
    }

    public static Set<String> parse(String commaDelimitedList) {

        final HashSet<String> set = new HashSet<String>();
        if (isEmpty(commaDelimitedList)) return set;
        commaDelimitedList = commaDelimitedList.trim();

        final String[] a = commaDelimitedList.split(",");
        if (a == null || a.length == 0) return set;

        for (String code : a) {
            if (isEmpty(code)) continue;
            String varCode = code.trim();

            set.add(varCode);

        }
        return set;
    }


    public Iff createIff(String varCode, String spaceDelimitedVarList) {
        if (isEmpty(varCode)) throw new IllegalArgumentException("varCode cannot be empty");
        Var expr1 = getVar(varCode);
        BoolExpr expr2 = exprParser.parseImplication(spaceDelimitedVarList);
        return this.iff(expr1, expr2);
    }

    private ExprParser exprParser = new ExprParser(this);

    public Implication createImplication(String varCode, String spaceDelimitedVarList) {
        if (isEmpty(varCode)) throw new IllegalArgumentException("varCode cannot be empty");
        Var expr1 = getVar(varCode);
        BoolExpr expr2 = exprParser.parseImplication(spaceDelimitedVarList);
        return this.imply(expr1, expr2);
    }

    public Conflict createConflict(String varCode, String spaceDelimitedVarList) {
        if (isEmpty(varCode)) throw new IllegalArgumentException("varCode cannot be empty");
        Var expr1 = getVar(varCode);
        BoolExpr expr2 = exprParser.parseConflict(spaceDelimitedVarList);
        return this.conflict(expr1, expr2);
    }

//    public And createConjunctionFromSpaceDelimitedList(String spaceDelimitedVarList) {
//        final List<Var> varList = parseVarList(spaceDelimitedVarList);
//        final List<BoolExpr> exprList = FeatureModel.upCastList(varList);
//        return this.and(exprList);
//    }

//    public Or createDisjunctionFromSpaceDelimitedList(String spaceDelimitedList) {
//        final List<Var> varList = parseVarList(spaceDelimitedList);
//        final List<BoolExpr> exprList = FeatureModel.upCastList(varList);
//        return this.or(exprList);
//    }

    public List<Var> parseVarList(String spaceDelimitedList) {
        final ArrayList<Var> list = new ArrayList<Var>();
        if (isEmpty(spaceDelimitedList)) throw new IllegalArgumentException();
        spaceDelimitedList = spaceDelimitedList.trim();
        final String[] a = spaceDelimitedList.split("\\s+");
        if (a == null || a.length == 0) throw new IllegalArgumentException();

        if (!containsWhitespace(spaceDelimitedList)) {
            //spaceDelimitedList must be a single code
            String code = spaceDelimitedList;
            Var var = getVarOrNull(code);
            if (var == null) throw new IllegalArgumentException("Invalid Feature Code: [" + code + "]");
            list.add(var);
        } else {
            for (String code : a) {
                if (isEmpty(code)) continue;
                code = code.trim();
                Var var = getVarOrNull(code);
                if (var == null) throw new IllegalArgumentException("Invalid Feature Code: [" + code + "]");
                list.add(var);
            }
        }
        return list;
    }

    public static boolean containsWhitespace(String s) {
        return Strings.containsWhitespace(s);
    }

    public List<Var> getAccessories() {
        Var accessoriesVar = getAccessoriesVar();
        return accessoriesVar.getDescendantLeafs();
    }

    public Var getAccessoriesVar() {
        return getVar(CommonVarNames.Accessories);
    }

    public void test() throws Exception {
        for (Var var : vars) {
            if (var.getExtraConstraintCount() == 0) System.out.println(var);
        }

    }


}