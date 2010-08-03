package com.tms.threed.previewPane.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Composite;

abstract public class PreviewPane extends Composite {

    static {
        Log.info("PreviewPane - static init");
    }

    abstract public void updateImage(
            String flash_key,
            String model,
            String option,
            String excolor,
            String incolor,
            String accessory,
            String msrp,
            String seriesname,
            String helpimgid,
            String helpimgurl,
            String flashdescription,
            String modelYear);


}
