package com.tms.threed.featureModel.shared.picks;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.featureModel.shared.Assignment;
import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Source;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.boolExpr.And;
import com.tms.threed.featureModel.shared.boolExpr.BoolExpr;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.tms.threed.util.lang.shared.Strings.isEmpty;

public class Picks implements PicksRO, PicksMutable {

    private final HandlerManager bus = new HandlerManager(this);

    protected final FeatureModel fm;
//    protected final HashMap<Var, Assignment> map = new HashMap<Var, Assignment>();

    protected Assignment[] map;

    private boolean dirty;

    private Var mostRecentSinglePick;
    private PicksSnapshot lastState;

    public Picks(FeatureModel fm) {
        this.fm = fm;

        List<Var> varList = fm.getVars();

        this.map = new Assignment[varList.size()];

        for (int i = 0; i < map.length; i++) {
            map[i] = Assignment.UNASSIGNED;
        }
    }

    /**
     * For use with allSat and jpgGen
     */
    public Picks(FeatureModel fm, boolean[] picks) {
        this.fm = fm;
        assert picks.length == fm.getVarCount();

        this.map = new Assignment[picks.length];
        for (int i = 0; i < picks.length; i++) {
            map[i] = picks[i] ? Assignment.TRUE_INIT : Assignment.FALSE_FIXUP;
        }
    }

