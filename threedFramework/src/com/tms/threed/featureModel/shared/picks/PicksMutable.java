package com.tms.threed.featureModel.shared.picks;

import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.Var;

import java.util.List;
import java.util.Set;

public interface PicksMutable extends PicksRO {

    public void resetDirtyFlag();

    public void resetAllAssignments();

    public boolean autoAssign(Var var, boolean newValue);

    public void pick(Var var);

    public void userAssign(Var var, Bit newValue);

    public void userAssign(Var var, boolean value);

    public void userPick(String varCode);

    public void pick(Set<String> vars);

    public void userAssign(String varCode, boolean value);

    public void userAssign(String code, Bit value);

    public void userAssign(List<Var> vars);

    public void pick(Var... vars);

    public void pick(String... vars);

    public boolean fixup();

    public boolean fixupLeafVarsBasedOnDefaults();

    public void initVisibleDefaults();

    public boolean fixupNonLeafVarsBasedOnDefaults();

    public void unpick(Var var);

    public void parseAndPick(String commaDelimitedList);


    public HandlerRegistration addPicksChangeHandler(PicksChangeHandler handler);

    public void firePicksChangeEvent();


}
