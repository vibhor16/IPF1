package model.Logic;

import model.Clients.Client;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class DriverClass  {
	
	private static final long serialVersionUID = 1L;
	public   String NOT_FOUND="No match found!";
	public   int GLOBAL_FILE_COUNT=1;
	public   int GLOBAL_DIRECTORY_VISITED=1;
	public   String SEARCH_STRING;
	private  String thisDirectoryPath;
	ArrayList<String> flow=new ArrayList<>();
		
	LinkedHashMap<String,String> file_selected_index_range ;
	LinkedHashMap<Integer,String> all_functions ;
	LinkedHashMap<String, String> file_package_name=new LinkedHashMap<>() ;
    public LinkedHashMap<String,String> FILE_DIRECTORY_PATH=new LinkedHashMap<>();
	LinkedHashMap<String, ArrayList<String>> final_result_hashmap;
	int index;
	public Client clientObject;

	public DriverClass(Client clientObject){
		this.clientObject=clientObject;
	}


	//static RequestSearch req_search=RequestMapper.getRequestSearchObject();
	public void init()
	{
		final_result_hashmap=new LinkedHashMap<>();
		file_selected_index_range=new LinkedHashMap<>();	
		all_functions=new LinkedHashMap<>();
		index=0;
	}
	public void setThisRepoPath(String path)
	{
		thisDirectoryPath=path;
	}
	public String getThisRepoPath()
	{
		return thisDirectoryPath;
	}
	
	public void execute(){
		// TODO Auto-generated method stub
		
 
		
 		FilesDispatcher fdob=new FilesDispatcher(clientObject);
		
		init();
		LinkedHashMap<String, ArrayList<String>> hm;
		ArrayList<String> values=null;
		
		Iterator it;
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
	
		
 
		fdob.setSearchString(SEARCH_STRING);
		System.out.println("\n--- (NEW SEARCH) ---\n");
		System.out.println("\n[INFO]\t<SEARCH STARTED -> '"+fdob.getSearchString()+"' IN "+thisDirectoryPath+">");
 
 		// thisDirectoryPath="D:\\SVN\\se-rms-13_2";
		fdob.init(thisDirectoryPath.replace("\\", "/"));
		fdob.initFinalResultsHashMap();
	 
		long startTime=System.currentTimeMillis();
		
//		System.out.println("DC: "+fdob.getMainDirectory());
		fdob.Dispatch(fdob.getMainDirectory());
		hm=fdob.getFinalResult();
		final_result_hashmap.putAll(hm);
	
		file_package_name=fdob.getFilePackageNames();
		
//		System.out.println("DC: "+dob.file_package_name);
//		System.out.println("DC: "+dob.file_package_name.size());
		
		 int choice=0;
			int count=1;
			int fn_count=0;
			 // System.out.println("LinkedHashMap: "+hm);
			it = hm.entrySet().iterator();
		    // for pls files
			while (it.hasNext())
		    {
		        Map.Entry pair = (Map.Entry)it.next();
		        values=(ArrayList<String>) pair.getValue();
		        if(values==null)
		        	continue;
		        String file_name=(String) pair.getKey();
		        
		        
		        System.out.println();
		       System.out.println("\t[FOUND]\t<FILE>\t"+count++ +") "+file_name);
		        fn_count=0;
		       
		        
		        for(String s:values)
		        {
		        	String[] temp=s.split(";");
		        	String times=temp[0];
			        String function=temp[1];
			        String scope=temp[2];
			        String line_occur=temp[3];
			        
			     
			       System.out.println("\t[FOUND]\t<FUNCTION>\t"+ ++fn_count +") "+function+"("+scope+")\tLINE: "+line_occur+"\tTIMES: "+times);
			        setAllFunctions(fn_count, function);
			       
		        }
		        
		        setFileSelectedIndexRange(file_name, values.size()+"");
		        
		      //  System.out.println(pair.getKey() + " = " + pair.getValue());
		        it.remove(); // avoids a ConcurrentModificationException
		    }
		    
		   
		   
		   
			it = hm.entrySet().iterator();
		   

		    // for other files
			while (it.hasNext())
		    {
		        Map.Entry pair = (Map.Entry)it.next();
		        values=(ArrayList<String>) pair.getValue();
		        if(values==null)
		        {
		        	    System.out.println();
				        System.out.println("\t[INFO]\t<FILE>\t"+count++ +") "+pair.getKey());
				     
		        }
		    }
 	    
		    
		    
		    
		    long endTime=System.currentTimeMillis();
		 	long milliseconds=(endTime-startTime);
		 	int seconds = (int) (milliseconds / 1000) % 60 ;
			int minutes = (int) ((milliseconds / (1000*60)) % 60);
			int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
			System.out.println("\n[INFO]\t<EXECUTION TIME -> "+hours+":"+minutes+":"+seconds+">");
			System.out.println("[INFO]\t<FILES SEARCHED -> "+ clientObject.GLOBAL_FILE_COUNT+">\n");
			System.out.println("\n--- (SEARCH FINISHED) ---\n");

			
		 
		  
	}
	
	public void setAllFunctions(int fn_count,String function)
	{
		all_functions.put(fn_count, function);
	}
	public void getAllFunctions() 
	{
		System.out.println("\nAll functions: "+all_functions);
		
	}
	public void setFileSelectedIndexRange(String file_name,String times)
	{
		 
		int times1=Integer.parseInt(times);
		 
		if(!file_selected_index_range.containsKey(file_name))
		{
//			System.out.println("v: "+index+"   "+times1+","+(index+times1));
		file_selected_index_range.put(file_name,index+","+(index+times1));
		index=index+times1;
		}
		
		
	}
	
	public void getFileSelectedIndexRange()
	{
		
		System.out.println("\nFile_selected_index_range: \n"+file_selected_index_range);
		 
	}
	
	//Returns comma separated combination of file and package name at this index
	public String filePackageNameAtIndex(int selectedFunctionIndex)
	{
		 
		int startIndex=0;
		int endIndex=0;
	//	System.out.println("fpn: "+file_package_name);
		Iterator it=file_selected_index_range.entrySet().iterator();
		
		
		String range="";
		String file_package="";
		String file_name="";
		
		while(it.hasNext())
		{

	
	        Map.Entry pair = (Map.Entry)it.next();
	        
//			System.out.println("Key: "+pair);
	        file_name=(String) pair.getKey();
	        range=(String) pair.getValue();
	        String[] temp=range.split(",");
	        
	        startIndex=Integer.parseInt(temp[0]);
	        endIndex=Integer.parseInt(temp[1]);
	        
	        if(selectedFunctionIndex>startIndex && selectedFunctionIndex<=endIndex)
	        {
	        	file_package=file_name.toLowerCase()+","+file_package_name.get(file_name).toUpperCase();
	        }
	        
	        
		}
//		System.out.println("File_package: "+file_package);
		return file_package;
	}
	
	 
	public String getNewSearchString(String pls_funcName)
	{
		String parts[]=pls_funcName.split("-");
		String file_name=parts[0];
		String function_name=parts[1];
		String file_package=null;
		//System.out.println("file_package_name:  "+file_package_name);
		//System.out.println("file_name: "+file_name);
		//System.out.println("function_name:  "+function_name);
		
		Iterator it=file_package_name.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<String, String> pair=(Map.Entry<String, String>)it.next();
			String key=(String)pair.getKey();
			if(key.substring(key.lastIndexOf("\\")+1,key.length()).equals(file_name))
			{
				 file_package=(String)pair.getValue();
			}
			
			 
			
		}
		String new_search_string;
		//System.out.println("v: "+filePackageNameAtIndex(selectedFunctionIndex));
		
		new_search_string=file_package.toUpperCase()+"."+function_name.toUpperCase();
		return new_search_string;
	}

	
	public void setSearchString(String search) {
		// TODO Auto-generated method stub
		SEARCH_STRING=search;
		
	}

	
	public String getSearchString() {
		// TODO Auto-generated method stub
		return SEARCH_STRING;
	}

	 

	
	public LinkedHashMap<String, ArrayList<String>> getResults() {
		// TODO Auto-generated method stub
		
		//System.out.println("vihbo:  "+final_result_hashmap);
		return final_result_hashmap;
		
		
		
	}
	
	//contains hm result without path
	
	public LinkedHashMap<String,ArrayList<String>> getRefined(LinkedHashMap<String,ArrayList<String>> hm)
	{
		LinkedHashMap<String,ArrayList<String>> res=new LinkedHashMap<>();
		ArrayList<String> func_names=new ArrayList<>();
		ArrayList<String> values;
		Iterator it = hm.entrySet().iterator();
		
		while (it.hasNext())
		    {
		        Map.Entry pair = (Map.Entry)it.next();
		        values=(ArrayList<String>) pair.getValue();
				String dir_path_thisFile=(String) pair.getKey();
				String file_name=dir_path_thisFile.substring(dir_path_thisFile.lastIndexOf("\\")+1,dir_path_thisFile.length());
		        
		        if(values==null)
				{
					FILE_DIRECTORY_PATH.put(file_name,dir_path_thisFile);
					continue;
				}
		        
				//add file to directory HM to Application FILE_DIRECTORY_PATH
				FILE_DIRECTORY_PATH.put(file_name,dir_path_thisFile);
								
		       
		        func_names=new ArrayList<>();
		        for(String s:values)
		        {
		        	String[] temp=s.split(";");
		        	String times=temp[0];
			        String function=temp[1];
			        String scope=temp[2];
			        String line_occur=temp[3];
			        
					func_names.add(function);
			     
			       
			       
		        }
		        
				res.put(file_name,func_names);
		         
				
				
		        it.remove(); // avoids a ConcurrentModificationException
		    }
		    
		   
			it = hm.entrySet().iterator();
		    // for other files
			while (it.hasNext())
		    {
				
		        Map.Entry pair = (Map.Entry)it.next();
				
				String file_name=(String) pair.getKey();
				file_name=file_name.substring(file_name.lastIndexOf("\\")+1,file_name.length());
		       
				
		        values=(ArrayList<String>) pair.getValue();
		        if(values==null)
		        {
		        	    res.put(file_name,null);
				     
		        }
		    }
			return res;
	}
	
	
	
	//Contains pls func mapped as they come in serial (sno. -> count in this file), rest formats with (eg. trg -> count etc.)
	
	public LinkedHashMap<String,String> getTypeCount(LinkedHashMap<String,ArrayList<String>> hm)
	{
		//System.out.println("vihbo Pack:  "+hm);
		LinkedHashMap<String,String> res=new LinkedHashMap<>();
		//hm.put("f1.fmb", null);
		//hm.put("f2.fmb", null);
		//hm.put("c2.pc", null);
		//hm.put("b2.trg", null);
		// hm.put("b3.trg", null);
		int pls=0,func=0,trg=0,pc=0,fmb=0,h=0;
		ArrayList<String> values;
		String key;
		Iterator it = hm.entrySet().iterator();
		int ind=1;
		while (it.hasNext())
		    {
				 
			    
		        Map.Entry pair = (Map.Entry)it.next();
		        values=(ArrayList<String>) pair.getValue();
				key=(String)pair.getKey();
		        if(values==null)
		        {
		        	if(key.contains(".trg"))
		        	{
		        	trg++;
		        	}
		        	else
		        		if(key.contains(".pc"))
		        		{
		        			pc++;
		        		}
		        		else
		        			if(key.contains(".fmb"))
		        			{
		        				fmb++;
		        			}
		        			else
		        			if(key.contains(".h"))
		        			{
		        				h++;
		        			}
		        }
		        else
		        {
		        pls++;
				func=values.size();
		         
				String file_name=key.substring(key.lastIndexOf("\\")+1,key.length());
				res.put(ind++ +"",func+"");
		        }
		        
			}
			res.put("pls", pls+"");
			res.put("trg", trg+"");
			res.put("pc", pc+"");
			res.put("fmb", fmb+"");
			res.put("h", h+"");
			
	 
			return res;
	}
	
	public void serialize()throws IOException
	{
		FileOutputStream fout=new FileOutputStream("C:/SVN/se-rms-13_2/f2.txt");  
		  ObjectOutputStream out=new ObjectOutputStream(fout);  
		  
		  out.writeObject(clientObject.GLOBAL_FILE_COUNT);
		  out.flush();  
		ObjectOutputStream oos=new ObjectOutputStream(out);
	}

}
