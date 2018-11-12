package com.bytecake.raml2markdown.markdowngenerator;

public class Parameter {
    private String parameter;
    private String description;

    public Parameter(StringBuilder parameter, StringBuilder description) {
        this.parameter = parameter.toString();
        this.description = description.toString();
    }

    public Parameter(String parameter, String description) {
        this.parameter = parameter;
        this.description = description;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
    public void setParameter(StringBuilder parameter) {
        this.parameter = parameter.toString();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void setDescription(StringBuilder description) {
        this.description = description.toString();
    }
}
