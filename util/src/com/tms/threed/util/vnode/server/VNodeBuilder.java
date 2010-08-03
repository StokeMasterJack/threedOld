package com.tms.threed.util.vnode.server;

import com.tms.threed.util.vnode.shared.VNode;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class VNodeBuilder {

    private FileFilter fileFilter = EXCLUDE_HIDDEN_FILE_FILTER;
    private VFileFilter vFileFilter = INCLUDE_ALL_VFILE_FILTER;
    private final File rootFile;


    public VNodeBuilder(File rootFile, FileFilter fileFilter) {
        this.rootFile = rootFile;
        if (fileFilter == null) {
            this.fileFilter = EXCLUDE_HIDDEN_FILE_FILTER;
        } else {
            this.fileFilter = fileFilter;
        }
    }

//    public VNodeBuilder(File rootFile, VNodeBuilderFileFilter fileFilter) {
//        this(rootFile, toFileFilter(fileFilter, rootFile));
//    }

    public VNodeBuilder(File rootFile) {
        this(rootFile, (FileFilter) null);
    }

    public void setFileFilter(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }

    public void setVFileFilter(VFileFilter vFileFilter) {
        this.vFileFilter = vFileFilter;
    }

    public VNode buildVNode() {
        return buildVNode(rootFile, 0);
    }

    private VNode buildVNode(File f, int depth) {
        String name = f.getName();
        assert name != null;
        name = name.trim();
        boolean directory = f.isDirectory();

        List<VNode> childNodes;
        if (directory && f.canRead()) {
            childNodes = createChildNodes(f, depth + 1);
        } else {
            childNodes = null;
        }

        return new VNode(name, directory, childNodes, depth);
    }

    private List<VNode> createChildNodes(File parent, int depth) {

        File[] childFiles = parent.listFiles(fileFilter);
        if (childFiles == null || childFiles.length == 0) {
            return null;
        }
        List<VNode> a = new ArrayList<VNode>();
        for (File childFile : childFiles) {

            if (vFileFilter.accept(childFile, rootFile, depth)) {
                VNode childVNode = buildVNode(childFile, depth);
                assert childVNode != null;
                a.add(childVNode);
            }
        }
        return a;
    }

    public static final VFileFilter INCLUDE_ALL_VFILE_FILTER = new VFileFilter() {
        @Override public boolean accept(File file, File root, int depth) {
            return true;
        }
    };

    public static final FileFilter EXCLUDE_HIDDEN_FILE_FILTER = new FileFilter() {
        @Override public boolean accept(File f) {
            return !isHidden(f);
        }
    };

    public static boolean isHidden(File f) {
        return f.isHidden() || f.getName().charAt(0) == '.';
    }
}
