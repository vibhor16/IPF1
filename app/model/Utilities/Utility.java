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
	int countFiles=0;
    int percentComplete=0;
	ArrayList<String> PLS_DIRECTORIES=null,FMB_DIRECTORIES=null,OTHER_DIRECTORIES=null;
	ArrayList<String> allFiles=new ArrayList<>();
	static String MAIN_DIRECTORY_PATH="C:/SVN";
	public static String repoToSearch=MAIN_DIRECTORY_PATH;
	public static TreeMap<String, String> fileNameToPkgName=new TreeMap();
	TreeMap<String, String> PkgNameToFileName=new TreeMap(); 
    ArrayList<String> funcNames;
	HashMap<String, ArrayList<String>> fileFuncNames=new HashMap<>();
	public static int finishedPkgName_flag=0;
	public void allRepoResults()  {
		// TODO Auto-generated method stub
 
 



 		File f=new File(MAIN_DIRECTORY_PATH);
		if(f.isDirectory())
		{
			int numOfRepos=0;
			int numChecked=0;
			File list[]=f.listFiles();

			for(File thisFile:list)
			{
				if(thisFile.isDirectory())
				{
					numOfRepos++;
				}
			}


			for(File thisFile:list)
			{
				
				
				if(thisFile.isDirectory())
				{
					
					setDirectories(MAIN_DIRECTORY_PATH+"/"+thisFile.getName());
					countFiles=0;
					
					if(repoToSearch.equals(MAIN_DIRECTORY_PATH+"/"+thisFile.getName()))
					thisRecursiveRepo(MAIN_DIRECTORY_PATH+"/"+thisFile.getName());

					
					System.out.println("\t[INFO]\t<UtilityThread_thread>\t\t"+MAIN_DIRECTORY_PATH+"/"+thisFile.getName()+"\tCount: "+countFiles);
//					ProgressBar.allRepoFileCount.put(MAIN_DIRECTORY_PATH+"/"+thisFile.getName(),countFiles);
					numChecked++;
					percentComplete=(numChecked*100)/numOfRepos;
				}
			}
		}
		 
		 
 
	 
//		FileOutputStream fout=new FileOutputStream("D:/SVN/se-rms-13_2/f1.txt");  
//		  ObjectOutputStream out=new ObjectOutputStream(fout);  
//		  
//		  out.writeObject(allFiles);  
//		  out.flush();  
//		ObjectOutputStream oos=new ObjectOutputStream(out);
//		System.out.println("ser");
		 
	}
	
	public void startPkgNmeFileSearch(String repoName)
	{
	    String path=MAIN_DIRECTORY_PATH+"/"+repoName;
		setDirectories(path);
		thisRecursiveRepo(path);
	}
	public void thisRecursiveRepo(String path)
	{

	
		File mainDirectory=new File(path);
 
		if(mainDirectory.isDirectory())
		{
			File[] ff=mainDirectory.listFiles();
			
			for(File file:ff)
			{
				if(file.isDirectory())
				{
					
					  		
					thisRecursiveRepo(file.getPath());
					getFilePackageNameThisRepo(file.getAbsolutePath().replace("\\", "/"));
				}
				 
				
			}
		}
		else
		{
			System.out.println("[ERROR]\t<Utility.java -> thisRecursiveRepo>\tPath: "+path+" --\tNOT A DIRECTORY!");
		}
	}
	
	public void getFilePackageNameThisRepo(String directoryPath) //sets fileName to pkgName hashmaps
	{
		//Pass respective directories to classes to manipulate
		 
		File directory=new File(directoryPath);
		int countFiles=0;

		if(directory.isDirectory())
		{
			if(PLS_DIRECTORIES.contains(directoryPath)) 
			{
				File[] ff=directory.listFiles();
				
				for(File file:ff)
				{
					if(file.isFile())
					{
						if(file.getName().toLowerCase().indexOf("b.pls")>=0)
						{
						String path=file.getAbsolutePath();
						String packageName=getPackageNameThisFile(path);
						fileNameToPkgName.put(path, packageName);
						PkgNameToFileName.put(packageName, path);
						allFiles.add(file.getName());	
				 		countFiles++;
						}
					}
				}

				System.out.println("\t[INFO]\t<UtilityThread_thread>\t\t"+directoryPath+"\tCount: "+countFiles);
			}
		}
		else
		{
			System.out.println("\n[ERROR]\t<Utility.java -> getFilePackageNameThisRepo>\tPath: "+directoryPath+" --\tNOT A DIRECTORY!");
	
		}
		finishedPkgName_flag=1;
	}
	public String getPackageNameThisFile(String path)
	{
		String regex_package_name1="^(?!.*(--)).*(BODY)\\s*([a-zA-Z_0-9]+)\\s*";
 		String regex_package_name2="(\\b+REPLACE\\b+FUNCTION)\\s*([a-zA-Z_0-9]+)\\s*";
 		File f=new File(path);
 		Pattern pattern_package_name1=Pattern.compile(regex_package_name1);
        Pattern pattern_package_name2=Pattern.compile(regex_package_name2);
        Matcher matcher_package_name1;
  		Matcher matcher_package_name2;
//  		Matcher matcher_regex;
  		
  		
//  		String regex_comment="\\s*[/*].*|\\s*[--].*";
//  		String regex_comment1="\\A\\s*[/*].*[*/]";
//  		String regex_comment2="\\s*[--].*";
//  		Pattern pattern_regex1=Pattern.compile(regex_comment1,Pattern.DOTALL);
//  		Pattern pattern_regex2=Pattern.compile(regex_comment2,Pattern.DOTALL);
  		try {
			FileReader fr=new FileReader(f);
			BufferedReader br=new BufferedReader(fr);
			String line;
			int l=1,cmnt_lck=0;
			
			
			while((line=br.readLine())!=null)
			{
//			    matcher_regex=pattern_regex1.matcher(line.toUpperCase());
//			    if(matcher_regex.find())
//			    {
//			    	System.out.println("Comment at "+l+"  line: "+line);
//			    }
				
				matcher_package_name1=pattern_package_name1.matcher(line.toUpperCase());
				matcher_package_name2=pattern_package_name2.matcher(line.toUpperCase());
				if(matcher_package_name1.find())
				{
//					System.out.println("Line: "+l+" name: "+matcher_package_name1.group(3));
					return matcher_package_name1.group(3); 
				}
				if(matcher_package_name2.find())
				{
//					System.out.println("Line: "+l+" name: "+matcher_package_name2.group(2));
					return matcher_package_name2.group(2);
				}
//				if(line.toUpperCase().indexOf("PACKAGE\nBODY")>=0)
//	    		{
//	    			
//					System.out.println("Line: "+l+" line: "+line);
//	    		}
				  
				 l++;
				// System.out.println("line: "+l++);
			}
			fr.close();
			br.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		return "Null";
	}
	public  ArrayList<String> allFuncAndProcsInFile(String PkgName,String type) //type for proc or func 
	{
		String PATH=PkgNameToFileName.get(PkgName);

		File f=new File(PATH);

		String regex_FUNC1="(^(?!.*(--)).*(\\bFUNCTION\\s+)([a-zA-Z_\\d]+)(\\s*\\(*))";    // see for function vv( type regex --- check 
		String regex_FUNC2="(^(?!.*(--)).*(\\bPROCEDURE\\s+)([a-zA-Z_\\d]+)(\\s*\\(*))";    // see for function vv( type regex --- check 
		String regex_END="^(?!.*(--|\\s*,\\s*|\\s+IF\\s*;|\\s+CASE\\s*;|\\s+LOOP\\s*;)).*((\\bEND\\s+[a-zA-Z_\\d]+)(\\s*;)|(\\bEND\\s*;))";
		Pattern pattern_FUNC1=Pattern.compile(regex_FUNC1);
 		Pattern pattern_FUNC2=Pattern.compile(regex_FUNC2);
 		Pattern pattern_END=Pattern.compile(regex_END);
 		Matcher matcher_FUNC1;
 		Matcher matcher_FUNC2;
 		Matcher matcher_END;
 		
 		ArrayList<String> function_name=new ArrayList<>();
		
		try {
			FileReader fr=new FileReader(f);
			BufferedReader br=new BufferedReader(fr);
			String line;
			int l=1;
			int count=0;
			int lock=0,flag_first_BEGIN=0,flag_END=0;
			String functionName="",prev_proc_name="";
			
 			while((line=br.readLine())!=null)
			{
				matcher_FUNC1=pattern_FUNC1.matcher(line.toUpperCase());
				matcher_FUNC2=pattern_FUNC2.matcher(line.toUpperCase());
				matcher_END=pattern_END.matcher(line.toUpperCase());
				
				if(flag_first_BEGIN==0 && line.trim().toLowerCase().equals("begin"))
				{
					flag_first_BEGIN=1;;
				}

			    //****************Finds func|proc head********************
			    			
			    			
				    		matcher_FUNC1=pattern_FUNC1.matcher(line.toUpperCase());
							matcher_FUNC2=pattern_FUNC2.matcher(line.toUpperCase());
					    	if(flag_END==0)
			    			{
			    				if(type.equals("FUNCTION"))
			    				{
				    				if(matcher_FUNC1.find())
									{
									 // String[] parts=line.split(" ");
									 // String[] tt=parts[1].split("\\(");
									 // functionName=tt[0];
									functionName=matcher_FUNC1.group(4).toLowerCase();
									// System.out.println("FUNC:  "+functionName +" File: "+ff.getName());
	//								 System.out.println(line);
							 		 
									 prev_proc_name=functionName;
	//								regex_END="^(?!.*( IF| LOOP)).*END\\s[a-zA-Z_]+";
									 
				 				       
			 				         if(!function_name.isEmpty())
			 				        	 {
			 				        	 
			 				        	 flag_END=1;
			 				        	//regex_END="\\s*END\\s*"+functionName.toUpperCase();
	//		 				        	regex_END="^(?!.*( IF| LOOP)).*END\\s[a-zA-Z_]+";
			 							
			 				        	 }
			 				         
			 				        
									}
								}
								else if(type.equals("PROCEDURE"))
								{
									if(matcher_FUNC2.find())
									{
									 // String[] parts=line.split(" ");
									 // String[] tt=parts[1].split("\\(");
									 // functionName=tt[0];
									functionName=matcher_FUNC2.group(4).toLowerCase();
									// System.out.println("FUNC:  "+functionName +" File: "+ff.getName());
	//								 System.out.println(line);
							 		 
							 		 
									 prev_proc_name=functionName;
	//								regex_END="^(?!.*( IF| LOOP)).*END\\s[a-zA-Z_]+";
									 
				 				       
			 				         if(!function_name.isEmpty())
			 				        	 {
			 				        	 
			 				        	 flag_END=1;
			 				        	//regex_END="\\s*END\\s*"+functionName.toUpperCase();
	//		 				        	regex_END="^(?!.*( IF| LOOP)).*END\\s[a-zA-Z_]+";
			 							
			 				        	 }
			 				         
			 				        
									}
								}
							
							
			    			}
			    			
			    			if(flag_first_BEGIN==1 && function_name.isEmpty())
			    			{
			    				flag_END=1;
			    				//regex_END="\\s*END\\s*"+prev_proc_name.toUpperCase();
			    			}
			    			
			    			
			    			
			    			
//			    //************ To check for internal calls **************
//				
//			    			if(flag_END==1)
//			    			{
//			    				String fn_file_line="";
//			    				matcher_internal_calls=pattern_internal_calls.matcher(line.toUpperCase());
//			    				if(matcher_internal_calls.find() )
//			    				{
//			    					fn_file_line=functionName+","+ff.getName()+","+lineNumber;   													// fn and file name and line
//			    					 
//			    					internal_fn_calls.add(matcher_internal_calls.group(1));
//			    					internal_fn_file_line.add(fn_file_line);
////			    					System.out.println("Internal:  "+matcher_internal_calls.group(1)+"    at line - "+lineNumber); // make group (0) for full definition
//			    				}
//			    			}
//			    			
			    			
			    			
				        	 
			    			
			    //******************Finds end of func or proc**********************
							
			    			
			    			
			    			
			    			if(flag_END==1 && flag_first_BEGIN==1 )
							{

								

								matcher_END=pattern_END.matcher(line.toUpperCase());
								 
							 if(matcher_END.find())	
							 {	
								  
							    // System.out.println(line+" end funcName:  "+functionName+" prev_proc_name: "+prev_proc_name);
							    flag_END=0;
								// String[] parts=line.split(" ");
								// String[] tt=parts[1].split(";");
								// functionName=tt[0];
								
						 	
								if(function_name.isEmpty())
									{
										// System.out.println("isEmpty() - 404");
										function_name.add(prev_proc_name);
									}
							 	else
									{
										// System.out.println("isNotEmpty() - 404");
										function_name.add(functionName);
									}
								
								}
							}	 l++;
				
			}
			fr.close();
			br.close();
//			 System.out.println("AL: "+function_name+"\n"+function_name.size());
//			System.out.println(count);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collections.sort(function_name);
		// System.out.println(PkgName+" "+type+" "+PATH+" "+f.isFile()+"\n"+function_name);
		return function_name;

	 	
	}
	public void setDirectories(String THIS_DIRECTORY_PATH)
	{
		PLS_DIRECTORIES=new ArrayList<>();
		OTHER_DIRECTORIES=new ArrayList<>();
		FMB_DIRECTORIES=new ArrayList<>();
	 
		/*********   for versions before 14th   *******/
			
			//test
//			PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH+"/pls");
			//official
			 
			PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH+"/Packages/Source");
	
		 		
	     /*********** After 14th version *******************/
			
			PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH+"/Cross_Pillar/packages/source");
			PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH+"/Financials/packages/source");
			PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH+"/Foundation/packages/source");
			PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH+"/Import_Export/packages/source");
			PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH+"/Inventory_Movement/packages/source");
			PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH+"/Inventory_Tracking/packages/source");
			PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH+"/Localization/packages/source");
			PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH+"/Procurement/packages/source");
			
	}
	public static void serialize(Utility obj)
	{
		
		File f=new File("C:\\Users\\vibhor_personal\\Desktop\\ProcessFlow_ORACLE\\java_play\\source\\HelloWorld2\\coreFiles\\filePkgName.ser"); 
		
		try{
		
			if(!f.exists())
				f.createNewFile();
		FileOutputStream fout=new FileOutputStream(f);  
		ObjectOutputStream out=new ObjectOutputStream(fout);  
		  
		  out.writeObject(obj);  
		  out.flush();  
			ObjectOutputStream oos=new ObjectOutputStream(out);
			System.out.println("[\tINFO\t]\t<Utility.java>\t<serialize>\t{"+f.getAbsolutePath()+"}..SUCCESS   ");
		}
		catch(IOException e)
		{
		  System.out.println("[\tEXCEPTION\t]\t<IOException>\t<Utility.java>\t<Serialize>\t{"+f.getAbsolutePath()+"}");
		  e.printStackTrace();
		}
	}
	public Utility deserialize()
	{
 
		Utility obj=null;
		File f=new File("C:\\Users\\vibhor_personal\\Desktop\\ProcessFlow_ORACLE\\java_play\\source\\HelloWorld2\\coreFiles\\filePkgName.ser"); 
		try{
		 ObjectInputStream in=new ObjectInputStream(new FileInputStream(f));  
		 obj=(Utility)in.readObject();
		 System.out.println("[\tINFO\t]\t<Utility.java>\t<deserialize>\t{"+f.getAbsolutePath()+"}\t..SUCCESS   ");
		  }
		catch(IOException e)
		{
			 System.out.println("[\tEXCEPTION]\t<IOException>\t<Utility.java>\t<serialize>\t{"+f.getAbsolutePath()+"}");
			e.printStackTrace();
		}
		catch(ClassNotFoundException e)
		{
			 System.out.println("[\tEXCEPTION\t]\t<ClassNotFoundException>\t<Utility.java>\t<deserialize>\t{"+f.getAbsolutePath()+"}");
			e.printStackTrace();
		}
		  
		 return obj;
	}
	
	public void refreshFilePkgNames()
	{
		allRepoResults();
		serialize(this);
		
	}
// 	public static void main(String[] args) throws IOException {
// 		// TODO Auto-generated method stub
// 		Utility ob=new Utility();
// //		Utility ob1=new Utility();
// //		ob.refreshFilePkgNames();
// //		ob1=ob.deserialize();
		
// 		repoToSearch="D:/SVN/se-rms-13_2_10";
// 		ob.allRepoResults();
// //		ob1.cntAllFilesInRepo("D:\\SVN\\se-rms-13_2_10");
// 		TreeMap<String, String> fntoPkgN=ob.fileNameToPkgName;
// 		System.out.println(ob.fileNameToPkgName);
// //		ArrayList<String> res=ob.allFuncAndProcsInFile("D:\\SVN\\se-rms-13_2_10\\Packages\\Source\\ordrcvb.pls");
// //		System.out.println(ob.getPackageName("D:\\SVN\\se-rms-13_2_10\\Packages\\Source\\ordrcvb.pls"));
// //		Collections.sort(res);
// //		System.out.println(res);

// 	}

}
