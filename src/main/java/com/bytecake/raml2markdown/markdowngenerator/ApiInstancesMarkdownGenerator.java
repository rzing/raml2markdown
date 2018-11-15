package com.bytecake.raml2markdown.markdowngenerator;

import net.steppschuh.markdowngenerator.table.Table;
import org.raml.v2.api.model.v10.api.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;

public class ApiInstancesMarkdownGenerator extends MarkdownGenerator {
    private final static Logger logger = LoggerFactory.getLogger(ApiInstancesMarkdownGenerator.class);

    private final String fileName = "apiinstances";
    private final String title = "API Instances";
    private final String instanceName;
    private final String environment;
    private final String url;
    private final String visibilityLevel;

    public ApiInstancesMarkdownGenerator(
            Api ramlModelApi, String outputFolderPath, String apiName, String apiType, boolean isDraft,
            String instanceName, String environment, String url, String visibilityLevel) {
        super(ramlModelApi, outputFolderPath, apiName, apiType, isDraft);

        this.instanceName = instanceName;
        this.environment = environment;
        this.url = url;
        this.visibilityLevel = visibilityLevel;
    }

    @Override
    public void generate() throws IOException {
        generateApiInstancesMarkdownFile();
    }

    private void generateApiInstancesMarkdownFile() throws IOException {
        logger.info("Generating Api Instances Markdown File");

        BufferedWriter bufferedWriter = generateBasicMarkdownFile(fileName, title, title, true);
        bufferedWriter.newLine();

        Table.Builder tableBuilder = new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .withRowLimit(0) // No row limit
                .addRow("Instances", "Environment", "URL", "Visibility");
        tableBuilder.addRow(instanceName, environment, url, visibilityLevel);

        bufferedWriter.write(tableBuilder.build().serialize());
        bufferedWriter.newLine();
        bufferedWriter.newLine();

        bufferedWriter.close();
    }
}
