package com.tms.threed.featureModel.shared;

import com.tms.threed.featureModel.shared.boolExpr.BoolExpr;
import com.tms.threed.featureModel.shared.boolExpr.BoolExprVisitor;
import com.tms.threed.featureModel.shared.boolExpr.GetLeafVarsVisitor;
import com.tms.threed.featureModel.shared.boolExpr.GetVarsVisitor;
import com.tms.threed.featureModel.shared.boolExpr.Not;
import com.tms.threed.featureModel.shared.boolExpr.Xor;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.featureModel.shared.picks.PicksRO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.tms.threed.util.lang.shared.Strings.*;

public class Var extends BoolExpr implements Comparable<Var> {

    private final Var parent;
    private final String code;
    private final String name;
    private List<Var> childVars;
    private Cardinality cardinality;
    private Boolean manditory;
    private Boolean derived;
    private Integer leafRelationCount;

    private Set<String> flashKeys;
    public static final VarComparator VAR_COMPARATOR = new VarComparator();
    public static VarCodeComparator VAR_CODE_COMPARATOR = new VarCodeComparator();

    private Boolean defaultValue;
    public int index;

    Var(FeatureModel fm, Var parent, String code, String name) {
        super(fm);

        if (isEmpty(code)) {
            throw new IllegalArgumentException("varCode must be nonEmpty");
        }
        if (fm.containsCode(code)) {
            throw new IllegalArgumentException(getSimpleName(fm) + " already contains varCode[" + code + "]");
        }

        this.parent = parent;
        assert notEmpty(code);

        this.code = code;
        this.name = name;
    }

    public void accept(BoolExprVisitor visitor) {
        visitor.visit(this);
    }

    public String getComputedName() {
        if (isEmpty(name)) return code;
        else return name;
    }

    public Var addVar(String code, String name) {
        Var newChildVar = new Var(fm, this, code, name);
        if (childVars == null) childVars = new ArrayList<Var>();
        childVars.add(newChildVar);
        fm.getVarCollection().add(newChildVar);
        return newChildVar;
    }

    public Var addVar(String code) {
        return addVar(code, code);
    }

    public Var getParent() {
        return parent;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        Var that = (Var) o;
        return this.code.equals(that.code);
    }


    @Override
    public int hashCode() {
        return code.hashCode();
    }


    public void addFlashKey(String flashKey) {
        if (flashKeys == null) classes = new HashSet();
        flashKeys.add(flashKey);
    }


    @Override public String toString() {
        return code;
    }

    @Override public Var asVar() {
        return this;
    }


    public boolean isRoot() {
        return parent == null;
    }

    public int getDepth() {
        if (isRoot()) return 0;
        else return parent.getDepth() + 1;
    }

    public String indent() {
        return indent(getDepth());
    }

    protected static void indent(int tabCount, Object thingToPrint) {
        System.out.println(indent(tabCount) + thingToPrint);
    }

    protected static String indent(int tabCount) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < tabCount; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

    public void print() {
        int depth = getDepth();
        indent(depth, getName());
        printChildNodes();
    }

    public void printChildNodes() {
        if (childVars != null) {
            for (Var child : childVars) {
                child.print();
            }
        }
    }

    public List<Var> getChildVars() {
        return childVars;
    }

    public List<Var> getLeafChildNodes() {
        ArrayList<Var> leafs = new ArrayList<Var>();
        for (Var child : childVars) {
            if (child.isLeaf()) leafs.add(child);
        }
        return leafs;
    }

    public int getChildCount() {
        return childVars == null ? 0 : childVars.size();
    }

    public boolean isDerived() {
        if(this.derived != null) return this.derived;
        if (isRoot()) return false;
        return getParent().isDerived();
    }

    public boolean isVisible() {
        return !isDerived();
    }

    public Cardinality getCardinality() {
        if (cardinality == null && (isTrim() | isColor())) return Cardinality.AllGroup;
        return cardinality;
    }

    public boolean isPickOneGroup() {
        if (cardinality == null) return false;
        if (!cardinality.equals(Cardinality.PickOneGroup)) return false;
        if (childVars == null || childVars.size() == 0) {
            throw new IllegalStateException("Var [" + this.getLabel() + "] has Cardinality=PickOneGroup but has childCount=[" + childVars.size() + "]");
        }
        return true;
    }

    public boolean isAllGroup() {
        if (cardinality == null) return false;
        return cardinality.equals(Cardinality.AllGroup);
    }

    public boolean isOptional() {
        return !isManditory();
    }

    public boolean isPickOneChild() {
        if (parent == null) return false;
        return parent.isPickOneGroup();
    }

    public boolean isAllGroupChild() {
        if (parent == null) return false;
        return parent.isAllGroup();
    }

