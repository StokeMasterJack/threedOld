package com.tms.threed.threedCore.server;

import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.util.lang.shared.Path;

public class ThreedConfigHelper extends ConfigHelper {


    public String getJpgRootHttp() {
        return getProperty("threed.jpgRootHttp");
    }

    public String getPngRootHttp() {
        return getProperty("threed.pngRootHttp");
    }

    public String getThreedRootHttp() {
        return getProperty("threed.threedRootHttp");
    }

    public String getThreedRootFileSystem() {
        return getProperty("threed.threedRootFileSystem");
    }

    public ThreedConfig getThreedConfig() {

        String rootHttp = getThreedRootHttp();
        String rootFs = getThreedRootFileSystem();
        String jpgRootHttp = getJpgRootHttp();
        String pngRootHttp = getPngRootHttp();

        return new ThreedConfig(new Path(rootHttp), new Path(rootFs), new Path(pngRootHttp), new Path(jpgRootHttp));
    }

    private static ThreedConfigHelper threedConfigHelper;

    public static ThreedConfigHelper get() {
        if (threedConfigHelper == null) {
            threedConfigHelper = new ThreedConfigHelper();
        }
        return threedConfigHelper;
    }

}
