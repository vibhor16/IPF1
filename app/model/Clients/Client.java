package model.Clients;

import model.Logic.*;
import model.Multithreading.Threading;
import model.Time.TimeDurationOfSearch;
import model.Time.TimeStampExample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by shuklv1 on 29-Sep-17.
 */
public class Client {
    Threading threadingObject;
    DriverClass driverClassObject;
    FilesDispatcher fileDispatcherObject;
    PlsHandler plsHandlerObject;
    OtherFormatsHandler otherFormatsHanlerObject;
    String CLIENT_IP;                            //same as Client thread name
    GetFileCountInAllRepos getFileCountInAllReposObject;
    TimeDurationOfSearch timeDurationOfSearchObject;
    TimeStampExample timeStampExampleObject;
    FiltersClass filtersClassObject;

    int percentComplete;           // For file count repo thread during initialisation
    int clientInstancesRunning=0;  // Number of clients running for a given IP
    int graphWindowsOpened=0;      // Number of graph windows opened per client
    Client(String CLIENT_IP) {
        this.CLIENT_IP = CLIENT_IP;
    }

    public void init() {
        threadingObject = new Threading(this);
        getFileCountInAllReposObject = new GetFileCountInAllRepos(this);
        timeDurationOfSearchObject = new TimeDurationOfSearch();
        timeStampExampleObject = new TimeStampExample();
        filtersClassObject = new FiltersClass(this);
    }

    public void init2(){
        System.out.println("inside int2()");
        driverClassObject = new DriverClass(this);
        fileDispatcherObject = new FilesDispatcher(this);
        plsHandlerObject = new PlsHandler(this);
        otherFormatsHanlerObject = new OtherFormatsHandler(this);


        /**** for main search  ****/
        selectedRepository = "";
        popupText = "";
        SEARCH_STRING = null;
        typeCount = null;
        numOfSearches = 0;
        refinedHM = null;
        CURRENT_PATH = new ArrayList<>();
        selectRepoList = new ArrayList<>();
        ALL_SEARCH_STRINGS = new ArrayList<>();
        ALL_REFINED_HM = new ArrayList<>();
        ALL_TYPE_COUNTS = new ArrayList<>();
        FINAL_ALL_PATHS = new LinkedHashMap<>();
        FILE_DIRECTORY_PATH = new LinkedHashMap<>();
        colorSelectedFunctions = new ArrayList<>();
        finAllPathsCount = 1;
        displayTillLevel = 1;
        populateRepoList();
        this.GLOBAL_FILE_COUNT = 0;
        elapsedTime="";

    }
    public void setNumberGraphWindows(int change){
        graphWindowsOpened+=change;         // Change can be negative
     }
    public int getNumberGraphWindows(){
        return graphWindowsOpened;
    } 
    public int getPercentComplete() {
        return this.percentComplete;
    }

    public void setPercentComplete(int percentComplete) {
        this.percentComplete = percentComplete;
    }

    public GetFileCountInAllRepos getGetFileCountInAllReposObject() {
        return this.getFileCountInAllReposObject;
    }

    public OtherFormatsHandler getOtherFormatsHanlerObject() {
        return this.otherFormatsHanlerObject;
    }

    public PlsHandler getPlsHandlerObject() {
        return this.plsHandlerObject;
    }

    public void countFileNumInAllRepos() {
        threadingObject.newFileCountRepoThread();
    }

    public TimeDurationOfSearch getTimeDurationOfSearchObject() {
        return this.timeDurationOfSearchObject;
    }

    public DriverClass getDriverClassObject() {
        return this.driverClassObject;
    }

    public FiltersClass getFiltersClassObject() { return this.filtersClassObject; }
    public int getClientInstancesRunning(){ return this.clientInstancesRunning;}

    public void setClientInstancesRunning(int value){ this.clientInstancesRunning+=value;}


    /******************   Search Logic Begins  ********************/

