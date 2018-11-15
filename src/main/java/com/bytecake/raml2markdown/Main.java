package com.bytecake.raml2markdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting Main ... ");

        if(args.length < 1) {
            logger.error("Config file name not specified");
            return;
        }
        String configFileName = args[0];
        try {
            Config.loadConfig(configFileName);
        } catch (IOException e) {
            logger.error("Error loading Config file");
            e.printStackTrace();
            return;
        }

        final String inputFilePathName = Config.getProperty("InputFilePathName");
        final String outputPath = Config.getProperty("OutputPath");
        final String apiName = Config.getProperty("ApiName");
        final String apiType = Config.getProperty("ApiType");
        final boolean isDraft = Config.getBooleanProperty("IsDraft");
        final String instanceName = Config.getProperty("InstanceName");
        final String environment = Config.getProperty("Environment");
        final String url = Config.getProperty("URL");
        final String visibilityLevel = Config.getProperty("VisibilityLevel");


        try {
            Raml2Markdown raml2md = new Raml2Markdown(inputFilePathName, outputPath, apiName, apiType, isDraft,
                    instanceName, environment, url, visibilityLevel);

            raml2md.processRamlFile();
        } catch (ProcessingException e) {
            logger.error("Error Processing RAML file");
            e.printStackTrace();
            return;
        }

        logger.info("Main Finished Successfully ... ");
    }
}