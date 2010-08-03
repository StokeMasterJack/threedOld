package com.tms.threed.previewPane.client.threedServiceClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedModel.client.JsonUnmarshaller;
import com.tms.threed.threedModel.shared.ThreedModel;

public class ThreedServiceJson {

    String urlTemplate = GWT.getModuleBaseURL() + "threedServiceJson?seriesName=${seriesName}&seriesYear=${seriesYear}";

    private JsonUnmarshaller jsonThreedModelBuilder;

    public ThreedServiceJson(JsonUnmarshaller jsonThreedModelBuilder) {
        this.jsonThreedModelBuilder = jsonThreedModelBuilder;
    }


    public void fetchThreedModel(SeriesKey seriesKey, final Callback2 callback) {
        assert callback != null;

        String url = urlTemplate.replace("${seriesName}", seriesKey.getName());
        url = url.replace("${seriesYear}", seriesKey.getYear() + "");

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);

        requestBuilder.setCallback(new RequestCallback() {
            @Override public void onResponseReceived(Request request, Response response) {
                String jsonResponseText = response.getText();
                System.out.println("jsonResponseText = [" + jsonResponseText + "]");
                ThreedModel threedModel = parseJsonThreedModel(jsonResponseText);
                callback.onThreeModelReceived(threedModel);
            }

            @Override public void onError(Request request, Throwable e) {
                e.printStackTrace();
            }
        });

        try {
            requestBuilder.send();
        } catch (RequestException e) {
            e.printStackTrace();
        }

    }

    public ThreedModel parseJsonThreedModel(String threedModelJsonText) {
        return jsonThreedModelBuilder.createModelFromJsonText(threedModelJsonText);
    }

    public void fetchThreedModel2(SeriesKey seriesKey, final Callback1 callback) {
        assert callback != null;

        String url = urlTemplate.replace("${seriesName}", seriesKey.getName());
        url = url.replace("${seriesYear}", seriesKey.getYear() + "");

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);

        requestBuilder.setCallback(new RequestCallback() {
            @Override public void onResponseReceived(Request request, Response response) {
                String jsonResponseText = response.getText();
                callback.onThreeModelReceived(jsonResponseText);
            }

            @Override public void onError(Request request, Throwable e) {
                e.printStackTrace();
            }
        });

        try {
            requestBuilder.send();
        } catch (RequestException e) {
            e.printStackTrace();
        }

    }

    public ThreedModel fetchThreedModelFromPage() {
        return jsonThreedModelBuilder.createModelFromJsInPage();
    }

    public static interface Callback1 {
        void onThreeModelReceived(String threedModelJsonText);
    }

    public static interface Callback2 {
        void onThreeModelReceived(ThreedModel threedModel);
    }
}