    public String SVNMAINDIR = "C:/SVN";
    public String SEARCH_STRING;
    public String popupText;
    public String selectedRepository;
    public String file_path;
    public int finAllPathsCount;
    public int displayTillLevel;
    public int numOfSearches;
    public LinkedHashMap<String, String> typeCount;
    public LinkedHashMap<String, ArrayList<String>> refinedHM;
    public LinkedHashMap<Integer, ArrayList<String>> FINAL_ALL_PATHS;
    public LinkedHashMap<String, String> FILE_DIRECTORY_PATH;
    public LinkedHashMap<String, ArrayList<String>> mapForGraph = new LinkedHashMap<>();
    public HashMap<String, ArrayList<String>> filters_hm = new HashMap<String, ArrayList<String>>();
    public ArrayList<String> colorSelectedFunctions;
    public ArrayList<String> CURRENT_PATH;
    public ArrayList<String> ALL_SEARCH_STRINGS;
    public ArrayList<String> selectRepoList;
    public ArrayList<String> contents = new ArrayList<>();
    public ArrayList<LinkedHashMap<String, ArrayList<String>>> ALL_REFINED_HM;
    public ArrayList<LinkedHashMap<String, String>> ALL_TYPE_COUNTS;
    public HashMap<String,Integer> allRepoFileCount=new HashMap<>();
    public int GLOBAL_FILE_COUNT=0;
    public int FileCountRepoThread_lck=0;
    public int UtilityThread_lck=0;

    //For Filters Class
    public  String MAIN_DIRECTORY_PATH;
    public  ArrayList<String> PLS_DIRECTORIES;
    public  ArrayList<String> FMB_DIRECTORIES;
    public  ArrayList<String> OTHER_DIRECTORIES;
    public  String filters_searchType;	//Values: PACKAGES;FORMS;BATCHES;TRIGGERS;
    public  String searchType="DEFAULT"; //Values: DEFAULT/USER-SET --> Useful in getting dir file count to find progress.
    public  HashMap<String,Integer> filtersDirFileCount=new HashMap<>();
    public  int countDirFileCount=0;



    /**
     * ****************Progress Bar elements
     **************************/

    public long startTime;
    public long endTime;
    public String elapsedTime;
    public int searching_goingOn_ind = 0;// if searching is happening
    public int stop_searching_ind = 0;  //when stop is pressed in the UI
    public String typeOfThisSearch = null; // which type of search is this - "level_1" or "level>1"


    /******************************************************************/


