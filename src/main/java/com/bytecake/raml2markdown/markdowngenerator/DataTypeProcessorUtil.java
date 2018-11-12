package com.bytecake.raml2markdown.markdowngenerator;

import net.steppschuh.markdowngenerator.table.Table;
import org.apache.commons.text.StringEscapeUtils;
import org.raml.v2.api.model.v10.datamodel.*;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;
import org.raml.v2.api.model.v10.system.types.MarkdownString;

import java.util.ArrayList;
import java.util.List;

public class DataTypeProcessorUtil {
    public static void processObjectProperties(Table.Builder tableBuilder, String parentObjectName,
                                               List<TypeDeclaration> propertyList, boolean generateFullyQualifieName) {
        for (TypeDeclaration property : propertyList) {
            Parameter headerParameter = DataTypeProcessorUtil.getTypeParameter(property, parentObjectName);
            tableBuilder.addRow(headerParameter.getParameter(), headerParameter.getDescription());

            if (property.type().equalsIgnoreCase("Object")) {
                ObjectTypeDeclaration ObjectTypeProperty = (ObjectTypeDeclaration) property;
                List<TypeDeclaration> childPropertyList = ObjectTypeProperty.properties();

                String newParentObjectName;
                if(generateFullyQualifieName && parentObjectName != null && parentObjectName.length() > 0) {
                    newParentObjectName = parentObjectName + "." + property.name();
                } else {
                    newParentObjectName = property.name();
                }

                processObjectProperties(tableBuilder, newParentObjectName, childPropertyList, generateFullyQualifieName);
            }
        }
    }

    public static Parameter getTypeParameter(TypeDeclaration typeDeclaration) {
        return getTypeParameter(typeDeclaration, null);
    }

    public static Parameter getTypeParameter(TypeDeclaration typeDeclaration, String parentObjectName) {
        String parameterValue = getTypeParameterValue(typeDeclaration, parentObjectName);
        String descriptionValue = getTypeDescriptionValue(typeDeclaration);
        return new Parameter(parameterValue, descriptionValue);
    }

    public static String getTypeParameterValue(TypeDeclaration typeDeclaration, String parentObjectName) {
        StringBuilder parameterValue;
        String name = typeDeclaration.name();
        Boolean isRequired = typeDeclaration.required();
        String typeValue = typeDeclaration.type();

        if(parentObjectName != null && parentObjectName.length() > 0) {
            name = parentObjectName + "." + name;
        }

        // Name
        parameterValue = new StringBuilder(name);

        // Type
        appendToDescriptionValue(parameterValue, "", typeValue);

        // Required flag
        if (isRequired) {
            parameterValue.append(" Required");
        }
        return parameterValue.toString();
    }

    public static String getTypeDescriptionValue(TypeDeclaration typeDeclaration) {
        StringBuilder descriptionValue = new StringBuilder();

        // Description
        MarkdownString description = typeDeclaration.description();
        if(description != null) {
            appendToDescriptionValue(descriptionValue, "", description.value());
        }

        // Annotations (e.g. oas-schema-title)
        List<AnnotationRef> annotationList = typeDeclaration.annotations();
        generateAnnotations(descriptionValue, annotationList);

        processAsPerType(typeDeclaration, descriptionValue);

        // Default Value
        appendToDescriptionValue(descriptionValue, "Default Value: ", typeDeclaration.defaultValue());

        // Example
        appendToDescriptionValue(descriptionValue, "Example: ", typeDeclaration.example());

        // Example List
        appendExampleListToDescriptionValue(descriptionValue, "Examples: ", typeDeclaration.examples());

        return descriptionValue.toString();
    }

    private static void processAsPerType(TypeDeclaration typeDeclaration, StringBuilder descriptionValue) {
        String typeValue = typeDeclaration.type().toLowerCase();
        switch (typeValue) {
            case "string" :
                processStringType((StringTypeDeclaration) typeDeclaration, descriptionValue);
                break;
            case "array":
                processArrayType((ArrayTypeDeclaration) typeDeclaration, descriptionValue);
                break;
            case "boolean":
                processBooleanType((BooleanTypeDeclaration) typeDeclaration, descriptionValue);
                break;
            case "dateTime":
                processDateTimeType((DateTimeTypeDeclaration) typeDeclaration, descriptionValue);
                break;
            case "number":
                processNumberType((NumberTypeDeclaration) typeDeclaration, descriptionValue);
                break;
            case "object":
                processObjectType((ObjectTypeDeclaration) typeDeclaration, descriptionValue);
                break;
        }
    }

