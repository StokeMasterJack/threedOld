package com.tms.threed.featureModel.shared.picks;

import com.tms.threed.featureModel.shared.Assignment;
import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.ProposePickResponse;
import com.tms.threed.featureModel.shared.Source;
import com.tms.threed.featureModel.shared.Var;

import java.util.Collection;
import java.util.Set;

public interface PicksRO {

    FeatureModel getFeatureModel();

    Bit get(Var var);

    Set<Var> getUnassignedVars();

    Set<Var> getVarsByValue(Bit filter);

    boolean anyAssignments();

    Set<Var> toVarSet(Bit value, Source source);

    Set<Var> getUserPicks();

    PicksKey getKey();

    int getUnassignedVarCount();

    int getAssignedVarCount();

    int getPickCount();

    void printPicks();

    void printAssignments(Bit filter);

    void printUnassignedVars();

    Set<Var> getAllPicks();

    Set<String> getAllPicks2();

    boolean isAssigned(Var var);

    boolean isUnassigned(Var var);

    boolean isTrue(Var var);

    boolean isFalse(Var var);

    boolean isPicked(Var var);

    boolean isPicked(String code);

    boolean containsAll(Collection<String> features);

    boolean containsAllVars(Collection<Var> features);

    @Override String toString();

    @Override boolean equals(Object o);

    @Override int hashCode();

    Assignment getAssignment(Var var);

    PicksSnapshot createSnapshot();

    Picks copyIgnoreFixupPicks();

}
