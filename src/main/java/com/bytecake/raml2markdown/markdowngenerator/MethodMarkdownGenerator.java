package com.bytecake.raml2markdown.markdowngenerator;

import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.apache.log4j.Logger;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.methods.TraitRef;
import org.raml.v2.api.model.v10.security.SecuritySchemeRef;
import org.raml.v2.api.model.v10.system.types.AnnotableStringType;
import org.raml.v2.api.model.v10.system.types.MarkdownString;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class MethodMarkdownGenerator extends MarkdownGenerator {
    private final static Logger logger = Logger.getLogger(MethodMarkdownGenerator.class);
    private Method method;
    private String resourceTitle;
    private final String resourceUri;

    public MethodMarkdownGenerator(Api ramlModelApi, String outputFolderPath, String apiName, String apiType, boolean isDraft,
                                   Method method, String resourceTitle, String resourceUri) {
        super(ramlModelApi, outputFolderPath, apiName, apiType, isDraft);
        this.method = method;
        this.resourceTitle = resourceTitle;
        this.resourceUri = resourceUri;
    }

    @Override
    public void generate() throws IOException {
        logger.info("Generating Individual Method Markdown File ...");
        String methodName = method.method();

        // Generate basic file structure
        BufferedWriter bufferedWriter = generateBasicMarkdownFile(methodName.toLowerCase(), methodName.toUpperCase(),
                resourceTitle + ": " + methodName.toUpperCase(), true);

        // Traits
        List<TraitRef> traitRefList = method.is();
        StringBuilder traitRefBuilder = new StringBuilder();
        for (TraitRef traitRef : traitRefList) {
            if(traitRefBuilder.length() > 0) {
                traitRefBuilder.append(", ");
            }
            traitRefBuilder.append(traitRef.trait().name());
        }
        bufferedWriter.write("Traits: " + traitRefBuilder);
        bufferedWriter.newLine();

        // Annotations (e.g. oas-summary)
        List<AnnotationRef> annotationList = method.annotations();
        MarkdownGenerator.generateAnnotations(bufferedWriter, annotationList);

        // Request
        generateRequestMarkdown(bufferedWriter, method, resourceUri);

        // Headers
        List<TypeDeclaration> headerList = method.headers();
        if(headerList != null && headerList.size() > 0) {
            MethodHeaderMarkdownGenerator.generateHeadersMarkdown(bufferedWriter, headerList, 4);
        }

        // Body
        List<TypeDeclaration> body = method.body();
        MethodBodyMarkdownGenerator.generateBodyMarkdown(bufferedWriter, body, 4);

        // Response
        MethodResponseMarkdownGenerator.generateResponseMarkdown(bufferedWriter, method);

        MarkdownString description = method.description();
        AnnotableStringType displayName = method.displayName();

        List<String> protocols = method.protocols();
        List<TypeDeclaration> queryParameters = method.queryParameters();
        TypeDeclaration queryString = method.queryString();

        List<SecuritySchemeRef> securedBy = method.securedBy();

        bufferedWriter.close();
    }

    private void generateRequestMarkdown(BufferedWriter bufferedWriter, Method method, String resourceUri) throws IOException {
        logger.info("Generating Request Markdown");
        String methodName = method.method();

        Heading heading;
        heading = new Heading("Request", 3);
        heading.setUnderlineStyle(false);
        bufferedWriter.write(heading.toString());
        bufferedWriter.newLine();

        heading = new Heading(methodName.toUpperCase() + " " + resourceUri, 4);
        heading.setUnderlineStyle(false);
        bufferedWriter.write(heading.toString());
        bufferedWriter.newLine();
        bufferedWriter.newLine();
    }

}