    public static void generateAnnotations(StringBuilder descriptionValue, List<AnnotationRef> annotationList) {
        for (AnnotationRef annotation : annotationList) {
            String annotationName = annotation.annotation().name();
            String annotationValue = annotation.structuredValue().value().toString();

            appendToDescriptionValue(descriptionValue, annotationName + ": ", annotationValue);
        }
    }

    private static StringBuilder appendToDescriptionValue(StringBuilder descriptionValue, String label, String item) {
        if (item != null) {
            if(descriptionValue.length() > 0) {
                descriptionValue.append("<br>");
            }
            descriptionValue.append(label).append(escapeStringForMarkdown(item));
        }
        return descriptionValue;
    }

    private static StringBuilder appendToDescriptionValue(StringBuilder descriptionValue, String label, ExampleSpec item) {
        if (item != null) {
            appendToDescriptionValue(descriptionValue, label, item.value());
        }
        return descriptionValue;
    }

    private static StringBuilder appendToDescriptionValue(StringBuilder descriptionValue, String label, Integer item) {
        if (item != null) {
            appendToDescriptionValue(descriptionValue, label, item.toString());
        }
        return descriptionValue;
    }

    private static StringBuilder appendToDescriptionValue(StringBuilder descriptionValue, String label, Double item) {
        if (item != null) {
            appendToDescriptionValue(descriptionValue, label, item.toString());
        }
        return descriptionValue;
    }

    private static StringBuilder appendToDescriptionValue(StringBuilder descriptionValue, String label, Boolean item) {
        if (item != null) {
            appendToDescriptionValue(descriptionValue, label, item.toString());
        }
        return descriptionValue;
    }

    private static StringBuilder appendToDescriptionValue(StringBuilder descriptionValue, String label, List<String> itemList, boolean separateWithBr) {
        if (itemList != null && itemList.size() > 0) {
            if(descriptionValue.length() > 0) {
                descriptionValue.append("<br>");
            }
            descriptionValue.append(label);
            boolean firstValue = true;
            for (String item : itemList) {
                if(separateWithBr) { // Separate with Line Breaks
                    descriptionValue.append("<br>");
                } else { // Separate with comma
                    if (firstValue) {
                        firstValue = false;
                    } else {
                        descriptionValue.append(", ");
                    }
                }
                descriptionValue.append(item);
            }
        }
        return descriptionValue;
    }

    private static StringBuilder appendExampleListToDescriptionValue(StringBuilder descriptionValue, String label, List<ExampleSpec> itemList) {
        if (itemList != null && itemList.size() > 0) {
            List<String> stringItemList = new ArrayList<>(itemList.size());
            for (ExampleSpec item : itemList) {
                stringItemList.add(item.value());
            }
            appendToDescriptionValue(descriptionValue, label, stringItemList, true);
        }
        return descriptionValue;
    }

    private static StringBuilder appendNumberListToDescriptionValue(StringBuilder descriptionValue, String label, List<Number> itemList) {
        if (itemList != null && itemList.size() > 0) {
            List<String> stringItemList = new ArrayList<>(itemList.size());
            for (Number item : itemList) {
                stringItemList.add(item.toString());
            }
            appendToDescriptionValue(descriptionValue, label, stringItemList, false);
        }
        return descriptionValue;
    }

    private static StringBuilder appendBooleanListToDescriptionValue(StringBuilder descriptionValue, String label, List<Boolean> itemList) {
        if (itemList != null && itemList.size() > 0) {
            List<String> stringItemList = new ArrayList<>(itemList.size());
            for (Boolean item : itemList) {
                stringItemList.add(item.toString());
            }
            appendToDescriptionValue(descriptionValue, label, stringItemList, false);
        }
        return descriptionValue;
    }

