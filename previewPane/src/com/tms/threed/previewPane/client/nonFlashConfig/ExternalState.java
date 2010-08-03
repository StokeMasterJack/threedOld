package com.tms.threed.previewPane.client.nonFlashConfig;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.UmbrellaException;
import com.tms.threed.featureModel.shared.picks.IllegalPicksStateException;
import com.tms.threed.featureModel.shared.picks.PicksChangeHandler;
import com.tms.threed.previewPane.client.externalState.picks.PicksChangeHandlers;
import com.tms.threed.previewPane.client.externalState.raw.ExternalStateChangeEvent;
import com.tms.threed.previewPane.client.externalState.raw.ExternalStateSnapshot;
import com.tms.threed.previewPane.client.threedServiceClient.ThreedServiceJson;
import com.tms.threed.previewPanel.client.ChatInfo;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedModel.client.JsonUnmarshaller;
import com.tms.threed.threedModel.client.RawPicksSnapshot;
import com.tms.threed.threedModel.client.VarPicksSnapshot;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.gwt.client.Console;
import com.tms.threed.util.gwt.client.events2.ValueChangeHandlers;

import java.util.Iterator;
import java.util.Set;

public class ExternalState {

    private final ValueChangeHandlers chatChangeHandlers;
    private final ValueChangeHandlers msrpChangeHandlers;
    private final PicksChangeHandlers picksChangeHandlers;

    private final ThreedModel threedModel;

    private ExternalStateSnapshot currentState;

    public ExternalState() {
        final ThreedServiceJson threedModelService = new ThreedServiceJson(new JsonUnmarshaller());
        threedModel = threedModelService.fetchThreedModelFromPage();
        if (threedModel == null) throw new IllegalStateException();

        chatChangeHandlers = new ValueChangeHandlers(this);
        msrpChangeHandlers = new ValueChangeHandlers(this);
        picksChangeHandlers = new PicksChangeHandlers(threedModel.getFeatureModel());
    }

    public void updateExternalState(ExternalStateSnapshot newState) {
        if (newState == null) return;

        ExternalStateChangeEvent ev = new ExternalStateChangeEvent(currentState, newState);
        this.currentState = newState;

        if (!ev.hasChanged()) return;

        if (ev.chatInfoChanged()) {
            Console.log("\t ChatInfoChange: " + newState.getChatInfo());
            chatChangeHandlers.fire(newState.getChatInfo());
        }

        if (ev.msrpChanged()) {
            Console.log("\t MsrpChange: " + newState.getMsrp());
            msrpChangeHandlers.fire(newState.getMsrp());
        }

        if (ev.rawPicksChanged()) {
            try {
                Console.log("\t PicksChange: " + newState.getPicksRaw());
                picksChangeHandlers.processPicksChange(newState.getPicksRaw());
                Console.log("\t\t PicksAreValid");
            }
            catch (UmbrellaException umbrellaException) {
                Set<Throwable> causes = umbrellaException.getCauses();
                Iterator<Throwable> it = causes.iterator();
                while (it.hasNext()) {
                    Throwable cause = it.next();
                    if (cause instanceof IllegalPicksStateException) {
                        Console.log("\t\t InvalidPicksFromLeftSide: " + cause.getMessage());
                    } else if (cause instanceof VarPicksSnapshot.UnknownVarCodeFromLeftSideException) {
                        Console.log("\t\t InvalidVarCodeFromLeftSide: " + cause.getMessage());
                    } else {
                        Console.log("\t\t Unexpected exception processing picksChange [" + cause + "]");
                        cause.printStackTrace();
                    }
                }
            }
            catch (IllegalPicksStateException e) {
                Console.log("\t\t InvalidPicksFromLeftSide: " + e.getMessage());
            }
            catch (VarPicksSnapshot.UnknownVarCodeFromLeftSideException e) {
                Console.log("\t\t InvalidVarCodeFromLeftSide: " + e.getMessage());
            }
            catch (Exception e) {
                Console.log("\t\t Unexpected exception processing picksChange [" + e + "]");
                e.printStackTrace();
            }

        }

    }

    public ThreedModel getThreedModel() {
        return threedModel;
    }

    public HandlerRegistration addChatInfoChangeHandler(ValueChangeHandler<ChatInfo> handler) {
        return chatChangeHandlers.addHandler(ValueChangeEvent.getType(), handler);
    }

    public HandlerRegistration addMsrpChangeHandler(ValueChangeHandler<String> handler) {
        return msrpChangeHandlers.addHandler(ValueChangeEvent.getType(), handler);
    }

    public HandlerRegistration addPicksChangeHandler(PicksChangeHandler handler) {
        return picksChangeHandlers.addPicksChangeHandler(handler);
    }

    public SeriesInfo getSeries() {
        return currentState == null ? null : currentState.getSeriesInfo();
    }

    public String getMsrp() {
        return currentState == null ? null : currentState.getMsrp();
    }

    public RawPicksSnapshot getPicksRaw() {
        return currentState == null ? null : currentState.getPicksRaw();
    }

    public ChatInfo getChatInfo() {
        return currentState == null ? null : currentState.getChatInfo();
    }

}