package com.tms.threed.threedCore.shared;

import com.tms.threed.util.lang.shared.Path;

public class ThreedConfig {

    public static final String ROOT_HTTP = "threedRootHttp";
    public static final String ROOT_FILE_SYSTEM = "threedRootFileSystem";
    public static final String PNG_ROOT_HTTP = "pngRootHttp";
    public static final String JPG_ROOT_HTTP = "jpgRootHttp";

    private final Path threedRootHttp;
    private final Path threedRootFileSystem;

    private final Path jpgRootHttp;
    private final Path pngRootHttp;

    private final Path jpgRootFileSystem;
    private final Path pngRootFileSystem;

    /**
     * @param threedRootHttp ex: "http://localhost:8080/threed";
     * @param threedRootFileSystem  ex: "/Users/dford/p-java/apache-tomcat-6.0.10/webapps/threed";
     */
    public ThreedConfig(Path threedRootHttp, Path threedRootFileSystem, Path pngRootHttp, Path jpgRootHttp) {
        this.threedRootHttp = threedRootHttp;
        this.threedRootFileSystem = threedRootFileSystem;

        if (jpgRootHttp != null) {
            this.jpgRootHttp = jpgRootHttp;
        } else {
            this.jpgRootHttp = threedRootHttp.append("jpgs");
        }

        if (pngRootHttp != null) {
            this.pngRootHttp = pngRootHttp;
        } else {
            this.pngRootHttp = threedRootHttp.append("pngs");
        }

        this.jpgRootFileSystem = threedRootFileSystem.append("jpgs");
        this.pngRootFileSystem = threedRootFileSystem.append("pngs");
    }

    public ThreedConfig(ThreedConfig threedConfig) {
        threedRootHttp = threedConfig.threedRootHttp;
        threedRootFileSystem = threedConfig.threedRootFileSystem;
        jpgRootHttp = threedConfig.getJpgRootHttp();
        pngRootHttp = threedConfig.getPngRootHttp();
        jpgRootFileSystem = threedConfig.getJpgRootFileSystem();
        pngRootFileSystem = threedConfig.getPngRootFileSystem();
    }

//    public ThreedConfig(Path threedRootHttp, Path threedRootFileSystem) {
//        this.threedRootHttp = threedRootHttp;
//        this.threedRootFileSystem = threedRootFileSystem;
//
//        this.jpgRootHttp = threedRootHttp.append("jpgs");
//        this.pngRootHttp = threedRootHttp.append("pngs");
//
//        this.jpgRootFileSystem = threedRootFileSystem.append("jpgs");
//        this.pngRootFileSystem = threedRootFileSystem.append("pngs");
//    }

    public Path getThreedRootHttp() {
        return threedRootHttp;
    }

    public Path getThreedRootFileSystem() {
        return threedRootFileSystem;
    }

    public Path getJpgRootHttp() {
        return jpgRootHttp;
    }

    public Path getPngRootHttp() {
        return pngRootHttp;
    }

    public Path getJpgRootFileSystem() {
        return jpgRootFileSystem;
    }

    public Path getPngRootFileSystem() {
        return pngRootFileSystem;
    }


    public void print() {
        System.out.println("ThreedConfig.print(..)");
        System.out.println("---------------------");
        System.out.println("threedRootHttp = [" + threedRootHttp + "]");
        System.out.println("threedRootFileSystem = [" + threedRootFileSystem + "]");

        System.out.println("jpgRootHttp = [" + jpgRootHttp + "]");
        System.out.println("pngRootHttp = [" + pngRootHttp + "]");


        System.out.println("jpgRootFileSystem = [" + jpgRootFileSystem + "]");
        System.out.println("pngRootFileSystem = [" + pngRootFileSystem + "]");
        System.out.println("---------------------");
    }
}
