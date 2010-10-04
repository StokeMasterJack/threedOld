package com.tms.threed.imageModel.server;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.imageModel.shared.ImPng;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesInfoBuilder;
import com.tms.threed.util.lang.shared.Strings;
import com.tms.threed.util.vnode.server.VFileFilter;
import com.tms.threed.util.vnode.shared.VNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VNodeBuilder {

    private final SeriesInfo seriesInfo;
    private final FeatureModel featureModel;
    private final File seriesDir;

    private boolean displayRejections;

    private final List<Veto> rejects = new ArrayList<Veto>();

    private final com.tms.threed.util.vnode.server.VNodeBuilder vNodeBuilder;

    public VNodeBuilder(FeatureModel featureModel, File seriesDir) {
        this.seriesInfo = SeriesInfoBuilder.createSeriesInfo(featureModel.getSeriesKey());
        this.featureModel = featureModel;
        this.seriesDir = seriesDir;
        this.vNodeBuilder = createVNodeBuilder();
    }

    public void setDisplayRejections(boolean displayRejections) {
        this.displayRejections = displayRejections;
    }

    public void setVFileFilter(ImVFileFilter vFileFilter) {
        this.vFileFilter = vFileFilter;
    }

    public VNode buildSeriesVNode() {
        return buildSeriesVNode(false);
    }

    public VNode buildSeriesVNode(boolean displayRejections) {
        this.displayRejections = displayRejections;
        VNode vNode = vNodeBuilder.buildVNode();
        if (displayRejections) {
            printVetoes();
        }
        return vNode;
    }

    private void printVetoes() {
        for (Veto veto : rejects) {
            veto.print();
        }
    }

    private com.tms.threed.util.vnode.server.VNodeBuilder createVNodeBuilder() {
        com.tms.threed.util.vnode.server.VNodeBuilder builder;
        builder = new com.tms.threed.util.vnode.server.VNodeBuilder(seriesDir);
        builder.setVFileFilter(vFileFilter);
        return builder;
    }

    private ImVFileFilter vFileFilter = new ImVFileFilter();

    public static class Veto {
        private final File file;
        private final String reason;

        public Veto(File file, String reason) {
            this.file = file;
            this.reason = reason;
        }

        public File getFile() {
            return file;
        }

        public String getReason() {
            return reason;
        }

        public void print() {
            System.out.println(toString());
        }

        @Override public String toString() {return reason + "  " + file;}
    }

    private class ImVFileFilter implements VFileFilter {

        @Override public boolean accept(File file, File root, int depth) {
            return acceptInternal(file, root, depth);
        }

        void veto(File f, String reason) {
            if (displayRejections) {
                rejects.add(new Veto(f, reason));
            }
        }

        boolean acceptInternal(File file, File root, int depth) {
            NodeLevel nl = NodeLevel.get(depth);
            if (nl.isSeries()) {
                if (!isValidSeriesName(file)) {
                    veto(file, "inValidLocalSeriesName");
                    return false;
                }
            } else if (nl.isView()) {
                if (!file.isDirectory()) {
                    veto(file, "non-dir view");
                    return false;
                }
                if (!isValidViewName(file)) {
                    veto(file, "inValidLocalViewName");
                    return false;
                }
            } else if (nl.isLayer()) {
                if (!file.isDirectory()) {
                    veto(file, "non-dir layer");
                    return false;
                }
            } else {
                if (file.isFile()) {
                    //should be a png
                    boolean b = ImPng.isValidLocalName(file.getName());
                    if (!b) veto(file, "inValidLocalPngName");
                    return b;
                }
                if (file.isDirectory()) {
                    //should be a feature
                    if (Strings.containsNonwordChar(file.getName())) {
                        veto(file, "feature code containsNonwordChar");
                        return false;
                    }
                    String varCode = file.getName();
                    if (!featureModel.containsCode(varCode)) {
                        veto(file, "feature code not in fm");
                        return false;
                    }
                }
            }
            return true;
        }


        private boolean isValidSeriesName(File file) {
            return seriesInfo.getSeriesName().equals(file.getName());
        }

        private boolean isValidViewName(File file) {
            return seriesInfo.isValidViewName(file.getName());
        }

    }

    private static final Log log = LogFactory.getLog(VNodeBuilder.class);
}
