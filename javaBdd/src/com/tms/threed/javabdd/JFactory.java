// JFactory.java, created Aug 1, 2003 7:06:47 PM by joewhaley
// Copyright (C) 2003 John Whaley <jwhaley@alum.mit.edu>
// Licensed under the terms of the GNU LGPL; see COPYING for details.
package com.tms.threed.javabdd;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is a 100% Java implementation of the BDD factory.  It is based on
 * the C source code for BuDDy.  As such, the implementation is very ugly,
 * but it works.  Like BuDDy, it uses a reference counting scheme for garbage
 * collection.</p>
 *
 * @author John Whaley
 * @version $Id$
 */
public class JFactory extends BDDFactoryIntImpl {

    /**** Options ****/

    /**
     * Flush the operation cache on every garbage collection.  If this is false,
     * we only clean the collected entries on every GC, rather than emptying the
     * whole cache.  For most problems, you should keep this set to true.
     */
    public static boolean FLUSH_CACHE_ON_GC = true;

    static final boolean VERIFY_ASSERTIONS = false;
    static final boolean CACHESTATS = false;
    static final boolean SWAPCOUNT = false;

    public static final String REVISION = "$Revision$";

    public String getVersion() {
        return "JFactory " + REVISION.substring(11, REVISION.length() - 2);
    }

    protected JFactory() {}

