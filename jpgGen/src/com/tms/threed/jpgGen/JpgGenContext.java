package com.tms.threed.jpgGen;

import com.tms.threed.threedCore.shared.*;
import com.tms.threed.util.lang.shared.Path;

import java.util.List;

public interface JpgGenContext {

    List<SeriesKey> getSeriesKeys();

    Path getJpgRootFileSystem();

    Path getPngRootFileSystem();

    List<PngSet> getPngSets(SeriesKey seriesKey);

}
