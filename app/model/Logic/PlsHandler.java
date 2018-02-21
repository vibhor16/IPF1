package model.Logic;

import model.Clients.Client;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PlsHandler {

    ArrayList<String> matchedFiles = new ArrayList<>();
    static String DIR_ERROR = "NOT A VALID DIRECTORY";
    static File[] FILES_LIST;


    ArrayList<String> function_name;
    ArrayList<String> func_lineNum_scope;
    ArrayList<Integer> lines_string_found;
    ArrayList<Integer> file_index_func_name;
    ArrayList<Integer> file_index_matched_lines;
    ArrayList<String> FINAL_FUNCTION_NAMES;

    LinkedHashMap<String, String> file_package_name = new LinkedHashMap<>();
    public static LinkedHashMap<String, String> global_file_package_name = new LinkedHashMap<>();
    LinkedHashMap<String, ArrayList<String>> FINAL_RETURN_RESULT = new LinkedHashMap<>();
    OtherFormatsHandler other_format_handler_obj;
    Client clientObject;

    public PlsHandler(Client clientObject) {
        this.clientObject = clientObject;
    }

    public void init() {
        function_name = new ArrayList<>();
        func_lineNum_scope = new ArrayList<>();
        lines_string_found = new ArrayList<>();
        file_index_func_name = new ArrayList<>();
        file_index_matched_lines = new ArrayList<>();
        FINAL_FUNCTION_NAMES = new ArrayList<>();
        other_format_handler_obj = clientObject.getOtherFormatsHanlerObject();
    }

    public LinkedHashMap<String, ArrayList<String>> returnResults(String path, String TO_SEARCH) {

        FINAL_RETURN_RESULT = new LinkedHashMap<>();
        TO_SEARCH = TO_SEARCH.trim();
        BufferedReader br = null;
        int lineNumber = 1, flag_END = 0, flag_first_BEGIN = 0, flag_string_found = 0, flag_pkgName_found = 0, global_flag_string_found = 0;

        //Holds fn name till first begin is found
        String prev_proc_name = "";


        String regex_FUNC1 = "(^(?!.*(--)).*(\\bFUNCTION\\s+)([a-zA-Z_\\d]+)(\\s*\\(*))";    // see for function vv( type regex --- check
        String regex_FUNC2 = "(^(?!.*(--)).*(\\bPROCEDURE\\s+)([a-zA-Z_\\d]+)(\\s*\\(*))";    // see for function vv( type regex --- check

        String regex_END = "^(?!.*(--|\\s*,\\s*|\\s+IF\\s*;|\\s+CASE\\s*;|\\s+LOOP\\s*;)).*((\\bEND\\s+[a-zA-Z_\\d]+)(\\s*;)|(\\bEND\\s*;))";

        String regex_package_name1 = "(\\s*PACKAGE\\s*BODY)\\s*([a-zA-Z_0-9]+)\\s*"; // not used
        String regex_package_name2 = "(\\s*REPLACE\\s*FUNCTION)\\s*([a-zA-Z_0-9]+)\\s*";  //not used


        Pattern pattern_FUNC1 = Pattern.compile(regex_FUNC1);
        Pattern pattern_FUNC2 = Pattern.compile(regex_FUNC2);
        Pattern pattern_END = Pattern.compile(regex_END);
        Pattern pattern_package_name1 = Pattern.compile(regex_package_name1);
        Pattern pattern_package_name2 = Pattern.compile(regex_package_name2);


        Matcher matcher_FUNC1;
        Matcher matcher_FUNC2;
        Matcher matcher_END;

        File f = new File(path);
        if (f.isDirectory()) {
            File[] list = f.listFiles();
            FILES_LIST = list;

            int list_index = -1;
            for (File ff : list) {

                if (clientObject.stop_searching_ind == 1) {
                    System.out.println("Breaking in PlsHandler");
                    break;
                }


                // if the file is a source file do not search in it
                if (ff.getName().toLowerCase().indexOf("b.pls") < 0)
                    continue;

                clientObject.GLOBAL_FILE_COUNT++;

                //If filter option set to PACKAGES
                if (!clientObject.filters_searchType.contains("PACKAGES"))
                    continue;


                int start_line = 0, end_line = 0;
                String functionName = "";
                flag_END = 0;
                flag_first_BEGIN = 0;
                flag_string_found = 0;
                flag_pkgName_found = 0;
                list_index++;
                try {
                    br = new BufferedReader(new FileReader(ff));
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                String line = null;
                try {
                    line = br.readLine().toLowerCase();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    lineNumber = 1;
                    while (!line.trim().toLowerCase().equals("/")) {

                        // check if first begin came
                        if (flag_first_BEGIN == 0 && line.trim().toLowerCase().equals("begin")) {

                            flag_first_BEGIN = 1;
                        }


                        //************* Associate package name with file name ****************


                        try {
                            while (flag_pkgName_found == 0) {
                                String line1 = line.toUpperCase();
                                String pkgName, parts[];

                                if (line.toUpperCase().indexOf("PACKAGE BODY") >= 0) {

                                    parts = line1.substring(line1.indexOf("BODY") + 4, line1.length()).split(" ");
                                    pkgName = parts[1];
                                    file_package_name.put(ff.getPath(), pkgName.trim());
                                    flag_pkgName_found = 1;
                                    break;
                                }
                                else if (line.toUpperCase().indexOf("REPLACE\\s*\n*FUNCTION") >= 0) {
                                    parts = line1.substring(line1.indexOf("FUNCTION") + 8, line1.length()).split(" ");
                                    pkgName = parts[1];
                                    file_package_name.put(ff.getPath(), pkgName.trim());
                                    flag_pkgName_found = 1;
                                    break;
                                }
                                line = br.readLine().toLowerCase();
                            }
                            file_package_name.forEach(global_file_package_name::putIfAbsent);
                        }
                        catch (NullPointerException e) {
                            System.out.println("NullPointerException in 'PlsHandler' for File: " + ff.getPath() + " line: " + lineNumber);

                        }
                        catch (StringIndexOutOfBoundsException e) {
                            System.out.println("StringIndexOutOfBoundsException File: " + ff.getPath() + " line: " + lineNumber);
                        }

                        //if no package name is found - files with no *b.pls type
                        if (flag_pkgName_found == 0)
                            continue;

                        //****************Finds func|proc head********************

                        matcher_FUNC1 = pattern_FUNC1.matcher(line.toUpperCase());
                        matcher_FUNC2 = pattern_FUNC2.matcher(line.toUpperCase());
                        if (flag_END == 0) {
                            if (matcher_FUNC1.find()) {
                                functionName = matcher_FUNC1.group(4).toLowerCase();
                                start_line = lineNumber;
                                prev_proc_name = functionName;
                                if (!function_name.isEmpty()) {
                                    flag_END = 1;
                                }
                            }
                            else if (matcher_FUNC2.find()) {
                                functionName = matcher_FUNC2.group(4).toLowerCase();
                                start_line = lineNumber;
                                prev_proc_name = functionName;
                                if (!function_name.isEmpty()) {
                                    flag_END = 1;
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
                                end_line = lineNumber;
                                if (function_name.isEmpty())
                                    function_name.add(prev_proc_name);
                                else
                                    function_name.add(functionName);

                                if (ff.getName().contains("ui_boolean_wrapperb"))
                                    System.out.println("FUNC:  " + functionName + " File: " + ff.getPath() + " lineNUM: " + lineNumber + "  line: " + line);
                                func_lineNum_scope.add(start_line + "," + end_line);
                                file_index_func_name.add(list_index);                        //stores file index for this function

                            }
                        }

                        //******************Find the occurrences of the search_string*******************

                        String TO_SEARCH2 = TO_SEARCH.substring(TO_SEARCH.lastIndexOf('.') + 1, TO_SEARCH.length());

                        String regex_match_string;
                        String regex_match_string2;
                        Pattern pattern_match_string;
                        Pattern pattern_match_string2;
                        Matcher matcher_match_string = null;
                        Matcher matcher_match_string2;

                        if (clientObject.typeOfThisSearch.equals("level_1")) {
                            regex_match_string = "\\b+" + TO_SEARCH.toLowerCase() + "\\b+"; // should not be a commented line
                            pattern_match_string = Pattern.compile(regex_match_string);
                            matcher_match_string = pattern_match_string.matcher(line.toLowerCase());
                        }
                        else if (clientObject.typeOfThisSearch.equals("level>1")) {
                            regex_match_string = "\\b" + TO_SEARCH.toLowerCase() + "\\s*\\(\\b+"; // eliminates fn call string  in comments or single quotes -- eg in L_PROGRAM, or in EXceptions
                            regex_match_string2 = "\\s+" + TO_SEARCH2.toLowerCase() + "\\s*\\(";// has non commented  line with package name before '.' in fn call string
                            pattern_match_string = Pattern.compile(regex_match_string);
                            pattern_match_string2 = Pattern.compile(regex_match_string2);

                            matcher_match_string = pattern_match_string.matcher(line.toLowerCase());
                            /******************Find the occurrences of the regex_match_string2 to check calls within package*******************/

                            if (!TO_SEARCH.equals(TO_SEARCH2)) {
                                if (global_file_package_name.get(ff.getPath()).equals(TO_SEARCH.substring(0, TO_SEARCH.indexOf('.')))) {
                                    matcher_match_string2 = pattern_match_string2.matcher(line.toLowerCase());
                                    if (matcher_match_string2.find()) {
                                        if (!line.toLowerCase().contains("l_program") && line.toLowerCase().indexOf("procedure ") < 0 && line.toLowerCase().indexOf("function ") < 0 && line.toLowerCase().indexOf("end ") < 0) {
                                            if (!matchedFiles.contains(ff.getName()))
                                                matchedFiles.add(ff.getName());

                                            if (!lines_string_found.contains(lineNumber)) {
                                                lines_string_found.add(lineNumber);
                                                file_index_matched_lines.add(list_index);
                                            }

                                            global_flag_string_found = 1;   // Make this 1 for at least once string is found in any file
                                            flag_string_found = 1;
                                        }

                                    }
                                }

                            }
                        }// else if ends


                        //**********************Check for main string regex1*****************************************************

                        if (matcher_match_string.find()) {

                            if (!line.toLowerCase().contains("l_program") && (line.indexOf("--") >= 0 ? (line.indexOf("--") < line.indexOf(TO_SEARCH) ? false : true) : true))    //Check for comment line .. if -- is before the the search string then neglect the line
                            {
                                if (!matchedFiles.contains(ff.getName()))
                                    matchedFiles.add(ff.getName());

                                if (!lines_string_found.contains(lineNumber)) {

                                    lines_string_found.add(lineNumber);
                                    file_index_matched_lines.add(list_index);
                                }

                                global_flag_string_found = 1;   // Make this 1 for at least once string is found in any file
                                flag_string_found = 1;
                            }
                        }
                        line = br.readLine().toLowerCase();
                        lineNumber++;
                    }//end of while
                }//try
                catch (Exception e) {
                    System.out.println("[EXCEPTION]\t<" + e.getClass().getName() + ">");
                    e.printStackTrace();
                }

                if (flag_string_found == 1)
                    function_of_occurence(ff);

                init();

                try {
                    br.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            System.out.println("[ERROR]\t<" + DIR_ERROR + ">\tExiting...");
            System.exit(0);
        }
        return FINAL_RETURN_RESULT;
    }


    public void function_of_occurence(File f) {
        String matched_times_fn_scope_line = "";
        int start_line = 0, end_line = 0;
        String parts[];
        int index = 0, lastIndex = -1;

        for (int i = 0; i < func_lineNum_scope.size(); i++) {
            String lines = func_lineNum_scope.get(i);
            parts = lines.split(",");
            start_line = Integer.parseInt(parts[0]);
            end_line = Integer.parseInt(parts[1]);

            String lines_one_ahead = "";
            if (i < func_lineNum_scope.size() - 1) {
                lines_one_ahead = func_lineNum_scope.get(i + 1);
                end_line = Integer.parseInt(lines_one_ahead.split(",")[0]);   //End line becomes start line number of the next function till size-1
            }
            else {
                end_line = Integer.MAX_VALUE;                                     //So that it end line number of end function call is wrong, then also it is bigger than the found string line number
            }


            for (int line : lines_string_found) {
                if (line >= start_line && line <= end_line) {
                    if (lastIndex != index) {
                        matched_times_fn_scope_line = lines_string_found.size() + ";" + function_name.get(index) + ";" + start_line + "," + end_line + ";" + line;
                        FINAL_FUNCTION_NAMES.add(matched_times_fn_scope_line);
                        lastIndex = index;
                    }
                }
            }
            index++;
        }
        FINAL_RETURN_RESULT.put(f.getPath(), FINAL_FUNCTION_NAMES);
    }
}
