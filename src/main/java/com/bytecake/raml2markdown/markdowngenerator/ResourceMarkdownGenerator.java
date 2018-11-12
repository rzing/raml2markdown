package com.bytecake.raml2markdown.markdowngenerator;

import org.apache.log4j.Logger;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.v2.api.model.v10.system.types.AnnotableStringType;
import org.raml.v2.api.model.v10.system.types.RelativeUriString;

import java.io.IOException;
import java.util.List;

public class ResourceMarkdownGenerator extends MarkdownGenerator {
    private final static Logger logger = Logger.getLogger(ResourceMarkdownGenerator.class);

    private final String fileName = "resource";
    private final String title = "Resource";

    public ResourceMarkdownGenerator(Api ramlModelApi, String outputFolderPath, String apiName, String apiType, boolean isDraft) {
        super(ramlModelApi, outputFolderPath, apiName, apiType, isDraft);
    }

    @Override
    public void generate() throws IOException {
        generateResourceMarkdownFile();
    }

    private void generateResourceMarkdownFile() throws IOException {
        generateBasicMarkdownFile(fileName, title, title);
        processResources();
    }

    private void processResources() throws IOException {
        logger.info("Processing resources ...");
        List<Resource> resources = ramlModelApi.resources();
        for (Resource resource : resources) {
            generateIndividualResourceMarkdownFile(resource);
        }
    }

    private void generateIndividualResourceMarkdownFile(Resource resource) throws IOException {
        RelativeUriString relativeUriString = resource.relativeUri();
        if(relativeUriString == null) {
            logger.error("Resource URI not found");
            return;
        }

        String uri = relativeUriString.value();

        // Get resource display name
        AnnotableStringType resourceDisplayName = resource.displayName();
        if(resourceDisplayName == null) {
            logger.error("Resource name not found");
            return;
        }
        String resourceName = resourceDisplayName.value();

        String titleHeading = "Resource: " + resourceName;

        // Removing leading slash from the resource name
        if(resourceName.startsWith("/")) {
            resourceName = resourceName.substring(1);
        }

        //Convert to all lowercase to for fileName
        String fileName = resourceName.toLowerCase();

        // Capitalize first character to create Title
        String title = resourceName.substring(0,1).toUpperCase() + resourceName.substring(1);

        // Generate basic file structure
        generateBasicMarkdownFile(fileName, title, titleHeading);

        List<Method> methods = resource.methods();
        for (Method method : methods) {
            MethodMarkdownGenerator methodMarkdownGenerator = new MethodMarkdownGenerator(
                    ramlModelApi, outputFolderPath, apiName, apiType, isDraft, method, title, uri);
            methodMarkdownGenerator.generate();
        }
    }
}
