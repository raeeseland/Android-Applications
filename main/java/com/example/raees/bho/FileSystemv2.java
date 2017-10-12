package com.example.raees.bho;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mzmimr001 on 2016/09/07.
 */
public class FileSystemv2 {

    // this class is used to access and manage the files created by the application
    // faciliates creation and access of files created by the app

    // this is the parent of the parent directory, the root of the visible files
    // rarely used only for finding other files
    // which is hopefully not the case,but just in case
    private static File root;
    private static final String ROOT_SEEK = "ROOT";
    private static long freeSpaceKB;
    // this is the parent directory which shall be accessed by application
    // to store log files, audio recordings, and video files
    private static File dir;
    // the directory where recordings will be stored in the parent directory
    private static File rec;
    // the directory where videos will be stored in the parent directory
    private static File vid;
    // the name of the parent directory
    // also the directory of log file
    private static final String DIR_NAME = "_Data";
    private static final String DIR_EXT = "DAT";
    private static String dir_name;
    // names of the subdirectories
    // audio etc recordings
    private static final String REC_DIR = "_Recordings";
    private static final String REC_SEEK = "REC";
    private static final String REC_EXT = "3gpp";
    private static String rec_dir;
    // videos
    public static final String VID_DIR = "_Videos";
    private static final String VID_SEEK = "VID";
    private static final String VID_EXT = "mp4";
    private static String vid_dir;
    // indicator of directory - -1 if no directory
    // mainly used for debugging
    private static int USED_DIRECTORY;
    // tell if the file system already exists
    private static boolean fileSystemOK;
    // tells the name of the app
    private static String appName;
    // tells the name of the valid prefixes
    private static String appPrefix;

    /* possible directories

        android 4.4 - internal
            /mnt/sdcard2
            /storage/sdcard

        android 4.4 - external
            /storage/sdcard0
            /sdcard		 NOTE: becomes the internal storage when cd card is not in phone
            /mnt/sdcard

        android 5.0 - internal
            /storage/emulated/legacy
            /storage/emulated/0
            /storage/sdcard0
            /sdcard
            /mnt/sdcard

        android 5.0 - external
            /mnt/extSdCard
            /storage/extSdCard
    */

    // setup utilities
    // constructor
    private FileSystemv2(){

        // the status ints
        USED_DIRECTORY = -1;
        // the file system check
        fileSystemOK = false;
        // get the app prefix
        appPrefix = AppConstants.APPLICATION_FILE_PREFIX;
        // get the app name
        appName = AppConstants.APPLICATION_NAME;
        // get the amount of free space available
        freeSpaceKB = getFreeKilobytes();

    }

    public static long getFreeKilobytes(){
        if (root!=null && root.exists()) {
            freeSpaceKB = (long) Math.floor(root.getFreeSpace() / 1024);
            return freeSpaceKB;
        }
        else return 0;
    }

    public static boolean sufficientStorageSpace(){
        return getFreeKilobytes()>1024;
    }

    // checks that the file system is still vaild
    private static boolean isFileSystemOK(String appName){

        // if none of the directory file handles are null
        if (dir!=null && rec!=null && vid!=null && appName!=null){
            // and if they all exist, return true - file system is ok
            return (dir.exists() && rec.exists() && vid.exists() && FileSystemv2.appName.equals(appName));
        }
        // otherwise the file system is not ok - may need to be rebuilt
        else{
            // return false
            return false;
        }
    }

    // checks that parent directory is still valid
    // internal utility for file creation methods
    private static boolean checkFileSystemValidity(){
        return (isFileSystemOK(FileSystemv2.appName) && (root!=null && root.exists()));
    }

