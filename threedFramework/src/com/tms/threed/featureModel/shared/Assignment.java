package com.tms.threed.featureModel.shared;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Assignment {

    public static final Assignment UNASSIGNED = new Assignment(Bit.UNASSIGNED, null);
    public static final Assignment TRUE_USER = new Assignment(Bit.TRUE, Source.User);
    public static final Assignment TRUE_FIXUP = new Assignment(Bit.TRUE, Source.Fixup);
    public static final Assignment TRUE_INIT = new Assignment(Bit.TRUE, Source.Initial);
    public static final Assignment FALSE_USER = new Assignment(Bit.FALSE, Source.User);
    public static final Assignment FALSE_FIXUP = new Assignment(Bit.FALSE, Source.Fixup);
    public static final Assignment FALSE_INIT = new Assignment(Bit.FALSE, Source.Initial);

    private final Bit value;
    private final Source source;

    private Assignment(@Nonnull Bit value, @Nullable Source source) {
        assert value != null;
        assert (value.isUnassigned() && source == null) || value.isAssigned() || source != null;
        this.value = value;
        this.source = source;
    }

    public static Assignment create(Bit value, Source source) {
        switch (value) {
            case UNASSIGNED:
                return UNASSIGNED;
            case TRUE:
                switch(source){
                    case Fixup: return TRUE_FIXUP;
                    case Initial: return TRUE_INIT;
                    case User: return TRUE_USER;
                    default:throw new IllegalStateException();
                }
            case FALSE:
                switch(source){
                    case Fixup: return FALSE_FIXUP;
                    case Initial: return FALSE_INIT;
                    case User: return FALSE_USER;
                    default:throw new IllegalStateException();
                }
            default:
                throw new IllegalStateException();
        }
    }

    public Bit getValue() {
        return value;
    }

    public Source getSource() {
        return source;
    }

    public boolean isAssigned() {
        return value.isAssigned();
    }

    public boolean isTrue() {
        return value.isTrue();
    }

    public boolean isUserAssigned() {
        return isAssigned() && source.isUser();
    }


}
