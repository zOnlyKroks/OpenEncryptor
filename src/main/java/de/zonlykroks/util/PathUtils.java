package de.zonlykroks.util;

import de.zonlykroks.OpenEncryptor;

public class PathUtils {

    public static String getExecutionPath() {
        try{
            String executionPath = System.getProperty("user.dir");
            return executionPath.replace("\\", "/");
        }catch (Exception e){
            OpenEncryptor.LOGGER.severe(e.getMessage());
        }
        return "C:/";
    }

}
