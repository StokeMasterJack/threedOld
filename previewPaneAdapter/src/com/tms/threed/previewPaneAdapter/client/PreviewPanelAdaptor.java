package com.tms.threed.previewPaneAdapter.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tms.threed.previewPane.client.PreviewPane;
import com.tms.threed.util.gwt.client.Console;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PreviewPanelAdaptor implements EntryPoint {

    static {
        Console.log("PreviewPanelAdaptor.static init");
    }

    //static PreviewPanel previewPanel = null;
    static PreviewPane previewPanel = null;
    static String modelYear;
    static String model;

    public PreviewPanelAdaptor() {
        Console.log("PreviewPanelAdaptor Constructor");
    }

    public void onModuleLoad() {
        Console.log("PreviewPanelAdaptor onModuleLoad");
        registerHooks();
    }

    public native void registerHooks()
        /*-{

                 $wnd.addPreviewPanel = $entry(@com.tms.threed.previewPaneAdapter.client.PreviewPanelAdaptor::addPreviewPanel(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;));

               $wnd.Configurator.FlashBridge.setSeries = $entry(@com.tms.threed.previewPaneAdapter.client.PreviewPanelAdaptor::setSeries(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;));
  //     		$wnd.getConfiguredImage = $entry(@com.tms.configurator.client.PreviewPanelAdaptor::getConfiguredImage(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;));

               $wnd.gwtLoaded();
      }-*/;

    public static native String getZipCode()
        /*-{
                 return $wnd.App.zip_code;
      }-*/;

    public static native void debugData(
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
            String seriesYear)
        /*-{
          $wnd.Debug.log( flash_key + '|' + model + '|' + option + '|' + excolor + '|' + incolor + '|' + accessory + '|' + msrp + '|' + seriesname + '|' + helpimgid + '|' + helpimgurl + '|' + flashdescription + "|" + seriesYear);
      }-*/;

    public static void debugData2(
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
            String seriesYear) {

        String s = flash_key + '|' + model + '|' + option + '|' + excolor + '|' + incolor + '|' + accessory + '|' + msrp + '|' + seriesname + '|' + helpimgid + '|' + helpimgurl + '|' + flashdescription + "|" + seriesYear;
        Console.log(s);
    }

    public static void addPreviewPanel(String divName, String panelType, String seriesName, String seriesYear) {
        VerticalPanel vPanel = new VerticalPanel();

        //change this to use a FlowPanel instead

        PanelType pType = PanelType.valueOf(panelType);
//		
//		vPanel.getElement().getStyle().setWidth(pType.getWidth(), Unit.PX);
//		vPanel.getElement().getStyle().setHeight(pType.getHeight(), Unit.PX);
//		vPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);

//		RootPanel.get(divName).add(vPanel);

        boolean b = pType.equals(PanelType.Summary);
        previewPanel = PreviewPanelFactory.makePreviewPanel(b);
        RootPanel.get(divName).add(previewPanel);
//		vPanel.add(previewPanel);


        modelYear = seriesYear;
        model = seriesName;
    }

    public static void setSeries(
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
            String flashdescription) {

        debugData(flash_key, model, option, excolor, incolor, accessory, msrp, seriesname, helpimgid, helpimgurl, flashdescription, modelYear);

        if (model == null || model.length() == 0)
            return;

        if (incolor == null || incolor.length() == 0)
            return;

        //Console.log();
        debugData("pulling->" + flash_key, model, option, excolor, incolor, accessory, msrp, seriesname, helpimgid, helpimgurl, flashdescription, modelYear);

        previewPanel.updateImage(flash_key, model, option, excolor, incolor, accessory, msrp, seriesname, helpimgid, helpimgurl, flashdescription, modelYear);

    }


//	public static String getConfiguredImage (
//			String flash_key, 
//			String model, 
//			String option, 
//			String excolor, 
//			String incolor, 
//			String accessory, 
//			String msrp, 
//			String seriesname, 
//			String helpimgid, 
//			String helpimgurl, 
//			String flashdescription,
//			String modelYear)
//	{
//		PreviewPane previewPanel = PreviewPanelFactory.makePreviewPanel();
//		
//		previewPanel.init(new VerticalPanel(), PanelType.Configurator );
//		
//		return previewPanel.getUrl( flash_key, model, option, excolor, incolor, accessory, msrp, seriesname, helpimgid, helpimgurl, flashdescription, modelYear );
//	}

}
