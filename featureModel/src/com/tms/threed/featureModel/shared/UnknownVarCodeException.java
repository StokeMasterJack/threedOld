package com.tms.threed.featureModel.shared;

import com.tms.threed.threedCore.shared.SeriesKey;

public class UnknownVarCodeException extends RuntimeException {

    private String badVarCode;
    private SeriesKey seriesKey;

    public UnknownVarCodeException(String badVarCode, SeriesKey seriesKey) {
        this.badVarCode = badVarCode;
        this.seriesKey = seriesKey;
    }

    public String getBadVarCode() {
        return badVarCode;
    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    @Override public String getMessage() {
        return "featureCode [" + badVarCode + "] is not in FeatureModel for series [" + seriesKey + "]";
    }
}
