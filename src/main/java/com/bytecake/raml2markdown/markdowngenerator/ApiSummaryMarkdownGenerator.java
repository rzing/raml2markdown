package com.bytecake.raml2markdown.markdowngenerator;

import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.MimeType;
import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.v2.api.model.v10.system.types.AnnotableStringType;
import org.raml.v2.api.model.v10.system.types.FullUriTemplateString;
import org.raml.v2.api.model.v10.system.types.MarkdownString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class ApiSummaryMarkdownGenerator extends MarkdownGenerator {
    private final static Logger logger = LoggerFactory.getLogger(ApiSummaryMarkdownGenerator.class);

    private final String fileName = "apisummary";
    private final String title = "API Summary";

    public ApiSummaryMarkdownGenerator(Api ramlModelApi, String outputFolderPath, String apiName, String apiType, boolean isDraft) {
        super(ramlModelApi, outputFolderPath, apiName, apiType, isDraft);
    }

    @Override
    public void generate() throws IOException {
        generateApiSummaryMarkdownFile();
    }

    private void generateApiSummaryMarkdownFile() throws IOException {
        BufferedWriter bufferedWriter = generateBasicMarkdownFile(fileName, title, title, true);

        // Version
        AnnotableStringType version = ramlModelApi.version();
        if(version != null) {
            String versionValue = version.value();
            bufferedWriter.write("Version: " + versionValue);
            bufferedWriter.newLine();
            bufferedWriter.newLine();
        }

        // Supported media type
        List<MimeType> supportedMediaTypes = ramlModelApi.mediaType();
        if(supportedMediaTypes != null && supportedMediaTypes.size() > 0) {
            StringBuilder supportedMediaTypesBuilder = new StringBuilder();
            for (MimeType supportedMediaType : supportedMediaTypes) {
                if (supportedMediaTypesBuilder.length() > 0) {
                    supportedMediaTypesBuilder.append(", ");
                }
                supportedMediaTypesBuilder.append(supportedMediaType.value());
            }
            bufferedWriter.write("Supported media type: " + supportedMediaTypesBuilder);
            bufferedWriter.newLine();
            bufferedWriter.newLine();
        }

        // Supported protocols
        List<String> supportedProtocols = ramlModelApi.protocols();
        if(supportedProtocols != null && supportedProtocols.size() > 0) {
            StringBuilder supportedProtocolsBuilder = new StringBuilder();
            for (String supportedProtocol : supportedProtocols) {
                if (supportedProtocolsBuilder.length() > 0) {
                    supportedProtocolsBuilder.append(", ");
                }
                supportedProtocolsBuilder.append(supportedProtocol);
            }
            bufferedWriter.write("Supported protocols: " + supportedProtocolsBuilder);
            bufferedWriter.newLine();
            bufferedWriter.newLine();
        }

        // Description
        MarkdownString description = ramlModelApi.description();
        if(description != null) {
            String descriptionValue = description.value();
            bufferedWriter.write(descriptionValue);
            bufferedWriter.newLine();
            bufferedWriter.newLine();
        }

        Heading heading;
        // API base URI
        heading = new Heading("API base URI", 3);
        heading.setUnderlineStyle(false);
        bufferedWriter.write(heading.toString());
        bufferedWriter.newLine();

        FullUriTemplateString baseUri = ramlModelApi.baseUri();
        if(baseUri != null) {
            bufferedWriter.write(baseUri.value());
            bufferedWriter.newLine();
        }

        bufferedWriter.newLine();

        // API resources
        heading = new Heading("API resources", 3);
        heading.setUnderlineStyle(false);
        bufferedWriter.write(heading.toString());
        bufferedWriter.newLine();

        List<Resource> resources = ramlModelApi.resources();
        for (Resource resource : resources) {
            bufferedWriter.write(resource.relativeUri().value());
            bufferedWriter.newLine();
        }

        bufferedWriter.newLine();

        bufferedWriter.close();
    }
}
