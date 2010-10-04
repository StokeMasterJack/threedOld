package com.tms.threed.util.vnode.shared;

import com.tms.threed.util.lang.shared.Path;
import com.tms.threed.util.lang.shared.Strings;

import java.util.Iterator;
import java.util.List;


public class VNode {

    private VNode _parent;

    protected final int depth;
    protected final String name;
    protected final boolean directory;
    protected final List<VNode> childNodes;

    public VNode(String name, boolean directory, List<VNode> childNodes, int depth) {
        this.name = name;
        this.directory = directory;
        this.depth = depth;

        if (childNodes != null) {
            for (VNode childNode : childNodes) {
                childNode.initParent(this);
            }
            this.childNodes = childNodes;
        } else {
            this.childNodes = null;
        }
    }

    void initParent(VNode parent) {
        assert (!isRoot());
        assert (this._parent == null);
        assert (parent != null);
        this._parent = parent;
    }

    public String getName() {
        return name;
    }

    public List<VNode> getChildNodes() {
        return childNodes;
    }

    public boolean hasChildNodes() {
        if (childNodes == null) return false;
        if (childNodes.isEmpty()) return false;
        return true;
    }

    public boolean isDirectory() {
        return directory;
    }

    public Path getLocalPath(){
        return new Path(getName());
    }

    public Path getPath() {
        VNode parent = getParent();
        if(parent==null) return getLocalPath();
        return parent.getPath().append(getLocalPath());
    }

    public boolean isRoot() {
        return depth == 0;
    }

    public void filterChildNodes(VNodeFilter filter) {
        if (childNodes == null) return;
        Iterator<VNode> it = childNodes.iterator();
        while (it.hasNext()) {
            VNode node = it.next();
            if (!filter.accept(node)) {
                it.remove();
            } else {
                node.filterChildNodes(filter);
            }
        }
    }


    public boolean isLeaf() {
        return !directory;
    }

    public void printTree() {
        int d = getDepth();
        System.out.println(Strings.tab(d) + name);
        if (!hasChildNodes()) return;
        for (VNode childNode : childNodes) {
            childNode.printTree();
        }
    }

    public int getDepth() {
        return depth;
    }

    public VNode getParent() {
        if (isParentReady()) return _parent;
        else throw new IllegalStateException("parent not set yet. Cannot access parent while node is being build");
    }

    public boolean isParentReady() {
        return isRoot() || _parent != null;
    }

    public int getTotalNodeCount() {
        Counter counter = new Counter();
        count(counter);
        return counter.count;
    }

    private class Counter {
        int count;

        void incr() {count++;}
    }

    private void count(Counter counter) {
        counter.incr();
        if (hasChildNodes()) {
            for (VNode node : childNodes) {
                node.count(counter);
            }
        }
    }

    public void process(Command command) {
        command.execute(this);
        if (!hasChildNodes()) return;
        for (VNode childNode : childNodes) {
            childNode.process(command);
        }
    }

    @Override public String toString() {
        return getName();
    }

    //    @Override public void printTreeX() {
//        int d = getDepth();
//        System.out.println(tab(d) + getName());
//        if (isParent()) {
//            IsParent p = (IsParent) this;
//            for (Object o : p.getChildNodes()) {
//                ImNode n = (ImNode) o;
//                n.printTree();
//            }
//        }
//
//    }
}
