package com.tms.threed.featureModel.shared;

public abstract class AbstractProgressProvider implements ProgressProvider {

    public String getProgressMessage() {
        long progress = getProgressPercent();
        if (getPreCount() == -1) {
            return progress + "";
        } else {
            return progress + "%";
        }
    }

    public abstract long getPreCount();

    public abstract long getItemsVisitedSoFar();

    public long getProgressPercent() {
        long pre = getPreCount();
        long visits = getItemsVisitedSoFar();
        if (pre == 0) {
            return 0;
        } else if (pre == -1) {
            return getItemsVisitedSoFar();
        } else {
            double dPercent = (double) visits / (double) pre;
            double ddPercent = dPercent * 100D;
            return (int) ddPercent;
        }
    }
}