    public JFactory(int nodenum, int cachesize) {
        initialize(nodenum, cachesize);
//        if (CACHESTATS) addShutdownHook(this);
    }

//    static void addShutdownHook(final BDDFactory f) {
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            public void run() {
//                System.out.println(f.getCacheStats().toString());
//            }
//        });
//    }

    boolean ZDD = false;

    /**
     * Implementation of BDDPairing used by JFactory.
     */
    class BddPair extends BDDPairing {
        int[] result;
        int last;
        int id;
        BddPair next;

        /* (non-Javadoc)
         * @see net.sf.javabdd.BDDPairing#set(int, int)
         */

        public void set(int oldvar, int newvar) {
            bdd_setpair(this, oldvar, newvar);
        }
        /* (non-Javadoc)
         * @see net.sf.javabdd.BDDPairing#set(int, net.sf.javabdd.BDD)
         */

        public void set(int oldvar, BDD newvar) {
            bdd_setbddpair(this, oldvar, unwrap(newvar));
        }
        /* (non-Javadoc)
         * @see net.sf.javabdd.BDDPairing#reset()
         */

        public void reset() {
            bdd_resetpair(this);
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append('{');
            boolean any = false;
            for (int i = 0; i < result.length; ++i) {
                if (result[i] != (ZDD ? zdd_makenode(i, 0, 1) : bdd_ithvar(bddlevel2var[i]))) {
                    if (any) sb.append(", ");
                    any = true;
                    sb.append(bddlevel2var[i]);
                    sb.append('=');
                    //if (ZDD)
                    //    sb.append(bddlevel2var[LEVEL(result[i])]);
                    //else
                    {
                        BDD b = makeBDD(result[i]);
                        sb.append(b);
                        b.free();
                    }
                }
            }
            sb.append('}');
            return sb.toString();
        }
    }

    /* (non-Javadoc)
    * @see net.sf.javabdd.BDDFactory#makePair()
    */

    public BDDPairing makePair() {
        BddPair p = new BddPair();
        p.result = new int[bddvarnum];
        int n;
        for (n = 0; n < bddvarnum; n++)
            if (ZDD)
                p.result[n] = bdd_addref(zdd_makenode(n, 0, 1));
            else
                p.result[n] = bdd_ithvar(bddlevel2var[n]);

        p.id = update_pairsid();
        p.last = -1;

        bdd_register_pair(p);
        return p;
    }

    // Redirection functions.

    protected void addref_impl(int v) { bdd_addref(v); }

    protected void delref_impl(int v) { bdd_delref(v); }

    protected int zero_impl() { return BDDZERO; }

    protected int one_impl() { return BDDONE; }

    protected int universe_impl() { return univ; }

    protected int invalid_bdd_impl() { return INVALID_BDD; }

    protected int var_impl(int v) { return bdd_var(v); }

    protected int level_impl(int v) { return getLevel(v); }

    protected int low_impl(int v) { return bdd_low(v); }

    protected int high_impl(int v) { return bdd_high(v); }

    protected int ithVar_impl(int var) { return bdd_ithvar(var); }

    protected int nithVar_impl(int var) { return bdd_nithvar(var); }

    protected int makenode_impl(int lev, int lo, int hi) {
        if (ZDD)
            return zdd_makenode(lev, lo, hi);
        else
            return bdd_makenode(lev, lo, hi);
    }

    protected int ite_impl(int v1, int v2, int v3) {
        return bdd_ite(v1, v2, v3);
    }

    protected int apply_impl(int v1, int v2, BDDOp opr) {
        return bdd_apply(v1, v2, opr.id);
    }

    protected int not_impl(int v1) { return bdd_not(v1); }

    protected int applyAll_impl(int v1, int v2, BDDOp opr, int v3) { return bdd_appall(v1, v2, opr.id, v3); }

    protected int applyEx_impl(int v1, int v2, BDDOp opr, int v3) { return bdd_appex(v1, v2, opr.id, v3); }

    protected int applyUni_impl(int v1, int v2, BDDOp opr, int v3) { return bdd_appuni(v1, v2, opr.id, v3); }

    protected int compose_impl(int v1, int v2, int var) { return bdd_compose(v1, v2, var); }

    protected int constrain_impl(int v1, int v2) { return bdd_constrain(v1, v2); }

    protected int restrict_impl(int v1, int v2) { return bdd_restrict(v1, v2); }

    protected int simplify_impl(int v1, int v2) { return bdd_simplify(v1, v2); }

    protected int support_impl(int v) { return bdd_support(v); }

    protected int exist_impl(int v1, int v2) { return bdd_exist(v1, v2); }

    protected int forAll_impl(int v1, int v2) { return bdd_forall(v1, v2); }

    protected int unique_impl(int v1, int v2) { return bdd_unique(v1, v2); }

    protected int fullSatOne_impl(int v) { return bdd_fullsatone(v); }

    protected int replace_impl(int v, BDDPairing p) { return bdd_replace(v, (BddPair) p); }

    protected int veccompose_impl(int v, BDDPairing p) { return bdd_veccompose(v, (BddPair) p); }

    protected int nodeCount_impl(int v) { return bdd_nodecount(v); }

    protected double pathCount_impl(int v) { return bdd_pathcount(v); }

    protected double satCount_impl(int v) { return bdd_satcount(v); }

    protected int satOne_impl(int v) { return bdd_satone(v); }

    protected int satOne_impl2(int v1, int v2, boolean pol) { return bdd_satoneset(v1, v2, pol); }

    protected int nodeCount_impl2(int[] v) { return bdd_anodecount(v); }

    protected int[] varProfile_impl(int v) { return bdd_varprofile(v); }

    protected void printTable_impl(int v) { bdd_fprinttable(System.out, v); }

    // More redirection functions.

    protected void initialize(int initnodesize, int cs) { bdd_init(initnodesize, cs); }

    public void addVarBlock(int first, int last, boolean fixed) { bdd_intaddvarblock(first, last, fixed); }

    public void varBlockAll() { bdd_varblockall(); }

    public void clearVarBlocks() { bddClrVarBlocks(); }

    public void printOrder() { bdd_fprintorder(System.out); }

    public int getNodeTableSize() { return bdd_getallocnum(); }

    public int setNodeTableSize(int size) { return bdd_setallocnum(size); }

    public int setCacheSize(int v) { return bdd_setcachesize(v); }

    public boolean isZDD() { return ZDD; }

    public boolean isInitialized() { return bddrunning; }

    public void close() {
        super.close();
        bdd_done();
    }

    public void setError(int code) { bdderrorcond = code; }

    public void clearError() { bdderrorcond = 0; }

    public int setMaxNodeNum(int size) { return bdd_setmaxnodenum(size); }

    public double setMinFreeNodes(double x) { return bdd_setminfreenodes((int) (x * 100.)) / 100.; }

    public int setMaxIncrease(int x) { return bdd_setmaxincrease(x); }

    public double setIncreaseFactor(double x) { return bdd_setincreasefactor(x); }

    public int getNodeNum() { return bdd_getnodenum(); }

    public int getCacheSize() { return cachesize; }

    public int reorderGain() { return bdd_reorder_gain(); }

    public void printStat() { bdd_fprintstat(System.out); }

    public double setCacheRatio(double x) { return bdd_setcacheratio((int) x); }

    public int varNum() { return bdd_varnum(); }

    public int setVarNum(int num) { return bdd_setvarnum(num); }

    public void printAll() { bdd_fprintall(System.out); }
//    public BDD load(BufferedReader in, int[] translate) throws IOException { return makeBDD(bdd_load(in, translate)); }
//    public void save(BufferedWriter out, BDD b) throws IOException { bdd_save(out, unwrap(b)); }

    public void setVarOrder(int[] neworder) { bddSetVarOrder(neworder); }

    public int level2Var(int level) { return bddlevel2var[level]; }

    public int var2Level(int var) { return bddvar2level[var]; }

    public int getReorderTimes() { return bddreordertimes; }

    public void disableReorder() { bdd_disable_reorder(); }

    public void enableReorder() { bdd_enable_reorder(); }

    public int reorderVerbose(int v) { return bdd_reorder_verbose(v); }

    public void reorder(ReorderMethod m) {
        bdd_reorder(m.id); 
    }

    public void autoReorder(ReorderMethod method) { bdd_autoreorder(method.id); }

    public void autoReorder(ReorderMethod method, int max) { bdd_autoreorder_times(method.id, max); }

    public void swapVar(int v1, int v2) { bdd_swapvar(v1, v2); }

    public ReorderMethod getReorderMethod() {
        switch (bddreordermethod) {
            case BDD_REORDER_NONE:
                return REORDER_NONE;
            case BDD_REORDER_WIN2:
                return REORDER_WIN2;
            case BDD_REORDER_WIN2ITE:
                return REORDER_WIN2ITE;
            case BDD_REORDER_WIN3:
                return REORDER_WIN3;
            case BDD_REORDER_WIN3ITE:
                return REORDER_WIN3ITE;
            case BDD_REORDER_SIFT:
                return REORDER_SIFT;
            case BDD_REORDER_SIFTITE:
                return REORDER_SIFTITE;
            case BDD_REORDER_RANDOM:
                return REORDER_RANDOM;
            default:
                throw new BDDException();
        }
    }

    // Experimental functions.

    public void validateAll() { bdd_validate_all(); }

    public void validateBDD(BDD b) { bdd_validate(unwrap(b)); }

//    public JFactory cloneFactory() {
//        JFactory INSTANCE = new JFactory();
//        if (applycache != null)
//            INSTANCE.applycache = this.applycache.copy();
//        if (itecache != null)
//            INSTANCE.itecache = this.itecache.copy();
//        if (quantcache != null)
//            INSTANCE.quantcache = this.quantcache.copy();
//        INSTANCE.appexcache = this.appexcache.copy();
//        if (replacecache != null)
//            INSTANCE.replacecache = this.replacecache.copy();
//        if (misccache != null)
//            INSTANCE.misccache = this.misccache.copy();
//        if (countcache != null)
//            INSTANCE.countcache = this.countcache.copy();
//        // TODO: potential difference here (!)
//        INSTANCE.rng = new Random();
//        INSTANCE.verbose = this.verbose;
//        INSTANCE.cachestats.copyFrom(this.cachestats);
//
//        INSTANCE.bddrunning = this.bddrunning;
//        INSTANCE.bdderrorcond = this.bdderrorcond;
//        INSTANCE.bddnodesize = this.bddnodesize;
//        INSTANCE.bddmaxnodesize = this.bddmaxnodesize;
//        INSTANCE.bddmaxnodeincrease = this.bddmaxnodeincrease;
//        INSTANCE.bddfreepos = this.bddfreepos;
//        INSTANCE.bddfreenum = this.bddfreenum;
//        INSTANCE.bddproduced = this.bddproduced;
//        INSTANCE.bddvarnum = this.bddvarnum;
//
//        INSTANCE.gbcollectnum = this.gbcollectnum;
//        INSTANCE.cachesize = this.cachesize;
//        INSTANCE.gbcclock = this.gbcclock;
//        INSTANCE.usednodes_nextreorder = this.usednodes_nextreorder;
//
//        INSTANCE.bddrefstacktop = this.bddrefstacktop;
//        INSTANCE.bddresized = this.bddresized;
//        INSTANCE.minfreenodes = this.minfreenodes;
//        INSTANCE.bddnodes = new int[this.bddnodes.length];
//        System.arraycopy(this.bddnodes, 0, INSTANCE.bddnodes, 0, this.bddnodes.length);
//        INSTANCE.bddrefstack = new int[this.bddrefstack.length];
//        System.arraycopy(this.bddrefstack, 0, INSTANCE.bddrefstack, 0, this.bddrefstack.length);
//        INSTANCE.bddvar2level = new int[this.bddvar2level.length];
//        System.arraycopy(this.bddvar2level, 0, INSTANCE.bddvar2level, 0, this.bddvar2level.length);
//        INSTANCE.bddlevel2var = new int[this.bddlevel2var.length];
//        System.arraycopy(this.bddlevel2var, 0, INSTANCE.bddlevel2var, 0, this.bddlevel2var.length);
//        INSTANCE.bddvarset = new int[this.bddvarset.length];
//        System.arraycopy(this.bddvarset, 0, INSTANCE.bddvarset, 0, this.bddvarset.length);
//
//        INSTANCE.domain = new BDDDomain[this.domain.length];
//        for (int i = 0; i < INSTANCE.domain.length; ++i) {
//            INSTANCE.domain[i] = INSTANCE.createDomain(i, this.domain[i].realsize);
//        }
//        return INSTANCE;
//    }

    /**
     * Use this function to translate BDD's from a JavaFactory into its clone.
     * This will only work immediately after cloneFactory() is called, and
     * before any other BDD operations are performed. 
     *
     * @param that BDD in old factory
     * @return a BDD in the new factory
     */
    public BDD copyNode(BDD that) {
        return makeBDD(unwrap(that));
    }

    public void reverseAllDomains() {
        reorderInit();
        for (int i = 0; i < fdvarnum; ++i) {
            reverseDomain0(domain[i]);
        }
        reorder_done();
    }

    public void reverseDomain(BDDDomain d) {
        reorderInit();
        reverseDomain0(d);
        reorder_done();
    }

    protected void reverseDomain0(BDDDomain d) {
        int n = d.varNum();
        BddTree[] trees = new BddTree[n];
        int v = d.ivar[0];
        addVarBlock(v, v, true);
        trees[0] = getBlock(vartree, v, v);
        BddTree parent = getParent(trees[0]);
        for (int i = 1; i < n; ++i) {
            v = d.ivar[i];
            addVarBlock(v, v, true);
            trees[i] = getBlock(vartree, v, v);
            BddTree parent2 = getParent(trees[i]);
            if (parent != parent2) {
                throw new BDDException();
            }
        }
        for (int i = 0; i < n; ++i) {
            for (int j = i + 1; j < n; ++j) {
                blockdown(trees[i]);
            }
        }
        BddTree newchild = trees[n - 1];
        while (newchild.prev != null) newchild = newchild.prev;
        if (parent == null) vartree = newchild;
        else parent.nextlevel = newchild;
    }


    /**
     * <p>Set the variable order to be the given list of domains.</p>
     *
     * @param domains  domain order
     */
    public void setVarOrder(List domains) {
        BddTree[] my_vartree = new BddTree[fdvarnum];
        boolean[] interleaved = new boolean[fdvarnum];
        int k = 0;
        for (Iterator i = domains.iterator(); i.hasNext();) {
            Object o = i.next();
            Collection c;
            if (o instanceof BDDDomain) c = Collections.singleton(o);
            else c = (Collection) o;
            for (Iterator j = c.iterator(); j.hasNext();) {
                BDDDomain d = (BDDDomain) j.next();
                int low = d.ivar[0];
                int high = d.ivar[d.ivar.length - 1];
                bdd_intaddvarblock(low, high, false);
                BddTree b = getBlock(vartree, low, high);
                my_vartree[k] = b;
                interleaved[k] = j.hasNext();
                k++;
            }
        }
        if (k <= 1) return;
        BddTree parent = getParent(my_vartree[0]);
        for (int i = 0; i < k; ++i) {
            if (parent != getParent(my_vartree[i])) {
                throw new BDDException("var block " + my_vartree[i].firstVar + ".." + my_vartree[i].lastVar + " is in wrong place in tree");
            }
        }
        reorderInit();
        BddTree prev = null;
        boolean prev_interleaved = false;
        for (int i = 0; i < k; ++i) {
            BddTree t = my_vartree[i];
            while (t.prev != prev) {
                blockdown(t.prev);
            }
            boolean inter = interleaved[i];
            if (prev_interleaved) {
                blockinterleave(t.prev);
                //++i;
                prev = t.prev;
            } else {
                prev = t;
            }
            prev_interleaved = inter;
        }
        BddTree newchild = my_vartree[0];
        assert (newchild.prev == null);
        //while (newchild.prev != null) newchild = newchild.prev;
        if (parent == null) vartree = newchild;
        else parent.nextlevel = newchild;
        reorder_done();
    }

    protected BddTree getParent(BddTree child) {
        for (BddTree p = vartree; p != null; p = p.next) {
            if (p == child) return null;
            BddTree q = getParent(p, child);
            if (q != null) return q;
        }
        throw new BDDException("Cannot find tree node " + child);
    }

    protected BddTree getParent(BddTree parent, BddTree child) {
        if (parent.nextlevel == null) return null;
        for (BddTree p = parent.nextlevel; p != null; p = p.next) {
            if (p == child) return parent;
            BddTree q = getParent(p, child);
            if (q != null) return q;
        }
        return null;
    }

    protected BddTree getBlock(BddTree t, int low, int high) {
        if (t == null) return null;
        for (BddTree p = t; p != null; p = p.next) {
            if (p.firstVar == low && p.lastVar == high) return p;
            BddTree q = getBlock(p.nextlevel, low, high);
            if (q != null) return q;
        }
        return null;
    }

    /***** IMPLEMENTATION BELOW *****/

    static final int REF_MASK = 0xFFC00000;
    static final int MARK_MASK = 0x00200000;
    static final int LEV_MASK = 0x001FFFFF;
    static final int MAXVAR = LEV_MASK;
    static final int INVALID_BDD = -1;

    static final int REF_INC = 0x00400000;

    static final int offset__refcou_and_level = 0;
    static final int offset__low = 1;
    static final int offset__high = 2;
    static final int offset__hash = 3;
    static final int offset__next = 4;
    static final int __node_size = 5;

    private final boolean hasRef(int node) {
        boolean r = (bddNodes[node * __node_size + offset__refcou_and_level] & REF_MASK) != 0;
        return r;
    }

    private final void setMaxRef(int node) {
        bddNodes[node * __node_size + offset__refcou_and_level] |= REF_MASK;
    }

    private final void clearRef(int node) {
        bddNodes[node * __node_size + offset__refcou_and_level] &= ~REF_MASK;
    }

    private final void incrRef(int node) {
        if ((bddNodes[node * __node_size + offset__refcou_and_level] & REF_MASK) != REF_MASK)
            bddNodes[node * __node_size + offset__refcou_and_level] += REF_INC;
    }

    private final void decrRef(int node) {
        int rc = bddNodes[node * __node_size + offset__refcou_and_level] & REF_MASK;
        if (rc != REF_MASK && rc != 0)
            bddNodes[node * __node_size + offset__refcou_and_level] -= REF_INC;
    }

    private final int getRef(int node) {
        return bddNodes[node * __node_size + offset__refcou_and_level] >>> 22;
    }

    private final int getLevel(int node) {
        return bddNodes[node * __node_size + offset__refcou_and_level] & LEV_MASK;
    }

    private final int getLevelAndMark(int node) {
        return bddNodes[node * __node_size + offset__refcou_and_level] & (LEV_MASK | MARK_MASK);
    }

    private final void setLevel(int node, int val) {
        assert (val == (val & LEV_MASK));
        bddNodes[node * __node_size + offset__refcou_and_level] &= ~LEV_MASK;
        bddNodes[node * __node_size + offset__refcou_and_level] |= val;
    }

    private final void setLevelAndMark(int node, int val) {
        assert (val == (val & (LEV_MASK | MARK_MASK)));
        bddNodes[node * __node_size + offset__refcou_and_level] &= ~(LEV_MASK | MARK_MASK);
        bddNodes[node * __node_size + offset__refcou_and_level] |= val;
    }

    private final void setMark(int n) {
        bddNodes[n * __node_size + offset__refcou_and_level] |= MARK_MASK;
    }

    private final void unmark(int n) {
        bddNodes[n * __node_size + offset__refcou_and_level] &= ~MARK_MASK;
    }

    private final boolean mark(int n) {
        return (bddNodes[n * __node_size + offset__refcou_and_level] & MARK_MASK) != 0;
    }

    private final int getLow(int r) {
        return bddNodes[r * __node_size + offset__low];
    }

    private final void setLow(int r, int v) {
        bddNodes[r * __node_size + offset__low] = v;
    }

    private final int getHigh(int r) {
        return bddNodes[r * __node_size + offset__high];
    }

    private final void setHigh(int r, int v) {
        bddNodes[r * __node_size + offset__high] = v;
    }

    private final int hash(int r) {
        return bddNodes[r * __node_size + offset__hash];
    }

    private final void setHash(int r, int v) {
        bddNodes[r * __node_size + offset__hash] = v;
    }

    private final int next(int r) {
        return bddNodes[r * __node_size + offset__next];
    }

    private final void setNext(int r, int v) {
        bddNodes[r * __node_size + offset__next] = v;
    }

    private final int VARr(int n) {
        return getLevelAndMark(n);
    }

    void SETVARr(int n, int val) {
        setLevelAndMark(n, val);
    }


    private abstract static class BddCacheData implements Serializable {
        int a, b, c;

        abstract BddCacheData copy();
    }

    private static class BddCacheDataI extends BddCacheData {
        int res;

        BddCacheData copy() {
            BddCacheDataI that = new BddCacheDataI();
            that.a = this.a;
            that.b = this.b;
            that.c = this.c;
            that.res = this.res;
            return that;
        }
    }

    private static class BddCacheDataD extends BddCacheData {
        long dres;

        BddCacheData copy() {
            BddCacheDataD that = new BddCacheDataD();
            that.a = this.a;
            that.b = this.b;
            that.c = this.c;
            that.dres = this.dres;
            return that;
        }
    }

    private static class BddCache implements Serializable {
        BddCacheData table[];
        int tablesize;

        BddCache copy() {
            BddCache that = new BddCache();
            boolean is_d = this.table instanceof BddCacheDataD[];
            if (is_d) {
                that.table = new BddCacheDataD[this.table.length];
            } else {
                that.table = new BddCacheDataI[this.table.length];
            }
            that.tablesize = this.tablesize;
            for (int i = 0; i < table.length; ++i) {
                that.table[i] = this.table[i].copy();
            }
            return that;
        }
    }

    private static class JavaBDDException extends BDDException {
        /**
         * Version ID for serialization.
         */
        private static final long serialVersionUID = 3257289144995952950L;

        public JavaBDDException(int x) {
            super(errorstrings[-x]);
        }
    }

    private static class ReorderException extends RuntimeException {
        /**
         * Version ID for serialization.
         */
        private static final long serialVersionUID = 3256727264505772345L;
    }

    static final int bddtrue = 1;
    static final int bddfalse = 0;

    static final int BDDONE = 1;
    static final int BDDZERO = 0;

    boolean bddrunning; /* Flag - package initialized */
    int bdderrorcond; /* Some error condition */
    int bddNodeSize; /* Number of allocated nodes */
    int bddmaxnodesize; /* Maximum allowed number of nodes */
    int bddmaxnodeincrease; /* Max. # of nodes used to inc. table */

    int[] bddNodes; /* All of the bdd nodes */

    int bddfreepos; /* First free node */
    int bddfreenum; /* Number of free nodes */
    int bddproduced; /* Number of new nodes ever produced */
    int bddvarnum; /* Number of defined BDD variables */
    int[] bddrefstack; /* Internal node reference stack */
    int bddrefstacktop; /* Internal node reference stack top */

    int[] bddvar2level; /* Variable -> level table */
    int[] bddlevel2var; /* Level -> variable table */

    boolean bddresized; /* Flag indicating a resize of the nodetable */

    int minFreeNodes = 20;

    /*=== PRIVATE KERNEL VARIABLES =========================================*/

    int[] bddvarset; /* Set of defined BDD variables */
    int univ = 1; /* Universal set (used for ZDD) */
    int gbcollectnum; /* Number of garbage collections */
    int cachesize; /* Size of the operator caches */
    long gbcclock; /* Clock ticks used in GBC */
    int usednodes_nextreorder; /* When to do reorder next time */

    static final int BDD_MEMORY = (-1); /* Out of memory */
    static final int BDD_VAR = (-2); /* Unknown variable */
    static final int BDD_RANGE = (-3); /* Variable value out of range (not in domain) */
    static final int BDD_DEREF = (-4); /* Removing external reference to unknown node */
    static final int BDD_RUNNING = (-5); /* Called bdd_init() twice without bdd_done() */
    static final int BDD_FILE = (-6); /* Some file operation failed */
    static final int BDD_FORMAT = (-7); /* Incorrect file format */
    static final int BDD_ORDER = (-8); /* Vars. not in order for vector based functions */
    static final int BDD_BREAK = (-9); /* User called break */
    static final int BDD_VARNUM = (-10); /* Different number of vars. for vector pair */
    static final int BDD_NODES = (-11); /* Tried to set max. number of nodes to be fewer */
    /* than there already has been allocated */
    static final int BDD_OP = (-12); /* Unknown operator */
    static final int BDD_VARSET = (-13); /* Illegal variable set */
    static final int BDD_VARBLK = (-14); /* Bad variable block operation */
    static final int BDD_DECVNUM = (-15); /* Trying to decrease the number of variables */
    static final int BDD_REPLACE = (-16); /* Replacing to already existing variables */
    static final int BDD_NODENUM = (-17); /* Number of nodes reached user defined maximum */
    static final int BDD_ILLBDD = (-18); /* Illegal bdd argument */
    static final int BDD_SIZE = (-19); /* Illegal size argument */

    static final int BVEC_SIZE = (-20); /* Mismatch in bitvector size */
    static final int BVEC_SHIFT = (-21); /* Illegal shift-left/right parameter */
    static final int BVEC_DIVZERO = (-22); /* Division by zero */

    static final int BDD_ERRNUM = 24;

    /* Strings for all error mesages */
    static String errorstrings[] =
            {
                    "",
                    "Out of memory",
                    "Unknown variable",
                    "Value out of range",
                    "Unknown BDD root dereferenced",
                    "bdd_init() called twice",
                    "File operation failed",
                    "Incorrect file format",
                    "Variables not in ascending order",
                    "User called break",
                    "Mismatch in size of variable sets",
                    "Cannot allocate fewer nodes than already in use",
                    "Unknown operator",
                    "Illegal variable set",
                    "Bad variable block operation",
                    "Trying to decrease the number of variables",
                    "Trying to replace with variables already in the bdd",
                    "Number of nodes reached user defined maximum",
                    "Unknown BDD - was not in node table",
                    "Bad size argument",
                    "Mismatch in bitvector size",
                    "Illegal shift-left/right parameter",
                    "Division by zero"};

    static final int DEFAULTMAXNODEINC = 10000000;

    /*=== OTHER INTERNAL DEFINITIONS =======================================*/

    static final int PAIR(int a, int b) {
        //return Math.abs((a + b) * (a + b + 1) / 2 + a);
        return ((a + b) * (a + b + 1) / 2 + a);
    }

    static final int TRIPLE(int a, int b, int c) {
        //return Math.abs(PAIR(c, PAIR(a, b)));
        return (PAIR(c, PAIR(a, b)));
    }

    final int NODEHASH(int lvl, int l, int h) {
        return Math.abs(TRIPLE(lvl, l, h) % bddNodeSize);
    }

    int bdd_ithvar(int var) {
        if (var < 0 || var >= bddvarnum) {
            bdd_error(BDD_VAR);
            return bddfalse;
        }

        return bddvarset[var * 2];
    }

    int bdd_nithvar(int var) {
        if (var < 0 || var >= bddvarnum) {
            bdd_error(BDD_VAR);
            return bddfalse;
        }

        return bddvarset[var * 2 + 1];
    }

    int bdd_varnum() {
        return bddvarnum;
    }

    static int bdd_error(int v) {
        throw new JavaBDDException(v);
    }

    static boolean ISZERO(int r) {
        return r == bddfalse;
    }

    static boolean ISONE(int r) {
        return r == bddtrue;
    }

    static boolean ISCONST(int r) {
        //return r == bddfalse || r == bddtrue;
        return r < 2;
    }

    void checkNode(int r) {
        assert bddrunning;
        assert !(r < 0 || r >= bddNodeSize);
        assert !(r >= 2 && getLow(r) == INVALID_BDD);

//        if (!bddrunning)
//            bdd_error(BDD_RUNNING);
//        else if (r < 0 || r >= bddnodesize)
//            bdd_error(BDD_ILLBDD);
//        else if (r >= 2 && LOW(r) == INVALID_BDD)
//            bdd_error(BDD_ILLBDD);
    }

    void CHECKa(int r, int x) {
        checkNode(r);
    }

    int bdd_var(int root) {
        checkNode(root);
        if (root < 2)
            bdd_error(BDD_ILLBDD);

        return (bddlevel2var[getLevel(root)]);
    }

    int bdd_low(int root) {
        checkNode(root);
        if (root < 2)
            return bdd_error(BDD_ILLBDD);

        return (getLow(root));
    }

    int bdd_high(int root) {
        checkNode(root);
        if (root < 2)
            return bdd_error(BDD_ILLBDD);

        return (getHigh(root));
    }

    void checkResize() {
        if (bddresized) {
            System.out.println("DAVE-bddresized");
            bdd_operator_noderesize();
        }
        bddresized = false;
    }

    static final int NOTHASH(int r) {
        return r;
    }

    static final int APPLYHASH(int l, int r, int op) {
        return TRIPLE(l, r, op);
    }

    static final int ITEHASH(int f, int g, int h) {
        return TRIPLE(f, g, h);
    }

    static final int RESTRHASH(int r, int var) {
        return PAIR(r, var);
    }

    static final int CONSTRAINHASH(int f, int c) {
        return PAIR(f, c);
    }

    static final int QUANTHASH(int r) {
        return r;
    }

    static final int REPLACEHASH(int r) {
        return r;
    }

    static final int VECCOMPOSEHASH(int f) {
        return f;
    }

    static final int COMPOSEHASH(int f, int g) {
        return PAIR(f, g);
    }

    static final int SATCOUHASH(int r) {
        return r;
    }

    static final int PATHCOUHASH(int r) {
        return r;
    }

    static final int APPEXHASH(int l, int r, int op) {
        return PAIR(l, r);
    }

    static final double M_LN2 = 0.69314718055994530942;

    static double log1p(double a) {
        return Math.log(1.0 + a);
    }

    final boolean INVARSET(int a) {
        return (quantvarset[a] == quantvarsetID); /* unsigned check */
    }

    final boolean INSVARSET(int a) {
        return Math.abs(quantvarset[a]) == quantvarsetID; /* signed check */
    }

    static final int bddop_and = 0;
    static final int bddop_xor = 1;
    static final int bddop_or = 2;
    static final int bddop_nand = 3;
    static final int bddop_nor = 4;
    static final int bddop_imp = 5;
    static final int bddop_biimp = 6;
    static final int bddop_diff = 7;
    static final int bddop_less = 8;
    static final int bddop_invimp = 9;

    /* Should *not* be used in bdd_apply calls !!! */
    static final int bddop_not = 10;
    static final int bddop_simplify = 11;

    int bdd_not(int r) {
        int res;
        int numReorder = 1;
        CHECKa(r, bddfalse);

        if (applycache == null) applycache = BddCacheI_init(cachesize);
        again:
        for (; ;) {
            try {
                initRef();

                if (numReorder == 0) bdd_disable_reorder();
                if (ZDD) res = zdiff_rec(univ, r);
                else res = not_rec(r);
                if (numReorder == 0) bdd_enable_reorder();
            } catch (ReorderException x) {
                bdd_checkReorder();
                numReorder--;
                continue again;
            }
            break;
        }

        checkResize();
        return res;
    }

    int not_rec(int r) {
        BddCacheDataI entry;
        int res;

        if (ISCONST(r))
            return 1 - r;

        entry = BddCache_lookupI(applycache, NOTHASH(r));

        if (entry.a == r && entry.c == bddop_not) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        PUSHREF(not_rec(getLow(r)));
        PUSHREF(not_rec(getHigh(r)));
        res = bdd_makenode(getLevel(r), READREF(2), READREF(1));
        POPREF(2);

        entry.a = r;
        entry.c = bddop_not;
        entry.res = res;

        return res;
    }

    int bdd_ite(int f, int g, int h) {
        int res;
        int numReorder = 1;

        CHECKa(f, bddfalse);
        CHECKa(g, bddfalse);
        CHECKa(h, bddfalse);

        if (applycache == null) applycache = BddCacheI_init(cachesize);
        if (itecache == null) itecache = BddCacheI_init(cachesize);

        again:
        for (; ;) {
            try {
                initRef();

                if (numReorder == 0) bdd_disable_reorder();
                res = ZDD ? zite_rec(f, g, h) : ite_rec(f, g, h);
                if (numReorder == 0) bdd_enable_reorder();
            } catch (ReorderException x) {
                bdd_checkReorder();
                numReorder--;
                continue again;
            }
            break;
        }

        checkResize();
        return res;
    }

    int ite_rec(int f, int g, int h) {
        BddCacheDataI entry;
        int res;

        if (ISONE(f))
            return g;
        if (ISZERO(f))
            return h;
        if (g == h)
            return g;
        if (ISONE(g) && ISZERO(h))
            return f;
        if (ISZERO(g) && ISONE(h))
            return not_rec(f);

        entry = BddCache_lookupI(itecache, ITEHASH(f, g, h));
        if (entry.a == f && entry.b == g && entry.c == h) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        if (getLevel(f) == getLevel(g)) {
            if (getLevel(f) == getLevel(h)) {
                PUSHREF(ite_rec(getLow(f), getLow(g), getLow(h)));
                PUSHREF(ite_rec(getHigh(f), getHigh(g), getHigh(h)));
                res = bdd_makenode(getLevel(f), READREF(2), READREF(1));
            } else if (getLevel(f) < getLevel(h)) {
                PUSHREF(ite_rec(getLow(f), getLow(g), h));
                PUSHREF(ite_rec(getHigh(f), getHigh(g), h));
                res = bdd_makenode(getLevel(f), READREF(2), READREF(1));
            } else /* f > h */ {
                PUSHREF(ite_rec(f, g, getLow(h)));
                PUSHREF(ite_rec(f, g, getHigh(h)));
                res = bdd_makenode(getLevel(h), READREF(2), READREF(1));
            }
        } else if (getLevel(f) < getLevel(g)) {
            if (getLevel(f) == getLevel(h)) {
                PUSHREF(ite_rec(getLow(f), g, getLow(h)));
                PUSHREF(ite_rec(getHigh(f), g, getHigh(h)));
                res = bdd_makenode(getLevel(f), READREF(2), READREF(1));
            } else if (getLevel(f) < getLevel(h)) {
                PUSHREF(ite_rec(getLow(f), g, h));
                PUSHREF(ite_rec(getHigh(f), g, h));
                res = bdd_makenode(getLevel(f), READREF(2), READREF(1));
            } else /* f > h */ {
                PUSHREF(ite_rec(f, g, getLow(h)));
                PUSHREF(ite_rec(f, g, getHigh(h)));
                res = bdd_makenode(getLevel(h), READREF(2), READREF(1));
            }
        } else /* f > g */ {
            if (getLevel(g) == getLevel(h)) {
                PUSHREF(ite_rec(f, getLow(g), getLow(h)));
                PUSHREF(ite_rec(f, getHigh(g), getHigh(h)));
                res = bdd_makenode(getLevel(g), READREF(2), READREF(1));
            } else if (getLevel(g) < getLevel(h)) {
                PUSHREF(ite_rec(f, getLow(g), h));
                PUSHREF(ite_rec(f, getHigh(g), h));
                res = bdd_makenode(getLevel(g), READREF(2), READREF(1));
            } else /* g > h */ {
                PUSHREF(ite_rec(f, g, getLow(h)));
                PUSHREF(ite_rec(f, g, getHigh(h)));
                res = bdd_makenode(getLevel(h), READREF(2), READREF(1));
            }
        }

        POPREF(2);

        entry.a = f;
        entry.b = g;
        entry.c = h;
        entry.res = res;

        return res;
    }

    int zite_rec(int f, int g, int h) {
        BddCacheDataI entry;
        int res;

        if (ISONE(f))
            return g;
        if (ISZERO(f))
            return h;
        if (g == h)
            return g;
        if (ISONE(g) && ISZERO(h))
            return f;
        if (ISZERO(g) && ISONE(h))
            return zdiff_rec(univ, f);

        int v = Math.min(getLevel(g), getLevel(h));
        if (getLevel(f) < v)
            return zite_rec(getLow(f), g, h);

        entry = BddCache_lookupI(itecache, ITEHASH(f, g, h));
        if (entry.a == f && entry.b == g && entry.c == h) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        if (getLevel(f) == getLevel(g)) {
            if (getLevel(f) == getLevel(h)) {
                PUSHREF(zite_rec(getLow(f), getLow(g), getLow(h)));
                PUSHREF(zite_rec(getHigh(f), getHigh(g), getHigh(h)));
                res = zdd_makenode(getLevel(f), READREF(2), READREF(1));
                POPREF(2);
            } else if (getLevel(f) < getLevel(h)) {
                PUSHREF(zite_rec(getLow(f), getLow(g), h));
                PUSHREF(zite_rec(getHigh(f), getHigh(g), 0));
                res = zdd_makenode(getLevel(f), READREF(2), READREF(1));
                POPREF(2);
            } else /* f > h */ {
                PUSHREF(zite_rec(f, g, getLow(h)));
                res = zdd_makenode(getLevel(h), getHigh(h), READREF(1));
                POPREF(1);
            }
        } else if (getLevel(f) < getLevel(g)) {
            if (getLevel(f) == getLevel(h)) {
                PUSHREF(zite_rec(getLow(f), g, getLow(h)));
                PUSHREF(zite_rec(getHigh(f), 0, getHigh(h)));
                res = zdd_makenode(getLevel(f), READREF(2), READREF(1));
                POPREF(2);
            } else if (getLevel(f) < getLevel(h)) {
                res = zite_rec(getLow(f), g, h);
            } else /* f > h */ {
                PUSHREF(zite_rec(f, g, getLow(h)));
                res = zdd_makenode(getLevel(h), getHigh(h), READREF(1));
                POPREF(1);
            }
        } else /* f > g */ {
            if (getLevel(g) == getLevel(h)) {
                PUSHREF(zite_rec(f, getLow(g), getLow(h)));
                res = zdd_makenode(getLevel(g), getHigh(h), READREF(1));
                POPREF(1);
            } else if (getLevel(g) < getLevel(h)) {
                PUSHREF(zite_rec(f, getLow(g), h));
                res = zdd_makenode(getLevel(g), 0, READREF(1));
                POPREF(1);
            } else /* g > h */ {
                PUSHREF(zite_rec(f, g, getLow(h)));
                res = zdd_makenode(getLevel(h), getHigh(h), READREF(1));
                POPREF(1);
            }
        }

        entry.a = f;
        entry.b = g;
        entry.c = h;
        entry.res = res;

        return res;
    }

    int bdd_replace(int r, BddPair pair) {
        int res;
        int numReorder = 1;

        CHECKa(r, bddfalse);

        if (replacecache == null) replacecache = BddCacheI_init(cachesize);
        if (ZDD && applycache == null) applycache = BddCacheI_init(cachesize);

        again:
        for (; ;) {
            try {
                initRef();
                replacepair = pair.result;
                replacelast = pair.last;
                replaceid = (pair.id << 2) | CACHEID_REPLACE;
                if (ZDD) applyop = bddop_or;

                if (numReorder == 0) bdd_disable_reorder();
                res = replace_rec(r);
                if (numReorder == 0) bdd_enable_reorder();
            } catch (ReorderException x) {
                bdd_checkReorder();
                numReorder--;
                continue again;
            }
            break;
        }

        checkResize();
        return res;
    }

    int replace_rec(int r) {
        BddCacheDataI entry;
        int res;

        if (ISCONST(r) || getLevel(r) > replacelast)
            return r;

        entry = BddCache_lookupI(replacecache, REPLACEHASH(r));
        if (entry.a == r && entry.c == replaceid) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        PUSHREF(replace_rec(getLow(r)));
        PUSHREF(replace_rec(getHigh(r)));

        if (ZDD)
            res =
                    zdd_correctify(
                            getLevel(replacepair[getLevel(r)]),
                            READREF(2),
                            READREF(1));
        else
            res =
                    bdd_correctify(
                            getLevel(replacepair[getLevel(r)]),
                            READREF(2),
                            READREF(1));
        POPREF(2);

        entry.a = r;
        entry.c = replaceid;
        entry.res = res;

        return res;
    }

    int bdd_correctify(int level, int l, int r) {
        int res;

        if (level < getLevel(l) && level < getLevel(r))
            return bdd_makenode(level, l, r);

        if (level == getLevel(l) || level == getLevel(r)) {
            bdd_error(BDD_REPLACE);
            return 0;
        }

        if (getLevel(l) == getLevel(r)) {
            PUSHREF(bdd_correctify(level, getLow(l), getLow(r)));
            PUSHREF(bdd_correctify(level, getHigh(l), getHigh(r)));
            res = bdd_makenode(getLevel(l), READREF(2), READREF(1));
        } else if (getLevel(l) < getLevel(r)) {
            PUSHREF(bdd_correctify(level, getLow(l), r));
            PUSHREF(bdd_correctify(level, getHigh(l), r));
            res = bdd_makenode(getLevel(l), READREF(2), READREF(1));
        } else {
            PUSHREF(bdd_correctify(level, l, getLow(r)));
            PUSHREF(bdd_correctify(level, l, getHigh(r)));
            res = bdd_makenode(getLevel(r), READREF(2), READREF(1));
        }
        POPREF(2);

        return res; /* FIXME: cache ? */
    }

    int zdd_correctify(int level, int l, int r) {
        int res;

        // Here's the idea: Flip the "level" bit on the one branch,
        // then "or" the result with the zero branch.
        PUSHREF(zdd_makenode(level, 0, 1));
        PUSHREF(zdd_change(r, READREF(1)));
        res = zor_rec(READREF(1), l);
        POPREF(2);

        return res;
    }

    // Flip zvar in r.

    int zdd_change(int r, int zvar) {
        int res;

        if (ISZERO(r))
            return r;
        if (ISONE(r))
            return zvar;

        if (getLevel(r) > getLevel(zvar)) {
            res = zdd_makenode(getLevel(zvar), BDDZERO, r);
        } else if (getLevel(r) == getLevel(zvar)) {
            res = zdd_makenode(getLevel(zvar), getHigh(r), getLow(r));
        } else {
            PUSHREF(zdd_change(getLow(r), zvar));
            PUSHREF(zdd_change(getHigh(r), zvar));
            res = zdd_makenode(getLevel(r), READREF(2), READREF(1));
            POPREF(2);
        }

        return res; /* FIXME: cache ? */
    }

    int bdd_apply(int l, int r, int op) {
        int retVal;

        checkNode(l);
        checkNode(r);

        assert op >= 0;
        assert op <= bddop_invimp;

        if (applycache == null) applycache = BddCacheI_init(cachesize);

        initRef();
        applyop = op;

        switch (op) {
            case bddop_and:
                retVal = andRecurse(l, r);
                break;
            case bddop_or:
                retVal = orRecurse(l, r);
                break;
            default:
                retVal = applyRecurse(l, r);
                break;
        }

        checkResize();
        return retVal;
    }

    int applyRecurse(int l, int r) {
        BddCacheDataI entry;
        int res;

        assert (!ZDD);
        assert (applyop != bddop_and && applyop != bddop_or);

        switch (applyop) {
            case bddop_xor:
                if (l == r)
                    return 0;
                if (ISZERO(l))
                    return r;
                if (ISZERO(r))
                    return l;
                break;
            case bddop_nand:
                if (ISZERO(l) || ISZERO(r))
                    return 1;
                break;
            case bddop_nor:
                if (ISONE(l) || ISONE(r))
                    return 0;
                break;
            case bddop_imp:
                if (ISZERO(l))
                    return 1;
                if (ISONE(l))
                    return r;
                if (ISONE(r))
                    return 1;
                break;
        }

        if (ISCONST(l) && ISCONST(r))
            res = oprres[applyop][l << 1 | r];
        else {
            entry = BddCache_lookupI(applycache, APPLYHASH(l, r, applyop));

            if (entry.a == l && entry.b == r && entry.c == applyop) {
                if (CACHESTATS)
                    cachestats.opHit++;
                return entry.res;
            }
            if (CACHESTATS)
                cachestats.opMiss++;

            if (getLevel(l) == getLevel(r)) {
                PUSHREF(applyRecurse(getLow(l), getLow(r)));
                PUSHREF(applyRecurse(getHigh(l), getHigh(r)));
                res = bdd_makenode(getLevel(l), READREF(2), READREF(1));
            } else if (getLevel(l) < getLevel(r)) {
                PUSHREF(applyRecurse(getLow(l), r));
                PUSHREF(applyRecurse(getHigh(l), r));
                res = bdd_makenode(getLevel(l), READREF(2), READREF(1));
            } else {
                PUSHREF(applyRecurse(l, getLow(r)));
                PUSHREF(applyRecurse(l, getHigh(r)));
                res = bdd_makenode(getLevel(r), READREF(2), READREF(1));
            }

            POPREF(2);

            entry.a = l;
            entry.b = r;
            entry.c = applyop;
            entry.res = res;
        }

        return res;
    }

    int andRecurse(int l, int r) {
        BddCacheDataI entry;
        int res;

        if (l == r)
            return l;
        if (ISZERO(l) || ISZERO(r))
            return 0;
        if (ISONE(l))
            return r;
        if (ISONE(r))
            return l;

        entry = BddCache_lookupI(applycache, APPLYHASH(l, r, bddop_and));

        if (entry.a == l && entry.b == r && entry.c == bddop_and) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        if (getLevel(l) == getLevel(r)) {
            PUSHREF(andRecurse(getLow(l), getLow(r)));
            PUSHREF(andRecurse(getHigh(l), getHigh(r)));
            res = bdd_makenode(getLevel(l), READREF(2), READREF(1));
        } else if (getLevel(l) < getLevel(r)) {
            PUSHREF(andRecurse(getLow(l), r));
            PUSHREF(andRecurse(getHigh(l), r));
            res = bdd_makenode(getLevel(l), READREF(2), READREF(1));
        } else {
            PUSHREF(andRecurse(l, getLow(r)));
            PUSHREF(andRecurse(l, getHigh(r)));
            res = bdd_makenode(getLevel(r), READREF(2), READREF(1));
        }

        POPREF(2);

        entry.a = l;
        entry.b = r;
        entry.c = bddop_and;
        entry.res = res;

        return res;
    }

    int zand_rec(int l, int r) {
        BddCacheDataI entry;
        int res;

        if (l == r)
            return l;
        if (ISZERO(l) || ISZERO(r))
            return 0;
        if (getLevel(l) < getLevel(r))
            return zand_rec(getLow(l), r);
        else if (getLevel(l) > getLevel(r))
            return zand_rec(l, getLow(r));
        assert (!ISCONST(l) && !ISCONST(r));

        entry = BddCache_lookupI(applycache, APPLYHASH(l, r, bddop_and));

        if (entry.a == l && entry.b == r && entry.c == bddop_and) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        PUSHREF(zand_rec(getLow(l), getLow(r)));
        PUSHREF(zand_rec(getHigh(l), getHigh(r)));
        res = zdd_makenode(getLevel(l), READREF(2), READREF(1));

        POPREF(2);

        entry.a = l;
        entry.b = r;
        entry.c = bddop_and;
        entry.res = res;

        return res;
    }

    int zrelprod_rec(int l, int r, int lev) {
        BddCacheDataI entry;
        int res;

        if (l == r)
            return zquant_rec(l, lev);
        if (ISZERO(l) || ISZERO(r))
            return 0;

        int LEVEL_l = getLevel(l);
        int LEVEL_r = getLevel(r);

        for (; ;) {
            if (lev > quantlast) {
                applyop = bddop_and;
                res = zand_rec(l, r);
                applyop = bddop_or;
                return res;
            }
            if (lev >= LEVEL_l || lev >= LEVEL_r)
                break;
            if (INVARSET(lev)) {
                res = zrelprod_rec(l, r, lev + 1);
                PUSHREF(res);
                res = zdd_makenode(lev, res, res);
                POPREF(1);
                return res;
            }
            ++lev;
        }

        entry = BddCache_lookupI(appexcache, APPEXHASH(l, r, bddop_and));
        if (entry.a == l && entry.b == r && entry.c == appexid) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        if (LEVEL_l == LEVEL_r) {
            assert (LEVEL_l == lev);
            PUSHREF(zrelprod_rec(getLow(l), getLow(r), lev + 1));
            PUSHREF(zrelprod_rec(getHigh(l), getHigh(r), lev + 1));
            if (INVARSET(lev)) {
                res = zor_rec(READREF(2), READREF(1));
                POPREF(2);
                PUSHREF(res);
                res = zdd_makenode(lev, res, res);
                POPREF(1);
            } else {
                res = zdd_makenode(lev, READREF(2), READREF(1));
                POPREF(2);
            }
        } else {
            if (LEVEL_l < LEVEL_r) {
                assert (LEVEL_l == lev);
                res = zrelprod_rec(getLow(l), r, lev + 1);
            } else {
                assert (LEVEL_r == lev);
                res = zrelprod_rec(l, getLow(r), lev + 1);
            }
            if (INVARSET(lev)) {
                PUSHREF(res);
                res = zdd_makenode(lev, res, res);
                POPREF(1);
            }
        }
        entry.a = l;
        entry.b = r;
        entry.c = appexid;
        entry.res = res;

        return res;
    }

    int orRecurse(int l, int r) {
        BddCacheDataI entry;
        int res;

        if (l == r)
            return l;
        if (ISONE(l) || ISONE(r))
            return 1;
        if (ISZERO(l))
            return r;
        if (ISZERO(r))
            return l;

        entry = BddCache_lookupI(applycache, APPLYHASH(l, r, bddop_or));

        if (entry.a == l && entry.b == r && entry.c == bddop_or) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        if (getLevel(l) == getLevel(r)) {
            PUSHREF(orRecurse(getLow(l), getLow(r)));
            PUSHREF(orRecurse(getHigh(l), getHigh(r)));
            res = bdd_makenode(getLevel(l), READREF(2), READREF(1));
        } else if (getLevel(l) < getLevel(r)) {
            PUSHREF(orRecurse(getLow(l), r));
            PUSHREF(orRecurse(getHigh(l), r));
            res = bdd_makenode(getLevel(l), READREF(2), READREF(1));
        } else {
            PUSHREF(orRecurse(l, getLow(r)));
            PUSHREF(orRecurse(l, getHigh(r)));
            res = bdd_makenode(getLevel(r), READREF(2), READREF(1));
        }

        POPREF(2);

        entry.a = l;
        entry.b = r;
        entry.c = bddop_or;
        entry.res = res;

        return res;
    }

    int zor_rec(int l, int r) {
        BddCacheDataI entry;
        int res;

        if (l == r)
            return l;
        //if (ISONE(l) || ISONE(r))
        //    return 1;
        if (ISZERO(l))
            return r;
        if (ISZERO(r))
            return l;
        entry = BddCache_lookupI(applycache, APPLYHASH(l, r, bddop_or));

        if (entry.a == l && entry.b == r && entry.c == bddop_or) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        if (getLevel(l) == getLevel(r)) {
            PUSHREF(zor_rec(getLow(l), getLow(r)));
            PUSHREF(zor_rec(getHigh(l), getHigh(r)));
            res = zdd_makenode(getLevel(l), READREF(2), READREF(1));
            POPREF(2);
        } else {
            if (getLevel(l) < getLevel(r)) {
                PUSHREF(zor_rec(getLow(l), r));
                res = zdd_makenode(getLevel(l), READREF(1), getHigh(l));
            } else {
                PUSHREF(zor_rec(l, getLow(r)));
                res = zdd_makenode(getLevel(r), READREF(1), getHigh(r));
            }
            POPREF(1);
        }

        entry.a = l;
        entry.b = r;
        entry.c = bddop_or;
        entry.res = res;

        return res;
    }

    int zdiff_rec(int l, int r) {
        BddCacheDataI entry;
        int res;

        if (ISZERO(l) /*|| ISONE(r)*/ || l == r)
            return 0;
        if (ISZERO(r))
            return l;
        if (getLevel(l) > getLevel(r))
            return zdiff_rec(l, getLow(r));

        entry = BddCache_lookupI(applycache, APPLYHASH(l, r, bddop_diff));

        if (entry.a == l && entry.b == r && entry.c == bddop_diff) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        if (getLevel(l) == getLevel(r)) {
            PUSHREF(zdiff_rec(getLow(l), getLow(r)));
            PUSHREF(zdiff_rec(getHigh(l), getHigh(r)));
            res = zdd_makenode(getLevel(l), READREF(2), READREF(1));
            POPREF(2);
        } else {
            PUSHREF(zdiff_rec(getLow(l), r));
            res = zdd_makenode(getLevel(l), READREF(1), getHigh(l));
            POPREF(1);
        }

        entry.a = l;
        entry.b = r;
        entry.c = bddop_diff;
        entry.res = res;

        return res;
    }

    int relprod_rec(int l, int r) {
        BddCacheDataI entry;
        int res;

        assert (!ZDD);

        if (l == 0 || r == 0)
            return 0;
        if (l == r)
            return quant_rec(l);
        if (l == 1)
            return quant_rec(r);
        if (r == 1)
            return quant_rec(l);

        int LEVEL_l = getLevel(l);
        int LEVEL_r = getLevel(r);
        if (LEVEL_l > quantlast && LEVEL_r > quantlast) {
            applyop = bddop_and;
            res = andRecurse(l, r);
            applyop = bddop_or;
        } else {
            entry = BddCache_lookupI(appexcache, APPEXHASH(l, r, bddop_and));
            if (entry.a == l && entry.b == r && entry.c == appexid) {
                if (CACHESTATS)
                    cachestats.opHit++;
                return entry.res;
            }
            if (CACHESTATS)
                cachestats.opMiss++;

            if (LEVEL_l == LEVEL_r) {
                PUSHREF(relprod_rec(getLow(l), getLow(r)));
                PUSHREF(relprod_rec(getHigh(l), getHigh(r)));
                if (INVARSET(LEVEL_l))
                    res = orRecurse(READREF(2), READREF(1));
                else
                    res = bdd_makenode(LEVEL_l, READREF(2), READREF(1));
            } else if (LEVEL_l < LEVEL_r) {
                PUSHREF(relprod_rec(getLow(l), r));
                PUSHREF(relprod_rec(getHigh(l), r));
                if (INVARSET(LEVEL_l))
                    res = orRecurse(READREF(2), READREF(1));
                else
                    res = bdd_makenode(LEVEL_l, READREF(2), READREF(1));
            } else {
                PUSHREF(relprod_rec(l, getLow(r)));
                PUSHREF(relprod_rec(l, getHigh(r)));
                if (INVARSET(LEVEL_r))
                    res = orRecurse(READREF(2), READREF(1));
                else
                    res = bdd_makenode(LEVEL_r, READREF(2), READREF(1));
            }

            POPREF(2);

            entry.a = l;
            entry.b = r;
            entry.c = appexid;
            entry.res = res;
        }

        return res;
    }

    int bdd_relprod(int a, int b, int var) {
        return bdd_appex(a, b, bddop_and, var);
    }

    int bdd_appex(int l, int r, int opr, int var) {
        int res;
        int numReorder = 1;

        CHECKa(l, bddfalse);
        CHECKa(r, bddfalse);
        CHECKa(var, bddfalse);

        if (opr < 0 || opr > bddop_invimp) {
            bdd_error(BDD_OP);
            return bddfalse;
        }

        if (var < 2) /* Empty set */
            return bdd_apply(l, r, opr);

        if (applycache == null) applycache = BddCacheI_init(cachesize);
        if (appexcache == null) appexcache = BddCacheI_init(cachesize);
        if (quantcache == null) quantcache = BddCacheI_init(cachesize);

        again:
        for (; ;) {
            if (varset2vartable(var) < 0)
                return bddfalse;
            try {
                initRef();

                applyop = bddop_or;
                appexop = opr;
                appexid = (var << 5) | (appexop << 1); /* FIXME: range! */
                quantid = (appexid << 3) | CACHEID_APPEX;

                if (numReorder == 0)
                    bdd_disable_reorder();
                if (opr == bddop_and)
                    res = ZDD ? zrelprod_rec(l, r, 0) : relprod_rec(l, r);
                else
                    res = appquant_rec(l, r);

                if (numReorder == 0)
                    bdd_enable_reorder();
            } catch (ReorderException x) {
                bdd_checkReorder();

                numReorder--;
                continue again;
            }
            break;
        }

        checkResize();
        return res;
    }

    int varset2vartable(int r) {
        int n;

        if (r < 2)
            return bdd_error(BDD_VARSET);

        quantvarsetID++;

        if (quantvarsetID == INT_MAX) {
            for (int i = 0; i < bddvarnum; ++i)
                quantvarset[i] = 0;
            quantvarsetID = 1;
        }

        quantlast = -1;
        for (n = r; n > 1; n = getHigh(n)) {
            quantvarset[getLevel(n)] = quantvarsetID;
            assert (quantlast < getLevel(n));
            quantlast = getLevel(n);
        }

        return 0;
    }

    static final int INT_MAX = Integer.MAX_VALUE;

    int varset2svartable(int r) {
        int n;

        if (r < 2)
            return bdd_error(BDD_VARSET);

        quantvarsetID++;

        if (quantvarsetID == INT_MAX / 2) {
            for (int i = 0; i < bddvarnum; ++i)
                quantvarset[i] = 0;
            quantvarsetID = 1;
        }

        quantlast = 0;
        for (n = r; !ISCONST(n);) {
            if (ISZERO(getLow(n))) {
                quantvarset[getLevel(n)] = quantvarsetID;
                n = getHigh(n);
            } else {
                quantvarset[getLevel(n)] = -quantvarsetID;
                n = getLow(n);
            }
            assert (quantlast < getLevel(n));
            quantlast = getLevel(n);
        }

        return 0;
    }

    int appquant_rec(int l, int r) {
        BddCacheDataI entry;
        int res;

        assert (appexop != bddop_and);

        switch (appexop) {
            case bddop_or:
                if (l == 1 || r == 1)
                    return 1;
                if (l == r)
                    return quant_rec(l);
                if (l == 0)
                    return quant_rec(r);
                if (r == 0)
                    return quant_rec(l);
                break;
            case bddop_xor:
                if (l == r)
                    return 0;
                if (l == 0)
                    return quant_rec(r);
                if (r == 0)
                    return quant_rec(l);
                break;
            case bddop_nand:
                if (l == 0 || r == 0)
                    return 1;
                break;
            case bddop_nor:
                if (l == 1 || r == 1)
                    return 0;
                break;
        }

        if (ISCONST(l) && ISCONST(r))
            res = oprres[appexop][(l << 1) | r];
        else if (getLevel(l) > quantlast && getLevel(r) > quantlast) {
            int oldop = applyop;
            applyop = appexop;
            switch (applyop) {
                case bddop_and:
                    res = andRecurse(l, r);
                    break;
                case bddop_or:
                    res = orRecurse(l, r);
                    break;
                default:
                    res = applyRecurse(l, r);
                    break;
            }
            applyop = oldop;
        } else {
            entry = BddCache_lookupI(appexcache, APPEXHASH(l, r, appexop));
            if (entry.a == l && entry.b == r && entry.c == appexid) {
                if (CACHESTATS)
                    cachestats.opHit++;
                return entry.res;
            }
            if (CACHESTATS)
                cachestats.opMiss++;

            int lev;
            if (getLevel(l) == getLevel(r)) {
                PUSHREF(appquant_rec(getLow(l), getLow(r)));
                PUSHREF(appquant_rec(getHigh(l), getHigh(r)));
                lev = getLevel(l);
            } else if (getLevel(l) < getLevel(r)) {
                PUSHREF(appquant_rec(getLow(l), r));
                PUSHREF(appquant_rec(getHigh(l), r));
                lev = getLevel(l);
            } else {
                PUSHREF(appquant_rec(l, getLow(r)));
                PUSHREF(appquant_rec(l, getHigh(r)));
                lev = getLevel(r);
            }
            if (INVARSET(lev)) {
                int r2 = READREF(2), r1 = READREF(1);
                switch (applyop) {
                    case bddop_and:
                        res = andRecurse(r2, r1);
                        break;
                    case bddop_or:
                        res = orRecurse(r2, r1);
                        break;
                    default:
                        res = applyRecurse(r2, r1);
                        break;
                }
            } else {
                res = bdd_makenode(lev, READREF(2), READREF(1));
            }

            POPREF(2);

            entry.a = l;
            entry.b = r;
            entry.c = appexid;
            entry.res = res;
        }

        return res;
    }

    int appuni_rec(int l, int r, int var) {
        BddCacheDataI entry;
        int res;

        int LEVEL_l, LEVEL_r, LEVEL_var;
        LEVEL_l = getLevel(l);
        LEVEL_r = getLevel(r);
        LEVEL_var = getLevel(var);

        if (LEVEL_l > LEVEL_var && LEVEL_r > LEVEL_var) {
            // Skipped a quantified node, answer is zero.
            return BDDZERO;
        }

        if (ISCONST(l) && ISCONST(r))
            res = oprres[appexop][(l << 1) | r];
        else if (ISCONST(var)) {
            int oldop = applyop;
            applyop = appexop;
            switch (applyop) {
                case bddop_and:
                    res = andRecurse(l, r);
                    break;
                case bddop_or:
                    res = orRecurse(l, r);
                    break;
                default:
                    res = applyRecurse(l, r);
                    break;
            }
            applyop = oldop;
        } else {
            entry = BddCache_lookupI(appexcache, APPEXHASH(l, r, appexop));
            if (entry.a == l && entry.b == r && entry.c == appexid) {
                if (CACHESTATS)
                    cachestats.opHit++;
                return entry.res;
            }
            if (CACHESTATS)
                cachestats.opMiss++;

            int lev;
            if (LEVEL_l == LEVEL_r) {
                if (LEVEL_l == LEVEL_var) {
                    lev = -1;
                    var = getHigh(var);
                } else {
                    lev = LEVEL_l;
                }
                PUSHREF(appuni_rec(getLow(l), getLow(r), var));
                PUSHREF(appuni_rec(getHigh(l), getHigh(r), var));
                lev = LEVEL_l;
            } else if (LEVEL_l < LEVEL_r) {
                if (LEVEL_l == LEVEL_var) {
                    lev = -1;
                    var = getHigh(var);
                } else {
                    lev = LEVEL_l;
                }
                PUSHREF(appuni_rec(getLow(l), r, var));
                PUSHREF(appuni_rec(getHigh(l), r, var));
            } else {
                if (LEVEL_r == LEVEL_var) {
                    lev = -1;
                    var = getHigh(var);
                } else {
                    lev = LEVEL_r;
                }
                PUSHREF(appuni_rec(l, getLow(r), var));
                PUSHREF(appuni_rec(l, getHigh(r), var));
            }
            if (lev == -1) {
                int r2 = READREF(2), r1 = READREF(1);
                switch (applyop) {
                    case bddop_and:
                        res = andRecurse(r2, r1);
                        break;
                    case bddop_or:
                        res = orRecurse(r2, r1);
                        break;
                    default:
                        res = applyRecurse(r2, r1);
                        break;
                }
            } else {
                res = bdd_makenode(lev, READREF(2), READREF(1));
            }

            POPREF(2);

            entry.a = l;
            entry.b = r;
            entry.c = appexid;
            entry.res = res;
        }

        return res;
    }

    int unique_rec(int r, int q) {
        BddCacheDataI entry;
        int res;
        int LEVEL_r, LEVEL_q;

        LEVEL_r = getLevel(r);
        LEVEL_q = getLevel(q);
        if (LEVEL_r > LEVEL_q) {
            // Skipped a quantified node, answer is zero.
            return BDDZERO;
        }

        if (r < 2 || q < 2)
            return r;

        entry = BddCache_lookupI(quantcache, QUANTHASH(r));
        if (entry.a == r && entry.c == quantid) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        if (LEVEL_r == LEVEL_q) {
            PUSHREF(unique_rec(getLow(r), getHigh(q)));
            PUSHREF(unique_rec(getHigh(r), getHigh(q)));
            res = applyRecurse(READREF(2), READREF(1));
        } else {
            PUSHREF(unique_rec(getLow(r), q));
            PUSHREF(unique_rec(getHigh(r), q));
            res = bdd_makenode(getLevel(r), READREF(2), READREF(1));
        }

        POPREF(2);

        entry.a = r;
        entry.c = quantid;
        entry.res = res;

        return res;
    }

    int quant_rec(int r) {
        BddCacheDataI entry;
        int res;

        if (r < 2 || getLevel(r) > quantlast)
            return r;

        entry = BddCache_lookupI(quantcache, QUANTHASH(r));
        if (entry.a == r && entry.c == quantid) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        PUSHREF(quant_rec(getLow(r)));
        PUSHREF(quant_rec(getHigh(r)));

        if (INVARSET(getLevel(r))) {
            int r2 = READREF(2), r1 = READREF(1);
            switch (applyop) {
                case bddop_and:
                    res = andRecurse(r2, r1);
                    break;
                case bddop_or:
                    res = orRecurse(r2, r1);
                    break;
                default:
                    res = applyRecurse(r2, r1);
                    break;
            }
        } else {
            res = bdd_makenode(getLevel(r), READREF(2), READREF(1));
        }

        POPREF(2);

        entry.a = r;
        entry.c = quantid;
        entry.res = res;

        return res;
    }

    int zquant_rec(int r, int lev) {
        BddCacheDataI entry;
        int res;

        for (; ;) {
            if (lev > quantlast)
                return r;
            if (lev == getLevel(r))
                break;
            if (INVARSET(lev)) {
                switch (applyop) {
                    case bddop_and:
                        return 0;
                    case bddop_or:
                        PUSHREF(zquant_rec(r, lev + 1));
                        res = zdd_makenode(lev, READREF(1), READREF(1));
                        POPREF(1);
                        return res;
                    default:
                        throw new BDDException();
                }
            }
            lev++;
        }

        if (r < 2)
            return r;

        entry = BddCache_lookupI(quantcache, QUANTHASH(r));
        if (entry.a == r && entry.c == quantid) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        int nlev = getLevel(r) + 1;
        PUSHREF(zquant_rec(getLow(r), nlev));
        PUSHREF(zquant_rec(getHigh(r), nlev));

        if (INVARSET(getLevel(r))) {
            int r2 = READREF(2), r1 = READREF(1);
            switch (applyop) {
                case bddop_and:
                    res = zand_rec(r2, r1);
                    break;
                case bddop_or:
                    res = zor_rec(r2, r1);
                    break;
                default:
                    throw new BDDException();
            }
            POPREF(2);
            PUSHREF(res);
            res = zdd_makenode(getLevel(r), READREF(1), READREF(1));
            POPREF(1);
        } else {
            res = zdd_makenode(getLevel(r), READREF(2), READREF(1));
            POPREF(2);
        }

        entry.a = r;
        entry.c = quantid;
        entry.res = res;

        return res;
    }

    int bdd_constrain(int f, int c) {
        int res;
        int numReorder = 1;

        CHECKa(f, bddfalse);
        CHECKa(c, bddfalse);

        if (misccache == null) misccache = BddCacheI_init(cachesize);

        again:
        for (; ;) {
            try {
                initRef();
                miscid = CACHEID_CONSTRAIN;

                if (numReorder == 0)
                    bdd_disable_reorder();
                res = constrain_rec(f, c);
                if (numReorder == 0)
                    bdd_enable_reorder();
            } catch (ReorderException x) {
                bdd_checkReorder();

                numReorder--;
                continue again;
            }
            break;
        }

        checkResize();
        return res;
    }

    int constrain_rec(int f, int c) {
        BddCacheDataI entry;
        int res;

        if (ISONE(c))
            return f;
        if (ISCONST(f))
            return f;
        if (c == f)
            return BDDONE;
        if (ISZERO(c))
            return BDDZERO;

        entry = BddCache_lookupI(misccache, CONSTRAINHASH(f, c));
        if (entry.a == f && entry.b == c && entry.c == miscid) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        if (getLevel(f) == getLevel(c)) {
            if (ISZERO(getLow(c)))
                res = constrain_rec(getHigh(f), getHigh(c));
            else if (ISZERO(getHigh(c)))
                res = constrain_rec(getLow(f), getLow(c));
            else {
                PUSHREF(constrain_rec(getLow(f), getLow(c)));
                PUSHREF(constrain_rec(getHigh(f), getHigh(c)));
                res = bdd_makenode(getLevel(f), READREF(2), READREF(1));
                POPREF(2);
            }
        } else if (getLevel(f) < getLevel(c)) {
            PUSHREF(constrain_rec(getLow(f), c));
            PUSHREF(constrain_rec(getHigh(f), c));
            res = bdd_makenode(getLevel(f), READREF(2), READREF(1));
            POPREF(2);
        } else {
            if (ISZERO(getLow(c)))
                res = constrain_rec(f, getHigh(c));
            else if (ISZERO(getHigh(c)))
                res = constrain_rec(f, getLow(c));
            else {
                PUSHREF(constrain_rec(f, getLow(c)));
                PUSHREF(constrain_rec(f, getHigh(c)));
                res = bdd_makenode(getLevel(c), READREF(2), READREF(1));
                POPREF(2);
            }
        }

        entry.a = f;
        entry.b = c;
        entry.c = miscid;
        entry.res = res;

        return res;
    }

    int bdd_compose(int f, int g, int var) {
        int res;
        int numReorder = 1;

        CHECKa(f, bddfalse);
        CHECKa(g, bddfalse);
        if (var < 0 || var >= bddvarnum) {
            bdd_error(BDD_VAR);
            return bddfalse;
        }

        if (applycache == null) applycache = BddCacheI_init(cachesize);
        if (itecache == null) itecache = BddCacheI_init(cachesize);

        again:
        for (; ;) {
            try {
                initRef();
                composelevel = bddvar2level[var];
                replaceid = (composelevel << 2) | CACHEID_COMPOSE;

                if (numReorder == 0)
                    bdd_disable_reorder();
                res = compose_rec(f, g);
                if (numReorder == 0)
                    bdd_enable_reorder();
            } catch (ReorderException x) {
                bdd_checkReorder();

                numReorder--;
                continue again;
            }
            break;
        }

        checkResize();
        return res;
    }

    int compose_rec(int f, int g) {
        BddCacheDataI entry;
        int res;

        if (getLevel(f) > composelevel)
            return f;

        entry = BddCache_lookupI(replacecache, COMPOSEHASH(f, g));
        if (entry.a == f && entry.b == g && entry.c == replaceid) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        if (getLevel(f) < composelevel) {
            if (getLevel(f) == getLevel(g)) {
                PUSHREF(compose_rec(getLow(f), getLow(g)));
                PUSHREF(compose_rec(getHigh(f), getHigh(g)));
                res = bdd_makenode(getLevel(f), READREF(2), READREF(1));
            } else if (getLevel(f) < getLevel(g)) {
                PUSHREF(compose_rec(getLow(f), g));
                PUSHREF(compose_rec(getHigh(f), g));
                res = bdd_makenode(getLevel(f), READREF(2), READREF(1));
            } else {
                PUSHREF(compose_rec(f, getLow(g)));
                PUSHREF(compose_rec(f, getHigh(g)));
                res = bdd_makenode(getLevel(g), READREF(2), READREF(1));
            }
            POPREF(2);
        } else
            /*if (LEVEL(f) == composelevel) changed 2-nov-98 */ {
            res = ite_rec(g, getHigh(f), getLow(f));
        }

        entry.a = f;
        entry.b = g;
        entry.c = replaceid;
        entry.res = res;

        return res;
    }

    int bdd_veccompose(int f, BddPair pair) {
        int res;
        int numReorder = 1;

        CHECKa(f, bddfalse);

        if (applycache == null) applycache = BddCacheI_init(cachesize);
        if (itecache == null) itecache = BddCacheI_init(cachesize);
        if (replacecache == null) replacecache = BddCacheI_init(cachesize);

        again:
        for (; ;) {
            try {
                initRef();
                replacepair = pair.result;
                replaceid = (pair.id << 2) | CACHEID_VECCOMPOSE;
                replacelast = pair.last;

                if (numReorder == 0)
                    bdd_disable_reorder();
                res = veccompose_rec(f);
                if (numReorder == 0)
                    bdd_enable_reorder();
            } catch (ReorderException x) {
                bdd_checkReorder();

                numReorder--;
                continue again;
            }
            break;
        }

        checkResize();
        return res;
    }

    int veccompose_rec(int f) {
        BddCacheDataI entry;
        int res;

        if (getLevel(f) > replacelast)
            return f;

        entry = BddCache_lookupI(replacecache, VECCOMPOSEHASH(f));
        if (entry.a == f && entry.c == replaceid) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        PUSHREF(veccompose_rec(getLow(f)));
        PUSHREF(veccompose_rec(getHigh(f)));
        res = ite_rec(replacepair[getLevel(f)], READREF(1), READREF(2));
        POPREF(2);

        entry.a = f;
        entry.c = replaceid;
        entry.res = res;

        return res;
    }

    int bdd_exist(int r, int var) {
        int res;
        int numReorder = 1;

        CHECKa(r, bddfalse);
        CHECKa(var, bddfalse);

        if (var < 2) /* Empty set */
            return r;

        if (applycache == null) applycache = BddCacheI_init(cachesize);
        if (quantcache == null) quantcache = BddCacheI_init(cachesize);

        again:
        for (; ;) {
            if (varset2vartable(var) < 0)
                return bddfalse;
            try {
                initRef();

                quantid = (var << 3) | CACHEID_EXIST; /* FIXME: range */
                applyop = bddop_or;

                if (numReorder == 0)
                    bdd_disable_reorder();
                res = ZDD ? zquant_rec(r, 0) : quant_rec(r);
                if (numReorder == 0)
                    bdd_enable_reorder();
            } catch (ReorderException x) {
                bdd_checkReorder();

                numReorder--;
                continue again;
            }
            break;
        }

        checkResize();
        if (false) bdd_validate(res);
        return res;
    }

    int bdd_forall(int r, int var) {
        int res;
        int numReorder = 1;

        CHECKa(r, bddfalse);
        CHECKa(var, bddfalse);

        if (var < 2) /* Empty set */
            return r;

        if (applycache == null) applycache = BddCacheI_init(cachesize);
        if (quantcache == null) quantcache = BddCacheI_init(cachesize);

        again:
        for (; ;) {
            if (varset2vartable(var) < 0)
                return bddfalse;
            try {
                initRef();
                quantid = (var << 3) | CACHEID_FORALL;
                applyop = bddop_and;

                if (numReorder == 0)
                    bdd_disable_reorder();
                res = ZDD ? zquant_rec(r, 0) : quant_rec(r);
                if (numReorder == 0)
                    bdd_enable_reorder();
            } catch (ReorderException x) {
                bdd_checkReorder();

                numReorder--;
                continue again;
            }
            break;
        }

        checkResize();
        return res;
    }

    int bdd_unique(int r, int var) {
        int res;
        int numReorder = 1;

        CHECKa(r, bddfalse);
        CHECKa(var, bddfalse);

        if (var < 2) /* Empty set */
            return r;

        if (applycache == null) applycache = BddCacheI_init(cachesize);
        if (quantcache == null) quantcache = BddCacheI_init(cachesize);

        again:
        for (; ;) {
            try {
                initRef();
                quantid = (var << 3) | CACHEID_UNIQUE;
                applyop = bddop_xor;

                if (numReorder == 0)
                    bdd_disable_reorder();
                res = unique_rec(r, var);
                if (numReorder == 0)
                    bdd_enable_reorder();
            } catch (ReorderException x) {
                bdd_checkReorder();

                numReorder--;
                continue again;
            }
            break;
        }

        checkResize();
        return res;
    }

    int bdd_restrict(int r, int var) {
        int res;
        int numReorder = 1;

        CHECKa(r, bddfalse);
        CHECKa(var, bddfalse);

        if (var < 2) /* Empty set */
            return r;

        if (misccache == null) misccache = BddCacheI_init(cachesize);

        again:
        for (; ;) {
            if (varset2svartable(var) < 0)
                return bddfalse;
            try {
                initRef();
                miscid = (var << 3) | CACHEID_RESTRICT;

                if (numReorder == 0)
                    bdd_disable_reorder();
                res = restrict_rec(r);
                if (numReorder == 0)
                    bdd_enable_reorder();
            } catch (ReorderException x) {
                bdd_checkReorder();

                numReorder--;
                continue again;
            }
            break;
        }

        checkResize();
        return res;
    }

    int restrict_rec(int r) {
        BddCacheDataI entry;
        int res;

        if (ISCONST(r) || getLevel(r) > quantlast)
            return r;

        entry = BddCache_lookupI(misccache, RESTRHASH(r, miscid));
        if (entry.a == r && entry.c == miscid) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        if (INSVARSET(getLevel(r))) {
            if (quantvarset[getLevel(r)] > 0) {
                res = restrict_rec(getHigh(r));
            } else {
                res = restrict_rec(getLow(r));
            }
        } else {
            PUSHREF(restrict_rec(getLow(r)));
            PUSHREF(restrict_rec(getHigh(r)));
            res = bdd_makenode(getLevel(r), READREF(2), READREF(1));
            POPREF(2);
        }

        entry.a = r;
        entry.c = miscid;
        entry.res = res;

        return res;
    }

    int bdd_simplify(int f, int d) {
        int res;
        int numReorder = 1;

        CHECKa(f, bddfalse);
        CHECKa(d, bddfalse);

        if (applycache == null) applycache = BddCacheI_init(cachesize);

        again:
        for (; ;) {
            try {
                initRef();
                applyop = bddop_or;

                if (numReorder == 0)
                    bdd_disable_reorder();
                res = simplify_rec(f, d);
                if (numReorder == 0)
                    bdd_enable_reorder();
            } catch (ReorderException x) {
                bdd_checkReorder();

                numReorder--;
                continue again;
            }
            break;
        }

        checkResize();
        return res;
    }

    int simplify_rec(int f, int d) {
        BddCacheDataI entry;
        int res;

        if (ISONE(d) || ISCONST(f))
            return f;
        if (d == f)
            return BDDONE;
        if (ISZERO(d))
            return BDDZERO;

        entry = BddCache_lookupI(applycache, APPLYHASH(f, d, bddop_simplify));

        if (entry.a == f && entry.b == d && entry.c == bddop_simplify) {
            if (CACHESTATS)
                cachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            cachestats.opMiss++;

        if (getLevel(f) == getLevel(d)) {
            if (ISZERO(getLow(d)))
                res = simplify_rec(getHigh(f), getHigh(d));
            else if (ISZERO(getHigh(d)))
                res = simplify_rec(getLow(f), getLow(d));
            else {
                PUSHREF(simplify_rec(getLow(f), getLow(d)));
                PUSHREF(simplify_rec(getHigh(f), getHigh(d)));
                res = bdd_makenode(getLevel(f), READREF(2), READREF(1));
                POPREF(2);
            }
        } else if (getLevel(f) < getLevel(d)) {
            PUSHREF(simplify_rec(getLow(f), d));
            PUSHREF(simplify_rec(getHigh(f), d));
            res = bdd_makenode(getLevel(f), READREF(2), READREF(1));
            POPREF(2);
        } else /* LEVEL(d) < LEVEL(f) */ {
            PUSHREF(orRecurse(getLow(d), getHigh(d))); /* Exist quant */
            res = simplify_rec(f, READREF(1));
            POPREF(1);
        }

        entry.a = f;
        entry.b = d;
        entry.c = bddop_simplify;
        entry.res = res;

        return res;
    }

    int supportSize = 0;

    int bdd_support(int r) {
        int n;
        int res = 1;

        CHECKa(r, bddfalse);

        if (r < 2)
            return bddtrue;

        /* On-demand allocation of support set */
        if (supportSize < bddvarnum) {
            supportSet = new int[bddvarnum];
            //memset(supportSet, 0, bddvarnum*sizeof(int));
            supportSize = bddvarnum;
            supportID = 0;
        }

        /* Update global variables used to speed up bdd_support()
         * - instead of always memsetting support to zero, we use
         *   a change counter.
         * - and instead of reading the whole array afterwards, we just
         *   look from 'min' to 'max' used BDD variables.
         */
        if (supportID == 0x0FFFFFFF) {
            /* We probably don't get here -- but let's just be sure */
            for (int i = 0; i < bddvarnum; ++i)
                supportSet[i] = 0;
            supportID = 0;
        }
        ++supportID;
        supportMin = getLevel(r);
        supportMax = supportMin;

        if (ZDD)
            zsupport_rec(r, 0, supportSet);
        else
            support_rec(r, supportSet);
        bdd_unmark(r);

        bdd_disable_reorder();

        for (n = supportMax; n >= supportMin; --n)
            if (supportSet[n] == supportID) {
                int tmp;
                bdd_addref(res);
                tmp = makenode_impl(n, 0, res);
                bdd_delref(res);
                res = tmp;
            }

        bdd_enable_reorder();

        return res;
    }

    void support_rec(int r, int[] support) {

        assert (!ZDD);

        if (r < 2)
            return;

        if (mark(r) || getLow(r) == INVALID_BDD)
            return;

        support[getLevel(r)] = supportID;

        if (getLevel(r) > supportMax)
            supportMax = getLevel(r);

        setMark(r);

        support_rec(getLow(r), support);
        support_rec(getHigh(r), support);
    }

    void zsupport_rec(int r, int lev, int[] support) {

        assert (ZDD);

        if (!ISZERO(r)) {
            while (lev != getLevel(r)) {
                if (lev > supportMax)
                    supportMax = lev;
                support[lev++] = supportID;
            }
        }

        if (r < 2)
            return;

        if (mark(r) || getLow(r) == INVALID_BDD)
            return;

        if (getLow(r) == getHigh(r)) {
            setMark(r);
            zsupport_rec(getLow(r), getLevel(r) + 1, support);
            return;
        }

        support[getLevel(r)] = supportID;

        if (getLevel(r) > supportMax)
            supportMax = getLevel(r);

        setMark(r);

        zsupport_rec(getLow(r), getLevel(r) + 1, support);
        zsupport_rec(getHigh(r), getLevel(r) + 1, support);
    }

    int bdd_appall(int l, int r, int opr, int var) {
        int res;
        int numReorder = 1;

        CHECKa(l, bddfalse);
        CHECKa(r, bddfalse);
        CHECKa(var, bddfalse);

        if (opr < 0 || opr > bddop_invimp) {
            bdd_error(BDD_OP);
            return bddfalse;
        }

        if (var < 2) /* Empty set */
            return bdd_apply(l, r, opr);

        if (applycache == null) applycache = BddCacheI_init(cachesize);
        if (appexcache == null) appexcache = BddCacheI_init(cachesize);
        if (quantcache == null) quantcache = BddCacheI_init(cachesize);

        again:
        for (; ;) {
            if (varset2vartable(var) < 0)
                return bddfalse;
            try {
                initRef();
                applyop = bddop_and;
                appexop = opr;
                appexid = (var << 5) | (appexop << 1) | 1; /* FIXME: range! */
                quantid = (appexid << 3) | CACHEID_APPAL;

                if (numReorder == 0)
                    bdd_disable_reorder();
                res = appquant_rec(l, r);
                if (numReorder == 0)
                    bdd_enable_reorder();
            } catch (ReorderException x) {
                bdd_checkReorder();

                numReorder--;
                continue again;
            }
            break;
        }

        checkResize();
        return res;
    }

    int bdd_appuni(int l, int r, int opr, int var) {
        int res;
        int numReorder = 1;

        CHECKa(l, bddfalse);
        CHECKa(r, bddfalse);
        CHECKa(var, bddfalse);

        if (opr < 0 || opr > bddop_invimp) {
            bdd_error(BDD_OP);
            return bddfalse;
        }

        if (var < 2) /* Empty set */
            return bdd_apply(l, r, opr);

        if (applycache == null) applycache = BddCacheI_init(cachesize);
        if (appexcache == null) appexcache = BddCacheI_init(cachesize);
        if (quantcache == null) quantcache = BddCacheI_init(cachesize);

        again:
        for (; ;) {
            try {
                initRef();
                applyop = bddop_xor;
                appexop = opr;
                appexid = (var << 5) | (appexop << 1) | 1; /* FIXME: range! */
                quantid = (appexid << 3) | CACHEID_APPUN;

                if (numReorder == 0)
                    bdd_disable_reorder();
                res = appuni_rec(l, r, var);
                if (numReorder == 0)
                    bdd_enable_reorder();
            } catch (ReorderException x) {
                bdd_checkReorder();
                numReorder--;
                continue again;
            }
            break;
        }

        checkResize();
        return res;
    }

    int bdd_satone(int r) {
        int res;

        CHECKa(r, bddfalse);
        if (r < 2)
            return r;

        bdd_disable_reorder();

        initRef();
        res = satone_rec(r);

        bdd_enable_reorder();

        checkResize();
        return res;
    }

    int satone_rec(int r) {
        if (ISCONST(r))
            return r;

        if (ISZERO(getLow(r))) {
            int res = satone_rec(getHigh(r));
            int m = makenode_impl(getLevel(r), BDDZERO, res);
            PUSHREF(m);
            return m;
        } else {
            int res = satone_rec(getLow(r));
            int m = makenode_impl(getLevel(r), res, (ZDD && getLow(r) == getHigh(r)) ? res : BDDZERO);
            PUSHREF(m);
            return m;
        }
    }

    int bdd_satoneset(int r, int var, boolean pol) {
        int res;

        CHECKa(r, bddfalse);
        if (ISZERO(r))
            return r;

        bdd_disable_reorder();

        initRef();
        satPolarity = pol;
        res = satoneset_rec(r, var);

        bdd_enable_reorder();

        checkResize();
        return res;
    }

    int satoneset_rec(int r, int var) {
        if (ISCONST(r) && ISCONST(var))
            return r;

        if (getLevel(r) < getLevel(var)) {
            // r is not in the set
            if (ISZERO(getLow(r))) {
                int res = satoneset_rec(getHigh(r), var);
                int m = makenode_impl(getLevel(r), BDDZERO, res);
                PUSHREF(m);
                return m;
            } else {
                int res = satoneset_rec(getLow(r), var);
                int m = makenode_impl(getLevel(r), res, (ZDD && getLow(r) == getHigh(r)) ? res : BDDZERO);
                PUSHREF(m);
                return m;
            }
        } else if (getLevel(var) < getLevel(r)) {
            int res = satoneset_rec(r, getHigh(var));
            if (!ZDD && satPolarity) {
                int m = makenode_impl(getLevel(var), BDDZERO, res);
                PUSHREF(m);
                return m;
            } else {
                int m = makenode_impl(getLevel(var), res, BDDZERO);
                PUSHREF(m);
                return m;
            }
        } else /* LEVEL(r) == LEVEL(var) */ {
            if (ISZERO(getLow(r))) {
                int res = satoneset_rec(getHigh(r), getHigh(var));
                int m = makenode_impl(getLevel(r), BDDZERO, res);
                PUSHREF(m);
                return m;
            } else {
                int res = satoneset_rec(getLow(r), getHigh(var));
                int m;
                if (ZDD && satPolarity && getLow(r) == getHigh(r))
                    m = zdd_makenode(getLevel(r), BDDZERO, res);
                else
                    m = makenode_impl(getLevel(r), res, BDDZERO);
                PUSHREF(m);
                return m;
            }
        }

    }

    int bdd_fullsatone(int r) {
        int res;
        int v;

        CHECKa(r, bddfalse);
        if (r == 0)
            return 0;

        bdd_disable_reorder();

        initRef();
        res = fullsatone_rec(r);

        for (v = getLevel(r) - 1; v >= 0; v--) {
            res = PUSHREF(makenode_impl(v, res, 0));
        }

        bdd_enable_reorder();

        checkResize();
        return res;
    }

    int fullsatone_rec(int r) {
        if (r < 2)
            return r;

        if (getLow(r) != 0) {
            int res = fullsatone_rec(getLow(r));
            int v;

            for (v = getLevel(getLow(r)) - 1; v > getLevel(r); v--) {
                res = PUSHREF(makenode_impl(v, res, 0));
            }

            return PUSHREF(makenode_impl(getLevel(r), res, 0));
        } else {
            int res = fullsatone_rec(getHigh(r));
            int v;

            for (v = getLevel(getHigh(r)) - 1; v > getLevel(r); v--) {
                res = PUSHREF(makenode_impl(v, res, 0));
            }

            return PUSHREF(makenode_impl(getLevel(r), 0, res));
        }
    }

    void bdd_gbc_rehash() {
        int n;

        bddfreepos = 0;
        bddfreenum = 0;

        for (n = bddNodeSize - 1; n >= 2; n--) {
            if (getLow(n) != INVALID_BDD) {
                int hash2;

                hash2 = NODEHASH(getLevel(n), getLow(n), getHigh(n));
                setNext(n, hash(hash2));
                setHash(hash2, n);
            } else {
                setNext(n, bddfreepos);
                bddfreepos = n;
                bddfreenum++;
            }
        }
    }

    long clock() {
        return System.currentTimeMillis();
    }

    void initRef() {
        bddrefstacktop = 0;
    }

    int PUSHREF(int a) {
        bddrefstack[bddrefstacktop++] = a;
        return a;
    }

    int READREF(int a) {
        return bddrefstack[bddrefstacktop - a];
    }

    void POPREF(int a) {
        bddrefstacktop -= a;
    }

    int bdd_nodecount(int r) {
        int[] num = new int[1];

        checkNode(r);

        bdd_markcount(r, num);
        bdd_unmark(r);

        return num[0];
    }

    int bdd_anodecount(int[] r) {
        int n;
        int[] cou = new int[1];

        for (n = 0; n < r.length; n++)
            bdd_markcount(r[n], cou);

        for (n = 0; n < r.length; n++)
            bdd_unmark(r[n]);

        return cou[0];
    }

    int[] bdd_varprofile(int r) {
        checkNode(r);

        int[] varprofile = new int[bddvarnum];

        varprofile_rec(r, varprofile);
        bdd_unmark(r);
        return varprofile;
    }

    void varprofile_rec(int r, int[] varprofile) {

        if (r < 2)
            return;

        if (mark(r))
            return;

        varprofile[bddlevel2var[getLevel(r)]]++;
        setMark(r);

        varprofile_rec(getLow(r), varprofile);
        varprofile_rec(getHigh(r), varprofile);
    }

    long bdd_pathcount(int r) {
        checkNode(r);

        miscid = CACHEID_PATHCOU;

        if (countcache == null) countcache = BddCacheD_init(cachesize);

        return bdd_pathcount_rec(r);
    }

    long bdd_pathcount_rec(int r) {
        BddCacheDataD entry;
        long size;

        if (ISZERO(r))
            return 0;
        if (ISONE(r))
            return 1;

        entry = BddCache_lookupD(countcache, PATHCOUHASH(r));
        if (entry.a == r && entry.c == miscid)
            return entry.dres;

        size = bdd_pathcount_rec(getLow(r)) + bdd_pathcount_rec(getHigh(r));

        entry.a = r;
        entry.c = miscid;
        entry.dres = size;

        return size;
    }

    long bdd_satcount(int r) {
        if (ZDD)
            return bdd_pathcount(r);

        long size = 1;

        checkNode(r);

        if (countcache == null) countcache = BddCacheD_init(cachesize);

        miscid = CACHEID_SATCOU;
        if (!ZDD)
            size = (long) Math.pow(2.0, (double) getLevel(r));

        return size * satcount_rec(r);
    }

    double bdd_satcountset(int r, int varset) {
        double unused = bddvarnum;
        int n;

        if (ISCONST(varset) || ISZERO(r)) /* empty set */
            return 0.0;

        for (n = varset; !ISCONST(n); n = getHigh(n))
            unused--;

        unused = bdd_satcount(r) / Math.pow(2.0, unused);

        return unused >= 1.0 ? unused : 1.0;
    }

    long satcount_rec(int root) {
        BddCacheDataD entry;
        long size, s;

        if (root < 2)
            return root;

        entry = BddCache_lookupD(countcache, SATCOUHASH(root));
        if (entry.a == root && entry.c == miscid)
            return entry.dres;

        size = 0;
        s = 1;
        if (!ZDD)
            s *= Math.pow(2.0, (float) (getLevel(getLow(root)) - getLevel(root) - 1));
        size += s * satcount_rec(getLow(root));

        s = 1;
        if (!ZDD)
            s *= Math.pow(2.0, (float) (getLevel(getHigh(root)) - getLevel(root) - 1));
        size += s * satcount_rec(getHigh(root));

        entry.a = root;
        entry.c = miscid;
        entry.dres = size;

        return size;
    }

    void bdd_gbc() {
        int r;
        int n;
        long c2, c1 = clock();

        //if (gbc_handler != NULL)
        {
            gcstats.nodes = bddNodeSize;
            gcstats.freenodes = bddfreenum;
            gcstats.time = 0;
            gcstats.sumtime = gbcclock;
            gcstats.num = gbcollectnum;
            gbc_handler(true, gcstats);
        }

        // Handle nodes that were marked as free by finalizer.
        handleDeferredFree();

        for (r = 0; r < bddrefstacktop; r++)
            bdd_mark(bddrefstack[r]);

        for (n = 0; n < bddNodeSize; n++) {
            if (hasRef(n))
                bdd_mark(n);
            setHash(n, 0);
        }

        bddfreepos = 0;
        bddfreenum = 0;

        for (n = bddNodeSize - 1; n >= 2; n--) {

            if (mark(n) && getLow(n) != INVALID_BDD) {
                int hash2;

                unmark(n);
                hash2 = NODEHASH(getLevel(n), getLow(n), getHigh(n));
                setNext(n, hash(hash2));
                setHash(hash2, n);
            } else {
                setLow(n, INVALID_BDD);
                setNext(n, bddfreepos);
                bddfreepos = n;
                bddfreenum++;
            }
        }

        if (FLUSH_CACHE_ON_GC) {
            bdd_operator_reset();
        } else {
            bdd_operator_clean();
        }

        c2 = clock();
        gbcclock += c2 - c1;
        gbcollectnum++;

        //if (gbc_handler != NULL)
        {
            gcstats.nodes = bddNodeSize;
            gcstats.freenodes = bddfreenum;
            gcstats.time = c2 - c1;
            gcstats.sumtime = gbcclock;
            gcstats.num = gbcollectnum;
            gbc_handler(false, gcstats);
        }

        //validate_all();
    }

    int bdd_addref(int root) {
        if (root == INVALID_BDD)
            bdd_error(BDD_BREAK); /* distinctive */
        if (root < 2 || !bddrunning)
            return root;
        if (root >= bddNodeSize)
            return bdd_error(BDD_ILLBDD);
        if (getLow(root) == INVALID_BDD)
            return bdd_error(BDD_ILLBDD);

        incrRef(root);
        if (false) System.out.println("INCREF(" + root + ") = " + getRef(root));
        return root;
    }

    int bdd_delref(int root) {
        if (root == INVALID_BDD)
            bdd_error(BDD_BREAK); /* distinctive */
        if (root < 2 || !bddrunning)
            return root;
        if (root >= bddNodeSize)
            return bdd_error(BDD_ILLBDD);
        if (getLow(root) == INVALID_BDD)
            return bdd_error(BDD_ILLBDD);

        /* if the following line is present, fails there much earlier */
        if (!hasRef(root))
            bdd_error(BDD_BREAK); /* distinctive */

        decrRef(root);
        if (false) System.out.println("DECREF(" + root + ") = " + getRef(root));
        return root;
    }

    void bdd_mark(int i) {

        if (i < 2)
            return;

        if (mark(i) || getLow(i) == INVALID_BDD)
            return;

        setMark(i);

        bdd_mark(getLow(i));
        bdd_mark(getHigh(i));
    }

    void bdd_markcount(int i, int[] cou) {

        if (i < 2)
            return;

        if (mark(i) || getLow(i) == INVALID_BDD)
            return;

        setMark(i);
        cou[0] += 1;

        bdd_markcount(getLow(i), cou);
        bdd_markcount(getHigh(i), cou);
    }

    void bdd_unmark(int i) {

        if (i < 2)
            return;

        if (!mark(i) || getLow(i) == INVALID_BDD)
            return;
        unmark(i);

        bdd_unmark(getLow(i));
        bdd_unmark(getHigh(i));
    }

    int bdd_makenode(int level, int low, int high) {
        assert (!ZDD);

        if (CACHESTATS)
            cachestats.uniqueAccess++;

        // check whether children are equal
        if (low == high)
            return low;

        return makenode(level, low, high);
    }

    int zdd_makenode(int level, int low, int high) {
        assert (ZDD);

        if (CACHESTATS)
            cachestats.uniqueAccess++;

        // check whether high child is zero
        if (high == 0)
            return low;

        return makenode(level, low, high);
    }

    // Don't call directly - call bdd_makenode or zdd_makenode instead.

    int hitCount;
    int misCount;

    private int makenode(int level, int low, int high) {
        int hash2;
        int res;

        /* Try to find an existing node of this kind */
        hash2 = NODEHASH(level, low, high);
        res = hash(hash2);

        while (res != 0) {
            if (getLevel(res) == level && getLow(res) == low && getHigh(res) == high) {
                if (CACHESTATS)
                    cachestats.uniqueHit++;
                return res;
            }

            res = next(res);
            if (CACHESTATS)
                cachestats.uniqueChain++;

        }


        /* No existing node => build one */
        if (CACHESTATS)
            cachestats.uniqueMiss++;

        /* Any free nodes to use ? */
        if (bddfreepos == 0) {
            if (bdderrorcond != 0)
                return 0;

            /* Try to allocate more nodes */
            bdd_gbc();

            if ((bddNodeSize - bddfreenum) >= usednodes_nextreorder &&
                    bdd_reorder_ready()) {
                throw new ReorderException();
            }

            if ((bddfreenum * 100) / bddNodeSize <= minFreeNodes) {
                bdd_noderesize(true);
                hash2 = NODEHASH(level, low, high);
            }

            /* Panic if that is not possible */
            if (bddfreepos == 0) {
                bdd_error(BDD_NODENUM);
                bdderrorcond = Math.abs(BDD_NODENUM);
                return 0;
            }
        }

        /* Build new node */
        res = bddfreepos;
        bddfreepos = next(bddfreepos);
        bddfreenum--;
        bddproduced++;

        setLevelAndMark(res, level);
        setLow(res, low);
        setHigh(res, high);

        /* Insert node */
        setNext(res, hash(hash2));
        setHash(hash2, res);

        return res;
    }

    int bdd_noderesize(boolean doRehash) {
        int oldsize = bddNodeSize;
        int newsize = bddNodeSize;

        if (bddmaxnodesize > 0) {
            if (newsize >= bddmaxnodesize)
                return -1;
        }

        if (increasefactor > 0) {
            newsize += (int) (newsize * increasefactor);
        } else {
            newsize = newsize << 1;
        }

        if (bddmaxnodeincrease > 0) {
            if (newsize > oldsize + bddmaxnodeincrease)
                newsize = oldsize + bddmaxnodeincrease;
        }

        if (bddmaxnodesize > 0) {
            if (newsize > bddmaxnodesize)
                newsize = bddmaxnodesize;
        }

        return doResize(doRehash, oldsize, newsize);
    }

    int doResize(boolean doRehash, int oldsize, int newsize) {

        newsize = bdd_prime_lte(newsize);

        if (oldsize > newsize) return 0;

        resize_handler(oldsize, newsize);

        int[] newnodes;
        int n;
        newnodes = new int[newsize * __node_size];
        System.arraycopy(bddNodes, 0, newnodes, 0, bddNodes.length);
        bddNodes = newnodes;
        bddNodeSize = newsize;

        if (doRehash)
            for (n = 0; n < oldsize; n++)
                setHash(n, 0);

        for (n = oldsize; n < bddNodeSize; n++) {
            setLow(n, INVALID_BDD);
            //SETREFCOU(n, 0);
            //SETHASH(n, 0);
            //SETLEVEL(n, 0);
            setNext(n, n + 1);
        }
        setNext(bddNodeSize - 1, bddfreepos);
        bddfreepos = oldsize;
        bddfreenum += bddNodeSize - oldsize;

        if (doRehash)
            bdd_gbc_rehash();

        bddresized = true;

        return 0;
    }

    void bdd_init(int initnodesize, int cs) {
        int n;

        if (bddrunning) bdd_error(BDD_RUNNING);

        bddNodeSize = bddPrimeGte(initnodesize);

        bddNodes = new int[bddNodeSize * __node_size];

        bddresized = false;

        for (n = 0; n < bddNodeSize; n++) {
            setLow(n, INVALID_BDD);
            //SETREFCOU(n, 0);
            //SETHASH(n, 0);
            //SETLEVEL(n, 0);
            setNext(n, n + 1);
        }
        setNext(bddNodeSize - 1, 0);

        setMaxRef(0);
        setMaxRef(1);
        setLow(0, 0);
        setHigh(0, 0);
        setLow(1, 1);
        setHigh(1, 1);


        bdd_operator_init(cs);

        bddfreepos = 2;
        bddfreenum = bddNodeSize - 2;
        bddrunning = true;
        bddvarnum = 0;
        gbcollectnum = 0;
        gbcclock = 0;
        cachesize = cs;
        usednodes_nextreorder = bddNodeSize;
        bddmaxnodeincrease = DEFAULTMAXNODEINC;

        bdderrorcond = 0;

        if (CACHESTATS) {
            //cachestats = new CacheStats();
        }

        //bdd_gbc_hook(bdd_default_gbchandler);
        //bdd_error_hook(bdd_default_errhandler);
        //bdd_resize_hook(NULL);
        bdd_pairs_init();
        bdd_reorder_init();

        return;
    }

    /* Hash value modifiers to distinguish between entries in misccache */
    static final int CACHEID_CONSTRAIN = 0x0;
    static final int CACHEID_RESTRICT = 0x1;
    static final int CACHEID_SATCOU = 0x2;
    static final int CACHEID_SATCOULN = 0x3;
    static final int CACHEID_PATHCOU = 0x4;

    /* Hash value modifiers for replace/compose */
    static final int CACHEID_REPLACE = 0x0;
    static final int CACHEID_COMPOSE = 0x1;
    static final int CACHEID_VECCOMPOSE = 0x2;

    /* Hash value modifiers for quantification */
    static final int CACHEID_EXIST = 0x0;
    static final int CACHEID_FORALL = 0x1;
    static final int CACHEID_UNIQUE = 0x2;
    static final int CACHEID_APPEX = 0x3;
    static final int CACHEID_APPAL = 0x4;
    static final int CACHEID_APPUN = 0x5;

    /* Number of boolean operators */
    static final int OPERATOR_NUM = 11;

    /* Operator results - entry = left<<1 | right  (left,right in {0,1}) */
    static int oprres[][] =
            {{0, 0, 0, 1}, /* and                       ( & )         */ {
                    0, 1, 1, 0}, /* xor                       ( ^ )         */ {
                    0, 1, 1, 1}, /* or                        ( | )         */ {
                    1, 1, 1, 0}, /* nand                                    */ {
                    1, 0, 0, 0}, /* nor                                     */ {
                    1, 1, 0, 1}, /* implication               ( >> )        */ {
                    1, 0, 0, 1}, /* bi-implication                          */ {
                    0, 0, 1, 0}, /* difference /greater than  ( - ) ( > )   */ {
                    0, 1, 0, 0}, /* less than                 ( < )         */ {
                    1, 0, 1, 1}, /* inverse implication       ( << )        */ {
                    1, 1, 0, 0} /* not                       ( ! )         */
            };

    int applyop; /* Current operator for apply */
    int appexop; /* Current operator for appex */
    int appexid; /* Current cache id for appex */
    int quantid; /* Current cache id for quantifications */
    int[] quantvarset; /* Current variable set for quant. */
    int quantvarsetID; /* Current id used in quantvarset */
    int quantlast; /* Current last variable to be quant. */
    int replaceid; /* Current cache id for replace */
    int[] replacepair; /* Current replace pair */
    int replacelast; /* Current last var. level to replace */
    int composelevel; /* Current variable used for compose */
    int miscid; /* Current cache id for other results */
    int supportID; /* Current ID (true value) for support */
    int supportMin; /* Min. used level in support calc. */
    int supportMax; /* Max. used level in support calc. */
    int[] supportSet; /* The found support set */
    BddCache applycache; /* Cache for apply results */
    BddCache itecache; /* Cache for ITE results */
    BddCache quantcache; /* Cache for exist/forall results */
    BddCache appexcache; /* Cache for appex/appall results */
    BddCache replacecache; /* Cache for replace results */
    BddCache misccache; /* Cache for other results */
    BddCache countcache; /* Cache for count results */
    int cacheratio;
    boolean satPolarity;

    void bdd_operator_init(int cachesize) {
        if (false) {
            applycache = BddCacheI_init(cachesize);
            itecache = BddCacheI_init(cachesize);
            quantcache = BddCacheI_init(cachesize);
            appexcache = BddCacheI_init(cachesize);
            replacecache = BddCacheI_init(cachesize);
            misccache = BddCacheI_init(cachesize);
            countcache = BddCacheD_init(cachesize);
        }

        quantvarsetID = 0;
        quantvarset = null;
        cacheratio = 0;
        supportSet = null;
        supportSize = 0;
    }

    void bdd_operator_done() {
        if (quantvarset != null) {
            quantvarset = null;
        }

        BddCache_done(applycache);
        applycache = null;
        BddCache_done(itecache);
        itecache = null;
        BddCache_done(quantcache);
        quantcache = null;
        BddCache_done(appexcache);
        appexcache = null;
        BddCache_done(replacecache);
        replacecache = null;
        BddCache_done(misccache);
        misccache = null;
        BddCache_done(countcache);
        countcache = null;

        if (supportSet != null) {
            supportSet = null;
            supportSize = 0;
        }
    }

    void bdd_operator_reset() {
        BddCache_reset(applycache);
        BddCache_reset(itecache);
        BddCache_reset(quantcache);
        BddCache_reset(appexcache);
        BddCache_reset(replacecache);
        BddCache_reset(misccache);
        BddCache_reset(countcache);
    }

    void bdd_operator_clean() {
        BddCache_clean_ab(applycache);
        BddCache_clean_abc(itecache);
        BddCache_clean_a(quantcache);
        BddCache_clean_ab(appexcache);
        BddCache_clean_ab(replacecache);
        BddCache_clean_ab(misccache);
        BddCache_clean_d(countcache);
    }

    void bdd_operator_varresize() {

        quantvarset = new int[bddvarnum];

        //memset(quantvarset, 0, sizeof(int)*bddvarnum);
        quantvarsetID = 0;

        BddCache_reset(countcache);
    }

    int bdd_setcachesize(int newcachesize) {
        int old = cachesize;
        BddCache_resize(applycache, newcachesize);
        BddCache_resize(itecache, newcachesize);
        BddCache_resize(quantcache, newcachesize);
        BddCache_resize(appexcache, newcachesize);
        BddCache_resize(replacecache, newcachesize);
        BddCache_resize(misccache, newcachesize);
        BddCache_resize(countcache, newcachesize);
        return old;
    }

    void bdd_operator_noderesize() {
        if (cacheratio > 0) {
            int newcachesize = bddNodeSize / cacheratio;

            BddCache_resize(applycache, newcachesize);
            BddCache_resize(itecache, newcachesize);
            BddCache_resize(quantcache, newcachesize);
            BddCache_resize(appexcache, newcachesize);
            BddCache_resize(replacecache, newcachesize);
            BddCache_resize(misccache, newcachesize);
            BddCache_resize(countcache, newcachesize);
        }
    }

    BddCache BddCacheI_init(int size) {
        int n;

        size = bddPrimeGte(size);

        BddCache cache = new BddCache();
        cache.table = new BddCacheDataI[size];

        for (n = 0; n < size; n++) {
            cache.table[n] = new BddCacheDataI();
            cache.table[n].a = -1;
        }
        cache.tablesize = size;

        return cache;
    }

    BddCache BddCacheD_init(int size) {
        int n;

        size = bddPrimeGte(size);

        BddCache cache = new BddCache();
        cache.table = new BddCacheDataD[size];

        for (n = 0; n < size; n++) {
            cache.table[n] = new BddCacheDataD();
            cache.table[n].a = -1;
        }
        cache.tablesize = size;

        return cache;
    }

    void BddCache_done(BddCache cache) {
        if (cache == null) return;

        cache.table = null;
        cache.tablesize = 0;
    }

    int BddCache_resize(BddCache cache, int newsize) {
        if (cache == null) return 0;
        int n;

        boolean is_d = cache.table instanceof BddCacheDataD[];

        cache.table = null;

        newsize = bddPrimeGte(newsize);

        if (is_d)
            cache.table = new BddCacheDataD[newsize];
        else
            cache.table = new BddCacheDataI[newsize];

        for (n = 0; n < newsize; n++) {
            if (is_d)
                cache.table[n] = new BddCacheDataD();
            else
                cache.table[n] = new BddCacheDataI();
            cache.table[n].a = -1;
        }
        cache.tablesize = newsize;

        return 0;
    }

    BddCacheDataI BddCache_lookupI(BddCache cache, int hash) {
        return (BddCacheDataI) cache.table[Math.abs(hash % cache.tablesize)];
    }

    BddCacheDataD BddCache_lookupD(BddCache cache, int hash) {
        return (BddCacheDataD) cache.table[Math.abs(hash % cache.tablesize)];
    }

    void BddCache_reset(BddCache cache) {
        if (cache == null) return;
        int n;
        for (n = 0; n < cache.tablesize; n++)
            cache.table[n].a = -1;
    }

    void BddCache_clean_d(BddCache cache) {
        if (cache == null) return;
        int n;
        for (n = 0; n < cache.tablesize; n++) {
            int a = cache.table[n].a;
            if (a >= 0 && getLow(a) == INVALID_BDD) {
                cache.table[n].a = -1;
            }
        }
    }

    void BddCache_clean_a(BddCache cache) {
        if (cache == null) return;
        int n;
        for (n = 0; n < cache.tablesize; n++) {
            int a = cache.table[n].a;
            if (a < 0) continue;
            if (getLow(a) == INVALID_BDD ||
                    getLow(((BddCacheDataI) cache.table[n]).res) == INVALID_BDD) {
                cache.table[n].a = -1;
            }
        }
    }

    void BddCache_clean_ab(BddCache cache) {
        if (cache == null) return;
        int n;
        for (n = 0; n < cache.tablesize; n++) {
            int a = cache.table[n].a;
            if (a < 0) continue;
            if (getLow(a) == INVALID_BDD ||
                    (cache.table[n].b != 0 && getLow(cache.table[n].b) == INVALID_BDD) ||
                    getLow(((BddCacheDataI) cache.table[n]).res) == INVALID_BDD) {
                cache.table[n].a = -1;
            }
        }
    }

    void BddCache_clean_abc(BddCache cache) {
        if (cache == null) return;
        int n;
        for (n = 0; n < cache.tablesize; n++) {
            int a = cache.table[n].a;
            if (a < 0) continue;
            if (getLow(a) == -1 ||
                    getLow(cache.table[n].b) == INVALID_BDD ||
                    getLow(cache.table[n].c) == INVALID_BDD ||
                    getLow(((BddCacheDataI) cache.table[n]).res) == INVALID_BDD) {
                cache.table[n].a = -1;
            }
        }
    }

    void bdd_setpair(BddPair pair, int oldvar, int newvar) {
        if (pair == null)
            return;

        if (oldvar < 0 || oldvar > bddvarnum - 1)
            bdd_error(BDD_VAR);
        if (newvar < 0 || newvar > bddvarnum - 1)
            bdd_error(BDD_VAR);

        if (ZDD) {
            // ZDD requires a permutation, not just a pairing.
            int oldlev = bddvar2level[oldvar], newlev = bddvar2level[newvar];
            int newIndex = newlev;
            if (getLevel(pair.result[newIndex]) != newlev) {
                // Find who points to newlev.
                for (newIndex = 0; newIndex < bddvarnum; ++newIndex) {
                    if (getLevel(pair.result[newIndex]) == newlev) {
                        break;
                    }
                }
                assert (newIndex != bddvarnum);
            }
            int tmp = pair.result[oldlev];
            pair.result[oldlev] = pair.result[newIndex];
            pair.result[newIndex] = tmp;

            if (newlev > pair.last)
                pair.last = newlev;
        } else {
            bdd_delref(pair.result[bddvar2level[oldvar]]);
            pair.result[bddvar2level[oldvar]] = bdd_ithvar(newvar);
        }
        pair.id = update_pairsid();

        if (bddvar2level[oldvar] > pair.last)
            pair.last = bddvar2level[oldvar];

    }

    void bdd_setbddpair(BddPair pair, int oldvar, int newvar) {
        int oldlevel;

        if (pair == null)
            return;

        if (ZDD)
            throw new BDDException("setbddpair not supported with ZDDs");

        checkNode(newvar);
        if (oldvar < 0 || oldvar >= bddvarnum)
            bdd_error(BDD_VAR);
        oldlevel = bddvar2level[oldvar];

        bdd_delref(pair.result[oldlevel]);
        pair.result[oldlevel] = bdd_addref(newvar);
        pair.id = update_pairsid();

        if (oldlevel > pair.last)
            pair.last = oldlevel;

    }

    void bdd_resetpair(BddPair p) {
        int n;

        for (n = 0; n < bddvarnum; n++) {
            if (ZDD) {
                bdd_delref(p.result[n]);
                p.result[n] = bdd_addref(zdd_makenode(n, 0, 1));
            } else
                p.result[n] = bdd_ithvar(bddlevel2var[n]);
        }
        p.last = 0;
    }

    BddPair pairs; /* List of all replacement pairs in use */
    int pairsid; /* Pair identifier */

//    static void free(Object o) {
//    }

    /*************************************************************************
     *************************************************************************/

    void bdd_pairs_init() {
        pairsid = 0;
        pairs = null;
    }

    void bdd_pairs_done() {
        BddPair p = pairs;
        int n;

        while (p != null) {
            BddPair next = p.next;
            for (n = 0; n < bddvarnum; n++)
                bdd_delref(p.result[n]);
            p.result = null;
            p = next;
        }
    }

    int update_pairsid() {
        pairsid++;

        if (pairsid == (INT_MAX >> 2)) {
            BddPair p;
            pairsid = 0;
            for (p = pairs; p != null; p = p.next)
                p.id = pairsid++;
            //bdd_operator_reset();
            BddCache_reset(replacecache);
        }

        return pairsid;
    }

    void bdd_register_pair(BddPair p) {
        p.next = pairs;
        pairs = p;
    }

    void bdd_pairs_vardown(int level) {
        BddPair p;

        for (p = pairs; p != null; p = p.next) {
            int tmp;

            tmp = p.result[level];
            p.result[level] = p.result[level + 1];
            p.result[level + 1] = tmp;

            if (p.last == level)
                p.last++;
        }
    }

    int bdd_pairs_resize(int oldsize, int newsize) {
        BddPair p;
        int n;

        for (p = pairs; p != null; p = p.next) {
            int[] new_result = new int[newsize];
            System.arraycopy(p.result, 0, new_result, 0, oldsize);
            p.result = new_result;

            for (n = oldsize; n < newsize; n++)
                if (ZDD)
                    p.result[n] = bdd_addref(zdd_makenode(n, 0, 1));
                else
                    p.result[n] = bdd_ithvar(bddlevel2var[n]);
        }

        return 0;
    }

    void bdd_disable_reorder() {
        reorderdisabled = 1;
    }

    void bdd_enable_reorder() {
        reorderdisabled = 0;
    }

    void bdd_checkReorder() {
        System.out.println("JFactory.bdd_checkReorder(..)");
        bdd_reorder_auto();

        /* Do not reorder before twice as many nodes have been used */
        usednodes_nextreorder = 2 * (bddNodeSize - bddfreenum);

        /* And if very little was gained this time (< 20%) then wait until
         * even more nodes (upto twice as many again) have been used */
        if (bdd_reorder_gain() < 20)
            usednodes_nextreorder
                    += (usednodes_nextreorder * (20 - bdd_reorder_gain()))
                    / 20;
    }

    boolean bdd_reorder_ready() {
        if ((bddreordermethod == BDD_REORDER_NONE)
                || (vartree == null)
                || (bddreordertimes == 0)
                || (reorderdisabled != 0))
            return false;
        return true;
    }

    void bdd_reorder(int method) {
        BddTree top;
        int savemethod = bddreordermethod;
        int savetimes = bddreordertimes;

        bddreordermethod = method;
        bddreordertimes = 1;

        if ((top = bddtree_new(-1)) != null) {
            if (reorderInit() >= 0) {

                usednum_before = bddNodeSize - bddfreenum;

                top.firstVar = top.firstLevel = 0;
                top.lastVar = top.lastLevel = bdd_varnum() - 1;
                top.fixed = false;
                top.interleaved = false;
                top.next = null;
                top.nextlevel = vartree;

                System.out.println("top.firstVar = [" + top.firstVar + "]");
                System.out.println("top.lastVar = [" + top.lastVar + "]");
                System.out.println();
                reorder_block(top, method);
                vartree = top.nextlevel;

                usednum_after = bddNodeSize - bddfreenum;

                reorder_done();
                bddreordermethod = savemethod;
                bddreordertimes = savetimes;
            }
        }
    }

    BddTree bddtree_new(int id) {
        BddTree t = new BddTree();

        t.firstVar = t.lastVar = t.firstLevel = t.lastLevel = -1;
        t.fixed = true;
        //t.interleaved = false;
        //t.next = t.prev = t.nextlevel = null;
        //t.seq = null;
        t.id = id;
        return t;
    }

    BddTree reorder_block(BddTree t, int method) {
        BddTree dis;

        if (t == null)
            return null;

        if (!t.fixed /*BDD_REORDER_FREE*/
                && t.nextlevel != null) {
            switch (method) {
                case BDD_REORDER_WIN2:
                    t.nextlevel = reorder_win2(t.nextlevel);
                    break;
                case BDD_REORDER_WIN2ITE:
                    t.nextlevel = reorder_win2ite(t.nextlevel);
                    break;
                case BDD_REORDER_SIFT:
                    t.nextlevel = reorder_sift(t.nextlevel);
                    break;
                case BDD_REORDER_SIFTITE:
                    t.nextlevel = reorder_siftite(t.nextlevel);
                    break;
                case BDD_REORDER_WIN3:
                    t.nextlevel = reorderWin3(t.nextlevel);
                    break;
                case BDD_REORDER_WIN3ITE:
                    t.nextlevel = reorder_win3ite(t.nextlevel);
                    break;
                case BDD_REORDER_RANDOM:
                    throw new UnsupportedOperationException("Dave");
//                    t.nextlevel = reorder_random(t.nextlevel);
//                    break;
            }
        }

        for (dis = t.nextlevel; dis != null; dis = dis.next)
            reorder_block(dis, method);

        if (t.seq != null) {
            //Arrays.sort(t.seq, 0, t.lastVar-t.firstVar + 1);
            varseq_qsort(t.seq, 0, t.lastVar - t.firstVar + 1);
            t.firstLevel = bddvar2level[t.seq[0]];
            t.lastLevel = bddvar2level[t.seq[t.lastVar - t.firstVar]];
        }

        return t;
    }

    // due to Akihiko Tozawa

    void varseq_qsort(int[] target, int from, int to) {

        int x, i, j;

        switch (to - from) {
            case 0:
                return;

            case 1:
                return;

            case 2:
                if (bddvar2level[target[from]] <= bddvar2level[target[from + 1]])
                    return;
                else {
                    x = target[from];
                    target[from] = target[from + 1];
                    target[from + 1] = x;
                }
                return;
        }

        int r = target[from];
        int s = target[(from + to) / 2];
        int t = target[to - 1];

        if (bddvar2level[r] <= bddvar2level[s]) {
            if (bddvar2level[s] <= bddvar2level[t]) {
            } else if (bddvar2level[r] <= bddvar2level[t]) {
                target[to - 1] = s;
                target[(from + to) / 2] = t;
            } else {
                target[to - 1] = s;
                target[from] = t;
                target[(from + to) / 2] = r;
            }
        } else {
            if (bddvar2level[r] <= bddvar2level[t]) {
                target[(from + to) / 2] = r;
                target[from] = s;
            } else if (bddvar2level[s] <= bddvar2level[t]) {
                target[to - 1] = r;
                target[(from + to) / 2] = t;
                target[from] = s;
            } else {
                target[to - 1] = r;
                target[from] = t;
            }
        }

        int mid = target[(from + to) / 2];

        for (i = from + 1, j = to - 1; i + 1 != j;) {
            if (target[i] == mid) {
                target[i] = target[i + 1];
                target[i + 1] = mid;
            }

            x = target[i];

            if (x <= mid)
                i++;
            else {
                x = target[--j];
                target[j] = target[i];
                target[i] = x;
            }
        }

        varseq_qsort(target, from, i);
        varseq_qsort(target, i + 1, to);
    }

    BddTree reorder_win2(BddTree t) {
        BddTree dis = t, first = t;

        if (t == null)
            return t;

        if (verbose > 1) {
            System.out.println("Win2 start: " + reorder_nodenum() + " nodes");
        }

        while (dis.next != null) {
            int best = reorder_nodenum();
            blockdown(dis);

            if (best < reorder_nodenum()) {
                blockdown(dis.prev);
                dis = dis.next;
            } else if (first == dis)
                first = dis.prev;

            if (verbose > 1) {
                System.out.print(".");
            }
        }

        if (verbose > 1) {
            System.out.println();
            System.out.println("Win2 end: " + reorder_nodenum() + " nodes");
        }

        return first;
    }

    BddTree reorderWin3(BddTree t) {
        BddTree dis = t, first = t;

        if (t == null)
            return t;

        if (verbose > 1) {
            System.out.println("Win3 start: " + reorder_nodenum() + " nodes");
        }

        while (dis.next != null) {
            BddTree[] f = new BddTree[1];
            f[0] = first;
            dis = reorder_swapwin3(dis, f);
            first = f[0];

            if (verbose > 1) {
                System.out.print(".");
            }
        }

        if (verbose > 1) {
            System.out.println();
            System.out.println("Win3 end: " + reorder_nodenum() + " nodes");
        }

        return first;
    }

    BddTree reorder_win3ite(BddTree t) {
        BddTree dis = t, first = t;
        int lastsize;

        if (t == null)
            return t;

        if (verbose > 1)
            System.out.println(
                    "Win3ite start: " + reorder_nodenum() + " nodes");

        do {
            lastsize = reorder_nodenum();
            dis = first;

            while (dis.next != null && dis.next.next != null) {
                BddTree[] f = new BddTree[1];
                f[0] = first;
                dis = reorder_swapwin3(dis, f);
                first = f[0];

                if (verbose > 1) {
                    System.out.print(".");
                }
            }

            if (verbose > 1)
                System.out.println(" " + reorder_nodenum() + " nodes");
        }
        while (reorder_nodenum() != lastsize);

        if (verbose > 1)
            System.out.println("Win3ite end: " + reorder_nodenum() + " nodes");

        return first;
    }

    BddTree reorder_swapwin3(BddTree dis, BddTree[] first) {
        boolean setfirst = dis.prev == null;
        BddTree next = dis;
        int best = reorder_nodenum();

        if (dis.next.next == null) /* Only two blocks left => win2 swap */ {
            blockdown(dis.prev);

            if (best < reorder_nodenum()) {
                blockdown(dis.prev);
                next = dis.next;
            } else {
                next = dis;
                if (setfirst)
                    first[0] = dis.prev;
            }
        } else /* Real win3 swap */ {
            int pos = 0;
            blockdown(dis); /* B A* C (4) */
            pos++;
            if (best > reorder_nodenum()) {
                pos = 0;
                best = reorder_nodenum();
            }

            blockdown(dis); /* B C A* (3) */
            pos++;
            if (best > reorder_nodenum()) {
                pos = 0;
                best = reorder_nodenum();
            }

            dis = dis.prev.prev;
            blockdown(dis); /* C B* A (2) */
            pos++;
            if (best > reorder_nodenum()) {
                pos = 0;
                best = reorder_nodenum();
            }

            blockdown(dis); /* C A B* (1) */
            pos++;
            if (best > reorder_nodenum()) {
                pos = 0;
                best = reorder_nodenum();
            }

            dis = dis.prev.prev;
            blockdown(dis); /* A C* B (0)*/
            pos++;
            if (best > reorder_nodenum()) {
                pos = 0;
                best = reorder_nodenum();
            }

            if (pos >= 1) /* A C B -> C A* B */ {
                dis = dis.prev;
                blockdown(dis);
                next = dis;
                if (setfirst)
                    first[0] = dis.prev;
            }

            if (pos >= 2) /* C A B -> C B A* */ {
                blockdown(dis);
                next = dis.prev;
                if (setfirst)
                    first[0] = dis.prev.prev;
            }

            if (pos >= 3) /* C B A -> B C* A */ {
                dis = dis.prev.prev;
                blockdown(dis);
                next = dis;
                if (setfirst)
                    first[0] = dis.prev;
            }

            if (pos >= 4) /* B C A -> B A C* */ {
                blockdown(dis);
                next = dis.prev;
                if (setfirst)
                    first[0] = dis.prev.prev;
            }

            if (pos >= 5) /* B A C -> A B* C */ {
                dis = dis.prev.prev;
                blockdown(dis);
                next = dis;
                if (setfirst)
                    first[0] = dis.prev;
            }
        }

        return next;
    }

    BddTree reorder_sift_seq(BddTree t, BddTree seq[], int num) {
        BddTree dis;
        int n;

        if (t == null)
            return t;

        for (n = 0; n < num; n++) {
            long c2, c1 = clock();

            if (verbose > 1) {
                System.out.print("Sift ");
                //if (reorder_filehandler)
                //   reorder_filehandler(stdout, seq[n].id);
                //else
                System.out.print(seq[n].id);
                System.out.print(": ");
            }

            reorder_sift_bestpos(seq[n], num / 2);

            if (verbose > 1) {
                System.out.println();
                System.out.print("> " + reorder_nodenum() + " nodes");
            }

            c2 = clock();
            if (verbose > 1)
                System.out.println(
                        " (" + (float) (c2 - c1) / (float) 1000 + " sec)\n");
        }

        /* Find first block */
        for (dis = t; dis.prev != null; dis = dis.prev)
            /* nil */
            ;

        return dis;
    }

    void reorder_sift_bestpos(BddTree blk, int middlePos) {
        int best = reorder_nodenum();
        int maxAllowed;
        int bestpos = 0;
        boolean dirIsUp = true;
        int n;

        if (bddmaxnodesize > 0)
            maxAllowed =
                    MIN(best / 5 + best, bddmaxnodesize - bddmaxnodeincrease - 2);
        else
            maxAllowed = best / 5 + best;

        /* Determine initial direction */
        if (blk.pos > middlePos)
            dirIsUp = false;

        /* Move block back and forth */
        for (n = 0; n < 2; n++) {
            int first = 1;

            if (dirIsUp) {
                while (blk.prev != null
                        && (reorder_nodenum() <= maxAllowed || first != 0)) {
                    first = 0;
                    blockdown(blk.prev);
                    bestpos--;

                    if (verbose > 1) {
                        System.out.print("-");
                    }

                    if (reorder_nodenum() < best) {
                        best = reorder_nodenum();
                        bestpos = 0;

                        if (bddmaxnodesize > 0)
                            maxAllowed =
                                    MIN(
                                            best / 5 + best,
                                            bddmaxnodesize - bddmaxnodeincrease - 2);
                        else
                            maxAllowed = best / 5 + best;
                    }
                }
            } else {
                while (blk.next != null
                        && (reorder_nodenum() <= maxAllowed || first != 0)) {
                    first = 0;
                    blockdown(blk);
                    bestpos++;

                    if (verbose > 1) {
                        System.out.print("+");
                    }

                    if (reorder_nodenum() < best) {
                        best = reorder_nodenum();
                        bestpos = 0;

                        if (bddmaxnodesize > 0)
                            maxAllowed =
                                    MIN(
                                            best / 5 + best,
                                            bddmaxnodesize - bddmaxnodeincrease - 2);
                        else
                            maxAllowed = best / 5 + best;
                    }
                }
            }

            if (reorder_nodenum() > maxAllowed && verbose > 1) {
                System.out.print("!");
            }

            dirIsUp = !dirIsUp;
        }

        /* Move to best pos */
        while (bestpos < 0) {
            blockdown(blk);
            bestpos++;
        }
        while (bestpos > 0) {
            blockdown(blk.prev);
            bestpos--;
        }
    }

//    BddTree reorder_random(BddTree t) {
//        BddTree dis;
//        BddTree[] seq;
//        int n, num = 0;
//
//        if (t == null)
//            return t;
//
//        for (dis = t; dis != null; dis = dis.next)
//            num++;
//        seq = new BddTree[num];
//        for (dis = t, num = 0; dis != null; dis = dis.next)
//            seq[num++] = dis;
//
//        for (n = 0; n < 4 * num; n++) {
//            int blk = rng.nextInt(num);
//            if (seq[blk].next != null)
//                blockdown(seq[blk]);
//        }
//
//        /* Find first block */
//        for (dis = t; dis.prev != null; dis = dis.prev)
//            /* nil */;
//
//        free(seq);
//
//        if (verbose != 0)
//            System.out.println("Random order: " + reorder_nodenum() + " nodes");
//        return dis;
//    }

    static int siftTestCmp(Object aa, Object bb) {
        sizePair a = (sizePair) aa;
        sizePair b = (sizePair) bb;

        if (a.val < b.val)
            return -1;
        if (a.val > b.val)
            return 1;
        return 0;
    }

    static class sizePair {
        int val;
        BddTree block;
    }

    BddTree reorder_sift(BddTree t) {
        BddTree dis, seq[];
        sizePair[] p;
        int n, num;

        for (dis = t, num = 0; dis != null; dis = dis.next)
            dis.pos = num++;

        p = new sizePair[num];
        seq = new BddTree[num];

        for (dis = t, n = 0; dis != null; dis = dis.next, n++) {
            int v;

            /* Accumulate number of nodes for each block */
            p[n] = new sizePair();
            p[n].val = 0;
            for (v = dis.firstVar; v <= dis.lastVar; v++)
                p[n].val -= levels[v].nodenum;

            p[n].block = dis;
        }

        /* Sort according to the number of nodes at each level */
        Arrays.sort(p, 0, num, new Comparator() {

            public int compare(Object o1, Object o2) {
                return siftTestCmp(o1, o2);
            }

        });

        /* Create sequence */
        for (n = 0; n < num; n++)
            seq[n] = p[n].block;

        /* Do the sifting on this sequence */
        t = reorder_sift_seq(t, seq, num);


        return t;
    }

    BddTree reorder_siftite(BddTree t) {
        BddTree first = t;
        int lastsize;
        int c = 1;

        if (t == null)
            return t;

        do {
            if (verbose > 1)
                System.out.println("Reorder " + (c++) + "\n");

            lastsize = reorder_nodenum();
            first = reorder_sift(first);
        } while (reorder_nodenum() != lastsize);

        return first;
    }

    void blockinterleave(BddTree left) {
        BddTree right = left.next;
        //System.out.println("Interleaving "+left.first+".."+left.last+" and "+right.first+".."+right.last);
        int n;
        int leftsize = left.lastVar - left.firstVar;
        int rightsize = right.lastVar - right.firstVar;
        int[] lseq = left.seq;
        int[] rseq = right.seq;
        int minsize = Math.min(leftsize, rightsize);
        for (n = 0; n <= minsize; ++n) {
            while (bddvar2level[lseq[n]] + 1 < bddvar2level[rseq[n]]) {
                reorderVarUp(rseq[n]);
            }
        }
        outer:
        for (; n <= rightsize; ++n) {
            for (; ;) {
                BddTree t = left.prev;
                if (t == null || !t.interleaved) break outer;
                int tsize = t.lastVar - t.firstVar;
                if (n <= tsize) {
                    int[] tseq = t.seq;
                    while (bddvar2level[tseq[n]] + 1 < bddvar2level[rseq[n]]) {
                        reorderVarUp(rseq[n]);
                    }
                    break;
                }
            }
        }
        right.next.prev = left;
        left.next = right.next;
        left.firstVar = Math.min(left.firstVar, right.firstVar);
        left.lastVar = Math.max(left.lastVar, right.lastVar);
        left.seq = new int[left.lastVar - left.firstVar + 1];
        update_seq(left);
    }

    void blockdown(BddTree left) {
        BddTree right = left.next;
        //System.out.println("Swapping "+left.first+".."+left.last+" and "+right.first+".."+right.last);
        int n;
        int leftsize = left.lastVar - left.firstVar;
        int rightsize = right.lastVar - right.firstVar;
        int leftstart = bddvar2level[left.seq[0]];
        int[] lseq = left.seq;
        int[] rseq = right.seq;

        /* Move left past right */
        while (bddvar2level[lseq[0]] < bddvar2level[rseq[rightsize]]) {
            for (n = 0; n < leftsize; n++) {
                if (bddvar2level[lseq[n]] + 1 != bddvar2level[lseq[n + 1]]
                        && bddvar2level[lseq[n]] < bddvar2level[rseq[rightsize]]) {
                    reorderVarDown(lseq[n]);
                }
            }

            if (bddvar2level[lseq[leftsize]] < bddvar2level[rseq[rightsize]]) {
                reorderVarDown(lseq[leftsize]);
            }
        }

        /* Move right to where left started */
        while (bddvar2level[rseq[0]] > leftstart) {
            for (n = rightsize; n > 0; n--) {
                if (bddvar2level[rseq[n]] - 1 != bddvar2level[rseq[n - 1]]
                        && bddvar2level[rseq[n]] > leftstart) {
                    reorderVarUp(rseq[n]);
                }
            }

            if (bddvar2level[rseq[0]] > leftstart)
                reorderVarUp(rseq[0]);
        }

        /* Swap left and right data in the order */
        left.next = right.next;
        right.prev = left.prev;
        left.prev = right;
        right.next = left;

        if (right.prev != null)
            right.prev.next = right;
        if (left.next != null)
            left.next.prev = left;

        n = left.pos;
        left.pos = right.pos;
        right.pos = n;

        left.interleaved = false;
        right.interleaved = false;

        left.firstLevel = bddvar2level[lseq[0]];
        left.lastLevel = bddvar2level[lseq[leftsize]];
        right.firstLevel = bddvar2level[rseq[0]];
        right.lastLevel = bddvar2level[rseq[rightsize]];
    }

    BddTree reorder_win2ite(BddTree t) {
        BddTree dis, first = t;
        int lastsize;
        int c = 1;

        if (t == null)
            return t;

        if (verbose > 1)
            System.out.println(
                    "Win2ite start: " + reorder_nodenum() + " nodes");

        do {
            lastsize = reorder_nodenum();

            dis = t;
            while (dis.next != null) {
                int best = reorder_nodenum();

                blockdown(dis);

                if (best < reorder_nodenum()) {
                    blockdown(dis.prev);
                    dis = dis.next;
                } else if (first == dis)
                    first = dis.prev;
                if (verbose > 1) {
                    System.out.print(".");
                }
            }

            if (verbose > 1)
                System.out.println(" " + reorder_nodenum() + " nodes");
            c++;
        }
        while (reorder_nodenum() != lastsize);

        return first;
    }

    void bdd_reorder_auto() {
        if (!bdd_reorder_ready())
            return;

        bdd_reorder(bddreordermethod);
        bddreordertimes--;
    }

    int bdd_reorder_gain() {
        if (usednum_before == 0)
            return 0;

        return (100 * (usednum_before - usednum_after)) / usednum_before;
    }

    void bdd_done() {
        /*sanitycheck(); FIXME */
        //bdd_fdd_done();
        //bdd_reorder_done();
        bdd_pairs_done();


        bddNodes = null;
        bddrefstack = null;
        bddvarset = null;
        bddvar2level = null;
        bddlevel2var = null;

        bdd_operator_done();

        bddrunning = false;
        bddNodeSize = 0;
        bddmaxnodesize = 0;
        bddvarnum = 0;
        bddproduced = 0;

        univ = 1;

        //err_handler = null;
        //gbc_handler = null;
        //resize_handler = null;
    }

    int bdd_setmaxnodenum(int size) {
        if (size > bddNodeSize || size == 0) {
            int old = bddmaxnodesize;
            bddmaxnodesize = size;
            return old;
        }

        return bdd_error(BDD_NODES);
    }

    int bdd_setminfreenodes(int mf) {
        int old = minFreeNodes;

        if (mf < 0 || mf > 100)
            return bdd_error(BDD_RANGE);

        minFreeNodes = mf;
        return old;
    }

    int bdd_setmaxincrease(int size) {
        int old = bddmaxnodeincrease;

        if (size < 0)
            return bdd_error(BDD_SIZE);

        bddmaxnodeincrease = size;
        return old;
    }

    double increasefactor;

    double bdd_setincreasefactor(double x) {
        if (x < 0)
            return bdd_error(BDD_RANGE);
        double old = increasefactor;
        increasefactor = x;
        return old;
    }

    int bdd_setcacheratio(int r) {
        int old = cacheratio;

        if (r <= 0)
            return bdd_error(BDD_RANGE);
        if (bddNodeSize == 0)
            return old;

        cacheratio = r;
        bdd_operator_noderesize();
        return old;
    }

    int bdd_setvarnum(int num) {
        int bdv;
        int oldbddvarnum = bddvarnum;

        if (num < 1 || num > MAXVAR) {
            bdd_error(BDD_RANGE);
            return bddfalse;
        }

        if (num < bddvarnum)
            return bdd_error(BDD_DECVNUM);
        if (num == bddvarnum)
            return 0;

        bdd_disable_reorder();

        if (bddvarset == null) {
            bddvarset = new int[num * 2];
            bddlevel2var = new int[num + 1];
            bddvar2level = new int[num + 1];
        } else {
            int[] bddvarset2 = new int[num * 2];
            System.arraycopy(bddvarset, 0, bddvarset2, 0, bddvarset.length);
            bddvarset = bddvarset2;
            int[] bddlevel2var2 = new int[num + 1];
            System.arraycopy(
                    bddlevel2var,
                    0,
                    bddlevel2var2,
                    0,
                    bddlevel2var.length);
            bddlevel2var = bddlevel2var2;
            int[] bddvar2level2 = new int[num + 1];
            System.arraycopy(
                    bddvar2level,
                    0,
                    bddvar2level2,
                    0,
                    bddvar2level.length);
            bddvar2level = bddvar2level2;
        }

        bddrefstack = new int[num * 2 + 1];
        bddrefstacktop = 0;

        if (ZDD)
            bddvarnum = 0; // need to recreate all of them for ZDD

        univ = 1;
        for (bdv = bddvarnum; bddvarnum < num; bddvarnum++) {
            if (ZDD) {
                int res = 1, res_not = 1;
                for (int k = num - 1; k >= 0; --k) {
                    int res2 = zdd_makenode(k, (k == bddvarnum) ? 0 : res, res);
                    incrRef(res2);
                    decrRef(res);
                    res = res2;

                    int res_not2 = (k == bddvarnum) ? res_not : zdd_makenode(k, res_not, res_not);
                    incrRef(res_not2);
                    decrRef(res_not);
                    res_not = res_not2;

                    if (bdv == bddvarnum) {
                        int univ2 = zdd_makenode(k, univ, univ);
                        incrRef(univ2);
                        decrRef(univ);
                        univ = univ2;
                    }
                }
                bddvarset[bddvarnum * 2] = res;
                bddvarset[bddvarnum * 2 + 1] = res_not;
                setMaxRef(univ);
            } else {
                bddvarset[bddvarnum * 2] = PUSHREF(bdd_makenode(bddvarnum, 0, 1));
                bddvarset[bddvarnum * 2 + 1] = bdd_makenode(bddvarnum, 1, 0);
                POPREF(1);
            }

            if (bdderrorcond != 0) {
                bddvarnum = bdv;
                return -bdderrorcond;
            }

            setMaxRef(bddvarset[bddvarnum * 2]);
            setMaxRef(bddvarset[bddvarnum * 2 + 1]);
            bddlevel2var[bddvarnum] = bddvarnum;
            bddvar2level[bddvarnum] = bddvarnum;
        }

        setLevelAndMark(0, num);
        setLevelAndMark(1, num);
        bddvar2level[num] = num;
        bddlevel2var[num] = num;

        bdd_pairs_resize(oldbddvarnum, bddvarnum);
        bdd_operator_varresize();

        if (ZDD) {
            System.out.println("Changed number of ZDD variables to " + num + ", all existing ZDDs are now invalid.");
            // Need to rebuild varsets for existing domains.
            for (int n = 0; n < fdvarnum; n++) {
                domain[n].var.free();
                domain[n].var = makeSet(domain[n].ivar);
            }
        }

        bdd_enable_reorder();

        return 0;
    }

    static class BddTree implements Serializable {
        int firstVar, lastVar; /* First and last variable in this block */
        int firstLevel, lastLevel; /* First and last level in this block */
        int pos; /* Sifting position */
        int[] seq; /* Sequence of first...last in the current order */
        boolean fixed; /* Are the sub-blocks fixed or may they be reordered */
        boolean interleaved; /* Is this block interleaved with the next one */
        int id; /* A sequential id number given by addblock */
        BddTree next, prev;
        BddTree nextlevel;
    }

    /* Current auto reord. method and number of automatic reorderings left */
    int bddreordermethod;
    int bddreordertimes;

    /* Flag for disabling reordering temporarily */
    int reorderdisabled;

    BddTree vartree;
    int blockid;

    int[] extroots;
    int extrootsize;

    LevelData levels[]; /* Indexed by variable! */

    static class LevelData implements Serializable {
        int start; /* Start of this sub-table (entry in "bddnodes") */
        int size; /* Size of this sub-table */
        int maxsize; /* Max. allowed size of sub-table */
        int nodenum; /* Number of nodes in this level */
    }

    static class Imatrix implements Serializable {
        byte rows[][];
        int size;
    }

    /* Interaction matrix */
    Imatrix iactmtx;

    int verbose = 3;
    //bddinthandler reorder_handler;
    //bddfilehandler reorder_filehandler;
    //bddsizehandler reorder_nodenum;

    /* Number of live nodes before and after a reordering session */
    int usednum_before;
    int usednum_after;

    void bdd_reorder_init() {
        reorderdisabled = 0;
        vartree = null;

        bddClrVarBlocks();
        //bdd_reorder_hook(bdd_default_reohandler);
        bdd_reorder_verbose(0);
        bdd_autoreorder_times(BDD_REORDER_NONE, 0);
        //reorder_nodenum = bdd_getnodenum;
        usednum_before = usednum_after = 0;
        blockid = 0;
    }

    int reorder_nodenum() {
        return bdd_getnodenum();
    }

    int bdd_getnodenum() {
        return bddNodeSize - bddfreenum;
    }

    int bdd_reorder_verbose(int v) {
        int tmp = verbose;
        verbose = v;
        return tmp;
    }

    int bdd_autoreorder(int method) {
        int tmp = bddreordermethod;
        bddreordermethod = method;
        bddreordertimes = -1;
        return tmp;
    }

    int bdd_autoreorder_times(int method, int num) {
        int tmp = bddreordermethod;
        bddreordermethod = method;
        bddreordertimes = num;
        return tmp;
    }

    static final int BDD_REORDER_NONE = 0;
    static final int BDD_REORDER_WIN2 = 1;
    static final int BDD_REORDER_WIN2ITE = 2;
    static final int BDD_REORDER_SIFT = 3;
    static final int BDD_REORDER_SIFTITE = 4;
    static final int BDD_REORDER_WIN3 = 5;
    static final int BDD_REORDER_WIN3ITE = 6;
    static final int BDD_REORDER_RANDOM = 7;

    static final int BDD_REORDER_FREE = 0;
    static final int BDD_REORDER_FIXED = 1;

    void bdd_reorder_done() {
        bddTreeDel(vartree);
        bdd_operator_reset();
        vartree = null;
    }

    void bddTreeDel(BddTree t) {
        if (t == null) return;
        bddTreeDel(t.nextlevel);
        bddTreeDel(t.next);
        t.seq = null;
    }

    void bddClrVarBlocks() {
        bddTreeDel(vartree);
        vartree = null;
        blockid = 0;
    }

    int NODEHASHr(int var, int l, int h) {
        return (Math.abs(PAIR(l, (h)) % levels[var].size) + levels[var].start);
    }

    void bddSetVarOrder(int[] newOrder) {
        int level;

        /* Do not set order when variable-blocks are used */
        if (vartree != null) {
            bdd_error(BDD_VARBLK);
            return;
        }

        reorderInit();

        for (level = 0; level < bddvarnum; level++) {
            int lowvar = newOrder[level];

            while (bddvar2level[lowvar] > level)
                reorderVarUp(lowvar);
        }

        reorder_done();
    }

    int reorderVarUp(int var) {
        if (var < 0 || var >= bddvarnum)
            return bdd_error(BDD_VAR);
        if (bddvar2level[var] == 0)
            return 0;
        return reorderVarDown(bddlevel2var[bddvar2level[var] - 1]);
    }

    int reorderVarDown(int var) {
        int n, level;

        if (var < 0 || var >= bddvarnum)
            return bdd_error(BDD_VAR);
        if ((level = bddvar2level[var]) >= bddvarnum - 1)
            return 0;

        resizedInMakenode = false;

        if (imatrixDepends(iactmtx, var, bddlevel2var[level + 1])) {
            // This var depends on the next one.
            // (ie there is some BDD with both this var and the next one)

            // Rehash this level and return a list of nodes that depend on the
            // next level.
            int toBeProcessed = reorder_downSimple(var);
            LevelData l = levels[var];

            if (l.nodenum < (l.size) / 3
                    || l.nodenum >= (l.size * 3) / 2
                    && l.size < l.maxsize) {
                // Hash table for this level is too big or too small, resize it.
                reorder_swapResize(toBeProcessed, var);
                reorder_localGbcResize(toBeProcessed, var);
            } else {
                // Swap the variable and do a GC pass on this level.
                reorder_swap(toBeProcessed, var);
                reorder_localGbc(var);
            }
        }

        // Swap the var<->level tables
        n = bddlevel2var[level];
        bddlevel2var[level] = bddlevel2var[level + 1];
        bddlevel2var[level + 1] = n;

        n = bddvar2level[var];
        bddvar2level[var] = bddvar2level[bddlevel2var[level]];
        bddvar2level[bddlevel2var[level]] = n;

        /* Update all rename pairs */
        bdd_pairs_vardown(level);

        if (resizedInMakenode) {
            reorder_rehashAll();
        }

        return 0;
    }

    boolean imatrixDepends(Imatrix mtx, int a, int b) {
        return (mtx.rows[a][b / 8] & (1 << (b % 8))) != 0;
    }

    void reorder_setLevellookup() {
        int n;

        for (n = 0; n < bddvarnum; n++) {
            levels[n].maxsize = bddNodeSize / bddvarnum;
            levels[n].start = n * levels[n].maxsize;
            levels[n].size =
                    Math.min(levels[n].maxsize, (levels[n].nodenum * 5) / 4);

            if (levels[n].size >= 4)
                levels[n].size = bdd_prime_lte(levels[n].size);

        }
    }

    void reorder_rehashAll() {
        int n;

        reorder_setLevellookup();
        bddfreepos = 0;

        for (n = bddNodeSize - 1; n >= 0; n--)
            setHash(n, 0);

        for (n = bddNodeSize - 1; n >= 2; n--) {
            if (hasRef(n)) {
                int hash2 = NODEHASH2(VARr(n), getLow(n), getHigh(n));
                setNext(n, hash(hash2));
                setHash(hash2, n);
            } else {
                setNext(n, bddfreepos);
                bddfreepos = n;
            }
        }
    }

    void reorder_localGbc(int var0) {
        int var1 = bddlevel2var[bddvar2level[var0] + 1];
        int vl1 = levels[var1].start;
        int size1 = levels[var1].size;
        int n;

        for (n = 0; n < size1; n++) {
            int hash = n + vl1;
            int r = hash(hash);
            setHash(hash, 0);

            while (r != 0) {
                int next = next(r);

                if (hasRef(r)) {
                    setNext(r, hash(hash));
                    setHash(hash, r);
                } else {
                    decrRef(getLow(r));
                    decrRef(getHigh(r));

                    setLow(r, INVALID_BDD);
                    setNext(r, bddfreepos);
                    bddfreepos = r;
                    levels[var1].nodenum--;
                    bddfreenum++;
                }

                r = next;
            }
        }
    }

    int reorder_downSimple(int var0) {
        int toBeProcessed = 0;

        // Next variable to swap with.
        int var1 = bddlevel2var[bddvar2level[var0] + 1];

        // Hash table range for source variable.
        int vl0 = levels[var0].start;
        int size0 = levels[var0].size;
        int n;

        // Rehash this level and recalculate the number of nodes.
        levels[var0].nodenum = 0;
        for (n = 0; n < size0; n++) {
            int r;

            r = hash(n + vl0);
            setHash(n + vl0, 0);

            while (r != 0) {
                int next = next(r);

                if (VARr(getLow(r)) != var1 && VARr(getHigh(r)) != var1) {
                    // Node does not depend on next var, put it in the chain
                    setNext(r, hash(n + vl0));
                    setHash(n + vl0, r);
                    levels[var0].nodenum++;
                } else {
                    // Node depends on next var - save it for later processing
                    setNext(r, toBeProcessed);
                    toBeProcessed = r;
                    if (SWAPCOUNT)
                        cachestats.swapCount++;
                }

                r = next;
            }
        }

        return toBeProcessed;
    }

    void reorder_swapResize(int toBeProcessed, int var0) {
        int var1 = bddlevel2var[bddvar2level[var0] + 1];

        while (toBeProcessed != 0) {
            int next = next(toBeProcessed);
            int f0 = getLow(toBeProcessed);
            int f1 = getHigh(toBeProcessed);
            int f00, f01, f10, f11;

            // Find the cofactors for the new nodes
            if (VARr(f0) == var1) {
                f00 = getLow(f0);
                f01 = getHigh(f0);
            } else
                f00 = f01 = f0;

            if (VARr(f1) == var1) {
                f10 = getLow(f1);
                f11 = getHigh(f1);
            } else
                f10 = f11 = f1;

            /* Note: makenode does refcou. */
            f0 = reorder_makenode(var0, f00, f10);
            f1 = reorder_makenode(var0, f01, f11);
            //node = bddnodes[toBeProcessed]; /* Might change in makenode */

            /* We know that the refcou of the grandchilds of this node
            * is greater than one (these are f00...f11), so there is
            * no need to do a recursive refcou decrease. It is also
            * possible for the node.low/high nodes to come alive again,
            * so deref. of the childs is delayed until the local GBC. */

            decrRef(getLow(toBeProcessed));
            decrRef(getHigh(toBeProcessed));

            // Update in-place
            SETVARr(toBeProcessed, var1);
            setLow(toBeProcessed, f0);
            setHigh(toBeProcessed, f1);

            levels[var1].nodenum++;

            // Do not rehash yet since we are going to resize the hash table

            toBeProcessed = next;
        }
    }

    static final int MIN(int a, int b) {
        return Math.min(a, b);
    }

    void reorder_localGbcResize(int toBeProcessed, int var0) {
        int var1 = bddlevel2var[bddvar2level[var0] + 1];
        int vl1 = levels[var1].start;
        int size1 = levels[var1].size;
        int n;

        for (n = 0; n < size1; n++) {
            int hash = n + vl1;
            int r = hash(hash);
            setHash(hash, 0);

            while (r != 0) {
                int next = next(r);

                if (hasRef(r)) {
                    setNext(r, toBeProcessed);
                    toBeProcessed = r;
                } else {
                    decrRef(getLow(r));
                    decrRef(getHigh(r));

                    setLow(r, INVALID_BDD);
                    setNext(r, bddfreepos);
                    bddfreepos = r;
                    levels[var1].nodenum--;
                    bddfreenum++;
                }

                r = next;
            }
        }

        /* Resize */
        if (levels[var1].nodenum < levels[var1].size)
            levels[var1].size =
                    MIN(levels[var1].maxsize, levels[var1].size / 2);
        else
            levels[var1].size =
                    MIN(levels[var1].maxsize, levels[var1].size * 2);

        if (levels[var1].size >= 4)
            levels[var1].size = bdd_prime_lte(levels[var1].size);

        /* Rehash the remaining live nodes */
        while (toBeProcessed != 0) {
            int next = next(toBeProcessed);
            int hash = NODEHASH2(VARr(toBeProcessed), getLow(toBeProcessed), getHigh(toBeProcessed));

            setNext(toBeProcessed, hash(hash));
            setHash(hash, toBeProcessed);

            toBeProcessed = next;
        }
    }

    void reorder_swap(int toBeProcessed, int var0) {
        int var1 = bddlevel2var[bddvar2level[var0] + 1];

        // toBeProcessed is a linked list of nodes that depend on the next level.

        while (toBeProcessed != 0) {
            int next = next(toBeProcessed);
            int f0 = getLow(toBeProcessed);
            int f1 = getHigh(toBeProcessed);
            int f00, f01, f10, f11, hash;

            // Find the cofactors for the new nodes
            if (VARr(f0) == var1) {
                f00 = getLow(f0);
                f01 = getHigh(f0);
            } else
                f00 = f01 = f0;

            if (VARr(f1) == var1) {
                f10 = getLow(f1);
                f11 = getHigh(f1);
            } else
                f10 = f11 = f1;

            /* Note: makenode does refcou. */
            f0 = reorder_makenode(var0, f00, f10);
            f1 = reorder_makenode(var0, f01, f11);
            //node = bddnodes[toBeProcessed]; /* Might change in makenode */

            /* We know that the refcou of the grandchilds of this node
             * is greater than one (these are f00...f11), so there is
             * no need to do a recursive refcou decrease. It is also
             * possible for the node.low/high nodes to come alive again,
             * so deref. of the childs is delayed until the local GBC. */

            decrRef(getLow(toBeProcessed));
            decrRef(getHigh(toBeProcessed));

            // Update in-place
            // NOTE: This node may be a duplicate.  However, we add this to the start
            // of the list so we will always encounter this one first.  The refcount
            // of the node we duplicated will go to zero.
            SETVARr(toBeProcessed, var1);
            setLow(toBeProcessed, f0);
            setHigh(toBeProcessed, f1);

            levels[var1].nodenum++;

            // Rehash the node since it has new children
            hash = NODEHASH2(VARr(toBeProcessed), getLow(toBeProcessed), getHigh(toBeProcessed));
            setNext(toBeProcessed, hash(hash));
            setHash(hash, toBeProcessed);

            toBeProcessed = next;
        }
    }

    int NODEHASH2(int var, int l, int h) {
        return (Math.abs(PAIR(l, h) % levels[var].size) + levels[var].start);
    }

    boolean resizedInMakenode;

    int reorder_makenode(int var, int low, int high) {
        int hash;
        int res;

        if (CACHESTATS)
            cachestats.uniqueAccess++;

        /* Note: We know that low,high has a refcou greater than zero, so
           there is no need to add reference *recursively* */

        if (ZDD) {
            /* check whether high child is zero */
            if (high == 0) {
                incrRef(low);
                return low;
            }
        } else {
            /* check whether childs are equal */
            if (low == high) {
                incrRef(low);
                return low;
            }
        }

        /* Try to find an existing node of this kind */
        hash = NODEHASH2(var, low, high);
        res = hash(hash);

        while (res != 0) {
            if (getLow(res) == low && getHigh(res) == high) {
                if (CACHESTATS)
                    cachestats.uniqueHit++;
                incrRef(res);
                return res;
            }
            res = next(res);

            if (CACHESTATS)
                cachestats.uniqueChain++;
        }

        /* No existing node -> build one */
        if (CACHESTATS)
            cachestats.uniqueMiss++;

        /* Any free nodes to use ? */
        if (bddfreepos == 0) {
            if (bdderrorcond != 0)
                return 0;

            /* Try to allocate more nodes - call noderesize without
             * enabling rehashing.
             * Note: if ever rehashing is allowed here, then remember to
             * update local variable "hash" */
            bdd_noderesize(false);
            resizedInMakenode = true;

            /* Panic if that is not possible */
            if (bddfreepos == 0) {
                bdd_error(BDD_NODENUM);
                bdderrorcond = Math.abs(BDD_NODENUM);
                return 0;
            }
        }

        /* Build new node */
        res = bddfreepos;
        bddfreepos = next(bddfreepos);
        levels[var].nodenum++;
        bddproduced++;
        bddfreenum--;

        SETVARr(res, var);
        setLow(res, low);
        setHigh(res, high);

        /* Insert node in hash chain */
        setNext(res, hash(hash));
        setHash(hash, res);

        /* Make sure it is reference counted */
        clearRef(res);
        incrRef(res);
        incrRef(getLow(res));
        incrRef(getHigh(res));

        return res;
    }

    int reorderInit() {
        // This method does the following:
        //  - Calculate interaction matrix "iactmtx"
        //  - Calculates the number of nodes with each variable.
        //  - Mutates each node to store the var instead of the level.
        //  - Sets refcounts for all links, including internal ones.

        int n;

        reorder_handler(true, reorderstats);

        // Split the hash table into a separate region for each variable.
        levels = new LevelData[bddvarnum];
        for (n = 0; n < bddvarnum; n++) {
            levels[n] = new LevelData();
            levels[n].start = -1;
            levels[n].size = 0;
            levels[n].nodenum = 0;
        }

        // First mark and recursive refcou. all roots and childs. Also do some
        // setup here for both setLevellookup and reorder_gbc
        if (mark_roots() < 0)
            return -1;

        // Initialize the hash tables
        reorder_setLevellookup();

        // Garbage collect and rehash to new scheme
        reorder_gbc();

        return 0;
    }

    int mark_roots() {
        boolean[] dep = new boolean[bddvarnum];
        int n;

        for (n = 2, extrootsize = 0; n < bddNodeSize; n++) {
            /* This is where we go from .level to .var!
            * - Do NOT use the LEVEL macro here. */
            setLevelAndMark(n, bddlevel2var[getLevelAndMark(n)]);

            if (hasRef(n)) {
                setMark(n);
                extrootsize++;
            }
        }

        extroots = new int[extrootsize];

        iactmtx = imatrixNew(bddvarnum);

        // Loop to compute dependences and node refcounts.
        for (n = 2, extrootsize = 0; n < bddNodeSize; n++) {

            if (mark(n)) {
                // Node has an external reference.
                unmark(n);
                extroots[extrootsize++] = n;

                // Calculate the set of variables in this BDD.
                // Also sets refcounts on internal nodes.
                for (int i = 0; i < bddvarnum; ++i)
                    dep[i] = false;

                dep[VARr(n)] = true;
                levels[VARr(n)].nodenum++;

                addref_rec(getLow(n), dep);
                addref_rec(getHigh(n), dep);

                addDependencies(dep);
            }

            /* Make sure the hash field is empty. This saves a loop in the
            initial GBC */
            setHash(n, 0);
        }

        setHash(0, 0);
        setHash(1, 0);

        return 0;
    }

    Imatrix imatrixNew(int size) {
        Imatrix mtx = new Imatrix();
        int n;

        mtx.rows = new byte[size][];

        for (n = 0; n < size; n++) {
            mtx.rows[n] = new byte[size / 8 + 1];
        }

        mtx.size = size;

        return mtx;
    }

    void addref_rec(int r, boolean[] dep) {
        if (r < 2)
            return;

        if (!hasRef(r) || mark(r)) {
            // We haven't processed the node yet.
            // Processed nodes have a refcount and are unmarked.

            bddfreenum--;

            // Detect variable dependencies for the interaction matrix
            dep[VARr(r) & ~MARK_MASK] = true;

            // Make sure the nodenum field is updated. Used in the initial GBC
            levels[VARr(r) & ~MARK_MASK].nodenum++;

            addref_rec(getLow(r), dep);
            addref_rec(getHigh(r), dep);
        } else {
            int n;

            // Update (from previously found) variable dependencies
            // for the interaction matrix
            for (n = 0; n < bddvarnum; n++)
                dep[n]
                        |= imatrixDepends(iactmtx, VARr(r) & ~MARK_MASK, n);
        }

        incrRef(r);
    }

    void addDependencies(boolean[] dep) {
        int n, m;

        for (n = 0; n < bddvarnum; n++) {
            for (m = n; m < bddvarnum; m++) {
                if ((dep[n]) && (dep[m])) {
                    imatrixSet(iactmtx, n, m);
                    imatrixSet(iactmtx, m, n);
                }
            }
        }
    }

    void imatrixSet(Imatrix mtx, int a, int b) {
        mtx.rows[a][b / 8] |= 1 << (b % 8);
    }

    void reorder_gbc() {
        int n;

        bddfreepos = 0;
        bddfreenum = 0;

        /* No need to zero all hash fields - this is done in mark_roots */

        for (n = bddNodeSize - 1; n >= 2; n--) {

            if (hasRef(n)) {
                int hash;

                hash = NODEHASH2(VARr(n), getLow(n), getHigh(n));
                setNext(n, hash(hash));
                setHash(hash, n);

            } else {
                setLow(n, INVALID_BDD);
                setNext(n, bddfreepos);
                bddfreepos = n;
                bddfreenum++;
            }
        }
    }

    void reorder_done() {
        int n;

        for (n = 0; n < extrootsize; n++)
            setMark(extroots[n]);
        for (n = 2; n < bddNodeSize; n++) {
            if (mark(n))
                unmark(n);
            else
                clearRef(n);

            /* This is where we go from .var to .level again!
             * - Do NOT use the LEVEL macro here. */
            setLevelAndMark(n, bddvar2level[getLevelAndMark(n)]);
        }



        imatrixDelete(iactmtx);
        bdd_gbc();

        reorder_handler(false, reorderstats);
    }

    void imatrixDelete(Imatrix mtx) {
        int n;

        for (n = 0; n < mtx.size; n++) {
            mtx.rows[n] = null;
        }
        mtx.rows = null;
    }

    int bdd_getallocnum() {
        return bddNodeSize;
    }

    int bdd_setallocnum(int size) {
        int old = bddNodeSize;
        doResize(true, old, size);
        return old;
    }

    int bdd_swapvar(int v1, int v2) {
        int l1, l2;

        /* Do not swap when variable-blocks are used */
        if (vartree != null)
            return bdd_error(BDD_VARBLK);

        /* Don't bother swapping x with x */
        if (v1 == v2)
            return 0;

        /* Make sure the variable exists */
        if (v1 < 0 || v1 >= bddvarnum || v2 < 0 || v2 >= bddvarnum)
            return bdd_error(BDD_VAR);

        l1 = bddvar2level[v1];
        l2 = bddvar2level[v2];

        /* Make sure v1 is before v2 */
        if (l1 > l2) {
            int tmp = v1;
            v1 = v2;
            v2 = tmp;
            l1 = bddvar2level[v1];
            l2 = bddvar2level[v2];
        }

        reorderInit();

        /* Move v1 to v2's position */
        while (bddvar2level[v1] < l2)
            reorderVarDown(v1);

        /* Move v2 to v1's position */
        while (bddvar2level[v2] > l1)
            reorderVarUp(v2);

        reorder_done();

        return 0;
    }

    void bdd_fprintall(PrintStream out) {
        int n;

        for (n = 0; n < bddNodeSize; n++) {
            if (getLow(n) != INVALID_BDD) {
                out.print(
                        "["
                                + right(n, 5)
                                + " - "
                                + right(getRef(n), 2)
                                + "] ");
                // TODO: labelling of vars
                out.print(right(bddlevel2var[getLevel(n)], 3));

                out.print(": " + right(getLow(n), 3));
                out.println(" " + right(getHigh(n), 3));
            }
        }
    }

    void bdd_fprinttable(PrintStream out, int r) {
        int n;

        out.println("ROOT: " + r);
        if (r < 2)
            return;

        bdd_mark(r);

        for (n = 0; n < bddNodeSize; n++) {
            if (mark(n)) {
                unmark(n);

                out.print("[" + right(n, 5) + "] ");
                // TODO: labelling of vars
                out.print(right(bddlevel2var[getLevel(n)], 3));

                out.print(": " + right(getLow(n), 3));
                out.println(" " + right(getHigh(n), 3));
            }
        }
    }

    int lh_nodenum;
    int lh_freepos;
    int[] loadvar2level;
    LoadHash[] lh_table;

//    int bdd_load(BufferedReader ifile, int[] translate) throws IOException {
//        int n, vnum, tmproot;
//        int root;
//
//        lh_nodenum = Integer.parseInt(readNext(ifile));
//        vnum = Integer.parseInt(readNext(ifile));
//
//        // Check for constant true / false
//        if (lh_nodenum == 0 && vnum == 0) {
//            root = Integer.parseInt(readNext(ifile));
//            return root;
//        }
//
//        // Not actually used.
//        loadvar2level = new int[vnum];
//        for (n = 0; n < vnum; n++) {
//            loadvar2level[n] = Integer.parseInt(readNext(ifile));
//        }
//
//        if (vnum > bddvarnum)
//            bdd_setvarnum(vnum);
//
//        lh_table = new LoadHash[lh_nodenum];
//
//        for (n = 0; n < lh_nodenum; n++) {
//            lh_table[n] = new LoadHash();
//            lh_table[n].first = -1;
//            lh_table[n].next = n + 1;
//        }
//        lh_table[lh_nodenum - 1].next = -1;
//        lh_freepos = 0;
//
//        tmproot = bdd_loaddata(ifile, translate);
//
//        for (n = 0; n < lh_nodenum; n++)
//            bdd_delref(lh_table[n].data);
//
//        free(lh_table);
//        lh_table = null;
//        free(loadvar2level);
//        loadvar2level = null;
//
//        root = tmproot;
//        return root;
//    }

    static class LoadHash {
        int key;
        int data;
        int first;
        int next;
    }

//    int bdd_loaddata(BufferedReader ifile, int[] translate) throws IOException {
//        int key, var, low, high, root = 0, n;
//
//        for (n = 0; n < lh_nodenum; n++) {
//            key = Integer.parseInt(readNext(ifile));
//            var = Integer.parseInt(readNext(ifile));
//            if (translate != null)
//                var = translate[var];
//            low = Integer.parseInt(readNext(ifile));
//            high = Integer.parseInt(readNext(ifile));
//
//            if (low >= 2)
//                low = loadhash_get(low);
//            if (high >= 2)
//                high = loadhash_get(high);
//
//            if (low < 0 || high < 0 || var < 0)
//                return bdd_error(BDD_FORMAT);
//
//            if (ZDD) {
//                // The terminal "1" in BDD means universal set.
//                if (low == 1) low = univ;
//                if (high == 1) high = univ;
//            }
//
//            root = bdd_addref(bdd_ite(bdd_ithvar(var), high, low));
//
//            loadhash_add(key, root);
//        }
//
//        return root;
//    }

    void loadhash_add(int key, int data) {
        int hash = key % lh_nodenum;
        int pos = lh_freepos;

        lh_freepos = lh_table[pos].next;
        lh_table[pos].next = lh_table[hash].first;
        lh_table[hash].first = pos;

        lh_table[pos].key = key;
        lh_table[pos].data = data;
    }

    int loadhash_get(int key) {
        int hash = lh_table[key % lh_nodenum].first;

        while (hash != -1 && lh_table[hash].key != key)
            hash = lh_table[hash].next;

        if (hash == -1)
            return -1;
        return lh_table[hash].data;
    }


    // TODO: revisit for ZDD

    static String right(int x, int w) {
        return right(Integer.toString(x), w);
    }

    static String right(String s, int w) {
        int n = s.length();
        //if (w < n) return s.substring(n - w);
        StringBuffer b = new StringBuffer(w);
        for (int i = n; i < w; ++i) {
            b.append(' ');
        }
        b.append(s);
        return b.toString();
    }

    int bdd_intaddvarblock(int first, int last, boolean fixed) {
        BddTree t;

        if (first < 0 || first >= bddvarnum || last < 0 || last >= bddvarnum)
            return bdd_error(BDD_VAR);

        if ((t = bddtree_addrange(vartree, first, last, fixed, blockid))
                == null)
            return bdd_error(BDD_VARBLK);

        vartree = t;
        return blockid++;
    }

    BddTree bddtree_addrange_rec(BddTree t, BddTree prev,
                                 int first, int last, boolean fixed, int id) {
        if (first < 0 || last < 0 || last < first)
            return null;

        /* Empty tree -> build one */
        if (t == null) {
            t = bddtree_new(id);
            t.firstVar = first;
            t.firstLevel = bddvar2level[first];
            t.fixed = fixed;
            t.seq = new int[last - first + 1];
            t.lastVar = last;
            t.lastLevel = bddvar2level[last];
            update_seq(t);
            t.prev = prev;
            return t;
        }

        /* Check for identity */
        if (first == t.firstVar && last == t.lastVar)
            return t;

        int firstLev = Math.min(bddvar2level[first], bddvar2level[last]);
        int lastLev = Math.max(bddvar2level[first], bddvar2level[last]);

        /* Inside this section -> insert in next level */
        if (firstLev >= t.firstLevel && lastLev <= t.lastLevel) {
            t.nextlevel =
                    bddtree_addrange_rec(t.nextlevel, null, first, last, fixed, id);
            return t;
        }

        /* Before this section -> insert */
        if (lastLev < t.firstLevel) {
            BddTree tnew = bddtree_new(id);
            tnew.firstVar = first;
            tnew.firstLevel = firstLev;
            tnew.lastVar = last;
            tnew.lastLevel = lastLev;
            tnew.fixed = fixed;
            tnew.seq = new int[last - first + 1];
            update_seq(tnew);
            tnew.next = t;
            tnew.prev = t.prev;
            t.prev = tnew;
            return tnew;
        }

        /* After this this section -> go to next */
        if (firstLev > t.lastLevel) {
            t.next = bddtree_addrange_rec(t.next, t, first, last, fixed, id);
            return t;
        }

        /* Covering this section -> insert above this level */
        if (firstLev <= t.firstLevel) {
            BddTree tnew;
            BddTree dis = t;

            while (true) {
                /* Partial cover ->error */
                if (lastLev >= dis.firstLevel && lastLev < dis.lastLevel)
                    return null;

                if (dis.next == null || last < dis.next.firstLevel) {
                    tnew = bddtree_new(id);
                    tnew.firstVar = first;
                    tnew.firstLevel = firstLev;
                    tnew.lastVar = last;
                    tnew.lastLevel = lastLev;
                    tnew.fixed = fixed;
                    tnew.seq = new int[last - first + 1];
                    update_seq(tnew);
                    tnew.nextlevel = t;
                    tnew.next = dis.next;
                    tnew.prev = t.prev;
                    if (dis.next != null)
                        dis.next.prev = tnew;
                    dis.next = null;
                    t.prev = null;
                    return tnew;
                }

                dis = dis.next;
            }

        }

        return null;
    }

    void update_seq(BddTree t) {
        int n;
        int low = t.firstVar;
        int high = t.lastVar;

        for (n = t.firstVar; n <= t.lastVar; n++) {
            if (bddvar2level[n] < bddvar2level[low])
                low = n;
            if (bddvar2level[n] > bddvar2level[high])
                high = n;
        }

        for (n = t.firstVar; n <= t.lastVar; n++)
            t.seq[bddvar2level[n] - bddvar2level[low]] = n;

        t.firstLevel = bddvar2level[low];
        t.lastLevel = bddvar2level[high];
    }

    BddTree bddtree_addrange(BddTree t, int first, int last, boolean fixed, int id) {
        return bddtree_addrange_rec(t, null, first, last, fixed, id);
    }

    void bdd_varblockall() {
        int n;

        for (n = 0; n < bddvarnum; n++)
            bdd_intaddvarblock(n, n, true);
    }

    void print_order_rec(PrintStream o, BddTree t, int level) {
        if (t == null)
            return;

        if (t.nextlevel != null) {
            for (int i = 0; i < level; ++i)
                o.print("   ");
            // todo: better reorder id printout
            o.print(right(t.id, 3));
            if (t.interleaved) o.print('x');
            o.println("{\n");

            print_order_rec(o, t.nextlevel, level + 1);

            for (int i = 0; i < level; ++i)
                o.print("   ");
            // todo: better reorder id printout
            o.print(right(t.id, 3));
            o.println("}\n");

            print_order_rec(o, t.next, level);
        } else {
            for (int i = 0; i < level; ++i)
                o.print("   ");
            // todo: better reorder id printout
            o.print(right(t.id, 3));
            if (t.interleaved) o.print('x');
            o.println();

            print_order_rec(o, t.next, level);
        }
    }

    void bdd_fprintorder(PrintStream ofile) {
        print_order_rec(ofile, vartree, 0);
    }

    void bdd_fprintstat(PrintStream out) {
        CacheStats s = cachestats;
        out.print(s.toString());
    }

    void bdd_validate_all() {
        int n;
        for (n = bddNodeSize - 1; n >= 2; n--) {
            if (hasRef(n)) {
                bdd_validate(n);
            }
        }
    }

    void bdd_validate(int k) {
        try {
            validate(k, -1);
        } finally {
            bdd_unmark(k);
        }
    }

    void validate(int k, int lastLevel) {
        if (k < 2) return;
        int lev = getLevel(k);
        //System.out.println("Level("+k+") = "+lev);
        if (lev <= lastLevel)
            throw new BDDException(lev + " <= " + lastLevel);
        if (ZDD) {
            if (getHigh(k) == 0)
                throw new BDDException("HIGH(" + k + ")==0");
        } else {
            if (getLow(k) == getHigh(k))
                throw new BDDException("LOW(" + k + ") == HIGH(" + k + ")");
        }
        if (mark(k)) return;
        setMark(k);
        //System.out.println("Low:");
        validate(getLow(k), lev);
        //System.out.println("High:");
        validate(getHigh(k), lev);
    }

    //// Prime stuff below.

//    Random rng = new Random();

//    final int Random(int i) {
//        return rng.nextInt(i) + 1;
//    }

    static boolean isEven(int src) {
        return (src & 0x1) == 0;
    }

    static boolean hasFactor(int src, int n) {
        return (src != n) && (src % n == 0);
    }

    static boolean BitIsSet(int src, int b) {
        return (src & (1 << b)) != 0;
    }

    static final int CHECKTIMES = 20;

    static final int u64_mulmod(int a, int b, int c) {
        return (int) (((long) a * (long) b) % (long) c);
    }

    /*************************************************************************
     Miller Rabin check
     *************************************************************************/

    static int numberOfBits(int src) {
        int b;

        if (src == 0)
            return 0;

        for (b = 31; b > 0; --b)
            if (BitIsSet(src, b))
                return b + 1;

        return 1;
    }

    static boolean isWitness(int witness, int src) {
        int bitNum = numberOfBits(src - 1) - 1;
        int d = 1;
        int i;

        for (i = bitNum; i >= 0; --i) {
            int x = d;

            d = u64_mulmod(d, d, src);

            if (d == 1 && x != 1 && x != src - 1)
                return true;

            if (BitIsSet(src - 1, i))
                d = u64_mulmod(d, witness, src);
        }

        return d != 1;
    }

    boolean isMillerRabinPrime(int src) {
//        throw new UnsupportedOperationException("Dave");
//        int n;
//
//        for (n = 0; n < CHECKTIMES; ++n) {
//            int witness = Random(src - 1);
//
//            if (isWitness(witness, src))
//                return false;
//        }
        return true;
    }

    /*************************************************************************
     Basic prime searching stuff
     *************************************************************************/

    static boolean hasEasyFactors(int src) {
        return hasFactor(src, 3)
                || hasFactor(src, 5)
                || hasFactor(src, 7)
                || hasFactor(src, 11)
                || hasFactor(src, 13);
    }

    boolean isPrime(int src) {
        if (hasEasyFactors(src))
            return false;

        return isMillerRabinPrime(src);
    }

    /*************************************************************************
     External interface
     *************************************************************************/

    int bddPrimeGte(int src) {
        if (isEven(src))
            ++src;

        while (!isPrime(src))
            src += 2;

        return src;
    }

    int bdd_prime_lte(int src) {
        if (isEven(src))
            --src;

        while (!isPrime(src))
            src -= 2;

        return src;
    }

}
