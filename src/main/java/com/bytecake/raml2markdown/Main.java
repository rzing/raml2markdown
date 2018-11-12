package com.bytecake.raml2markdown;

import org.apache.log4j.Logger;

import java.io.IOException;


public class Main {
    private final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting Main ... ");

        final String inputFilePathName;
        final String outputPath;
        final String apiName;
        final String apiType;
        final boolean isDraft;
        final String instanceName;
        final String environment;
        final String url;
        final String visibilityLevel;
        try {
            inputFilePathName = Config.getProperty("InputFilePathName");
            outputPath = Config.getProperty("OutputPath");
            apiName = Config.getProperty("ApiName");
            apiType = Config.getProperty("ApiType");
            isDraft = Config.getBooleanProperty("IsDraft");
            instanceName = Config.getProperty("InstanceName");
            environment = Config.getProperty("Environment");
            url = Config.getProperty("URL");
            visibilityLevel = Config.getProperty("VisibilityLevel");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Raml2Markdown raml2md = new Raml2Markdown(inputFilePathName, outputPath, apiName, apiType, isDraft,
                instanceName, environment, url, visibilityLevel);
        raml2md.processRamlFile();

        logger.info("Main Finished ... ");
    }
}