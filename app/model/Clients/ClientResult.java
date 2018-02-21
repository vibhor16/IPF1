package model.Clients;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by shuklv1 on 04-Oct-17.
 */
public class ClientResult {
    public LinkedHashMap<String,String> FILE_DIRECTORY_PATH;
    public String selectedRepository;
    public int GLOBAL_FILE_COUNT;
    public ArrayList<String> selectRepoList;
    public LinkedHashMap<Integer,ArrayList<String>> FINAL_ALL_PATHS;
    public ArrayList<String> colorSelectedFunctions;
    public int displayTillLevel;
    public ArrayList<String> CURRENT_PATH;
    public ArrayList<String> ALL_SEARCH_STRINGS;
    public ArrayList<LinkedHashMap<String,ArrayList<String>>> ALL_REFINED_HM;
    public ArrayList<LinkedHashMap<String,String>> ALL_TYPE_COUNTS;
    public boolean lastSearchStringAnyResult;

    ClientResult(){
        FILE_DIRECTORY_PATH=new LinkedHashMap<>();
        selectedRepository="";
        selectRepoList=new ArrayList<>();
        FINAL_ALL_PATHS=new LinkedHashMap<>();
        colorSelectedFunctions=new ArrayList<>();
        displayTillLevel=0;
        CURRENT_PATH=new ArrayList<>();
        ALL_SEARCH_STRINGS=new ArrayList<>();
        ALL_REFINED_HM=new ArrayList<>();
        ALL_TYPE_COUNTS=new ArrayList<>();
        lastSearchStringAnyResult=false;
    }
}
