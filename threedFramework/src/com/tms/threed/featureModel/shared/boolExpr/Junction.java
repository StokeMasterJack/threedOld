package com.tms.threed.featureModel.shared.boolExpr;

import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.featureModel.shared.Var;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Junction extends BoolExpr {

    protected List<BoolExpr> exprList;

    public Junction(FeatureModel fm, List<BoolExpr> expressions) {
        super(fm);
        ArrayList<BoolExpr> a = new ArrayList<BoolExpr>();
        for (BoolExpr expr : expressions) {
            a.add(expr.simplify());
        }
        this.exprList = a;
    }

    public Junction(FeatureModel fm, BoolExpr... expressions) {
        this(fm, new ArrayList(Arrays.asList(expressions)));
    }

    public Junction(FeatureModel fm) {
        super(fm);
        exprList = new ArrayList<BoolExpr>();
    }

    public void accept(BoolExprVisitor visitor) {
        visitor.visit(this);
    }

    public void add(BoolExpr e) {
        assert e != null;
        exprList.add(e.simplify());
    }

    public void addAll(List<BoolExpr> all) {
        exprList.addAll(all);
    }

    public void add(List<Var> vars) {
        for (Var var : vars) {
            add(var);
        }
    }

    public void add(Collection<Var> vars) {
        for (Var var : vars) {
            add(var);
        }
    }

    abstract public String getSymbol();

    public int getExpressionCount() {
        return exprList.size();
    }

    @Override public String toString() {
        if (exprList.size() == 0) {
            throw new IllegalStateException();
        }
        StringBuilder a = new StringBuilder();
        for (int i = 0; i < exprList.size(); i++) {
            BoolExpr e = exprList.get(i);
            a.append(e.toString());
            if (i != exprList.size() - 1) {
                a.append(' ');
                a.append(getSymbol());
                a.append(' ');
            }
        }
        return "(" + a.toString() + ")";
    }

    public void clear() {
        exprList.clear();
    }

    public <T extends Pair> Set<T> toPairSet(Class<T> pairImplClass) {
        Set<T> set = new HashSet<T>();
        for (BoolExpr e1 : exprList) {
            for (BoolExpr e2 : exprList) {
                if (e1 != e2) {
                    T pair = fm.createPair(pairImplClass, e1, e2);
                    set.add(pair);
                }
            }
        }
        return set;
    }

    public <T extends Pair> List<T> toPairList(Class<T> pairImplClass) {
        return new ArrayList<T>(toPairSet(pairImplClass));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Junction that = (Junction) o;
        return this.getExprSet().equals(that.getExprSet());
    }

    public Set<BoolExpr> getExprSet() {
        HashSet set = new HashSet();
        for (BoolExpr expr : exprList) {
            boolean b = set.add(expr);
            if (!b) System.out.println("DUP: " + expr);
        }
        return set;
    }

    public List<BoolExpr> getExprList() {
        return exprList;
    }

    public Counts getValueCounts(Picks picks) {
        Counts counts = new Counts();
        counts.exprCount = getExpressionCount();

        for (BoolExpr expr : exprList) {
            counts.incr(picks,expr);
        }

        return counts;

    }

    public static class Counts {

        int exprCount;

        int trueCount;
        int falseCount;
        int unassignedCount;

        BoolExpr onlyUnassigned;

        public void incr(Picks picks,BoolExpr expr) {
            Bit exprValue = expr.eval(picks);
            switch (exprValue) {
                case TRUE:
                    trueCount++;
                    break;
                case FALSE:
                    falseCount++;
                    break;
                case UNASSIGNED:
                    unassignedCount++;
                    onlyUnassigned = expr;
                    break;
            }
        }

        public boolean allTrueButOneUnassigned() {
            return trueCount == (exprCount - 1) && unassignedCount == 1;
        }

        public boolean allFalseButOneUnassigned() {
            return falseCount == (exprCount - 1) && unassignedCount == 1;
        }

        public boolean oneTrueAndRestAreUnassigned() {
            return unassignedCount == (exprCount - 1) && trueCount == 1;
        }

        public boolean oneTrueAndRestAreUnassignedOrFalse() {
            return trueCount == 1;
        }

        public boolean allFalseButOneTrue() {
            return falseCount == (exprCount - 1) && trueCount == 1;
        }

        public boolean anyUnassigned() {
            return unassignedCount > 0;
        }

        public boolean allTrue() {
            return trueCount == exprCount;
        }

        public boolean allFalse() {
            return falseCount == exprCount;
        }

        public boolean anyFalse() {
            return falseCount>0;
        }
    }


    @Override
    public int hashCode() {
        return getExprSet().hashCode();
    }

    public boolean containsOnlyVars() {
        for (BoolExpr expr : exprList) {
            Var var = expr.asVar();
            if (var == null) return false;
        }
        return true;
    }


}