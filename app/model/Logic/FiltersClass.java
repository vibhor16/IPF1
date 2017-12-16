package model.Logic;

import model.Clients.Client;

import java.io.*;
import java.util.ArrayList;

public class FiltersClass {

	 Client clientObject;
	 public FiltersClass(Client clientObject){
	 	this.clientObject=clientObject;
	 }
	public String filters_searchType;
	
	// public static void main(String[] args) {
	// 	// TODO Auto-generated method stub
	// 	FiltersClass ob=new FiltersClass();
	// 	ob.initFilterOptionsBackend();
	// 	System.out.println("Before:");
	// 	System.out.println("type: "+filters_searchType);
	// 	System.out.println("PLS: "+PLS_DIRECTORIES);
	// 	System.out.println("FORM: "+FMB_DIRECTORIES);
	// 	System.out.println("OTHER: "+OTHER_DIRECTORIES);
		
	// 	ob.setFilterTypeSearch("PACKAGES;TRIGGERS;");
	// 	ob.setFilterDirectories("/Cross_Pillar/packages/source,/Financials/procedures/source,/Financials/forms/source,/Localization/packages/source,/Cross_Pillar/batch/source");
	// 	System.out.println("\nAfter:");
	// 	System.out.println("type: "+filters_searchType);
	// 	System.out.println("PLS: "+PLS_DIRECTORIES);
	// 	System.out.println("FORM: "+FMB_DIRECTORIES);
	// 	System.out.println("OTHER: "+OTHER_DIRECTORIES);
	// }
	 
