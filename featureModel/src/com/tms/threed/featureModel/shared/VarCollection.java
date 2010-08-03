package com.tms.threed.featureModel.shared;

import com.tms.threed.util.gwt.client.Console;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VarCollection implements Iterable<Var> {

    private ArrayList<Var> varList = new ArrayList<Var>();
    private Map<String, Var> varMap = new HashMap<String, Var>();

    public Var get(int varIndex) {
        return varList.get(varIndex);
    }

    public Var get(String code) {
        String key = code.trim().toUpperCase();
        return varMap.get(key);
    }

    public int size() {
        return varList.size();
    }

    void add(Var var) {
        String key = var.getCode().trim().toUpperCase();
        assert !varMap.containsKey(key) : "Var code[" + var.getCode() + "] already exists in fm";
        varMap.put(key, var);

        varList.add(var);
        var.index = varList.size() - 1;
    }

    public boolean containsCode(String code) {
        String key = code.trim().toUpperCase();
        return varMap.containsKey(key);
    }

    List<Var> getVarList() {
        return varList;
    }

//    public List<String> toCodeList() {
//        return Collections.unmodifiableList(codeList);
//    }

    public Var[] toVarArray() {
        final Var[] a = new Var[varList.size()];
        varList.toArray(a);
        return a;
    }

//    public Set<String> toCodeSet() {
//        return varMap.keySet();
//    }

    public Collection<Var> getByClass(final String className) {
        ArrayList<Var> list = new ArrayList<Var>();
        for (Var var : varList) {
            if (var.hasClass(className)) list.add(var);
        }
        return Collections.unmodifiableCollection(list);
    }

    @Override public Iterator<Var> iterator() {
        return varList.iterator();
    }

    public List<Var> getLeafVars() {
        ArrayList a = new ArrayList();
        for (Var var : varList) {
            if (var.isLeaf()) a.add(var);
        }
        return a;
    }

//    public Map<Integer, String> getVarNames(boolean oneBased) {
//        HashMap<Integer, String> m = new HashMap<Integer, String>();
//        for (int i = 0; i < varList.size(); i++) {
//            Var var = varList.get(i);
//            int adder = oneBased ? 1 : 0;
//            m.put(i + adder, var.getName());
//        }
//        return m;
//    }


    @Override public String toString() {
        return varMap.keySet().toString();
    }
}
