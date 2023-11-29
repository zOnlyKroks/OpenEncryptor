package de.zonlykroks.util;

public class PathUtils {

    public static String getExecutionPath() {
        try{
            String executionPath = System.getProperty("user.dir");
            return executionPath.replace("\\", "/");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "C:/";
    }

}
