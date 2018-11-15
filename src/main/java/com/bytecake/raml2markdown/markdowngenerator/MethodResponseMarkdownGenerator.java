package com.bytecake.raml2markdown.markdowngenerator;

import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.system.types.MarkdownString;
import org.raml.v2.api.model.v10.system.types.StatusCodeString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class MethodResponseMarkdownGenerator {
    private final static Logger logger = LoggerFactory.getLogger(MethodResponseMarkdownGenerator.class);

    public static void generateResponseMarkdown(BufferedWriter bufferedWriter, Method method) throws IOException {
        logger.info("Generating Method Response Markdown");

        Heading heading;
        heading = new Heading("Response", 3);
        heading.setUnderlineStyle(false);
        bufferedWriter.write(heading.toString());
        bufferedWriter.newLine();
        bufferedWriter.newLine();

        List<Response> responseList = method.responses();
        for (Response response : responseList) {
            generateIndividualResponseMarkdown(bufferedWriter, response);
        }
    }

    private static void generateIndividualResponseMarkdown(BufferedWriter bufferedWriter, Response response) throws IOException {
        StatusCodeString statusCode = response.code();
        if(statusCode == null) {
            logger.error("Response status code not found");
            return;
        }

        logger.info("Generating Method Response Markdown for code: " + statusCode.value());

        // code
        Heading heading;
        heading = new Heading("Response HTTP Code: " + statusCode.value(), 4);
        heading.setUnderlineStyle(false);
        bufferedWriter.write(heading.toString());
        bufferedWriter.newLine();

        // description
        MarkdownString description = response.description();
        if(description != null) {
            bufferedWriter.write(description.value());
            bufferedWriter.newLine();
        }
        bufferedWriter.newLine();

        // Headers
        List<TypeDeclaration> headerList = response.headers();
        if(headerList != null && headerList.size() > 0) {
            MethodHeaderMarkdownGenerator.generateHeadersMarkdown(bufferedWriter, headerList, 5);
        }

        // Body
        List<TypeDeclaration> body = response.body();
        if(body != null && body.size() > 0) {
            MethodBodyMarkdownGenerator.generateBodyMarkdown(bufferedWriter, body, 5);
        }
    }
}
