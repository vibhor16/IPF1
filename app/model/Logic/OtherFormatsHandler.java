package model.Logic;

import model.Clients.Client;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtherFormatsHandler {

    LinkedHashMap<String, ArrayList<String>> FINAL_RETURN_RESULT;
    static String NOT_DIR = "Not a valid Directory";

    Client clientObject;

    public OtherFormatsHandler(Client clientObject) {
        this.clientObject = clientObject;
    }

    public void init() {
        FINAL_RETURN_RESULT = new LinkedHashMap<>();
    }
    //If string found, return file name mapped to null

    public LinkedHashMap<String, ArrayList<String>> returnResults(String path, String TO_SEARCH) {

        String regex_match_string = "\\b" + TO_SEARCH.toLowerCase() + "\\b";
        Pattern pattern_match_string = Pattern.compile(regex_match_string);
        Matcher matcher_match_string;
        BufferedReader br = null;
        File f = new File(path);
        if (f.isDirectory()) {
            File[] list = f.listFiles();
            for (File ff : list) {
                if (clientObject.stop_searching_ind == 1)
                    break;
                // Count files globally
                clientObject.GLOBAL_FILE_COUNT++;
                //If filter option set to categories other than PLS

                String allowedFormats = "";
                if (clientObject.filters_searchType.contains("BATCHES")) {
                    allowedFormats += ".pc;";
                }
                if (clientObject.filters_searchType.contains("FORMS")) {
                    allowedFormats += ".fmb;.mmb;";
                }
                if (clientObject.filters_searchType.contains("TRIGGERS")) {
                    allowedFormats += ".trg;";
                }
                if (clientObject.filters_searchType.contains("OTHER")) {
                    allowedFormats += ".h;,.mk;,.c;,.ksh;,.d;,.lib;,.sql;,.ctl;,.tmp;,.SQL;,.dat;,.cfg;,.seq;,.pll;";
                }
                String extension = "";
                if (ff.getName().lastIndexOf(".") != -1 && ff.getName().lastIndexOf(".") != 0)
                    extension = "." + ff.getName().substring(ff.getName().lastIndexOf(".") + 1) + ";";

                if (!allowedFormats.contains(extension))
                    continue;
                try {
                    br = new BufferedReader(new FileReader(ff));
                }
                catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                String line;

                try {
                    while ((line = br.readLine()) != null) {

                        line = line.toLowerCase().trim();

                        //******************Find the occurrences of the search_string*******************


                        matcher_match_string = pattern_match_string.matcher(line);
                        if (matcher_match_string.find()) {

                            if (!FINAL_RETURN_RESULT.keySet().contains(ff.getName()))
                                FINAL_RETURN_RESULT.put(ff.getPath(), null);

                        }
                    }
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }

            }
        }

        else {
            System.out.println(NOT_DIR);
        }

        return FINAL_RETURN_RESULT;
    }

}
