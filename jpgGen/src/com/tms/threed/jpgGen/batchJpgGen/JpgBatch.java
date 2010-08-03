package com.tms.threed.jpgGen.batchJpgGen;

import com.tms.threed.jpgGen.singleJpgGen.Jpg;

public interface JpgBatch {

    int getJpgCount();

    Jpg getJpgDef(int index);

}
