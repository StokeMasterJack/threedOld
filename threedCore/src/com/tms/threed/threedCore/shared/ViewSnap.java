package com.tms.threed.threedCore.shared;

import javax.annotation.Nonnull;

public final class ViewSnap {

    private final ViewKey view;
    private final int angle;

    public ViewSnap(@Nonnull ViewKey view, int angle) {
        assert view != null;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ViewSnap viewSnap = (ViewSnap) o;

        if (angle != viewSnap.angle) return false;
        if (!view.equals(viewSnap.view)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = view.hashCode();
        result = 31 * result + angle;
        return result;
    }

    @Override public String toString() {
        return view + "-" + angle;
    }
    
}
