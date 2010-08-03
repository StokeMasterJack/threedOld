package com.tms.threed.util.vnode.server;

import java.io.File;

public interface VFileFilter {

    boolean accept(File file, File root, int depth);

}
