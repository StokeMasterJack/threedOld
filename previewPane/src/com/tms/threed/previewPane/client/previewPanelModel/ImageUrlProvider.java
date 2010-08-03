package com.tms.threed.previewPane.client.previewPanelModel;

import com.tms.threed.imageModel.shared.ImageStack;
import com.tms.threed.threedCore.shared.ViewSnap;

public interface ImageUrlProvider {
    ImageStack getImageUrl(ViewSnap viewState);
}
