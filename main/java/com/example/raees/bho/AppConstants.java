package com.example.raees.bho;

/**
 * Class contains the constants required for customization purposes
 * once compiled the class is not expected to do anything else
 */
public class AppConstants extends MainActivity {

    // the name of the app
    public static String APPLICATION_NAME;
    // the prefix of video files which shall be found by the app
    public static String APPLICATION_FILE_PREFIX;
    // the language of the app presented in the UI
    public static String APPLICATION_LANGUAGE;

    public  AppConstants(String APPLICATION_NAME, String APPLICATION_FILE_PREFIX,String APPLICATION_LANGUAGE){
        this.APPLICATION_NAME = APPLICATION_NAME;
        this.APPLICATION_FILE_PREFIX = APPLICATION_FILE_PREFIX;
        this.APPLICATION_LANGUAGE = APPLICATION_LANGUAGE;

    }

}
