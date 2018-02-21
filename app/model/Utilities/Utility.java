package model.Utilities;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*****
 Features:

 1) Use allRepoResults() to find in all the repos in MAIN_DIRECTORY_PATH.
 2) User cntAllFilesInRepo(dir) to find Utility results in this particular Repo only.





 *****/
public class Utility implements Serializable {

    private static final long serialVersionUID = 1L;
    int countFiles = 0;
    int percentComplete = 0;
    ArrayList<String> PLS_DIRECTORIES = null, FMB_DIRECTORIES = null, OTHER_DIRECTORIES = null;
    ArrayList<String> allFiles = new ArrayList<>();
    static String MAIN_DIRECTORY_PATH = "C:/SVN";
    public static String repoToSearch = MAIN_DIRECTORY_PATH;
    public static TreeMap<String, String> fileNameToPkgName = new TreeMap();
    TreeMap<String, String> PkgNameToFileName = new TreeMap();
    ArrayList<String> funcNames;
    HashMap<String, ArrayList<String>> fileFuncNames = new HashMap<>();
    public static int finishedPkgName_flag = 0;

    public void allRepoResults() {
        // TODO Auto-generated method stub


        File f = new File(MAIN_DIRECTORY_PATH);
        if (f.isDirectory()) {
            int numOfRepos = 0;
            int numChecked = 0;
            File list[] = f.listFiles();

            for (File thisFile : list) {
                if (thisFile.isDirectory()) {
                    numOfRepos++;
                }
            }


            for (File thisFile : list) {


                if (thisFile.isDirectory()) {

                    setDirectories(MAIN_DIRECTORY_PATH + "/" + thisFile.getName());
                    countFiles = 0;

                    if (repoToSearch.equals(MAIN_DIRECTORY_PATH + "/" + thisFile.getName()))
                        thisRecursiveRepo(MAIN_DIRECTORY_PATH + "/" + thisFile.getName());


                    System.out.println("\t[INFO]\t<UtilityThread_thread>\t\t" + MAIN_DIRECTORY_PATH + "/" + thisFile.getName() + "\tCount: " + countFiles);
                    numChecked++;
                    percentComplete = (numChecked * 100) / numOfRepos;
                }
            }
        }
    }

    public void startPkgNmeFileSearch(String repoName) {
        String path = MAIN_DIRECTORY_PATH + "/" + repoName;
        setDirectories(path);
        thisRecursiveRepo(path);
    }

    public void thisRecursiveRepo(String path) {


        File mainDirectory = new File(path);

        if (mainDirectory.isDirectory()) {
            File[] ff = mainDirectory.listFiles();

            for (File file : ff) {
                if (file.isDirectory()) {
                    thisRecursiveRepo(file.getPath());
                    getFilePackageNameThisRepo(file.getAbsolutePath().replace("\\", "/"));
                }
            }
        }
        else {
            System.out.println("[ERROR]\t<Utility.java -> thisRecursiveRepo>\tPath: " + path + " --\tNOT A DIRECTORY!");
        }
    }

    public void getFilePackageNameThisRepo(String directoryPath) //sets fileName to pkgName hashmaps
    {
        //Pass respective directories to classes to manipulate

        File directory = new File(directoryPath);
        int countFiles = 0;

        if (directory.isDirectory()) {
            if (PLS_DIRECTORIES.contains(directoryPath)) {
                File[] ff = directory.listFiles();

                for (File file : ff) {
                    if (file.isFile()) {
                        if (file.getName().toLowerCase().indexOf("b.pls") >= 0) {
                            String path = file.getAbsolutePath();
                            String packageName = getPackageNameThisFile(path);
                            fileNameToPkgName.put(path, packageName);
                            PkgNameToFileName.put(packageName, path);
                            allFiles.add(file.getName());
                            countFiles++;
                        }
                    }
                }

                System.out.println("\t[INFO]\t<UtilityThread_thread>\t\t" + directoryPath + "\tCount: " + countFiles);
            }
        }
        else {
            System.out.println("\n[ERROR]\t<Utility.java -> getFilePackageNameThisRepo>\tPath: " + directoryPath + " --\tNOT A DIRECTORY!");

        }
        finishedPkgName_flag = 1;
    }

    public String getPackageNameThisFile(String path) {
        String regex_package_name1 = "^(?!.*(--)).*(BODY)\\s*([a-zA-Z_0-9]+)\\s*";
        String regex_package_name2 = "(\\b+REPLACE\\b+FUNCTION)\\s*([a-zA-Z_0-9]+)\\s*";
        File f = new File(path);
        Pattern pattern_package_name1 = Pattern.compile(regex_package_name1);
        Pattern pattern_package_name2 = Pattern.compile(regex_package_name2);
        Matcher matcher_package_name1;
        Matcher matcher_package_name2;
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int l = 1, cmnt_lck = 0;


            while ((line = br.readLine()) != null) {
                matcher_package_name1 = pattern_package_name1.matcher(line.toUpperCase());
                matcher_package_name2 = pattern_package_name2.matcher(line.toUpperCase());
                if (matcher_package_name1.find()) {
                    return matcher_package_name1.group(3);
                }
                if (matcher_package_name2.find()) {
                    return matcher_package_name2.group(2);
                }
                l++;
            }
            fr.close();
            br.close();

        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "Null";
    }

