package com.bytecake.raml2markdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private final static Logger logger = LoggerFactory.getLogger(Config.class);

    // public static final String CONFIG_FILE_NAME = "config.properties";
    private static Config config = null;
    private static Properties properties = null;

    private Config(String fileName) throws IOException {
        if(properties == null) {
            properties = new Properties();
            properties.load(new FileInputStream(fileName));
        }
    }

    public static Config loadConfig(String fileName) throws IOException {
        config = new Config(fileName);
        return config;
    }

    public static Config getConfig() {
        return config;
    }

    public Properties getProperties() {
        return properties;
    }

    public static String setProperty(String key, String value) {
        return getConfig().getProperties().setProperty(key, value).toString();
    }

    public static String getProperty(String key) {
        return getConfig().getProperties().getProperty(key);
    }

    public static Boolean getBooleanProperty(String key) {
        String booleanValueString = getProperty(key);
        return Boolean.valueOf(booleanValueString);
    }

    public static String getProperty(String key, String defaultValue) {
        return getConfig().getProperties().getProperty(key, defaultValue);
    }

    public static Boolean getBooleanProperty(String key, Boolean defaultValue) {
        String booleanValueString = getProperty(key, defaultValue.toString());
        return Boolean.valueOf(booleanValueString);
    }
}
