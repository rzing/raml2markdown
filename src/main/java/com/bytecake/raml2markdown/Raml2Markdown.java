package com.bytecake.raml2markdown;

import com.bytecake.raml2markdown.markdowngenerator.ApiInstancesMarkdownGenerator;
import com.bytecake.raml2markdown.markdowngenerator.ApiSummaryMarkdownGenerator;
import com.bytecake.raml2markdown.markdowngenerator.ResourceMarkdownGenerator;
import com.bytecake.raml2markdown.markdowngenerator.TypesMarkdownGenerator;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.system.types.AnnotableStringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Raml2Markdown {
    private final static Logger logger = LoggerFactory.getLogger(Raml2Markdown.class);

    private File inputRamlFile;
    private File outputFolder;
    private final String inputRamlFilePathName;
    private String outputFolderPath;
    private final String apiName;
    private final String apiType;
    private String apiVersion;
    private final boolean isDraft;
    private final String instanceName;
    private final String environment;
    private final String url;
    private final String visibilityLevel;

    public Raml2Markdown(String inputRamlFilePathName, String outputFolderPath, String apiName, String apiType,
                         boolean isDraft, String instanceName, String environment, String url, String visibilityLevel) throws ProcessingException {

        validateNullOrEmptyString(inputRamlFilePathName, "Input Raml File PathName specified in config file is invalid");
        this.inputRamlFilePathName = inputRamlFilePathName;

        validateNullOrEmptyString(outputFolderPath, "Output Folder Path specified in config file is invalid");
        this.outputFolderPath = outputFolderPath;

        validateNullOrEmptyString(apiName, "API Name specified in config file is invalid");
        this.apiName = apiName;

        validateNullOrEmptyString(apiType, "API Type Name specified in config file is invalid");
        this.apiType = apiType;

        this.isDraft = isDraft;

        validateNullOrEmptyString(instanceName, "Instance Name specified in config file is invalid");
        this.instanceName = instanceName;

        this.environment = environment;

        validateNullOrEmptyString(url, "URL specified in config file is invalid");
        this.url = url;

        validateNullOrEmptyString(visibilityLevel, "Visibility Level specified in config file is invalid");
        this.visibilityLevel = visibilityLevel;
    }

    private void validateNullOrEmptyString(String inputString, String errorMessage) throws ProcessingException {
        if(inputString == null || inputString.isEmpty()) {
            logger.error(errorMessage);
            throw new ProcessingException(errorMessage);
        }
    }

    public void processRamlFile() throws ProcessingException {
        logger.info("Processing RAML File ... " + this.inputRamlFilePathName);

        Api ramlModelApi = getRamlModelApi();
        if (ramlModelApi == null) {
            String errorMessage = "Error parsing input RAML File";
            logger.error(errorMessage);
            throw new ProcessingException(errorMessage);
        }

        // Version
        AnnotableStringType version = ramlModelApi.version();
        if (version == null) {
            String errorMessage = "API version not specified in RAML File";
            logger.error(errorMessage);
            throw new ProcessingException(errorMessage);
        }
        this.apiVersion = version.value();

        this.outputFolderPath = Utils.createFolderHierarchy(this.outputFolderPath, this.apiType, this.apiVersion);

        try {
            ApiSummaryMarkdownGenerator apiSummaryMarkdownGenerator = new ApiSummaryMarkdownGenerator(ramlModelApi, outputFolderPath, apiName, apiType, isDraft);
            apiSummaryMarkdownGenerator.generate();

            ApiInstancesMarkdownGenerator apiInstancesMarkdownGenerator = new ApiInstancesMarkdownGenerator(
                    ramlModelApi, outputFolderPath, apiName, apiType, isDraft,
                    instanceName, environment, url, visibilityLevel);
            apiInstancesMarkdownGenerator.generate();


            TypesMarkdownGenerator typesMarkdownGenerator = new TypesMarkdownGenerator(ramlModelApi, outputFolderPath, apiName, apiType, isDraft);
            typesMarkdownGenerator.generate();

            ResourceMarkdownGenerator resourceMarkdownGenerator = new ResourceMarkdownGenerator(ramlModelApi, outputFolderPath, apiName, apiType, isDraft);
            resourceMarkdownGenerator.generate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Api getRamlModelApi() {
        inputRamlFile = new File(inputRamlFilePathName);
        if (!Utils.isInputFileValid(inputRamlFile)) return null;

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
}