    public ArrayList<String> allFuncAndProcsInFile(String PkgName, String type) //type for proc or func
    {
        String PATH = PkgNameToFileName.get(PkgName);

        File f = new File(PATH);

        String regex_FUNC1 = "(^(?!.*(--)).*(\\bFUNCTION\\s+)([a-zA-Z_\\d]+)(\\s*\\(*))";    // see for function vv( type regex --- check
        String regex_FUNC2 = "(^(?!.*(--)).*(\\bPROCEDURE\\s+)([a-zA-Z_\\d]+)(\\s*\\(*))";    // see for function vv( type regex --- check
        String regex_END = "^(?!.*(--|\\s*,\\s*|\\s+IF\\s*;|\\s+CASE\\s*;|\\s+LOOP\\s*;)).*((\\bEND\\s+[a-zA-Z_\\d]+)(\\s*;)|(\\bEND\\s*;))";
        Pattern pattern_FUNC1 = Pattern.compile(regex_FUNC1);
        Pattern pattern_FUNC2 = Pattern.compile(regex_FUNC2);
        Pattern pattern_END = Pattern.compile(regex_END);
        Matcher matcher_FUNC1;
        Matcher matcher_FUNC2;
        Matcher matcher_END;

        ArrayList<String> function_name = new ArrayList<>();

        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int l = 1;
            int count = 0;
            int lock = 0, flag_first_BEGIN = 0, flag_END = 0;
            String functionName = "", prev_proc_name = "";

            while ((line = br.readLine()) != null) {
                matcher_FUNC1 = pattern_FUNC1.matcher(line.toUpperCase());
                matcher_FUNC2 = pattern_FUNC2.matcher(line.toUpperCase());
                matcher_END = pattern_END.matcher(line.toUpperCase());

                if (flag_first_BEGIN == 0 && line.trim().toLowerCase().equals("begin")) {
                    flag_first_BEGIN = 1;
                    ;
                }

                //****************Finds func|proc head********************


                matcher_FUNC1 = pattern_FUNC1.matcher(line.toUpperCase());
                matcher_FUNC2 = pattern_FUNC2.matcher(line.toUpperCase());
                if (flag_END == 0) {
                    if (type.equals("FUNCTION")) {
                        if (matcher_FUNC1.find()) {
                            functionName = matcher_FUNC1.group(4).toLowerCase();
                            prev_proc_name = functionName;
                            if (!function_name.isEmpty()) {

                                flag_END = 1;
                            }


                        }
                    }
                    else if (type.equals("PROCEDURE")) {
                        if (matcher_FUNC2.find()) {
                            functionName = matcher_FUNC2.group(4).toLowerCase();
                            prev_proc_name = functionName;

                            if (!function_name.isEmpty()) {

                                flag_END = 1;
                            }
                        }
                    }


                }

                if (flag_first_BEGIN == 1 && function_name.isEmpty()) {
                    flag_END = 1;
                }


                //******************Finds end of func or proc**********************


                if (flag_END == 1 && flag_first_BEGIN == 1) {


                    matcher_END = pattern_END.matcher(line.toUpperCase());

                    if (matcher_END.find()) {
                        flag_END = 0;
                        if (function_name.isEmpty()) {
                            function_name.add(prev_proc_name);
                        }
                        else {
                            function_name.add(functionName);
                        }

                    }
                }
                l++;

            }
            fr.close();
            br.close();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Collections.sort(function_name);
        return function_name;


    }

    public void setDirectories(String THIS_DIRECTORY_PATH) {
        PLS_DIRECTORIES = new ArrayList<>();
        OTHER_DIRECTORIES = new ArrayList<>();
        FMB_DIRECTORIES = new ArrayList<>();

        /*********   for versions before 14th   *******/


        PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Packages/Source");


        /*********** After 14th version *******************/

        PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Cross_Pillar/packages/source");
        PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Financials/packages/source");
        PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Foundation/packages/source");
        PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Import_Export/packages/source");
        PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Inventory_Movement/packages/source");
        PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Inventory_Tracking/packages/source");
        PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Localization/packages/source");
        PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Procurement/packages/source");

    }

    public static void serialize(Utility obj) {

        File f = new File("C:\\Users\\vibhor_personal\\Desktop\\ProcessFlow_ORACLE\\java_play\\source\\HelloWorld2\\coreFiles\\filePkgName.ser");

        try {

            if (!f.exists())
                f.createNewFile();
            FileOutputStream fout = new FileOutputStream(f);
            ObjectOutputStream out = new ObjectOutputStream(fout);

            out.writeObject(obj);
            out.flush();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            System.out.println("[\tINFO\t]\t<Utility.java>\t<serialize>\t{" + f.getAbsolutePath() + "}..SUCCESS   ");
        }
        catch (IOException e) {
            System.out.println("[\tEXCEPTION\t]\t<IOException>\t<Utility.java>\t<Serialize>\t{" + f.getAbsolutePath() + "}");
            e.printStackTrace();
        }
    }
}