    public void setDerived(Boolean newValue) {
        this.derived = newValue;
    }

//    public BDDVarSet getChildNodesAsVarSet() {
//        And a = fm.and();
//        a.add(getChildNodes());
//        return a.getBdd().toVarSet();
//    }

    public boolean isLeaf() {
        return childVars == null || childVars.size() == 0;
    }

    public boolean hasChildVars() {
        return childVars != null && childVars.size() > 0;
    }

    public void printVarTree() {
        printVarTree(0);
    }

    public String getLabel() {
        if (isEmpty(name)) return code;
        if (isEmpty(code)) throw new IllegalStateException();
        if (code.equalsIgnoreCase(name)) return name;
        else return code + ":" + name;
    }

    public void printVarTree(int depth) {
        printVarTree(depth, null);
    }


    public void printVarTree(int depth, Picks picks) {
        String atts = "[" + ((manditory!=null && manditory) ? "manditory" : "") + ((defaultValue != null && defaultValue) ? "defaultValue[true]" : "") + ((derived!=null && derived) ? "derived" : "") + ((cardinality != null) ? cardinality : "") + "]";

        System.out.println(getIndent(depth) + getDepth() + ":" + getLabel() + (picks != null ? ": " + picks.get(this) : "") + "  " + atts);
        final List<Var> childVars = getChildVars();
        if (childVars != null) {
            for (Var childVar : childVars) {
                childVar.printVarTree(depth + 1, picks);
            }
        }
    }

    private static String getIndent(int depth) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < depth; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

//    public boolean fixup2(BDDFactory f) {
//        if (isAssigned()) return false;
//
//        final BDD bdd = fm.bdd(f);
//
//        final BDD v = bdd(f);
//        final BDD nv = nbdd(f);
//
//        final long pSatCount = (long) bdd.restrict(v).satCount();
//        final long npSatCount = (long) bdd.restrict(nv).satCount();
//
//        if (pSatCount == 0 && npSatCount > 0) {
//            fm.assign(this, Bit.NOT_PICKED);
//            return true;
//        } else if (pSatCount > 0 && npSatCount == 0) {
//            fm.assign(this, Bit.PICKED);
//            return true;
//        } else if (pSatCount > 0 && npSatCount > 0) {
//            return false;
//        } else if (pSatCount == 0 && npSatCount == 0) {
//            throw new IllegalStateException("WARN: NO POSSIBLE SOLUTIONS FOR VAR[" + getCode() + "]");
//        } else {
//            throw new IllegalStateException();
//        }
//
//    }


    public boolean isManditory() {
        return manditory != null && manditory;
    }

    public void setManditory(Boolean manditory) {
        this.manditory = manditory;
    }

    public void setCardinality(Cardinality cardinality) {
        this.cardinality = cardinality;
    }


    public boolean hasCardinality() {
        return cardinality != null;
    }

    public boolean isDescendantOf(Var var) {
        if (isRoot()) return false;
        if (var == parent) return true;
        return parent.isDescendantOf(var);
    }


    public Set<Var> getRelatedVars() {
        Set<BoolExpr> extraConstraints = getExtraConstraints();
        GetVarsVisitor getVars = new GetVarsVisitor();
        for (BoolExpr expr : extraConstraints) {
            expr.accept(getVars);
        }
        return getVars.getVars();
    }

    public void initLeafRelationCount() {
        if (leafRelationCount == null) {
            leafRelationCount = getRelatedLeafVars().size();
            if (hasChildVars()) {
                for (Var childVar : childVars) {
                    childVar.initLeafRelationCount();
                }
            }
        }
    }

    public Set<Var> getRelatedLeafVars() {
        Set<BoolExpr> extraConstraints = getExtraConstraints();

        GetLeafVarsVisitor getVars = new GetLeafVarsVisitor();

        for (BoolExpr expr : extraConstraints) {
            expr.accept(getVars);
        }

        return getVars.getLeafVars();
    }

    public Set<BoolExpr> getTreeConstraints() {
        List<BoolExpr> treeConstraints = fm.getTreeConstraint().getExprList();
        Set<BoolExpr> set = new HashSet<BoolExpr>();
        for (BoolExpr constraint : treeConstraints) {
            if (constraint.dependsOn(this)) {
                set.add(constraint);
            }
        }
        return set;
    }


    public Set<BoolExpr> getExtraConstraints() {
        List<BoolExpr> extraConstraints = fm.getExtraConstraint().getExprList();
        Set<BoolExpr> set = new HashSet<BoolExpr>();
        for (BoolExpr constraint : extraConstraints) {
            if (constraint.dependsOn(this)) {
                set.add(constraint);
            }
        }
        return set;
    }

