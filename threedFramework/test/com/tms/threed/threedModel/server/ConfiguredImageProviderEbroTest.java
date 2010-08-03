package com.tms.threed.threedModel.server;

import junit.framework.TestCase;

import java.net.URL;

/**
 *
 * @author dford
 *
 */
public class ConfiguredImageProviderEbroTest extends TestCase {

    public void testCamry2011() throws Exception {
        //"2513", "040", "13", "CF", "WB");
        EBrochureRequest r = new EBrochureRequest();
        r.clear();
        r._setModelCode("2513");
        r._setExteriorColorCode("040");
        r._setInteriorColorCode("FB13");
        r._setPackageCodes("CF");
        r._setAccessoryCodes("WB");

        final ConfiguredImageProviderEBro p = new ConfiguredImageProviderEBro("2011", "camry", r);
        URL exteriorUrl = p.getExteriorConfiguredImage();
        URL interiorUrl = p.getInteriorConfiguredImage();

        System.out.println("exteriorUrl = [" + exteriorUrl + "]");
        System.out.println("interiorUrl = [" + interiorUrl + "]");
    }


    public void testAvalon2011() throws Exception {

        EBrochureRequest r = new EBrochureRequest();
        r.clear();
        r._setModelCode("3544");
        r._setExteriorColorCode("1F7");
        r._setInteriorColorCode("FE17");
        r._setPackageCodes("EJ");
        r._setAccessoryCodes("CF");

        final ConfiguredImageProviderEBro p = new ConfiguredImageProviderEBro("2011", "avalon", r);
        URL exteriorUrl = p.getExteriorConfiguredImage();
        URL interiorUrl = p.getInteriorConfiguredImage();

        System.out.println("exteriorUrl = [" + exteriorUrl + "]");
        System.out.println("interiorUrl = [" + interiorUrl + "]");
    }

}