	public void setFilterTypeSearch(String choices)
	{
		  
		clientObject.filters_searchType=choices;
		System.out.println("[INFO]\t[FiltersClass]\t<Filter Search Type SET>");
	}
	public void setFilterDirectories(String choices)
	{
		 
		 ArrayList<String> NEW_PLS_DIRECTORIES=new ArrayList<>();
		 ArrayList<String> NEW_FMB_DIRECTORIES=new ArrayList<>();
		 ArrayList<String> NEW_OTHER_DIRECTORIES=new ArrayList<>();
		String parts[]=choices.split(";");
		for(String path:parts)
		{
			path=path.trim();
			if(clientObject.PLS_DIRECTORIES.contains(path))
			{
				NEW_PLS_DIRECTORIES.add(path);
			}
			else
				if(clientObject.FMB_DIRECTORIES.contains(path))
				{
					NEW_FMB_DIRECTORIES.add(path);
				}
				else
					if(clientObject.OTHER_DIRECTORIES.contains(path))
					{
						NEW_OTHER_DIRECTORIES.add(path);
					}
		}

		clientObject.PLS_DIRECTORIES=NEW_PLS_DIRECTORIES;
		clientObject.FMB_DIRECTORIES=NEW_FMB_DIRECTORIES;
		clientObject.OTHER_DIRECTORIES=NEW_OTHER_DIRECTORIES;
		System.out.println("[INFO]\t[FiltersClass]\t<Filter Directories SET>");
	}
	public void initFilterOptionsBackend()
	{
		System.out.println("\n[INFO]\t[FiltersClass]\t[defaultFiltersOptions READING -> File: coreFiles/defaultFiltersOptions.txt]");
		String defaultFilterFilePath="coreFiles/defaultFiltersOptions.txt";
		
		//Set filter for type of search

		clientObject.filters_searchType="PACKAGES;FORMS;BATCHES;TRIGGERS;OTHER;";

		//Set filter for directories for searching

		clientObject.PLS_DIRECTORIES=new ArrayList<>();
		clientObject.FMB_DIRECTORIES=new ArrayList<>();
		clientObject.OTHER_DIRECTORIES=new ArrayList<>();
		File f=new File(defaultFilterFilePath);
		try {
			FileReader fr=new FileReader(f);
			BufferedReader br=new BufferedReader(fr);
			String line;
			int lineNum=1;
			try {
				while((line=br.readLine())!=null)
				{
					
					if(line.indexOf("PLS:")>=0)
					{
						
						String dirPath=line.substring(line.indexOf(":")+1, line.length()).trim();
						clientObject.PLS_DIRECTORIES.add(dirPath);
					}
					else
						if(line.indexOf("FMB:")>=0)
						{
							String dirPath=line.substring(line.indexOf(":")+1, line.length()).trim();
							clientObject.FMB_DIRECTORIES.add(dirPath);
						}
						else
							if(line.indexOf("OTHER:")>=0)
							{
								String dirPath=line.substring(line.indexOf(":")+1, line.length()).trim();
								clientObject.OTHER_DIRECTORIES.add(dirPath);
							}
					lineNum++;
				}
				System.out.println("[INFO]\t[FiltersClass]\t[defaultFiltersOptions DONE -> File: coreFiles/defaultFiltersOptions.txt]");
		
				br.close();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("[IOEXCEPTION]\t<FiltersClass>\t<initFilterOptionsBackend() -> "+f.getAbsolutePath()+" at line: "+lineNum+">");
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("[FILENOTFOUNDEXCEPTION]\t<FiltersClass>\t<initFilterOptionsBackend() -> "+f.getAbsolutePath()+">");
			e.printStackTrace();
		}
		
	}
    public String getSearchType(String userFilters)
	{
		String parts[]=userFilters.split("-");
		String res="";
		if(parts[0].trim().equals("1"))
			res+="PACKAGES;";
		if(parts[1].trim().equals("1"))
			res+="FORMS;";
		if(parts[2].trim().equals("1"))
			res+="BATCHES;";
		if(parts[3].trim().equals("1"))
			res+="TRIGGERS;";
		if(parts[4].trim().equals("1"))
			res+="OTHER;";

		return res;
	}
	public String getSearchDirs(String userFilters)
	{
		String parts[]=userFilters.split("-");
		String res="";
		for(int i=4;i<parts.length;i++)
		{
			res+=parts[i].trim().replace("+","/")+";";
		}
		return res;
	}
	public void UserSetDirFileCount()
	{
		clientObject.countDirFileCount=0;
		int count=0;
		for(String s: clientObject.PLS_DIRECTORIES)
		{
			File f=new File(s);
			if(f.isDirectory())
			{
				File[] ff=f.listFiles();
				for(File file:ff)
				{
					if(file.isFile())
					{
						count++;
					}
				}


				clientObject.filtersDirFileCount.put(s,count);
			}
			// else
			// 	System.out.println("\n\n[ERROR]\t[FiltersClass -> UserSetDirFileCount()]\t<Not a directory -> "+s+">");
			
			
		}
		clientObject.countDirFileCount+=count;


		count=0;
		for(String s: clientObject.FMB_DIRECTORIES)
		{
			 
			File f=new File(s);
			if(f.isDirectory())
			{
				File[] ff=f.listFiles();
				for(File file:ff)
				{
					if(file.isFile())
					{
						count++;
					}
				}
				clientObject.filtersDirFileCount.put(s,count);
			}
			// else
			// 	System.out.println("\n\n[ERROR]\t[FiltersClass -> UserSetDirFileCount()]\t<Not a directory -> "+s+">");
			
			
		}
		clientObject.countDirFileCount+=count;



		count=0;
		for(String s: clientObject.OTHER_DIRECTORIES)
		{
			File f=new File(s);
			if(f.isDirectory())
			{
				File[] ff=f.listFiles();
				for(File file:ff)
				{
					if(file.isFile())
					{
						count++;
					}
				}
				clientObject.filtersDirFileCount.put(s,count);
			}
			// else
			// 	System.out.println("\n\n[ERROR]\t[FiltersClass -> UserSetDirFileCount()]\t<Not a directory -> "+s+">");
			
			
		}
		clientObject.countDirFileCount+=count;
	}
}
