package com.bytecake.raml2markdown.markdowngenerator;

import com.bytecake.raml2markdown.ProcessingException;
import com.bytecake.raml2markdown.Utils;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.v2.api.model.v10.system.types.AnnotableStringType;
import org.raml.v2.api.model.v10.system.types.RelativeUriString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ResourceMarkdownGenerator extends MarkdownGenerator {
    private final static Logger logger = LoggerFactory.getLogger(ResourceMarkdownGenerator.class);

    private final String fileName = "resource";
    private final String title = "Resource";

    public ResourceMarkdownGenerator(Api ramlModelApi, String outputFolderPath, String apiName, String apiType, boolean isDraft) {
        super(ramlModelApi, outputFolderPath, apiName, apiType, isDraft);
    }

    @Override
    public void generate() throws IOException, ProcessingException {
        generateResourceMarkdownFile();
    }

    private void generateResourceMarkdownFile() throws IOException, ProcessingException {
        generateBasicMarkdownFile(fileName, title, title);
        processResources();
    }

    private void processResources() throws IOException, ProcessingException {
        logger.info("Processing resources ...");
        List<Resource> resources = ramlModelApi.resources();
        for (Resource resource : resources) {
            IndividualResourceMarkdownGenerator individualResourceMarkdownGenerator
                    = new IndividualResourceMarkdownGenerator(ramlModelApi, outputFolderPath, apiName, apiType, isDraft, resource);
            individualResourceMarkdownGenerator.generate();
        }
    }
}
