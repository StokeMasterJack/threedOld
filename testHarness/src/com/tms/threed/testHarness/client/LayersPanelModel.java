package com.tms.threed.testHarness.client;

import com.tms.threed.imageModel.shared.ImLayer;
import com.tms.threed.imageModel.shared.ImPng;

import java.util.List;

public interface LayersPanelModel {
    void selectAll();

    void selectNone();

    List<ImLayer> getLayers();

    ImPng getPngForLayer(ImLayer layer);

    void toggleLayer(ImLayer layer);
}
