package com.bytecake.raml2markdown;

import com.bytecake.raml2markdown.markdowngenerator.ApiInstancesMarkdownGenerator;
import com.bytecake.raml2markdown.markdowngenerator.ApiSummaryMarkdownGenerator;
import com.bytecake.raml2markdown.markdowngenerator.ResourceMarkdownGenerator;
import com.bytecake.raml2markdown.markdowngenerator.TypesMarkdownGenerator;
import org.apache.log4j.Logger;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;

import java.io.File;
import java.io.IOException;

public class Raml2Markdown {
    private final static Logger logger = Logger.getLogger(Raml2Markdown.class);

    private File inputRamlFile;
    private File outputFolder;
    private final String inputRamlFilePathName;
    private final String outputFolderPath;
    private final String apiName;
    private final String apiType;
    private final boolean isDraft;
    private final String instanceName;
    private final String environment;
    private final String url;
    private final String visibilityLevel;

    public Raml2Markdown(String inputRamlFilePathName, String outputFolderPath, String apiName, String apiType,
                         boolean isDraft, String instanceName, String environment, String url, String visibilityLevel) {
        this.inputRamlFilePathName = inputRamlFilePathName;
        this.outputFolderPath = outputFolderPath;
        this.apiName = apiName;
        this.apiType = apiType;
        this.isDraft = isDraft;
        this.instanceName = instanceName;
        this.environment = environment;
        this.url = url;
        this.visibilityLevel = visibilityLevel;
    }

    public void processRamlFile() {
        logger.info("Processing RAML File ... ");
        Api ramlModelApi = getRamlModelApi();
        if (ramlModelApi == null) {
            logger.fatal("Error parsing input RAML File");
            return;
        }

        try {
            TypesMarkdownGenerator typesMarkdownGenerator = new TypesMarkdownGenerator(ramlModelApi, outputFolderPath, apiName, apiType, isDraft);
            typesMarkdownGenerator.generate();

            ApiSummaryMarkdownGenerator apiSummaryMarkdownGenerator = new ApiSummaryMarkdownGenerator(ramlModelApi, outputFolderPath, apiName, apiType, isDraft);
            apiSummaryMarkdownGenerator.generate();

            ApiInstancesMarkdownGenerator apiInstancesMarkdownGenerator = new ApiInstancesMarkdownGenerator(
                    ramlModelApi, outputFolderPath, apiName, apiType, isDraft,
                    instanceName, environment, url, visibilityLevel);
            apiInstancesMarkdownGenerator.generate();

            ResourceMarkdownGenerator resourceMarkdownGenerator = new ResourceMarkdownGenerator(ramlModelApi, outputFolderPath, apiName, apiType, isDraft);
            resourceMarkdownGenerator.generate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Api getRamlModelApi() {
        inputRamlFile = new File(inputRamlFilePathName);
        if (!isInputRamlFileValid(inputRamlFile)) return null;

        outputFolder = new File(outputFolderPath);
        if (!isOutputFolderValid(outputFolder)) return null;

        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(inputRamlFile);
        if (ramlModelResult.hasErrors()) {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                logger.error(validationResult.getMessage());
            }
            return null;
        }
        else {
            Api ramlModelApi = ramlModelResult.getApiV10();
            return ramlModelApi;
        }
    }

    private boolean isOutputFolderValid(File outputFolder) {
        if(!outputFolder.exists()) {
            logger.fatal("Specified output folder does not exists.");
            return false;
        }
        if(outputFolder.isFile()) {
            logger.fatal("Specified output folder is a file.");
            return false;
        }
        if(!(outputFolder.canExecute() && outputFolder.canWrite())) {
            logger.fatal("Specified output folder is not writable.");
            return false;
        }
        return true;
    }

    private boolean isInputRamlFileValid(File inputRamlFile) {
        if(!inputRamlFile.exists()) {
            logger.fatal("Specified input RAML file does not exists.");
            return false;
        }
        if(!inputRamlFile.isFile()) {
            logger.fatal("Specified input RAML file is not a file.");
            return false;
        }
        if(!inputRamlFile.canRead()) {
            logger.fatal("Specified input RAML file is not readable.");
            return false;
        }
        return true;
    }
}
