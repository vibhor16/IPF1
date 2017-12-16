package model.Logic;

import model.Clients.Client;

import java.util.ArrayList;
import java.util.HashMap;

public class FormsHandler {

	HashMap<String, ArrayList<String>> FINAL_RESULT;
	Client clientObject;

	public FormsHandler(Client clientObject){
		this.clientObject=clientObject;
	}
	public void init()
	{
		
	}
	public HashMap<String, ArrayList<String>> returnResults(String path,String TO_SEARCH)
	{
	  	
    	// Count files globally
		clientObject.GLOBAL_FILE_COUNT++;
    	return FINAL_RESULT;
	}

}
