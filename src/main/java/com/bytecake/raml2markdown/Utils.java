package com.bytecake.raml2markdown;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Utils {
    private final static Logger logger = LoggerFactory.getLogger(Utils.class);

    public static String createFolderHierarchy(String folderPath, String ... subFolders) throws ProcessingException {
        if(!isOutputFolderValid(folderPath)) {
            throw new ProcessingException("Output folder is invalid " + folderPath);
        }

        StringBuilder outputFolderPathBuilder = new StringBuilder(folderPath);

        for (String subFolder : subFolders) {
            outputFolderPathBuilder.append(File.separator).append(subFolder);
        }

        String outputFolderPath = outputFolderPathBuilder.toString();

        try {
            FileUtils.forceMkdir(new File(outputFolderPath));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ProcessingException("Error creating Output folder hierarchy " + outputFolderPath);
        }
        return outputFolderPath;
    }

    /*
    public static String createFolderHierarchy(String folderPath, String apiType, String apiVersion) throws ProcessingException {
        if(!isOutputFolderValid(folderPath)) {
            throw new ProcessingException("Output folder is invalid " + folderPath);
        }

        StringBuilder outputFolderPathBuilder = new StringBuilder(folderPath);

        // Add folder for TYPE
        outputFolderPathBuilder.append(File.separator).append(apiType);

        // Add folder for Version
        outputFolderPathBuilder.append(File.separator).append(apiVersion);

        String outputFolderPath = outputFolderPathBuilder.toString();

        try {
            FileUtils.forceMkdir(new File(outputFolderPath));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ProcessingException("Error creating Output folder hierarchy " + outputFolderPath);
        }

        return outputFolderPath;
    }
    */

    public static boolean isOutputFolderValid(String outputFolderPath) {
        return isOutputFolderValid(new File(outputFolderPath));
    }

    public static boolean isOutputFolderValid(File outputFolder) {
        if(!outputFolder.exists()) {
            logger.error("Specified output folder does not exists.");
            return false;
        }
        if(outputFolder.isFile()) {
            logger.error("Specified output folder is a file.");
            return false;
        }
        if(!(outputFolder.canWrite())) {
            logger.error("Specified output folder is not writable.");
            return false;
        }
        return true;
    }

    public static boolean isInputFileValid(File inputRamlFile) {
        if(!inputRamlFile.exists()) {
            logger.error("Specified input RAML file does not exists.");
            return false;
        }
        if(!inputRamlFile.isFile()) {
            logger.error("Specified input RAML file is not a file.");
            return false;
        }
        if(!inputRamlFile.canRead()) {
            logger.error("Specified input RAML file is not readable.");
            return false;
        }
        return true;
    }
}
