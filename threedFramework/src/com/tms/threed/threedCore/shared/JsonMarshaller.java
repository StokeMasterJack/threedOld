package com.tms.threed.threedCore.shared;

public interface JsonMarshaller<MT> {

    String toJsonString(MT model);

}
