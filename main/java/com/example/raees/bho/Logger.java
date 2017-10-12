package com.example.raees.bho;

import android.text.format.DateFormat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by mzmimr001 on 2016/08/31.
 */
public class Logger {

    // this class will log video player activity
    // must write to csv file
    // other system aspects are invisible to this class
    // and will be passed from the classes which use this one

    // data to determine the name of the file
    // the user id
    private static String userID;
    // the user name
    private static String userName;
    // the file type this class generates
    private static final String ftype = "DATA";
    // file containing the userID and userName
    private static File userData;
    // video views: index 0
    // video pauses: index 1
    // file sharing: index 2
    // audio rec: index 3
    // raw actions: index 4
    private static File[] logs;
    private static final String[] LOG_NAMES = {"VIEW","PAUSE","SHARE","RECORD","RAW"};
    private static String[] log_names;
    private static final String ext = "_LOG.csv";
    private static final int logFiles = 5;
    // csv writer helpers
    private static final String cm = ",";
    // the last video played
    private static String lastVideoPlayed = "No video played yet";

    // default constructor
    private Logger(){}

    // get methods
    public static String getUserName(){
        return userName;
    }
    public static String getUserID(){
        return userID;
    }
    // sets up the log file using the user name parameters
    public static boolean setLogFile(){
        // whether to actually continue setting up the log file after getting valid user data
        // this is to prevent the app from going into settings while knowing the
        // user ID and name
        boolean proceed = true;
        // first see if the user id and name is null
        // if they are
        if (Logger.userName==null && Logger.userID==null){
            // try and read them from a file
            String[] userData = getStoredUserData();
            // if the returned array is not null
            // and has at least two indexes
            if (userData!=null && userData.length>1){
                // set the user data within the class
                Logger.userID = userData[0];
                Logger.userName = userData[1];
                proceed = true;
            }
            else{
                // otherwise the file probably does not exist
                // and it needs to be created by the settings menu
                proceed = false;
            }
        }
        // otherwise with the logfile creation
        if (proceed && userID!=null && userName!=null) {
            // the success and failure of the log file creation
            boolean setupFlag = true;
            // assign the log file
            logs = new File[logFiles];
            log_names = new String[logFiles];
            // create each of the log files using the file system
            for (int i = 0; i < logFiles; i++) {
                // change the log names
                log_names[i] = userID + "_" + LOG_NAMES[i] + ext;
                // create the log file
                logs[i] = FileSystemv2.createFile(log_names[i], ftype);
            }
            // check that the log files were created according to the user data files
            String[] userData = getStoredUserData();
            // if the user data has 2 entries in it then check the log files exist
            if (userData!=null && userData.length>=2) {
                setupFlag = checkLogFiles(userData[0], userData[1]);
            }
            // otherwise there was ann error in the setup
            else{
                setupFlag = false;
            }
            // return the flag
            return setupFlag;
        }
        else{
            return false;
        }
    }
    // gets the userID and userName from the file in order
    public static String[] getStoredUserData(){
        // the storage for the user data
        String[] out = null;
        // the file handle of the file
        if (userData==null || !userData.exists()) {
            // create it if it does not
            userData = FileSystemv2.createFile("user.txt", ftype);
        }
        // get the file
        if (userData!=null && userData.exists()) {
            Scanner sc;
            // read the data from the file
            try {
                sc = new Scanner(userData);
                // create the arrray
                out = new String[2];
                // tracks progress
                int i = 0;
                while (sc.hasNextLine() && i < 2){
                    out[i] = sc.nextLine();
                    i++;
                }
            }catch(FileNotFoundException e){}
            // done, return the data
        }
        return out;
    }
    // sets the stored userName and userName to a file in order
    public static void setStoredUserData(String userID, String userName){

        // check that the file does not already exist
        if (userData==null || !userData.exists()) {
            // create it if it does not
            userData = FileSystemv2.createFile("user.txt", ftype);
        }
        if (!(Logger.userID == userID && Logger.userName == userName)) {
            try {
                // try to write the user data, overwrite any existing data
                FileWriter fw = new FileWriter(userData.getAbsolutePath(), false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw);
                // write the datas to the file
                out.print(userID + "\n" + userName);
                out.close();
            } catch (IOException e) {}
            // set the data within the class
            Logger.userID = userID;
            Logger.userName = userName;
            // now setup the log files
            setLogFile();
        }

    }
    // helper method for the log file setup method
    // determine if the log file needs to be setup
    // returns true if log files are present
    private static boolean checkLogFiles(String uID, String uName){
        // the flag which checks that the log files exist
        boolean flag = true;
        // check that the user name and ID have not changed
        flag = flag && (userName!=null && userName.equals(uName)) && (userID!=null && userID.equals(uID));
        // optimization
        // if the user names and IDs are correct we check if the logs exist
        // if not we just assert that the appropriate log files do not exist
        if (flag){
            // check that they have all been created
            for (int i = 0; i < logFiles; i++){
                // each file must exist in order for this to be true
                flag = flag && (logs[i]!=null && logs[i].exists());
            }
        }
        // return the flag
        return flag;
    }
    // method to log activity to the raw log file
    // and to the appropriate log file
    public static boolean logActivity(String videoName, String action){
        // the return value to indicate successful log activity
        // default to false and will only change upon successful writing
        boolean flag = false;
        // reset the last video played
        // if the videoName is not a space then reset the last video played
        if ( !videoName.equals(" ")){
            lastVideoPlayed = videoName;
        }
        // attempt to create a line for the csv log file,
        // write to it and store the success of the writing
        try{
            // write a raw log entry to the log file which cotains the raw activity
            writeCSVEntry(createRawCSVEntry(action, lastVideoPlayed), logs[4]);
            // will only reach this line if the writing was successfull
            flag = true;
        }
        // the writers may throw a file not found exception, but no other exception
        catch (IOException e){}
        // return the success flag
        // a success flag of false will show that an exception occurred
        return flag;
    }
    // helper method to determine which log file to write to
    private static File findLogFile(String action){
        // determine which action was done
        if (action.startsWith("VIEW")){
            return logs[0];
        }
        else if (action.startsWith("PAUSE")){
            return logs[1];
        }
        else if (action.startsWith("SHARE")){
            return logs[2];
        }
        else if (action.startsWith("RECORD")){
            return logs[3];
        }
        else
            return logs[4];
    }
    // helper method to create a csv entry for the raw log file
    private static String createRawCSVEntry(String action, String videoName){
        // determine which action was done
        String date = getDate();
        String time = getTime();
        String[] entryContent = {userID, userName, date,time, videoName, action};
        return createCSVEntry(entryContent);
    }
    // a more general method for creating a csv entry
    private static String createCSVEntry(String[] logContents){
        // the string which is the final entry
        String entry = "";
        // build the string
        for (int i = 0; i < logContents.length; i++){
            // if we are at the end
            if (i==logContents.length-1){
                // just append the final entry
                entry = entry + logContents[i];
            }
            else{
                // otherwise add a comma at the end
                entry = entry + logContents[i] + cm;
            }
        }
        // return the entry which is the final csv entry
        return entry;
    }
    // this method does the writing to the csv file
    private static void writeCSVEntry(String entry, File log) throws IOException {
        // series of file writers for logging purposes
        // this will open the log file in read only mode
        FileWriter fw = new FileWriter(log.getAbsolutePath(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        // the output writer we eventually use
        PrintWriter out = new PrintWriter(bw);
        // write the csv entry to the file with a new line character at the end
        out.println(entry);
        out.close();
    }
    //returns the current date in the format DD/MM/YYYY
    private static String getDate(){
        long milliTime = new Date().getTime();
        String d = DateFormat.format("dd/MM/yyyy hh:mm:ss", milliTime).toString();
        String [] dArray = d.split(" ");
        return dArray[0];
    }
    //returns the current time in the format HH:MM:SS
    private static String getTime(){
        long milliTime = new Date().getTime();
        String d = DateFormat.format("dd/MM/yyyy hh:mm:ss", milliTime).toString();
        String [] dArray = d.split(" ");
        return dArray[1];
    }

    // method to return an arraylist of the file
    public static ArrayList<String> getLogfileList(){

        // this determines whether to return null or not
        boolean returnNull = false;
        ArrayList<String> container = new ArrayList<String>();
        // iterate through the log
        for (String f : log_names){
            if (f!=null ) {
                container.add(f);
            }
        }
        return container;
    }
}
