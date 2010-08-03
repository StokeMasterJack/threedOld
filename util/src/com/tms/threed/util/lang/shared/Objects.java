package com.tms.threed.util.lang.shared;

import javax.annotation.Nullable;

public class Objects {

    public static boolean ne(@Nullable Object a, @Nullable Object b) {
        return !eq(a, b);
    }
    
    public static boolean eq(@Nullable Object a, @Nullable Object b) {
        return a == b || (a != null && a.equals(b));
    }

}
