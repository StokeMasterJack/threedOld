package com.tms.threed.threedCore.shared;

import javax.annotation.Nonnull;

public class SeriesId {

    private final SeriesKey seriesKey;
    private final int version; //0 is equivalent to "initial" version (with no number or suffix)

    public SeriesId(@Nonnull SeriesKey seriesKey, int version) {
        assert seriesKey != null;
        assert version >= 0;
        this.seriesKey = seriesKey;
        this.version = version;
    }

    public SeriesId(SeriesKey seriesKey) {
        this(seriesKey, 0);
    }

    public SeriesId(int year, String name, int version) {
        this(new SeriesKey(year, name), version);
    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    public int getVersion() {
        return version;
    }

    public String getName() {return seriesKey.getName();}

    public int getYear() {return seriesKey.getYear();}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeriesId id = (SeriesId) o;

        if (version != id.version) return false;
        if (!seriesKey.equals(id.seriesKey)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = seriesKey.hashCode();
        result = 31 * result + version;
        return result;
    }

    public boolean isInitialVersion() {
        return version == 0;
    }

    @Override public String toString() {
        return seriesKey.toString() + " v" + version;
    }

}