    public void populateRepoList() {

        try {
            File f = new File(SVNMAINDIR);
            if (f.isDirectory()) {
                File[] repoDirs = f.listFiles();

                for (File repoDir : repoDirs) {
                    if (repoDir.isDirectory()) {
                        selectRepoList.add(repoDir.getAbsolutePath());
                         System.out.println("File:  "+repoDir.getPath());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void setSearchString(String search_string) {
        // System.out.println("New string: "+search_string);
        SEARCH_STRING = search_string;
    }
    public void setClientResultObject(ClientResult clientResultObject){
        clientResultObject.FILE_DIRECTORY_PATH=this.FILE_DIRECTORY_PATH;
        clientResultObject.selectedRepository=this.selectedRepository;
        clientResultObject.GLOBAL_FILE_COUNT=this.GLOBAL_FILE_COUNT;
        clientResultObject.selectRepoList=this.selectRepoList;
        clientResultObject.FINAL_ALL_PATHS=this.FINAL_ALL_PATHS;
        clientResultObject.colorSelectedFunctions=this.colorSelectedFunctions;
        clientResultObject.displayTillLevel=this.displayTillLevel;
        clientResultObject.CURRENT_PATH=this.CURRENT_PATH;
        clientResultObject.ALL_SEARCH_STRINGS=this.ALL_SEARCH_STRINGS;
        clientResultObject.ALL_REFINED_HM=this.ALL_REFINED_HM;
        clientResultObject.ALL_TYPE_COUNTS=this.ALL_TYPE_COUNTS;
    }


    String id = null;

    public void mainSearch(String visitNum, String SEARCH_STRING, String selectedRepository,ClientResult clientResultObject) {


        init2();
        // setting this repo path
        selectedRepository = SVNMAINDIR + "/se-" + selectedRepository;
        driverClassObject.setThisRepoPath(selectedRepository.replace('\\', '/'));
        // id cant contain \ / : * ? "" < > | & ' ;
        id = timeStampExampleObject.timeStamp() + "__" + getProperFileName(SEARCH_STRING) + "__" + selectedRepository;
        popupText = "";

        if (visitNum.equals("1")) {
            setClientResultObject(clientResultObject);
            return;
        }

        typeOfThisSearch = "level_1";
        setSelRepoAppendedPath(selectedRepository);

        if (SEARCH_STRING == null || SEARCH_STRING.trim().length() == 0)
            popupText += "Enter a search string!\n ";
        else {
            if (!isAlreadySearched(SEARCH_STRING) && !SEARCH_STRING.trim().equals("") && !selectedRepository.trim().equals("")) {
                addToCurrentPath(SEARCH_STRING);
                execute(SEARCH_STRING);
            }
        }
        setClientResultObject(clientResultObject);
//        return 0;
    }


    public String currentSearchString="";
    //receives level of UI and string eg - ordrcvb.pls-po_line_item
    public void newSearch(String level, String pls_funcName)  {

        this.GLOBAL_FILE_COUNT = 0;
        typeOfThisSearch = "level>1";
        popupText = null;
        int intLevel = Integer.parseInt(level);

        //contains current selected fn calls that need to be colored


        currentSearchString = driverClassObject.getNewSearchString(pls_funcName);

        if (!isAlreadySearched(currentSearchString)) {
            displayTillLevel = intLevel;
            addToCurrentPath(currentSearchString);
            execute(currentSearchString);
            String newColorString = (intLevel - 1) + "-" + pls_funcName;
            if (!colorSelectedFunctions.isEmpty()) {

                //contains selected fn in the form of: 1-ordcv.pls-po_line_item_online
                if (!colorSelectedFunctions.contains(newColorString)) {
                    if ((intLevel - 2) < colorSelectedFunctions.size()) {
                        colorSelectedFunctions.set(intLevel - 2, newColorString);
                    } else
                        colorSelectedFunctions.add(newColorString);
                }
            } else {
                colorSelectedFunctions.add(newColorString);
            }
        }
        if (intLevel < CURRENT_PATH.size()) {
                setToCurrentPath(intLevel, pls_funcName);
        }
        if(intLevel == CURRENT_PATH.size()){
            if(!isStringAlreadySearched(currentSearchString)){
                setToCurrentPath(intLevel, pls_funcName);
            }
        }
    }

    public boolean noEntryAtThisLevel(int level){
        for(String temp:colorSelectedFunctions){
            if(temp.contains(level+""))
                return false;
        }
        return true;
    }
    public void pathTerminators(String level, String file_name) {
        int flagForPls = 0;
        if (file_name.contains(".pls-"))
            flagForPls = 1;


        int intLevel = Integer.parseInt(level);

        //contains current selected fn calls that need to be colored
        String newColorString = (intLevel - 1) + "-" + file_name;
        if (!colorSelectedFunctions.isEmpty()) {

            //contains selected fn in the form of: 1-ordcv.pls-po_line_item_online
            if (!colorSelectedFunctions.contains(newColorString.toLowerCase())) {
                if ((intLevel - 2) < colorSelectedFunctions.size()) {
                    colorSelectedFunctions.set(intLevel - 2, newColorString);
                } else{
                    if(noEntryAtThisLevel(intLevel-1))
                        colorSelectedFunctions.add(newColorString);
                    else
                        colorSelectedFunctions.set(intLevel-1,newColorString);
                }
            }
        } else {
            colorSelectedFunctions.add(newColorString);
        }

        displayTillLevel = intLevel;
        if (intLevel <= CURRENT_PATH.size())
            setToCurrentPath(intLevel, file_name);
        else
            addToCurrentPath(file_name);
        ArrayList<String> temp = new ArrayList<>();
        temp.addAll(CURRENT_PATH);

        FILE_DIRECTORY_PATH = driverClassObject.FILE_DIRECTORY_PATH;

        System.out.println("FDP : " + FILE_DIRECTORY_PATH);
        if (isUniquePath()) {
            FINAL_ALL_PATHS.put(finAllPathsCount++, temp);
        }
        System.out.println("ALL paths:  " + FINAL_ALL_PATHS);
        if (flagForPls == 0)
            showFileContents(file_name);
    }

    public void execute(String searchString) {
        System.out.println("execute: " + searchString);
        startTime = System.currentTimeMillis();
        threadingObject.newTimeElapsedThread();
        driverClassObject.init();
        driverClassObject.setSearchString(searchString);

        searching_goingOn_ind = 1;
        driverClassObject.execute();
        searching_goingOn_ind = 0;
        typeOfThisSearch = null;


        numOfSearches++;

        LinkedHashMap<String, ArrayList<String>> hm = driverClassObject.getResults();


        typeCount = driverClassObject.getTypeCount(hm);
        refinedHM = driverClassObject.getRefined(hm);
        ALL_REFINED_HM.add(refinedHM);
        ALL_TYPE_COUNTS.add(typeCount);
        ALL_SEARCH_STRINGS.add(searchString);

        displayTillLevel = CURRENT_PATH.size();
        FILE_DIRECTORY_PATH = driverClassObject.FILE_DIRECTORY_PATH;

    }

    public void addToCurrentPath(String functionCall) {
        CURRENT_PATH.add(functionCall);
    }

    public void setToCurrentPath(int level, String functionCall) {

        int index = level - 1;
        String newFunctionCall="";
        String newColorString=index + "-" + functionCall;;
        ArrayList<String> temp = new ArrayList<>();

        if(functionCall.contains(".pls"))
            newFunctionCall = driverClassObject.getNewSearchString(functionCall);

        for (int i = 0; i < index; i++) {
            temp.add(CURRENT_PATH.get(i));
        }
        temp.add(newFunctionCall.toUpperCase());
        CURRENT_PATH = temp;

        temp = new ArrayList<>();

        index = index-1;
        for (int i = 0; i < index; i++) {
            temp.add(colorSelectedFunctions.get(i));
        }
        if(!temp.contains(newColorString.toLowerCase()))
             temp.add(newColorString.toLowerCase());
        colorSelectedFunctions = temp;
    }

    public void removeFromCurrentPath(String functionCall) {
        CURRENT_PATH.remove(functionCall);
    }

    public boolean isAlreadySearched(String newString) {
        if (CURRENT_PATH.contains(newString)) {
            popupText = "AlreadySearched";
            return true;
        }else
            popupText="";

        return false;
    }

    //receives level of UI and string eg - ordcvp.pls-po_line_item or a normal search string from left
    public boolean isStringAlreadySearched(String newString) {
        if (newString.contains("-"))
            newString = driverClassObject.getNewSearchString(newString);

        if (CURRENT_PATH.contains(newString))
            return true;
        return false;
    }


    public boolean isUniquePath() {
        Iterator it;
        it = FINAL_ALL_PATHS.entrySet().iterator();

        // for pls files
        while (it.hasNext()) {
            int flag = 0;
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList<String> values = (ArrayList<String>) pair.getValue();
            for (int i = 0; i < CURRENT_PATH.size(); i++) {
                if (values.size() == CURRENT_PATH.size()) {
                    if (!CURRENT_PATH.get(i).equals(values.get(i))) {
                        flag = 1;
                    }
                } else {
                    flag = 1;
                }
            }
            if (flag == 0)
                return false;
        }

        return true;
    }


    public LinkedHashMap<String, ArrayList<String>> getMap() {
        LinkedHashMap<String, ArrayList<String>> map = new LinkedHashMap<>();

        ArrayList<String> childrenOfSearchString = new ArrayList<>();
        ArrayList<String> childrenOfOthers = new ArrayList<>();

        if (FINAL_ALL_PATHS.size() > 0) {
            for (int i = 1; i <= FINAL_ALL_PATHS.size(); i++) {
                ArrayList<String> temp = FINAL_ALL_PATHS.get(i);
                for (int j = 0; j < temp.size() - 1; j++) {
                    if (j == 0) {
                        if (!childrenOfSearchString.contains(temp.get(j)))
                            childrenOfSearchString.add(temp.get(j));
                    }
                    System.out.println(temp.get(j) + "  j= " + j);
                    childrenOfOthers = map.get(temp.get(j));
                    if (childrenOfOthers == null) {
                        childrenOfOthers = new ArrayList<>();
                    }
                    if (!childrenOfOthers.contains(temp.get(j + 1)))
                        childrenOfOthers.add(temp.get(j + 1));
                    map.put(temp.get(j).trim(), childrenOfOthers);

                    if (j == temp.size() - 2) {
                        map.put(temp.get(j + 1).trim(), null);
                    }

                }
            }

        }
        System.out.println("MAP:  " + map.get(SEARCH_STRING));
        return map;

    }


    /*********************** Filters functions ********************/

    //Operations: DEFAULT and USER-SET
    public void Filters_options(String operation, String userFilters) {

        this.searchType = operation.trim();

        if (operation.equals("USER-SET")) {

            System.out.println("USER-SET (Filters) -> " + userFilters);
            filtersClassObject.setFilterTypeSearch(filtersClassObject.getSearchType(userFilters));      //Return value like: PACKAGES;FORMS etc.
            System.out.println("type: " + this.filters_searchType);


        }

        ArrayList<String> type = new ArrayList<String>();
        type.add(this.filters_searchType);
        filters_hm = new HashMap<String, ArrayList<String>>();

        //When operation is USER-SET


    }


    public void setSelRepoAppendedPath(String repo) {


        ArrayList<String> NEW_AR = new ArrayList<>();
        for (String s : this.PLS_DIRECTORIES) {
            if (!s.contains("/SVN/"))
                NEW_AR.add(repo + s);
            else {

                // Removes D:/SVN/se-rms part to add new repo path
                s = s.substring(s.indexOf("/") + 1, s.length());
                s = s.substring(s.indexOf("/") + 1, s.length());
                s = s.substring(s.indexOf("/"), s.length());


                NEW_AR.add(repo + s);
            }

        }
        this.PLS_DIRECTORIES = NEW_AR;

        NEW_AR = new ArrayList<>();
        for (String s : this.FMB_DIRECTORIES) {
            if (!s.contains("/SVN/"))
                NEW_AR.add(repo + s);
            else {

                // Removes D:/SVN/se-rms part to add new repo path
                s = s.substring(s.indexOf("/") + 1, s.length());
                s = s.substring(s.indexOf("/") + 1, s.length());
                s = s.substring(s.indexOf("/"), s.length());


                NEW_AR.add(repo + s);
            }
        }
        this.FMB_DIRECTORIES = NEW_AR;

        NEW_AR = new ArrayList<>();
        for (String s : this.OTHER_DIRECTORIES) {
            if (!s.contains("/SVN/"))
                NEW_AR.add(repo + s);
            else {

                // Removes D:/SVN/se-rms part to add new repo path
                s = s.substring(s.indexOf("/") + 1, s.length());
                s = s.substring(s.indexOf("/") + 1, s.length());
                s = s.substring(s.indexOf("/"), s.length());


                NEW_AR.add(repo + s);
            }
        }
        this.OTHER_DIRECTORIES = NEW_AR;


    }

    /********************* View File Contents *********************/

    ArrayList<String> viewFileContents;
    String viewFilePath;
    public ArrayList<String> getViewFileContents(){
        return viewFileContents;
    }
    public String getViewFilePath(){
        return viewFilePath;
    }
    public void showFileContents(String fileToShow)
	{
     
		System.out.println("fileToShow:  "+fileToShow);
		String temp="";
        viewFileContents=new ArrayList<String>();
		viewFilePath=FILE_DIRECTORY_PATH.get(fileToShow);
		File f=new File(viewFilePath);
		FileReader fr;
		try {
			fr = new FileReader(f);
			BufferedReader br =new BufferedReader(fr);
			temp=br.readLine();
			while(temp!=null)
			{
				viewFileContents.add(temp);
				temp=br.readLine();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


    /*********************** Utility functions ********************/


    // give repoName like se-rms-13_2_10 type


    // file name id cant contain \ / : * ? "" < > |
    public String getProperFileName(String id) {
        //id cant contain \ / : * ? "" < > | & ' ;
        if (id.contains("&")) {
            id = id.replace("&", "");
        }
        if (id.contains("'")) {
            id = id.replace("'", "");
        }
        if (id.contains(";")) {
            id = id.replace(";", "");
        }
        if (id.contains("\\")) {
            id = id.replace("\\", "");
        }
        if (id.contains("/")) {
            id = id.replace("/", "");
        }
        if (id.contains(":")) {
            id = id.replace(":", "");
        }
        if (id.contains("*")) {
            id = id.replace("*", "");
        }
        if (id.contains("?")) {
            id = id.replace("?", "");
        }
        if (id.contains("\"")) {
            id = id.replace("\"", "");
        }
        if (id.contains("<")) {
            id = id.replace("<", "");
        }
        if (id.contains(">")) {
            id = id.replace(">", "");
        }
        if (id.contains("|")) {
            id = id.replace("|", "");
        }
        return id;
    }


}
