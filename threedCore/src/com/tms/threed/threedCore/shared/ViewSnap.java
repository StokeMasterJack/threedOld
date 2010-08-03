package com.tms.threed.threedCore.shared;

public final class ViewSnap {

    private final ViewKey view;
    private final int angle;

    public ViewSnap(ViewKey view, int angle) {
        this.angle = angle;
        this.view = view;
    }

    public int getAngle() {
        return angle;
    }

    public String getAnglePadded() {
        if (angle >= 1 && angle <= 9) return "0" + angle;
        else return angle + "";
    }

    public ViewKey getView() {
        return view;
    }
}
