package com.tms.threed.threedCore.server;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class ConfigHelper {

    public static final String APP_NAME = "threed_framework";

    final private static String CONFIG_DIR = "configDir";
    final private static String DEFAULT_CONFIG_DIR = "/temp/tmsConfig";
    final private static String CONFIG_SUFFIX = ".properties";


    private static ReloadingConfig properties;


    public static Properties getAppProperties() {
        if (properties == null) {
            properties = new ReloadingConfig();
            properties.reload();
        }
        return properties;

    }

    public static String getProperty(String propertyName) {
        return getAppProperties().getProperty(propertyName);
    }

    public static class ReloadingConfig extends Properties {

        private static final long EXPIRE_TIME = 60 * 1000;
        private long lastReadTime;

        @Override public String getProperty(String key) {
            checkExpiry();
            return super.getProperty(key);
        }

        private void checkExpiry() {
            if (System.currentTimeMillis() - lastReadTime > EXPIRE_TIME) {
                reload();
            }
        }

        private void reload() {
            String configDir = System.getProperty(CONFIG_DIR);
            if (configDir == null) {
                configDir = DEFAULT_CONFIG_DIR;
            }

            String configFileName = configDir + "/" + APP_NAME + CONFIG_SUFFIX;
            File configFile = new File(configFileName);

            if (!configFile.isFile())
                throw new IllegalStateException("Config file [" + configFileName + "] is not a file.");
            if (!configFile.exists())
                throw new IllegalStateException("Config file [" + configFileName + "] does not exist.");
            if (!configFile.canRead())
                throw new IllegalStateException("Config file [" + configFileName + "] is not readable.");

            Reader r = null;

            try {
                r = new FileReader(configFile);
                load(r);
                lastReadTime = System.currentTimeMillis();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            finally {
                if (r != null) {
                    try {
                        r.close();
                    } catch (IOException ignore) {
                    }
                }
            }
        }
    }

}
