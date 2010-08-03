package com.tms.threed.previewPane.client;

import com.tms.threed.featureModel.shared.picks.PicksChangeEvent;
import com.tms.threed.featureModel.shared.picks.PicksChangeHandler;
import com.tms.threed.previewPane.client.externalState.raw.ExternalStateSnapshot;
import com.tms.threed.previewPane.client.nonFlashConfig.ExternalState;
import com.tms.threed.previewPane.client.summaryPane.SummaryPaneContext;
import com.tms.threed.threedCore.shared.SeriesKey;

public class PreviewPaneSummary extends PreviewPane {

    private ExternalState externalState;
    private SummaryPaneContext previewPaneContext;

    public PreviewPaneSummary() {

        externalState = new ExternalState();
        previewPaneContext = new SummaryPaneContext();

        previewPaneContext.setSeries(externalState.getThreedModel());

        externalState.addPicksChangeHandler(new PicksChangeHandler() {
            @Override public void onPicksChange(PicksChangeEvent e) {
                previewPaneContext.setPicks(e);
            }
        });


        initWidget(previewPaneContext.getSummaryPanel());
    }

    public SeriesKey getSeriesKey() {
        return externalState.getThreedModel().getSeriesKey();
    }

    @Override
    public void updateImage(
            String flash_key,
            String modelCode,
            String optionCodes,
            String exteriorColor,
            String interiorColor,
            String accessoryCodes,
            String msrp,
            String seriesName,
            String chatVehicleIconMediaId,
            String chatActionUrl,
            String flashDescription,
            String modelYear) {

        this.externalState.updateExternalState(new ExternalStateSnapshot(seriesName, modelYear, modelCode, exteriorColor, interiorColor, optionCodes, accessoryCodes, msrp, chatVehicleIconMediaId, chatActionUrl, flash_key, flashDescription));

    }

    public void updateImage(ExternalStateSnapshot externalStateSnapshot) {
        this.externalState.updateExternalState(externalStateSnapshot);
    }


}