package com.tms.threed.featureModel.shared;

import com.tms.threed.threedCore.shared.SeriesId;

public class UnknownVarCodeException extends RuntimeException {

    private final String badVarCode;
    private final SeriesId seriesId;

    public UnknownVarCodeException(String badVarCode, SeriesId seriesId) {
        this.badVarCode = badVarCode;
        this.seriesId = seriesId;
    }

    public String getBadVarCode() {
        return badVarCode;
    }

    public SeriesId getSeriesId() {
        return seriesId;
    }

    @Override public String getMessage() {
        return "featureCode [" + badVarCode + "] is not in FeatureModel for series [" + getSeriesId() + "]";
    }
}
