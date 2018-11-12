package com.bytecake.raml2markdown.markdowngenerator;

import net.steppschuh.markdowngenerator.rule.HorizontalRule;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class MarkdownGenerator {
    private final static Logger logger = Logger.getLogger(MarkdownGenerator.class);

    protected final String fileExtension = ".md";
    protected final Api ramlModelApi;
    protected final String outputFolderPath;
    protected final String apiName;
    protected final String apiType;
    protected final boolean isDraft;
    protected DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public MarkdownGenerator(Api ramlModelApi, String outputFolderPath, String apiName, String apiType, boolean isDraft) {
        this.ramlModelApi = ramlModelApi;
        this.outputFolderPath = outputFolderPath;
        this.apiName = apiName;
        this.apiType = apiType;
        this.isDraft = isDraft;
    }

    public abstract void generate() throws IOException;

    protected String createMarkdownFileHeader(String title) {
        logger.info("Creating Markdown File Header for Title: " + title);

        StringBuilder markdownBuilder = new StringBuilder()
                .append(new HorizontalRule()).append(System.lineSeparator())
                .append("apiname: \"").append(apiName).append("\"").append(System.lineSeparator())
                .append("title: \"").append(title).append("\"").append(System.lineSeparator())
                .append("date: ").append(formatter.format(ZonedDateTime.now())).append(System.lineSeparator())
                .append("type: \"").append(apiType).append("\"").append(System.lineSeparator())
                .append("draft: ").append(isDraft).append(System.lineSeparator())
                .append(new HorizontalRule()).append(System.lineSeparator());

        return markdownBuilder.toString();
    }

    protected void writeFileHeader(BufferedWriter bufferedWriter, String title) throws IOException {
        bufferedWriter.write(createMarkdownFileHeader(title));
        bufferedWriter.newLine();
        bufferedWriter.newLine();
    }

    protected void generateBasicMarkdownFile(String fileName, String title, String titleHeading) throws IOException {
        generateBasicMarkdownFile(fileName, title, titleHeading, false);

    }

    protected BufferedWriter generateBasicMarkdownFile(String fileName, String title, String titleHeading, boolean returnFileWriter) throws IOException {
        logger.info("Generating file ... " + fileName + fileExtension);
        File file = new File(outputFolderPath, fileName + fileExtension);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

        // File header
        writeFileHeader(bufferedWriter, title);

        // Title heading
        Heading typesHeading = new Heading(titleHeading, 2);
        typesHeading.setUnderlineStyle(false);
        bufferedWriter.write(typesHeading.toString());
        bufferedWriter.newLine();

        if(returnFileWriter) {
            return bufferedWriter;
        } else {
            bufferedWriter.close();
            return null;
        }
    }

    public static void generateAnnotations(BufferedWriter bufferedWriter, List<AnnotationRef> annotationList) throws IOException {
        for (AnnotationRef annotation : annotationList) {
            String annotationName = annotation.annotation().name();
            String annotationValue = annotation.structuredValue().value().toString();

            bufferedWriter.write(annotationName + ": " + annotationValue);
            bufferedWriter.newLine();
        }
        bufferedWriter.newLine();
    }


}
