package com.tms.threed.threedModel.server;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class EBrochureRequest {

    private String modelYear = "2010";
    private String modelCode = "5338";
    private String interiorColorCode = "FA12";
    private String exteriorColorCode = "01F7";
    private String packageCodes = "CQCFFELSSASR";
    private String accessoryCodes = "EF,WL,DI04,DI05,";

    public EBrochureRequest(HttpServletRequest request) {
        this.modelCode = request.getParameter("mc");
        this.modelYear = request.getParameter("my");
        this.interiorColorCode = request.getParameter("int");
        this.exteriorColorCode = request.getParameter("ext");
        this.packageCodes = request.getParameter("pkg");
        this.accessoryCodes = request.getParameter("commaDelimitedAccessoryCodes");
    }

    public void clear() {
        modelCode = null;
        modelYear = null;
        this.exteriorColorCode = null;
        this.interiorColorCode = null;
        this.accessoryCodes = null;
        this.packageCodes = null;
    }

    /**
     * Only used for testing
     */
    public EBrochureRequest() {
    }

    void _setModelYear(String modelYear) {
        this.modelYear = modelYear;
    }

    void _setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    void _setInteriorColorCode(String interiorColorCode) {
        this.interiorColorCode = interiorColorCode;
    }

    void _setExteriorColorCode(String exteriorColorCode) {
        this.exteriorColorCode = exteriorColorCode;
    }


    public void _setPackageCodes(String packageCodes) {
        this.packageCodes = packageCodes;
    }

    public void _setAccessoryCodes(String accessoryCodes) {
        this.accessoryCodes = accessoryCodes;
    }

    public String getModelYear() {
        return modelYear;
    }

    public LinkedHashSet<String> getFeatureCodes() {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        set.add(getModelCode());
        set.add(getInteriorColorCode());
        set.add(getExteriorColorCode());
        set.addAll(getPackageCodes());
        set.addAll(getAccessoryCodes());
        return set;

//        if (isPresent(cabCode)) set.add(cabCode);
//        if (isPresent(bedCode)) set.add(bedCode);
//        if (isPresent(driveCode)) set.add(driveCode);
//        if (isPresent(engineCode)) set.add(engineCode);
//        if (isPresent(gradeCode)) set.add(gradeCode);
//        if (isPresent(transmissionCode)) set.add(transmissionCode);


    }

    private static boolean isPresent(String s) {
        if (s == null) return false;
        if (s.trim().equals("")) return false;
        return true;
    }

    private String getModelCode() {return modelCode;}

    public String getExteriorColorCode() {
        if (exteriorColorCode.length() == 4 && exteriorColorCode.startsWith("0")) {
            return exteriorColorCode.substring(1);
        } else {
            return exteriorColorCode;
        }
    }

    public String getInteriorColorCode() {
        return interiorColorCode;
    }


    public Set<String> getPackageCodes() {
        final HashSet<String> set = new HashSet<String>();
        if (packageCodes == null) return set;
        packageCodes = packageCodes.trim();
        if (packageCodes.equals("")) return set;


        if ((packageCodes.length() % 2) != 0) throw new IllegalStateException("packageCodes.length must be even");
        StringBuffer sb = new StringBuffer(packageCodes);
        do {
            final String s = sb.substring(0, 2);
            set.add(s);
            sb.delete(0, 2);
        } while (sb.length() >= 2);

        return set;

    }

    public Set<String> getAccessoryCodes() {
        final HashSet<String> set = new HashSet<String>();
        if (accessoryCodes == null) return set;
        accessoryCodes = accessoryCodes.trim();
        if (accessoryCodes.equals("")) return set;

        final String[] a = accessoryCodes.split(",");
        for (String s : a) {
            set.add(s);
        }
        return set;

    }


}
