package model.Logic;

import model.Clients.Client;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;


public class FilesDispatcher implements Serializable {

    LinkedHashMap<String, ArrayList<String>> FINAL_RETURN_RESULT;
    LinkedHashMap<String, String> file_package_name = new LinkedHashMap<>();

    static String NOT_DIR = "Not a valid directory";
    static String PATH_ERROR = "Check the path for slashes!";
    static String TO_SEARCH = "";
    static String MAIN_DIRECTORY_PATH;
    String requestIP = null;
    int countFiles = 0;
    Client clientObject;

    public static ArrayList<String> names = null;

    public FilesDispatcher(Client clientObject) {
        this.clientObject = clientObject;
    }

    public void setMainDirectory(String mainDirectoryPath) {
        MAIN_DIRECTORY_PATH = mainDirectoryPath;
    }

    public String getMainDirectory() {
        return MAIN_DIRECTORY_PATH;
    }


    public void setFinalResult(LinkedHashMap<String, ArrayList<String>> res) {
        FINAL_RETURN_RESULT = res;
    }

    public LinkedHashMap<String, ArrayList<String>> getFinalResult() {
        return FINAL_RETURN_RESULT;
    }

    public LinkedHashMap<String, String> getFilePackageNames() {
        return file_package_name;
    }

    public void setSearchString(String search) {
        TO_SEARCH = search.trim();
    }

    public String getSearchString() {
        return TO_SEARCH;
    }

    public void initFinalResultsHashMap() {
        FINAL_RETURN_RESULT = new LinkedHashMap<>();
    }


    public void init(String mainDirectoryPath) {
        countFiles = 0;
        dirCount = 0;
        setMainDirectory(mainDirectoryPath);
    }


    // Main repository path
    int dirCount = 0;

    public void Dispatch(String path) {
        File mainDirectory = new File(path);
        if (mainDirectory.isDirectory()) {
            File[] ff = mainDirectory.listFiles();
            for (File file : ff) {
                if (file.isDirectory()) {
                    Dispatch(file.getPath());
                    if (clientObject.stop_searching_ind == 1) {
                        return;
                    }
                    dispatch_returnResults(file.getAbsolutePath().replace("\\", "/"));
                }
            }
        }
        else {
            System.out.println(NOT_DIR);
        }
    }

    public void dispatch_returnResults(String directoryPath) {


        File directory = new File(directoryPath);

        //Pass respective directories to classes to manipulate
        if (directory.isDirectory()) {

            if (clientObject.PLS_DIRECTORIES.contains(directoryPath)) {
                System.out.println("\t[INFO]\t<" + ++dirCount + ") SEARCHING -> " + directoryPath + ">");
                clientObject.getPlsHandlerObject().init();
                file_package_name = PlsHandler.global_file_package_name;
                clientObject.getPlsHandlerObject().returnResults(directoryPath, TO_SEARCH).forEach(FINAL_RETURN_RESULT::putIfAbsent);
            }
            else if (clientObject.OTHER_DIRECTORIES.contains(directoryPath) || clientObject.FMB_DIRECTORIES.contains(directoryPath)) {
                System.out.println("\t[INFO]\t<" + ++dirCount + ") SEARCHING -> " + directoryPath + ">");
                clientObject.getOtherFormatsHanlerObject().init();
                LinkedHashMap<String, ArrayList<String>> temp = clientObject.getOtherFormatsHanlerObject().returnResults(directoryPath, TO_SEARCH);
                temp.forEach(FINAL_RETURN_RESULT::putIfAbsent);
            }
        }
        setFinalResult(FINAL_RETURN_RESULT);
    }
}
