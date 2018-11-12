package com.bytecake.raml2markdown.markdowngenerator;

import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.code.CodeBlock;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.apache.log4j.Logger;
import org.raml.v2.api.model.v10.datamodel.ExampleSpec;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;
import org.raml.v2.api.model.v10.system.types.MarkdownString;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class MethodBodyMarkdownGenerator {
    private final static Logger logger = Logger.getLogger(MethodBodyMarkdownGenerator.class);

    public static void generateBodyMarkdown(BufferedWriter bufferedWriter, List<TypeDeclaration> body, int level) throws IOException {
        logger.info("Generating Body Markdown");
        Heading heading;
        heading = new Heading("Body", level);
        heading.setUnderlineStyle(false);
        bufferedWriter.write(heading.toString());
        bufferedWriter.newLine();
        bufferedWriter.newLine();

        // List<TypeDeclaration> body = method.body();
        for (TypeDeclaration bodyTypeDeclaration : body) {
            String name = bodyTypeDeclaration.name();

            // Type
            heading = new Heading("Type: " + name, level + 1);
            heading.setUnderlineStyle(false);
            bufferedWriter.write(heading.toString());
            bufferedWriter.newLine();

            // Description
            MarkdownString description = bodyTypeDeclaration.description();
            if(description != null) {
                bufferedWriter.write(description.value());
                bufferedWriter.newLine();
            }

            // Annotations (e.g. oas-schema-title)
            List<AnnotationRef> annotationList = bodyTypeDeclaration.annotations();
            MarkdownGenerator.generateAnnotations(bufferedWriter, annotationList);

            //Build Parameter Table
            Table.Builder tableBuilder = new Table.Builder()
                    .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                    .withRowLimit(0) // No row limit
                    .addRow("Parameter", "Description");

            if (bodyTypeDeclaration.type().equalsIgnoreCase("Object")) {
                ObjectTypeDeclaration objectTypeBodyTypeDeclaration = (ObjectTypeDeclaration) bodyTypeDeclaration;
                List<TypeDeclaration> propertyList = objectTypeBodyTypeDeclaration.properties();

                DataTypeProcessorUtil.processObjectProperties(tableBuilder, null,
                        propertyList, false);
            }
            bufferedWriter.write(tableBuilder.build().serialize());
            bufferedWriter.newLine();
            bufferedWriter.newLine();

            // Example
            ExampleSpec example = bodyTypeDeclaration.example();
            if (example != null) {
                heading = new Heading("Example: ", level + 1);
                heading.setUnderlineStyle(false);
                bufferedWriter.write(heading.toString());
                bufferedWriter.newLine();
                String exampleText = new CodeBlock(example.value()).toString();
                bufferedWriter.write(exampleText);
                bufferedWriter.newLine();
            }

            // Example List
            List<ExampleSpec> exampleList = bodyTypeDeclaration.examples();
            if (exampleList != null && exampleList.size() > 0) {
                heading = new Heading("Examples: ", level + 1);
                heading.setUnderlineStyle(false);
                bufferedWriter.write(heading.toString());
                bufferedWriter.newLine();

                for (ExampleSpec exampleSpec : exampleList) {
                    bufferedWriter.newLine();
                    String exampleText = new CodeBlock(exampleSpec.value()).toString();
                    bufferedWriter.write(exampleText);
                    bufferedWriter.newLine();
                }
                bufferedWriter.newLine();
            }
        }
        bufferedWriter.newLine();
    }
}
