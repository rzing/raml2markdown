package com.bytecake.raml2markdown.markdowngenerator;

import com.bytecake.raml2markdown.ProcessingException;
import com.bytecake.raml2markdown.Utils;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.v2.api.model.v10.system.types.AnnotableStringType;
import org.raml.v2.api.model.v10.system.types.MarkdownString;
import org.raml.v2.api.model.v10.system.types.RelativeUriString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.SOAPPart;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class IndividualResourceMarkdownGenerator extends MarkdownGenerator {
    private final static Logger logger = LoggerFactory.getLogger(IndividualResourceMarkdownGenerator.class);
    private final Resource resource;

    public IndividualResourceMarkdownGenerator(Api ramlModelApi, String outputFolderPath, String apiName, String apiType, boolean isDraft, Resource resource) {
        super(ramlModelApi, outputFolderPath, apiName, apiType, isDraft);
        this.resource = resource;
    }

    @Override
    public void generate() throws IOException, ProcessingException {
        generateIndividualResourceMarkdownFile(resource);
    }

    private void generateIndividualResourceMarkdownFile(Resource resource) throws IOException, ProcessingException {
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
        BufferedWriter bufferedWriter = generateBasicMarkdownFile(fileName, title, titleHeading, true);

        // Details of the URI Parameter
        List<TypeDeclaration> uriParameters = resource.uriParameters();
        if(uriParameters.size() > 0) {
            Heading heading;
            heading = new Heading("URI Parameters", 3);
            heading.setUnderlineStyle(false);
            bufferedWriter.write(heading.toString());
            bufferedWriter.newLine();
        }
        for (TypeDeclaration uriParameter : uriParameters) {
            String name = uriParameter.name();
            String type = uriParameter.type();
            MarkdownString description = uriParameter.description();
            String descriptionValue = null;
            if(description != null) {
                descriptionValue = description.value();
            }

            Heading heading;
            heading = new Heading("Parameter: " + name, 4);
            heading.setUnderlineStyle(false);
            bufferedWriter.write(heading.toString());
            bufferedWriter.newLine();
            if(type != null && type.length() > 0) {
                bufferedWriter.write("Type: " + type);
                bufferedWriter.newLine();
            }
            if(descriptionValue != null && descriptionValue.length() > 0) {
                bufferedWriter.write(descriptionValue);
                bufferedWriter.newLine();
            }
        }

        List<Method> methods = resource.methods();
        String newOutputFolderPath = outputFolderPath;
        if(methods.size() > 0) { // Create sub folder for the resource
            newOutputFolderPath = Utils.createFolderHierarchy(outputFolderPath, fileName);
        }
        for (Method method : methods) {
            MethodMarkdownGenerator methodMarkdownGenerator = new MethodMarkdownGenerator(
                    ramlModelApi, newOutputFolderPath, apiName, apiType, isDraft, method, title, uri);
            methodMarkdownGenerator.generate();
        }

        List<Resource> resources = resource.resources();
        newOutputFolderPath = outputFolderPath;
        if(resources.size() > 0) { // Create sub folder for the resource
            newOutputFolderPath = Utils.createFolderHierarchy(outputFolderPath, fileName);
        }
        for (Resource subResource : resources) {
            IndividualResourceMarkdownGenerator individualResourceMarkdownGenerator
                    = new IndividualResourceMarkdownGenerator(ramlModelApi, newOutputFolderPath, apiName, apiType, isDraft, subResource);
            individualResourceMarkdownGenerator.generate();
        }


    }
}
