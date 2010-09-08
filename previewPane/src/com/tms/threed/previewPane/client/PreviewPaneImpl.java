package com.tms.threed.previewPane.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.tms.threed.featureModel.shared.picks.PicksChangeEvent;
import com.tms.threed.featureModel.shared.picks.PicksChangeHandler;
import com.tms.threed.previewPane.client.externalState.raw.ExternalStateSnapshot;
import com.tms.threed.previewPane.client.nonFlashConfig.ExternalState;
import com.tms.threed.previewPane.client.notification.AccessoryWithFlashOrientationHandler;
import com.tms.threed.previewPane.client.notification.NotificationCenterBridge;
import com.tms.threed.previewPanel.client.ChatInfo;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.util.gwt.client.Console;

import static com.tms.threed.util.date.shared.StringUtil.isEmpty;

public class PreviewPaneImpl extends PreviewPane {

    private final ExternalState externalState;
    private final PreviewPaneContext previewPaneContext;

    public PreviewPaneImpl() {

        externalState = new ExternalState();
        previewPaneContext = new PreviewPaneContext();

        previewPaneContext.setSeries(externalState.getThreedModel());

        externalState.addPicksChangeHandler(new PicksChangeHandler() {
            @Override public void onPicksChange(PicksChangeEvent e) {
                try {
                    previewPaneContext.setPicks(e);
                } catch (Exception e1) {
                    Console.log("\t\tUnexpected exception processing PicksChangeEvent");
                    Console.log("\t\t\tNewPicks[" + e.getNewPicks() + "]");
                    Console.log("\t\t\t " + e1);
                    e1.printStackTrace();
                }
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

        NotificationCenterBridge.addAccessoryWithFlashOrientationHandler(new AccessoryWithFlashOrientationHandler() {
            @Override public void handleEvent(int orientation) {
                if (previewPaneContext == null) return;
                previewPaneContext.setViewAndAngle(orientation);
            }
        });

        initWidget(previewPaneContext.getPreviewPanel());
    }

    public void test() throws Exception {

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
            String flashdescription) {

        if (isEmpty(modelCode)) throw new IllegalArgumentException("modelCode must not be null");

        ExternalStateSnapshot stateSnapshot = new ExternalStateSnapshot(modelCode, exteriorColor, interiorColor, option, accessory, msrp, helpimgid, helpimgurl, flash_key, flashdescription);
        externalState.updateExternalState(stateSnapshot);

    }

    public void updateImage2(ExternalStateSnapshot externalStateSnapshot) {
        externalState.updateExternalState(externalStateSnapshot);
    }


}
