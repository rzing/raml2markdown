package com.bytecake.raml2markdown.markdowngenerator;

import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class MethodHeaderMarkdownGenerator {
    private final static Logger logger = LoggerFactory.getLogger(MethodHeaderMarkdownGenerator.class);

    public static void generateHeadersMarkdown(BufferedWriter bufferedWriter, List<TypeDeclaration> headerList, int level) throws IOException {
        logger.info("Generating Headers Markdown");
        Heading heading;
        heading = new Heading("Headers", level);
        heading.setUnderlineStyle(false);
        bufferedWriter.write(heading.toString());
        bufferedWriter.newLine();
        bufferedWriter.newLine();

        Table.Builder tableBuilder = new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .withRowLimit(0) // No row limit
                .addRow("Parameter", "Description");

        for (TypeDeclaration header : headerList) {
            Parameter headerParameter = DataTypeProcessorUtil.getTypeParameter(header);
            tableBuilder.addRow(headerParameter.getParameter(), headerParameter.getDescription());
        }
        bufferedWriter.write(tableBuilder.build().serialize());
        bufferedWriter.newLine();
        bufferedWriter.newLine();
    }
}