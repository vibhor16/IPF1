package model.Logic;

import java.util.ArrayList;
import java.util.HashMap;

public class FormsHandler {

	HashMap<String, ArrayList<String>> FINAL_RESULT;
	 
	public void init()
	{
		
	}
	public HashMap<String, ArrayList<String>> returnResults(String path,String TO_SEARCH)
	{
	  	
    	// Count files globally
    	DriverClass.GLOBAL_FILE_COUNT++;
    	return FINAL_RESULT;
	}

}
