package com.tms.threed.previewPane.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.tms.threed.featureModel.shared.picks.PicksChangeEvent;
import com.tms.threed.featureModel.shared.picks.PicksChangeHandler;
import com.tms.threed.previewPane.client.externalState.raw.ExternalStateSnapshot;
import com.tms.threed.previewPane.client.nonFlashConfig.ExternalState;
import com.tms.threed.previewPanel.client.ChatInfo;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.util.gwt.client.Console;

import static com.tms.threed.util.date.shared.StringUtil.isEmpty;

public class PreviewPaneImpl extends PreviewPane {

    private ExternalState externalState;
    private PreviewPaneContext previewPaneContext;

    public PreviewPaneImpl() {

        Console.log("PreviewPane Constructor");

        externalState = new ExternalState();
        previewPaneContext = new PreviewPaneContext();

        previewPaneContext.setSeries(externalState.getThreedModel());

        externalState.addPicksChangeHandler(new PicksChangeHandler() {
            @Override public void onPicksChange(PicksChangeEvent e) {
                previewPaneContext.setPicks(e);
            }
        });

        externalState.addChatInfoChangeHandler(new ValueChangeHandler<ChatInfo>() {
            @Override public void onValueChange(ValueChangeEvent<ChatInfo> event) {
                ChatInfo chatInfo = event.getValue();
                previewPaneContext.setChatInfo(chatInfo);
            }
        });

        externalState.addMsrpChangeHandler(new ValueChangeHandler<String>() {
            @Override public void onValueChange(ValueChangeEvent<String> event) {
                previewPaneContext.setMsrp(event.getValue());
            }
        });

        initWidget(previewPaneContext.getPreviewPanel());
    }

    public SeriesKey getSeriesKey() {
        return externalState.getThreedModel().getSeriesKey();
    }

    @Override
    public void updateImage(
            String flash_key,
            String modelCode,
            String option,
            String exteriorColor,
            String interiorColor,
            String accessory,
            String msrp,
            String seriesname,
            String helpimgid,
            String helpimgurl,
            String flashdescription,
            String modelYear) {

        if (isEmpty(modelCode)) throw new IllegalArgumentException("modelCode must not be null");

        this.externalState.updateExternalState(new ExternalStateSnapshot(seriesname, modelYear, modelCode, exteriorColor, interiorColor, option, accessory, msrp, helpimgid, helpimgurl, flash_key, flashdescription));

    }

    public void updateImage(ExternalStateSnapshot externalStateSnapshot) {
        this.externalState.updateExternalState(externalStateSnapshot);
    }


}
