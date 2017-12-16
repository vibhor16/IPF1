package model.Logging;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by shuklv1 on 29-Sep-17.
 */
public class Logger {
    static String LogFilePath="";
    void setLogFile(String path){
        LogFilePath=path;
    }
    String getLogFilePath(){
        return LogFilePath;
    }
    void log(char logType,String logString,int initialTabSpace,int beforeNewLines,int afterNewLines){
        String line="";
        for(int i=1;i<=beforeNewLines;i++){
            line+="\n";
        }
        for(int i=1;i<=initialTabSpace;i++){
            line+="\t";
        }
        switch(logType){
            case 'E':
                line+="<ERROR> "+logString;
                break;
            case 'I':
                line+="<INFO>"+logString;
                break;
            case 'N':
                line+=logString;
                break;
            default:
                System.out.println("Incorrect Log Type value");
        }
        for(int i=1;i<=afterNewLines;i++){
            line+="\n";
        }
        writeToFile(line);
    }
    void writeToFile(String line){
        File f=new File(LogFilePath);
        try {
            FileWriter fw = new FileWriter(f);
            fw.write(line);
            fw.write("\r\n");
            fw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
