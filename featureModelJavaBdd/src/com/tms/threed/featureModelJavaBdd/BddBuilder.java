package com.tms.threed.featureModelJavaBdd;

import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Product;
import com.tms.threed.featureModel.shared.ProductDef;
import com.tms.threed.featureModel.shared.ProductMapper;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.boolExpr.And;
import com.tms.threed.featureModel.shared.boolExpr.BoolExpr;
import com.tms.threed.featureModel.shared.boolExpr.Conflict;
import com.tms.threed.featureModel.shared.boolExpr.Iff;
import com.tms.threed.featureModel.shared.boolExpr.Implication;
import com.tms.threed.featureModel.shared.boolExpr.Nand;
import com.tms.threed.featureModel.shared.boolExpr.Not;
import com.tms.threed.featureModel.shared.boolExpr.Or;
import com.tms.threed.featureModel.shared.boolExpr.Xor;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.javabdd.BDD;
import com.tms.threed.javabdd.BDDFactory;
import com.tms.threed.javabdd.BDDVarSet;
import com.tms.threed.javabdd.JFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BddBuilder {

    LogLevel logLevel = LogLevel.WARN;

    private final FeatureModel fm;

    public BDDFactory bddFactory = new JFactory(10000000, 100000);
    private BDD masterNode;

    public BddBuilder(FeatureModel fm) {
        assert fm != null;
        this.fm = fm;
    }


    public BDD buildBdd() {
        assert fm != null;
        vars = fm.getVars();
        addVars();
        addRootConstraint();
        addTreeConstraints();
        addCardConstraints(false);
        addExtraConstraints();
        return masterNode;
    }

    public BDD buildBddLeafOnly() {
        assert fm != null;
        vars = fm.getLeafVars();
        addVars();
        addCardConstraints(true);
        addExtraConstraints();
        return masterNode;
    }

    public BDDFactory getBddFactory() {
        return bddFactory;
    }

    /**
     *  careSet: all fm vars or all fm leafVars
     */
    public long satCount() {
        BDDVarSet bddVarSet = getBddVarSet();
        return (long) masterNode.satCount(bddVarSet);
    }

    /**
     * This seems to return 1 most of the time 
     * @param careSet
     * @return
     */
    public long satCount(Collection<Var> careSet) {
        BDDVarSet bddVarSet = getBddVarSet(careSet);
        return (long) masterNode.satCount(bddVarSet);
    }

    /**
     *  careSet: all fm vars or all fm leafVars
     */
    public BDD.BDDIterator iterator() {
        BDDVarSet bddVarSet = getBddVarSet();
        return masterNode.iterator(bddVarSet);
    }

    public BDD.BDDIterator iterator(Collection<Var> careSet) {
        BDDVarSet bddVarSet = getBddVarSet(careSet);
        return masterNode.iterator(bddVarSet);
    }

    /**
     * careSet: all fm vars or all fm leafVars
     * For testing purposes - should be = to satCount
     */
    public long iteratorCount() {
        BDD.BDDIterator it = iterator();
        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }
        return counter;
    }

    /**
     * For testing purposes - should be = to satCount
     */
    public long iteratorCount(Collection<Var> careSet) {
        BDD.BDDIterator it = iterator(careSet);
        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }
        return counter;
    }

    /**
     * For testing purposes - should be = to satCount
     */
//    public long satAllCount(long satCount) {
//        BDD.BDDIterator it = iterator();
//        int counter = 0;
//        while (it.hasNext()) {
//            it.next();
//            counter++;
//            if (satCount % counter == 10000) System.out.println(counter + " of " + satCount);
//        }
//        return counter;
//    }

    public BDD getBdd() {
        return masterNode; //todo
    }

//    public BDDVarSet getVarSet() {
//        return getBdd().toVarSet();
//    }

    private List<Var> vars;
