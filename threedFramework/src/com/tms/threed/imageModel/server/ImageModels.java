package com.tms.threed.imageModel.server;

import com.tms.threed.threedCore.shared.ThreedConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ImageModels {

    private ThreedConfig threedConfig;

    public ImageModels(ThreedConfig threedConfig) {
        this.threedConfig = threedConfig;
    }

    public List<File> getAllYearFolders() {
        File pngRoot = getPngRoot();
        File[] files = pngRoot.listFiles();
        ArrayList<File> yearFolder = new ArrayList<File>();

        for (File file : files) {
            String fn = file.getName();
            if (file.isDirectory() && fn.length() == 4 && isInt(fn)) {
                yearFolder.add(file);
            }
        }

        return yearFolder;
    }

    public File getYearFolder(Integer year) {
        return new File(getPngRoot(), year + "");
    }


    public List<Integer> getAllYears() {
        TreeSet<Integer> allYears = new TreeSet<Integer>();

        for (File yearFolder : getAllYearFolders()) {
            allYears.add(new Integer(yearFolder.getName()));
        }

        return new ArrayList<Integer>(allYears);
    }

    private File getPngRoot() {
        return new File(threedConfig.getPngRootFileSystem().toString());
    }

    private File getPngRootForYear(Integer modelYear) {
        return new File(getPngRoot(), modelYear + "");
    }

    public List<String> getAllSeriesNames() {
        TreeSet<String> seriesNames = new TreeSet<String>();

        for (File yearFolder : getAllYearFolders()) {
            File[] files = yearFolder.listFiles();
            for (File file : files) {
                if (isSeriesFolder(file)) {
                    String seriesName = file.getName();
                    seriesNames.add(seriesName);
                }
            }

        }
        return new ArrayList<String>(seriesNames);
    }

    public List<String> getSeriesNamesForYear(Integer modelYear) {
        File yearFolder = getYearFolder(modelYear);
        TreeSet<String> seriesNames = new TreeSet<String>();
        for (File file : yearFolder.listFiles()) {
            if (isSeriesFolder(file)) {
                seriesNames.add(file.getName());
            }
        }
        return new ArrayList(seriesNames);
    }

    public boolean isSeriesFolder(File seriesFolder){
        String fn = seriesFolder.getName();
        if(!seriesFolder.isDirectory()) return false;
        if(fn.contains("Old")) return false;
        if(fn.contains("-")) return false;
        return true;
    }

    public List<Integer> getYearsForSeriesName(String seriesName) {
        TreeSet<Integer> years = new TreeSet<Integer>();
        for (Integer year : getAllYears()) {
            if (doesYearContainsSeries(year, seriesName)) {
                years.add(year);
            }
        }
        return new ArrayList(years);
    }

    public boolean doesYearContainsSeries(Integer year, String seriesName) {
        return getSeriesNamesForYear(year).contains(seriesName);
    }


    public static boolean isInt(String s) {
        try {
            new Integer(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