    public int getExtraConstraintCount() {
        return this.getExtraConstraints().size();
    }

    public boolean referencesAnyNonLeafVars() {
        if (!isLeaf()) {
//            System.out.println(this + " references a [IS A] non-leaf var");
            return true;
        }
        return false;
    }

    public boolean hasAnyNonLeafChildNodes() {
        if (childVars == null) return false;
        for (Var child : childVars) {
            if (!child.isLeaf()) return true;
        }
        return false;
    }

    public Xor getChildNodesAsXor() {
        assert isPickOneGroup();
        assert childVars != null;
        assert childVars.size() != 0;
        Xor xor = fm.xor();
        for (Var childVar : getChildVars()) {
            xor.add(childVar);
        }
        return xor;
    }

    public List<Var> getDescendantLeafs() {
        List<Var> list = new ArrayList<Var>();
        getDescendantLeafs(list);
        return list;
    }

    private void getDescendantLeafs(List<Var> list) {
        if (childVars == null) return;
        for (Var child : childVars) {
            if (child.isLeaf()) {
                list.add(child);
            } else {
                child.getDescendantLeafs(list);
            }
        }
    }

    public List<Var> getSelfPlusDescendants(boolean leafOnly) {
        List<Var> list = new ArrayList<Var>();
        getSelfPlusDescendantVars(list, leafOnly);
        return list;
    }

    private void getSelfPlusDescendantVars(List<Var> list, boolean leafOnly) {
        if (!leafOnly || isLeaf()) list.add(this);
        if (childVars == null) return;
        for (Var child : childVars) {
            child.getSelfPlusDescendantVars(list, leafOnly);
        }
    }

    private void sortChildVarsByLeafRelationCount() {
        if (childVars != null) {
            Collections.sort(childVars, VAR_COMPARATOR);
        }
    }

    public void sortDescendantVarsByLeafRelationCount() {
        sortChildVarsByLeafRelationCount();
        if (hasChildVars()) {
            for (Var child : childVars) {
                child.sortDescendantVarsByLeafRelationCount();
            }
        }
    }

    public Not getCompliment() {
        return fm.not(this);
    }

    public boolean isUnconstrained() {
        return getExtraConstraintCount() == 0;
    }

    public boolean fixupAssignDefault(Picks picks) {
        boolean defVal = isDefault();
        return autoAssign(picks, defVal);
    }

    public void initialAssignDefault(Picks picks) {
        boolean defVal = isDefault();
        if(defVal) initialAssign(picks, defVal);
    }

    public Boolean getDefaultValue() {
        return defaultValue;
    }

    public boolean isDefault() {
        if (defaultValue == null) {
            return isFirstPickOneChild();
        } else {
            return defaultValue;
        }
    }

    public boolean isFirstPickOneChild() {
        return isPickOneChild() && isFirstChild();
    }

    private boolean isFirstChild() {return parent.childVars.indexOf(this) == 0;}

    public void setDefaultValue(Boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isAssigned(Picks picks) {
        return picks.isAssigned(this);
    }

    public Bit getValue(Picks picks) {
        return picks.get(this);
    }

    public boolean isAccessory() {
        Var accessoriesVar = getFeatureModel().getAccessoriesVar();
        return isDescendantOf(accessoriesVar);
    }

    public boolean isAccessories() {
        return code.equalsIgnoreCase(CommonVarNames.Accessories);
    }

    public boolean isTrim() {
        return code.equalsIgnoreCase(CommonVarNames.Trim);
    }

    public boolean isColor() {
        return code.equalsIgnoreCase(CommonVarNames.Color);
    }


    private static class VarComparator implements Comparator<Var> {
        @Override public int compare(Var v1, Var v2) {
            Integer c1 = v1.leafRelationCount;
            Integer c2 = v2.leafRelationCount;
            return c2.compareTo(c1);
        }
    }

    @Override public boolean autoAssign(Picks picks, boolean newValue) {
        return picks.autoAssign(this, newValue);
    }

    public void initialAssign(Picks picks, boolean newValue) {
        picks.initialAssign(this, newValue);
    }

    @Override public Bit eval(PicksRO picks) {
        return picks.get(this);
    }

    @Override public int compareTo(Var that) {
        return this.code.compareTo(that.code);
    }

    private static class VarCodeComparator implements Comparator<Var> {

        @Override public int compare(Var v1, Var v2) {
            return v1.code.compareTo(v2.code);
        }
    }

    public boolean isMandatory() {
        if (manditory != null) return manditory;
        else if (isTrim() || isColor()) return true;
        return false;
    }

    public Boolean getManditory() {
        return manditory;
    }

    public Boolean getDerived() {
        return derived;
    }
}