//    private Map<Integer, Var> varMap1 = new HashMap<Integer, Var>();
    private Var[] varMap1;
    private Map<Var, Integer> varMap2 = new HashMap<Var, Integer>();

    private void addVars() {
        info("Creating vars...");
        info("\t bddVarCount: " + vars.size());
        varMap1 = new Var[vars.size()];
        bddFactory.setVarNum(vars.size());
        for (int i = 0; i < vars.size(); i++) {
            Var fmVar = vars.get(i);
            int bddVar = i;
            bddFactory.ithVar(bddVar);
//            varMap1.put(bddVar, fmVar);
            varMap1[bddVar] = fmVar;
            varMap2.put(fmVar, bddVar);
            debug("\t" + bddVar + ": " + fmVar.getCode());
        }
        System.out.println();
    }

    private void addRootConstraint() {
        info("Creating nodes for root constraint...");
        BoolExpr rcExpr = fm.getRootConstraint();
        debug("\t rcExpr: " + rcExpr);
        BDD rootConstraintNode = map(rcExpr);
        addBddConstraint(rootConstraintNode);
    }

    private void addTreeConstraints() {
        info("Creating nodes for treeConstraints...");
        List<BoolExpr> treeConstraints = fm.getTreeConstraint().getExprList();
        for (BoolExpr tcExpr : treeConstraints) {
            debug("\t tcExpr: " + tcExpr);
            BDD tcNode = map(tcExpr);
            addBddConstraint(tcNode);
        }
    }

    private void addCardConstraints(boolean leafOnly) {
        info("Creating nodes for cardConstraints...");
        List<BoolExpr> cardConstraints = fm.getCardinalityConstraint(leafOnly).getExprList();
        for (BoolExpr ccExpr : cardConstraints) {
            debug("\t ccExpr: " + ccExpr);
            BDD ccNode = map(ccExpr);
            addBddConstraint(ccNode);
        }
    }


    private void addExtraConstraints() {
        info("Creating nodes for extraConstraints...");
        List<BoolExpr> extraConstraints = fm.getExtraConstraint().getExprList();
        for (BoolExpr ecExpr : extraConstraints) {
            debug("\t ecExpr: " + ecExpr);
            BDD ecNode = map(ecExpr);
            addBddConstraint(ecNode);
        }
    }

    public BDD createNewBddBasedOnAssignments(Picks picks) {
        info("Creating nodes for assignmentConstraints...");

        BDD newBdd = null;
        for (Var acVar : vars) {
            BoolExpr acExpr;
            Bit val = picks.get(acVar);
            if (acVar.isAssigned(picks)) {
                BDD acNode;
                if (val.isTrue()) {
                    acExpr = acVar;
                    debug("\t acExpr: " + acExpr);
                    acNode = map(acVar);
                } else /* val.isFalse() */ {
                    acExpr = acVar.getCompliment();
                    debug("\t acExpr: " + acExpr);
                    acNode = map(acExpr);
                }

                if (newBdd == null) {
                    newBdd = acNode;
                } else {
                    newBdd = newBdd.and(acNode);
//                    newBdd = bddFactory.and.apply(newBdd, acNode);
                }

            }
        }

        return newBdd;

    }

    public void addAssignmentConstraints(Picks picks) {
        info("Creating nodes for assignmentConstraints...");

        for (Var acVar : vars) {
            BoolExpr acExpr;
            Bit val = acVar.getValue(picks);
            if (val.isTrue()) {
                acExpr = acVar;
                debug("\t acExpr: " + acExpr);
                BDD acNode = map(acVar);
                addBddConstraint(acNode);
            } else if (val.isFalse()) {
                acExpr = acVar.getCompliment();
                debug("\t acExpr: " + acExpr);
                BDD acNode = map(acExpr);
                addBddConstraint(acNode);
            }
        }

    }

    private void addBddConstraint(BDD constraint) {
        if (masterNode == null) {
            masterNode = constraint;
        } else {
            masterNode = constraint.and(masterNode);
        }
        assert !masterNode.isZero();
    }

    private void printMasterNodeTree() {
        System.out.println();
        System.out.println();
        System.out.println("masterNode.tree: ");
//        masterNode.printTree("Master", 0, fm);
    }

    private String formatRecord(boolean[] product) {
        return "todo"; //todo
    }


    BDD map(BoolExpr fmExpr) {
        if (fmExpr instanceof And) return mapAnd((And) fmExpr);
        if (fmExpr instanceof Or) return mapOr((Or) fmExpr);
        if (fmExpr instanceof Conflict) return mapConflict((Conflict) fmExpr);
        if (fmExpr instanceof Implication) return mapImplication((Implication) fmExpr);
        if (fmExpr instanceof Iff) return mapIff((Iff) fmExpr);
        if (fmExpr instanceof Var) return mapVar((Var) fmExpr);
        if (fmExpr instanceof Xor) return mapXor((Xor) fmExpr);
        if (fmExpr instanceof Nand) return mapNand((Nand) fmExpr);
        if (fmExpr instanceof Not) return mapNot((Not) fmExpr);
        throw new UnsupportedOperationException(fmExpr.getClass().getName());
    }

    BDD mapAnd(And fmAnd) {
        List<BoolExpr> exprList = fmAnd.getExprList();
        if (exprList.isEmpty()) throw new IllegalStateException("exprList is empty for fmAnd");
        if (exprList.size() == 1) throw new IllegalStateException("exprList is 1 for [" + fmAnd + "]");

        BDD retVal = null;
        for (BoolExpr expr : exprList) {

            BDD exprAsNode = map(expr);
            if (retVal == null) {
                retVal = exprAsNode;
            } else {
                retVal = retVal.and(exprAsNode);
            }
        }
        return retVal;
    }

    BDD mapOr(Or fmOr) {
        List<BoolExpr> exprList = fmOr.getExprList();
        if (exprList.isEmpty() || exprList.size() == 1) throw new IllegalStateException();

        BDD retVal = null;
        for (BoolExpr expr : exprList) {
            BDD exprAsNode = map(expr);
            if (retVal == null) {
                retVal = exprAsNode;
            } else {
                retVal = retVal.or(exprAsNode);
            }
        }
        return retVal;
    }

    BDD mapConflict(Conflict fmConflict) {
        BoolExpr e1 = fmConflict.getExpr1();
        BoolExpr e2 = fmConflict.getExpr2();

        BDD bdd1 = map(e1);
        BDD bdd2 = map(e2);

        return bdd1.ite(bdd2.not(), bddFactory.one());
    }

    BDD mapImplication(Implication fmImplication) {
        BoolExpr e1 = fmImplication.getExpr1();
        BoolExpr e2 = fmImplication.getExpr2();
        BDD n1 = map(e1);
        BDD n2 = map(e2);
        assert n1 != null;
        assert n2 != null;

        return n1.imp(n2);
    }

    BDD mapIff(Iff fmIff) {
        BoolExpr e1 = fmIff.getExpr1();
        BoolExpr e2 = fmIff.getExpr2();

        BDD n1 = map(e1);
        BDD n2 = map(e2);

        return n1.biimp(n2);
    }

    BDD mapXor(Xor fmXor) {
        And a = fmXor.toOrAndNandPairs();
        return mapAnd(a);
    }

    BDD mapNand(Nand fmNand) {
        BoolExpr e1 = fmNand.getExpr1();
        BoolExpr e2 = fmNand.getExpr2();
        BDD n1 = map(e1);
        BDD n2 = map(e2);

        return n1.ite(n2.not(), bddFactory.one());
    }

    BDD mapNot(Not fmNot) {
        BoolExpr expr = fmNot.getExpr();
        BDD node = map(expr);
        return node.not();
    }

    BDD mapVar(Var fmVar) {
        assert fmVar != null;
        assert varMap2 != null;
        Integer ssIndex = varMap2.get(fmVar);
        assert ssIndex != null : fmVar + " is not in varMap2";
        return bddFactory.ithVar(ssIndex);
    }

    public void debug(String msg) {
        if (logLevel.ordinal() >= LogLevel.DEBUG.ordinal()) System.out.println(msg);
    }

    public void info(String msg) {
        if (logLevel.ordinal() >= LogLevel.INFO.ordinal()) System.out.println(msg);
    }

    public void warn(String msg) {
        if (logLevel.ordinal() >= LogLevel.WARN.ordinal()) System.out.println(msg);
    }

    public void error(String msg) {
        if (logLevel.ordinal() >= LogLevel.ERROR.ordinal()) System.out.println(msg);
    }

    public BDDVarSet getBddVarSet() {
        return getBddVarSet(vars);
    }

    public Set<Var> getVarSet(BDDVarSet bddVarSet) {
        HashSet<Var> varSet = new HashSet<Var>();
        int[] a = bddVarSet.toArray();
        for (int i = 0; i < a.length; i++) {
            int bddVar = a[i];
            Var var = getFmVar(bddVar);
            varSet.add(var);
        }
        return varSet;
    }

    public BDDVarSet getBddVarSet(Collection<Var> varSet) {
        And a = fm.and();
        for (Var var : varSet) {
            a.add(var);
        }
        return mapAnd(a).toVarSet();
    }


    public void printAllSat(BDD node) {
        System.out.println("\t masterNode.allSat: ");
        BDD.BDDIterator it = node.iterator(getBddVarSet());
        while (it.hasNext()) {
            boolean[] product = it.next();
            System.out.println("\t\t " + formatRecord(product));
        }
    }

    public BDD getMasterNode() {
        return masterNode;
    }

    public FeatureModel getFeatureMode() {
        return fm;
    }

    public Set<Product> selectDistinct(ProductDef outProductDef) {

        ProductDef fmProductDef = fm.getProductDef();
        if (fmProductDef.equals(outProductDef)) throw new IllegalArgumentException();

        ProductMapper mapper = new ProductMapper(fmProductDef, outProductDef);
        final HashSet<Product> outHashSet = new HashSet<Product>();


//        final FeatureModelIterator it = null; //todo fix
//        while (it.hasNext()) {
//            Product fmProduct = it.next();
//            Product outProduct = mapper.map(fmProduct);
//            outHashSet.add(outProduct);
//        }
        return outHashSet;
    }

    public int countDistinct(ProductDef outProductDef) {
        return selectDistinct(outProductDef).size();
    }

    public Var getFmVar(int bddVar) {
        return varMap1[bddVar];
    }

    public int getBddVar(Var fmVar) {
        return varMap2.get(fmVar);
    }

    public Picks boolArrayToPicks(boolean[] productAsBoolArray) {
        return new Picks(fm, productAsBoolArray, varMap1);
    }
}