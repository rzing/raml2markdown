package com.bytecake.raml2markdown;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    public static final String CONFIG_FILE_NAME = "config.properties";
    private static Config config = null;
    private static Properties properties = null;

    private Config() throws IOException {
        if(properties == null) {
            properties = new Properties();
            properties.load(new FileInputStream(CONFIG_FILE_NAME));
        }
    }

    public static Config getConfig() throws IOException {
        if(config == null) {
            config = new Config();
        }
        return config;
    }

    public Properties getProperties() {
        return properties;
    }

    public static String setProperty(String key, String value) throws IOException {
        return getConfig().getProperties().setProperty(key, value).toString();
    }

    public static String getProperty(String key) throws IOException {
        return getConfig().getProperties().getProperty(key);
    }

    public static Boolean getBooleanProperty(String key) throws IOException {
        String booleanValueString = getProperty(key);
        return Boolean.valueOf(booleanValueString);
    }

    public static String getProperty(String key, String defaultValue) throws IOException {
        return getConfig().getProperties().getProperty(key, defaultValue);
    }

    public static Boolean getBooleanProperty(String key, Boolean defaultValue) throws IOException {
        String booleanValueString = getProperty(key, defaultValue.toString());
        return Boolean.valueOf(booleanValueString);
    }
}