    // setup the file system, must be called by the main activity
    public static void setupFileSystem(AppCompatActivity c, String appName){

        // setup the file system if the file system is not ok
        // if the file system needs rebuilding
        if (!isFileSystemOK(appName)) {
            // adding hardcoded defaults
            setAppName(appName);
            // try the hard coded directories
            int switcher = 0;
            switch (switcher) {
                // try the one generic one
                case 0:
                    // try the first option
                    dir = new File("/storage/extSdCard");
                    if (dir.exists()) {
                        // make the directory
                        dir.mkdir();
                        // set status int
                        USED_DIRECTORY = 1;
                        // stop
                        break;
                    }
                case 1:
                    // try the second option option
                    dir = new File("/sdcard");
                    if (dir.exists()) {
                        // make the directory
                        dir.mkdir();
                        // set status int
                        USED_DIRECTORY = 2;
                        // stop
                        break;
                    }
                case 2:
                    // try getting the external storage using getExternalStorageDirectory
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        // try a
                        dir = Environment.getExternalStorageDirectory();
                        if (dir.exists()) {
                            // make the directory
                            dir.mkdir();
                            // set status int
                            USED_DIRECTORY = 3;
                            // stop
                            break;
                        }
                    }
                case 3:
                    // try getting the external storage using getExternalFilesDir
                    dir = c.getExternalFilesDir(dir_name);
                    if (dir.exists()) {
                        // make it a directory
                        dir.mkdir();
                        // set status int
                        USED_DIRECTORY = 4;
                        // now we are done
                        break;
                    }
                case 4:
                    // try getting the external storage using
                    // using Environment.getExternalStoragePublicDirectory(name of the folder)
                    // permissions dialog in android manifest
                    dir = Environment.getExternalStoragePublicDirectory(dir_name);
                    if (dir.exists()) {
                        // make it a directory
                        dir.mkdir();
                        // set status int
                        USED_DIRECTORY = 5;
                        // now we are done
                        break;
                    }
            }
            // the root has been found so assign it as such
            root = dir;
            // the parent directory is set, now make the subdirectories
            dir = new File(dir, dir_name);
            dir.mkdirs();
            // testing
            String t = dir.getAbsolutePath();
            boolean worked = createSubdirectories();
            //worked = createSubdirectories();
            // confirm that the file system is ok
            fileSystemOK = worked && dir!=null && dir.exists();
        }
    }

    // setup the file system which can be called by any activity
    public static void setupFileSystem(String appName){

        // only setup the file system if the file system is not ok
        if (!isFileSystemOK(appName)) {
            // adding hardcoded defaults
            setAppName(appName);
            // try the hard coded directories
            int switcher = 0;
            switch (switcher) {
                // try the one generic one
                case 0:
                    // try the first option
                    dir = new File("/storage/extSdCard");
                    if (dir.exists()) {
                        // make the directory
                        dir.mkdir();
                        // set status int
                        USED_DIRECTORY = 1;
                        // stop
                        break;
                    }
                case 1:
                    // try the second option option
                    dir = new File("/sdcard");
                    if (dir.exists()) {
                        // make the directory
                        dir.mkdir();
                        // set status int
                        USED_DIRECTORY = 2;
                        // stop
                        break;
                    }
                case 2:
                    // try getting the external storage using getExternalStorageDirectory
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        // try a
                        dir = Environment.getExternalStorageDirectory();
                        if (dir.exists()) {
                            // make the directory
                            dir.mkdir();
                            // set status int
                            USED_DIRECTORY = 3;
                            // stop
                            break;
                        }
                    }
                case 4:
                    // try getting the external storage another way
                    dir = Environment.getExternalStoragePublicDirectory(dir_name);
                    if (dir.exists()) {
                        // make it a directory
                        dir.mkdir();
                        // set status int
                        USED_DIRECTORY = 5;
                        // now we are done
                        break;
                    }
            }
            // the root has been found so assign it as such
            root = dir;
            // the parent directory is set, now make the subdirectories
            dir = new File(dir, dir_name);
            dir.mkdir();
            // create subdirectories
            // testing
            String t = dir.getAbsolutePath();
            boolean worked = createSubdirectories();
            worked = createSubdirectories();
            // confirm that the file system is valid
            fileSystemOK =true;
        }
    }

    // setup the file system if the sd card has been removed, must be called by the main activity
    private static void resetFileSystem(AppCompatActivity c){
        // reset using the standard setup method
        setupFileSystem(c, FileSystemv2.appName);
    }

    // local utility for resetting the file system without using the main activity
    private static void resetFileSystem(){
        // reset using the standard setup method
        setupFileSystem(FileSystemv2.appName);

    }

    // creates the subdirectories
    private static boolean createSubdirectories(){

        // create the sub directories
        //String the1 = dir.getAbsolutePath() + "/" + rec_dir;
        rec = new File(dir.getAbsolutePath() + "/" + rec_dir);
        rec.mkdirs();
        //String the2 = dir.getAbsolutePath() + "/" + vid_dir;
        vid = new File(dir.getAbsolutePath() + "/" + vid_dir);
        vid.mkdirs();
        // return the boolean
        return (rec.exists() && vid.exists());
    }

    // setup the directory names
    // part of basic implementation of the customization
    private static void setAppName(String appName){
        // change the names of the directories
        FileSystemv2.appName = appName;
        dir_name = appName + DIR_NAME;
        rec_dir = appName + REC_DIR;
        vid_dir = appName + VID_DIR;
    }

    // returns the parent directory of the app data
    public static File getAppDirectory(){
        // returns the directory in which the app operates
        return dir;
    }

    public static File getAppDirectory(String filetype){
        // returns the directory in which the app operates
        // depending on the string argument submitted
        if (filetype.equals(REC_SEEK)){
            // return the video directory
            return rec;
        }
        else if (filetype.equals(VID_SEEK)){
            // return the recordings directory
            return vid;
        }
        else if (filetype.equals(ROOT_SEEK)){
            // return the root directory
            return root;
        }
        else{
            // otherwise return the parent directory
            return dir;
        }

    }

    // returns the name of the parent directory of the app data
    public static String getAppDirectoryName(){
        // returns the directory name in which the app operates
        return dir_name;
    }

    public static String getAppDirectoryName(String filetype){
        // returns the directory in which the app operates
        // depending on the string argument submitted
        if (filetype.equals(REC_SEEK)){
            // return the video directory
            return rec_dir;
        }
        else if (filetype.equals(VID_SEEK)){
            // return the recordings directory
            return vid_dir;
        }
        else if (filetype.equals(ROOT_SEEK)){
            // return the name of the root directory
            return root.getAbsolutePath();
        }
        else{
            // otherwise return the parent directory
            return dir_name;
        }

    }

    // get the absolute directory of the applications storage directory
    public static String getStoragePath(){
        return dir.getAbsolutePath();
    }

    // gets the file handle to an existing file
    // for the bluetooth class
    public static File getFile(String filename, File directory){
        // get the file handle
        return new File(directory.getAbsolutePath() + "/" + filename);
        // if it exists return it
    }

    public static File getFile(String filename, String directory){
        // get the file handle
        return new File(directory + "/" + filename);
        // if it exists return it
    }

    public static File createFile(String path){
        File f = new File(path);
        if (!f.exists()){
            try {
                f.createNewFile();
            }catch(IOException e){e.printStackTrace();}
        }
        return f;
    }

    // creates a file within the file system
    // and returns the corresponding file handle
    public static File createFile(String filename, String filetype){
        // the return value is a file within the file system
        File f;
        //FileOutputStream fos;
        // will typically be an audio or a log
        // first check that the filesystem is still valid
        if (checkFileSystemValidity()){
            // if it is a video
            if (filetype.equals(VID_SEEK)) {
                // create a file in the video directory
                f = new File(vid, filename);
            }
            // if it is a recording
            else if (filetype.equals(REC_SEEK)){
                // create a file in the recording directory
                f = new File(rec,filename);
            }
            // otherwise just create the file in the app folder
            else{
                f = new File(dir, filename);
            }
            // now check that the file has been created
            // ensure that the file has been created
            if (!f.exists()){
                try {
                    f.createNewFile();
                }catch(IOException e){e.printStackTrace();}
            }
            // return the file handle
            return f;
        }
        // if the file is not valid
        else{
            // return null to indicate to caller that file system is no longer valid
            return null;
        }
    }

    // creates a file within the file system - robust to filesystem changes
    // this requires methods in the main activity which will handle resets
    // therefore, the main activity must have a method which allows other activities to call it
    // to pass the required information, being the context
    public static File createFile(String filename, String filetype, AppCompatActivity c){
        // the return value is a file within the file system
        // will typically be an audio or a log
        // first check that the filesystem is still valid
        if (checkFileSystemValidity()){
            // if it is a video
            if (filetype.equals(VID_SEEK)) {
                // create a file in the video directory
                return new File(vid_dir, filename);
            }
            // if it is a recording
            else if (filetype.equals(REC_SEEK)){
                // create a file in the recording directory
                return new File(rec_dir, filename);
            }
            // otherwise just create the file in the app folder
            else{
                return new File(dir, filename);
            }
        }
        // if the file is not valid
        else{
            // reset the file system
            resetFileSystem(c);
            // now try to create the file
            return createFile(filename, filetype);
        }
    }



    // finds all files of a particular type
    // returns their handles in an array list
    public static ArrayList<File> findFiles(String filetype){
        // get the required directroy to be searched
        File d = getAppDirectory(filetype);
        // get all the files in the directory
        ArrayList<File> all = findAllFiles(d, filetype);
        // return the required array list
        return all;
    }

    // helper method to findFiles
    // facilitates possible recursive searching
    private static ArrayList<File> findAllFiles (File file, String filetype){
        // initialize the array list which stores the required files
        ArrayList<File> all = new ArrayList<File>();
        // find all files and directories in the directory
        File[] files = file.listFiles();
        //String v = file.getName();
        // check that the file list is not null
        if (files!=null){
            // iterate through the subdirectory files
            for(File sFile : files){
                // if the file is a directory and not hidden
                if(sFile.isDirectory() && !sFile.isHidden()){
                    // recursively add the files to the array list
                    all.addAll(findAllFiles(sFile, filetype));
                }
                // otherwise if the file name ends with the appropriate extension
                else if (sFile.getName().endsWith( FileSystemv2.getExtension(filetype) )){
                    // add the file to the array list
                    all.add(sFile);
                }
            }
        }
        // return the array list
        return all;
    }


    // helper method to provide the correct extension
    // primitive implementation of a bigger concept
    // ISSUE: if the filetype is "" because we search the parent
    // directory, only things with "none" as an extension will show
    private static String getExtension(String filetype){
        // infer the filetype from the type aof file sought
        if (filetype.equals("VID")){
            return VID_EXT;
        }
        else if (filetype.equals("REC")){
            return REC_EXT;
        }
        else{
            return "none";
        }
    }
}