    private static void processObjectType(ObjectTypeDeclaration typeDeclaration, StringBuilder descriptionValue) {
        // Min Properties
        appendToDescriptionValue(descriptionValue, "Min Properties: ", typeDeclaration.minProperties());

        // Max Properties
        appendToDescriptionValue(descriptionValue, "Max Properties: ", typeDeclaration.maxProperties());

        // Additional Properties?
        appendToDescriptionValue(descriptionValue, "Additional Properties: ", typeDeclaration.additionalProperties());

        // Discriminator
        appendToDescriptionValue(descriptionValue, "Discriminator: ", typeDeclaration.discriminator());

        // Discriminator Value
        appendToDescriptionValue(descriptionValue, "Discriminator Value: ", typeDeclaration.discriminatorValue());
    }

    private static void processNumberType(NumberTypeDeclaration typeDeclaration, StringBuilder descriptionValue) {
        // minimum
        appendToDescriptionValue(descriptionValue, "Minimum: ", typeDeclaration.minimum());

        // maximum
        appendToDescriptionValue(descriptionValue, "Maximum: ", typeDeclaration.maximum());

        // Format
        appendToDescriptionValue(descriptionValue, "Format: ", typeDeclaration.format());

        // multipleOf
        appendToDescriptionValue(descriptionValue, "Multiple Of: ", typeDeclaration.multipleOf());

        // Enums
        appendNumberListToDescriptionValue(descriptionValue, "Enums: ", typeDeclaration.enumValues());
    }

    private static void processDateTimeType(DateTimeTypeDeclaration typeDeclaration, StringBuilder descriptionValue) {
        // Format
        appendToDescriptionValue(descriptionValue, "Format: ", typeDeclaration.format());
    }

    private static void processBooleanType(BooleanTypeDeclaration typeDeclaration, StringBuilder descriptionValue) {
        // Enums
        appendBooleanListToDescriptionValue(descriptionValue, "Enums: ", typeDeclaration.enumValues());
    }

    private static void processArrayType(ArrayTypeDeclaration typeDeclaration, StringBuilder descriptionValue) {
        // Min Items
        appendToDescriptionValue(descriptionValue, "Min Items: ", typeDeclaration.minItems());

        // Max Items
        appendToDescriptionValue(descriptionValue, "Max Items: ", typeDeclaration.maxItems());

        // Unique Items?
        appendToDescriptionValue(descriptionValue, "Unique Items: ", typeDeclaration.uniqueItems());

        // Array Items
        TypeDeclaration itemType = typeDeclaration.items();

        if(itemType != null) {
            appendToDescriptionValue(descriptionValue, "Items: ", "");

            // Description
            MarkdownString description = itemType.description();
            if (description != null) {
                appendToDescriptionValue(descriptionValue, "", description.value());
            }

            // Type
            appendToDescriptionValue(descriptionValue, "Item Type: ", itemType.type());

            processAsPerType(itemType, descriptionValue);
        }
    }

    private static void processStringType(StringTypeDeclaration typeDeclaration, StringBuilder descriptionValue) {
        // Validation Pattern
        appendToDescriptionValue(descriptionValue, "Pattern: ", typeDeclaration.pattern());

        // Min Length
        appendToDescriptionValue(descriptionValue, "Min Length: ", typeDeclaration.minLength());

        // Max Length
        appendToDescriptionValue(descriptionValue, "Max Length: ", typeDeclaration.maxLength());

        // Enums
        appendToDescriptionValue(descriptionValue, "Enums: ", typeDeclaration.enumValues(), false);
    }

    /*
    For future support:
        ExternalTypeDeclaration
            String schemaContent();
            String internalFragment();
            String schemaPath();
        FileTypeDeclaration
            Number minLength();
            Number maxLength();
            List<String> fileTypes();
        ExampleSpec
            String name();
            String value();
            TypeInstance structuredValue();
        UnionTypeDeclaration
            List<TypeDeclaration> of();
     */

    public static String escapeStringForMarkdown(String inputString) {
        if(inputString == null || inputString.length() == 0) {
            return inputString;
        }

        String outputString = inputString;
        outputString = StringEscapeUtils.escapeHtml4(outputString);
        outputString = outputString.replace(System.lineSeparator(), "<BR>");
        outputString = outputString.replace("\n", "<BR>");
        outputString = outputString.replace("|", "&#124;");
        return outputString;
    }
}
