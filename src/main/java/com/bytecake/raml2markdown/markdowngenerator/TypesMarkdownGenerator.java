package com.bytecake.raml2markdown.markdowngenerator;

import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.apache.log4j.Logger;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class TypesMarkdownGenerator extends MarkdownGenerator {
    private final static Logger logger = Logger.getLogger(TypesMarkdownGenerator.class);

    private final String fileName = "types";
    private final String title = "Types";


    public TypesMarkdownGenerator(Api ramlModelApi, String outputFolderPath, String apiName, String apiType, boolean isDraft) {
        super(ramlModelApi, outputFolderPath, apiName, apiType, isDraft);
    }

    @Override
    public void generate() throws IOException {
        generateTypesMarkdownFile();
        processTypes();
    }

    private void processTypes() throws IOException {
        List<TypeDeclaration> types = ramlModelApi.types();
        for (TypeDeclaration type : types) {
            String nameValue = type.name();
            String displayNameValue = type.displayName().value();
            String typeValue = type.type();
            String descriptionValue = type.description().value();


            generateIndividualTypeMarkdownFile(nameValue, displayNameValue, descriptionValue, typeValue);

            /*if(typeValue.equalsIgnoreCase("String")) {
                StringTypeDeclaration stringType = (StringTypeDeclaration) type;
                Integer minLength = stringType.minLength();
                Integer maxLength = stringType.maxLength();
            }*/
        }
    }

    private void generateTypesMarkdownFile() throws IOException {
        generateBasicMarkdownFile(fileName, title, title);
    }

    private void generateIndividualTypeMarkdownFile(String typeName, String displayName, String description, String type) throws IOException {
        String fileName = typeName.toLowerCase();
        BufferedWriter bufferedWriter = generateBasicMarkdownFile(fileName, displayName, displayName, true);

        Heading heading;
        // Type Header
        heading = new Heading("Type " + typeName, 3);
        heading.setUnderlineStyle(false);
        bufferedWriter.write(heading.toString());
        bufferedWriter.newLine();

        // description
        bufferedWriter.write(description);
        bufferedWriter.newLine();
        bufferedWriter.newLine();

        // Type value
        bufferedWriter.write("Type: " + type);
        bufferedWriter.newLine();

        bufferedWriter.close();
    }


}
