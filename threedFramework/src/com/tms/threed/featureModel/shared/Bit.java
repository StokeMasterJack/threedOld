package com.tms.threed.featureModel.shared;

public enum  Bit {

    TRUE((byte) 1),
    FALSE((byte) 0),
    UNASSIGNED((byte) -1);

    private final byte value;

    Bit(byte value) {
        this.value = value;
    }

    public byte value() { return value; }

    public char toChar() {
        switch (this) {
            case TRUE:
                return '1';
            case FALSE:
                return '0';
            case UNASSIGNED:
                return '-';
            default:
                throw new IllegalStateException();
        }
    }

    public static Bit fromInt(int bit) {
        return fromByte((byte) bit);
    }

    public static Bit fromByte(byte bit) {
        for (Bit b : values()) {
            if (b.value == bit) return b;
        }
        throw new IllegalArgumentException("Bad bit value: [" + bit + "]");
    }

    public boolean matches(byte bit) {
        return bit == value;
    }

    public static Bit fromBool(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static boolean isUnassigned(byte triState) {
        return triState == UNASSIGNED.value;
    }

    public static boolean isTrue(byte triState) {
        return triState == TRUE.value;
    }

    public boolean isTrue(){
        return this.equals(TRUE);
    }

    public boolean isFalse(){
        return this.equals(FALSE);
    }

    public boolean isUnassigned(){
        return this.equals(UNASSIGNED);
    }

     public boolean isAssigned(){
        return !isUnassigned();
    }

    


}