    /**
     * For use with allSat and jpgGen
     */
    public Picks(FeatureModel fm, boolean[] picks, Var[] varMap) {
        this(fm);
        this.map = new Assignment[fm.getVarCount()];
        for (int i = 0; i < picks.length; i++) {
            boolean value = picks[i];
            Var var = varMap[i];
            map[var.index] = value ? Assignment.TRUE_INIT : Assignment.FALSE_FIXUP;
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void resetDirtyFlag() {
        dirty = false;
        storeSnapshot();
        mostRecentSinglePick = null;
    }

    private void storeSnapshot() {
        lastState = this.createSnapshot();
    }

    public void resetAllAssignments() {
        for (int i = 0; i < map.length; i++) {
            map[i] = Assignment.UNASSIGNED;
        }
        mostRecentSinglePick = null;
    }

    public boolean autoAssign(Var var, boolean newValue) {

        Bit newVal = newValue ? Bit.TRUE : Bit.FALSE;
        Bit oldValue = map[var.index].getValue();
        if (oldValue.equals(newVal)) return false;

        map[var.index] = Assignment.create(newVal, Source.Fixup);

        dirty = true;
        return true;
    }

    public void initialAssign(Var var, boolean newValue) {
        Bit newVal = newValue ? Bit.TRUE : Bit.FALSE;
        map[var.index] = Assignment.create(newVal, Source.Initial);
    }

    public void pick(Var var) {
        userAssign(var, Bit.TRUE);
    }

    public void userAssign(Var var, Bit newValue) {
        Bit oldValue = map[var.index].getValue();
        if (oldValue.equals(newValue)) return;

        if (var.isPickOneChild() && newValue.isTrue()) {

            Var pickOneGroup = var.getParent();
            for (Var childVar : pickOneGroup.getChildVars()) {
                if (childVar == var) continue;
                if (isTrue(childVar) || isUnassigned(childVar)) {
                    userAssign(childVar, false);
                }

                if (isTrue(childVar)) {
                    map[var.index] = Assignment.UNASSIGNED;
                }
            }
        }

        map[var.index] = Assignment.create(newValue, Source.User);
        dirty = true;

        if (newValue.isTrue()) mostRecentSinglePick = var;
        else mostRecentSinglePick = null;
    }

    public void userAssign(Var var, boolean value) {
        userAssign(var, Bit.fromBool(value));
    }

    public void userPick(String varCode) {
        Var var = fm.getVarOrNull(varCode);
        if (var == null) {
            //System.out.println("Ignoring pick [" + varCode + "]. It is not in the FeatureModel.");
        } else {
            pick(var);
        }
    }

    public void pick(Set<String> vars) {
        for (String varCode : vars) {
            userPick(varCode);
        }
    }

    public void userAssign(String varCode, boolean value) {
        final Var var = fm.getVarOrNull(varCode);
        if (var == null) {
            //System.out.println("Ignoring assignment for var: [" + varCode + "]. Not in FeatureModel.");
        } else {
            userAssign(var, Bit.fromBool(value));
        }
    }

    public void userAssign(String code, Bit value) {
        userAssign(fm.getVarOrNull(code), value);
    }

    public void userAssign(List<Var> vars) {
        for (Var var : vars) {
            userAssign(var, true);
        }
    }


    public void pick(Var... vars) {
        for (Var var : vars) {
            userAssign(var, true);
        }
    }

    public void pick(String... vars) {
        for (String code : vars) {
            userAssign(code, true);
        }
    }

    public void resetAutoAssignments() {
        for (int i = 0; i < map.length; i++) {
            Source source = map[i].getSource();
            if (source != null && source.isFixup()) map[i] = Assignment.UNASSIGNED;
        }
    }


    public boolean fixup() throws IllegalPicksStateException {
        boolean ch1 = fixupBasedOnConstraints();
        boolean ch2 = fixupLeafVarsBasedOnDefaults();

        boolean ch3;
        if (ch1 || ch2) {
            ch3 = fixupBasedOnConstraints();
        } else {
            ch3 = false;
        }

        boolean ch4 = fixupNonLeafVarsBasedOnDefaults();

        return ch1 || ch2 || ch3 || ch4;
    }

    private boolean fixupBasedOnConstraints() throws IllegalPicksStateException {

        boolean anyChange = false;

        while (true) {

            BoolExpr masterConstraint = fm.getMasterConstraint();
//            boolean ch = masterConstraint.autoAssign(this, true);

//            Console.log("Processing RootConstraint");
            BoolExpr rootConstraint = fm.getRootConstraint();
            boolean ch1 = rootConstraint.autoAssign(this, true);

//            Console.log("Processing TreeConstraint");
            And treeConstraint = fm.getTreeConstraint();
            boolean ch2 = treeConstraint.autoAssign(this, true);

//            Console.log("Processing ExtraConstraint");
            And extraConstraint = fm.getExtraConstraint();
            boolean ch3 = extraConstraint.autoAssign(this, true);

//            Console.log("Processing CardinalityConstraint");
            And cardinalityConstraint = fm.getCardinalityConstraint(true);
            boolean ch4 = cardinalityConstraint.autoAssign(this, true);


            boolean ch = ch1 || ch2 || ch3 || ch4;

            if (ch) anyChange = true;
            else return anyChange;
        }

    }

    public boolean fixupLeafVarsBasedOnDefaults() {
        boolean anyChange = false;
        for (Var var : getUnassignedVars()) {
            if (var.isLeaf()) {
                boolean ch = var.fixupAssignDefault(this);
                if (ch) anyChange = true;
            }
        }
        return anyChange;
    }

    public void initVisibleDefaults() {
        for (Var var : getUnassignedVars()) {
            if (var.isLeaf() && !var.isDerived()) {
                var.initialAssignDefault(this);
            }
        }
    }

    public boolean fixupNonLeafVarsBasedOnDefaults() {
        boolean anyChange = false;
        for (Var var : getUnassignedVars()) {
            if (!var.isLeaf()) {
                boolean ch = var.fixupAssignDefault(this);
                if (ch) anyChange = true;
            }
        }
        return anyChange;
    }

    public void unpick(Var var) {
        userAssign(var, Bit.FALSE);
    }

    public void parseAndPick(String commaDelimitedList) {

        if (isEmpty(commaDelimitedList)) return;
        commaDelimitedList = commaDelimitedList.trim();

        final String[] a = commaDelimitedList.split(",");
        if (a == null || a.length == 0) return;

        for (String code : a) {
            if (isEmpty(code)) continue;
            String varCode = code.trim();
            userPick(varCode);
        }
    }


    public HandlerRegistration addPicksChangeHandler(PicksChangeHandler handler) {
        return bus.addHandler(PicksChangeEvent.TYPE, handler);
    }

    public void firePicksChangeEvent() {
        if (isDirty()) {
            PicksSnapshot currentState = this.createSnapshot();
            PicksChangeEvent pce = new PicksChangeEvent(lastState, currentState, mostRecentSinglePick);
            bus.fireEvent(pce);
            lastState = currentState;
            resetDirtyFlag();
        }
    }


    @Override public FeatureModel getFeatureModel() {
        return fm;
    }

    @Override public Bit get(Var var) {
        return map[var.index].getValue();
    }

    @Override public Set<Var> getUnassignedVars() {
        return getVarsByValue(Bit.UNASSIGNED);
    }

    @Override public Set<Var> getVarsByValue(Bit filter) {
        return getVarsByValue(filter, false);
    }

    public Set<Var> getVarsByValue(Bit filter, boolean leafOnly) {
        assert filter != null;
        Set<Var> set = new HashSet<Var>();

        for (int i = 0; i < map.length; i++) {
            Assignment assignment = map[i];
            Bit value;
            if (assignment == null) {
                value = Bit.UNASSIGNED;
            } else {
                value = assignment.getValue();
            }
            assert value != null;
            if (value.equals(filter)) {
                Var var = fm.getVar(i);
                if (!leafOnly || var.isLeaf()) set.add(var);
            }
        }

        return set;
    }


    @Override public boolean anyAssignments() {
        for (int i = 0; i < map.length; i++) {
            Assignment assignment = map[i];
            if (assignment.isAssigned()) return true;
        }
        return false;
    }

    @Override public Set<Var> toVarSet(Bit value, Source source) {

        HashSet<Var> set = new HashSet<Var>();

        for (int i = 0; i < map.length; i++) {
            Assignment assignment = map[i];
            Var itVar = fm.getVar(i);
            Bit itValue = assignment.getValue();
            Source itSource = assignment.getSource();

            if (value != null && !value.equals(itValue)) continue;
            if (source != null && !source.equals(itSource)) continue;

            set.add(itVar);
        }
        return set;
    }


    @Override public Set<Var> getUserPicks() {
        return toVarSet(Bit.TRUE, Source.User);
    }

    @Override public PicksKey getKey() {
        return new PicksKey(getUserPicks());
    }


    @Override public int getUnassignedVarCount() {
        int count = 0;
        for (int i = 0; i < map.length; i++) {
            Assignment assignment = map[i];
            if (!assignment.isAssigned()) count++;
        }
        return count;
    }

    @Override public int getAssignedVarCount() {
        int count = 0;
        for (int i = 0; i < map.length; i++) {
            Assignment assignment = map[i];
            if (assignment.isAssigned()) count++;
        }
        return count;
    }

    @Override public int getPickCount() {
        int count = 0;

        for (int i = 0; i < map.length; i++) {
            Assignment assignment = map[i];
            if (assignment.isTrue()) count++;
        }
        return count;
    }


    @Override public void printPicks() {
        System.out.println("Picks:");

        for (int i = 0; i < map.length; i++) {
            Assignment assignment = map[i];
            if (assignment.getValue().isTrue()) {
                Var var = fm.getVar(i);
                System.out.println("\t" + var.getCode() + " - " + var.getName());
            }
        }
    }

    @Override public void printAssignments(Bit filter) {
        System.out.println("Vars assigned to " + filter + ":");
        Set<Var> vars = getVarsByValue(filter);
        for (Var var : vars) {
            if (!var.isLeaf()) continue;
            System.out.println("\t" + var);
        }
    }

    @Override public void printUnassignedVars() {
        System.out.println("Unassigned Vars:");
        Set<Var> vars = getVarsByValue(Bit.UNASSIGNED);
        for (Var var : vars) {
            System.out.println("\t" + var + ": " + var.getExtraConstraintCount());
        }
    }


    @Override public Set<Var> getAllPicks() {
        return getVarsByValue(Bit.TRUE);
    }

    @Override public Set<String> getAllPicks2() {
        final HashSet<String> picks = new HashSet<String>();
        for (Var var : getAllPicks()) {
            picks.add(var.getCode());
        }
        return picks;
    }

    @Override public boolean isAssigned(Var var) {
        Bit bit = get(var);
        return bit.isAssigned();
    }

    @Override public boolean isUnassigned(Var var) {
        Bit bit = get(var);
        return bit.isUnassigned();
    }

    @Override public boolean isTrue(Var var) {
        Bit bit = get(var);
        return bit.isTrue();
    }

    @Override public boolean isFalse(Var var) {
        Bit bit = get(var);
        return bit.isFalse();
    }

    @Override public boolean isPicked(Var var) {
        return isTrue(var);
    }

    @Override public boolean isPicked(String code) {
        Var var = fm.getVar(code);
        return isPicked(var);
    }

    @Override public boolean containsAll(Collection<String> features) {
        for (String varCode : features) {
            if (!isPicked(varCode)) return false;
        }
        return true;
    }

    @Override public boolean containsAllVars(Collection<Var> features) {
        for (Var var : features) {
            if (!isPicked(var)) return false;
        }
        return true;
    }

    @Override public String toString() {
        Set<Var> varSet = null;
        varSet = getVarsByValue(Bit.TRUE, true);
        assert varSet != null;
        return varSet.toString();

    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("Picks does not support equals method");
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Picks does not support hashCode method");
    }

    @Override public Assignment getAssignment(Var var) {
        return map[var.index];
    }


    @Override public PicksSnapshot createSnapshot() {
        PicksSnapshotImpl picksSnapshot = new PicksSnapshotImpl(fm);

        for (int i = 0; i < map.length; i++) {
            picksSnapshot.map[i] = map[i];
        }
        return picksSnapshot;
    }

    @Override public Picks copyIgnoreFixupPicks() {
        Picks picks = new Picks(fm);

        for (int i = 0; i < map.length; i++) {
            Assignment assignment = map[i];
            if (!assignment.getSource().isFixup()) {
                picks.map[i] = map[i];
            }
        }
        return picks;
    }


}