package model.Logic;

import model.Clients.Client;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class GetFileCountInAllRepos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int countFiles=0;
	public static int percentComplete=0;
	static ArrayList<String> PLS_DIRECTORIES=null,FMB_DIRECTORIES=null,OTHER_DIRECTORIES=null;
	static ArrayList<String> allFiles=new ArrayList<>();
	static String MAIN_DIRECTORY_PATH="C:/SVN";
	Client clientObject;

	public GetFileCountInAllRepos(Client clientObject){
		this.clientObject=clientObject;
	}

	public void init() throws IOException {
		// TODO Auto-generated method stub
		//Calling filters class
 		clientObject.getFiltersClassObject().initFilterOptionsBackend();

 		File f=new File(MAIN_DIRECTORY_PATH);
		if(f.isDirectory())
		{
			int numOfRepos=0;
			int numChecked=0;
			File list[]=f.listFiles();

			for(File thisFile:list)
			{
				if(thisFile.isDirectory())
					numOfRepos++;
			}
			for(File thisFile:list)
			{
				if(thisFile.isDirectory())
				{
					setDirectories(MAIN_DIRECTORY_PATH+"/"+thisFile.getName());
					countFiles=0;
					cntAllFilesInRepo(MAIN_DIRECTORY_PATH+"/"+thisFile.getName());

					System.out.println("\t[INFO]\t<NumFilesInRepos_thread>\t"+MAIN_DIRECTORY_PATH+"/"+thisFile.getName()+"\tCount: "+countFiles);
//					ProgressBar.allRepoFileCount.put(MAIN_DIRECTORY_PATH+"/"+thisFile.getName(),countFiles);
					clientObject.allRepoFileCount.put(MAIN_DIRECTORY_PATH+"/"+thisFile.getName(),countFiles);
					numChecked++;
					clientObject.setPercentComplete((numChecked*100)/numOfRepos);
				}
			}
		}
 	}
	
	public void cntAllFilesInRepo(String path)
	{
		File mainDirectory = new File(path);
		if (mainDirectory.isDirectory()) {
			File[] ff = mainDirectory.listFiles();
			for (File file : ff) {
				if (file.isDirectory()) {
					cntAllFilesInRepo(file.getPath());
					countFilesInDir(file.getAbsolutePath().replace("\\", "/"));
				}
			}
		} else {
			System.out.println("ERROR: Not a directory - "+mainDirectory.getAbsolutePath());
		}
	}
	
	public void countFilesInDir(String directoryPath)
	{
		File directory = new File(directoryPath);
		//Pass respective directories to classes to manipulate

		if (directory.isDirectory()) {

			int count2 = countFiles;
			if (PLS_DIRECTORIES.contains(directoryPath)) {
				File[] ff = directory.listFiles();

				for (File file : ff) {
					if (file.isFile()) {
						if (file.getName().toLowerCase().indexOf("b.pls") >= 0) {
							allFiles.add(file.getName());
							countFiles++;
						}
					}
				}
			} else if (OTHER_DIRECTORIES.contains(directoryPath) || FMB_DIRECTORIES.contains(directoryPath)) {
				countFiles += directory.listFiles().length;

				// if(directoryPath.contains("14_1_x"))
				// 	{
				// 		System.out.println("dir: "+directoryPath+" count: "+(countFiles-count2));

				// 	}
				//System.out.println("dir: "+directoryPath+" count: "+(countFiles-count2));
			}
		}
	}

	public void setDirectories(String THIS_DIRECTORY_PATH)
	{
		PLS_DIRECTORIES = new ArrayList<>();
		OTHER_DIRECTORIES = new ArrayList<>();
		FMB_DIRECTORIES = new ArrayList<>();


		/*********   for versions before 14th   *******/

		//test
		PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/pls");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/txt");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/pc");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/fmb");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/trg");


		//official
		PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Packages/Source");

		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Procedures/Source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Batch/Proc/Source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Batch/Lib/Source");
//			OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH+"/Control_Scripts/Source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Data_conversion_Scripts/External_Table_Scripts");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Data_conversion_Scripts/Data_Load_Scripts");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Database_Change_Scripts/Source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Database_Migration_Scripts/Source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Triggers/Source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Types/Source");

		FMB_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Forms/Source");

		/*********** After 14th version *******************/

		PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Cross_Pillar/packages/source");
		PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Financials/packages/source");
		PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Foundation/packages/source");
		PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Import_Export/packages/source");
		PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Inventory_Movement/packages/source");
		PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Inventory_Tracking/packages/source");

		PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Localization/packages/source");
		PLS_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Procurement/packages/source");


		FMB_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Cross_Pillar/forms/source");
		FMB_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Financials/forms/source");
		FMB_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Foundation/forms/source");
		FMB_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Import_Export/forms/source");
		FMB_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Inventory_Movement/forms/source");
		FMB_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Inventory_Tracking/forms/source");
		FMB_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Localization/forms/source");
		FMB_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Procurement/forms/source");


		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Cross_Pillar/procedures/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Financials/procedures/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Foundation/procedures/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Inventory_Movement/procedures/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Inventory_Tracking/procedures/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Procurement/procedures/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Cross_Pillar/batch/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Cross_Pillar/batch/lib");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Cross_Pillar/control_scripts/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Cross_Pillar/install_scripts/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Cross_Pillar/db_change_scripts/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Cross_Pillar/triggers/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Cross_Pillar/types/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Financials/batch/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Financials/batch/lib");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Financials/data_conversion_scripts/external_table_scripts");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Financials/db_change_scripts/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Financials/triggers/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Financials/types/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Foundation/batch/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Foundation/batch/lib");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Foundation/data_conversion_scripts/external_table_scripts");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Foundation/db_change_scripts/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Foundation/triggers/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Foundation/types/source");
		//OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH+"/Foundation/data_conversion_scripts/data_load_scripts");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Import_Export/batch/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Import_Export/db_change_scripts/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Import_Export/triggers/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Import_Export/types/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Inventory_Movement/batch/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Inventory_Movement/batch/lib");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Inventory_Movement/db_change_scripts/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Inventory_Movement/triggers/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Inventory_Movement/types/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Inventory_Tracking/batch/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Inventory_Tracking/db_change_scripts/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Inventory_Tracking/triggers/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Inventory_Tracking/types/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Localization/batch/proc/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Localization/batch/lib/source");
		//	OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH+"/Localization/data_conversion_scripts/data_load_scripts");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Localization/db_change_scripts/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Localization/types/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Procurement/batch/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Procurement/batch/lib");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Procurement/db_change_scripts/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Procurement/types/source");
		OTHER_DIRECTORIES.add(THIS_DIRECTORY_PATH + "/Procurement/triggers/source");
			
			
		
		
	}

	public  void serialize(HashMap<String,Integer> allRepoFileCount)throws IOException
	{
		FileOutputStream fout=new FileOutputStream("coreFiles/allRepoFileCount.ser");  
		  ObjectOutputStream out=new ObjectOutputStream(fout);  
		  
		  out.writeObject(allRepoFileCount);
		  out.flush();  
		ObjectOutputStream oos=new ObjectOutputStream(out);
		// System.out.println("OK.. Serialized");
	}
	public  HashMap<String,Integer> deserialize()
	{
		ObjectInputStream in= null;
		try {
			in = new ObjectInputStream(new FileInputStream("coreFiles/allRepoFileCount.ser"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		HashMap<String,Integer> allRepoFileCount= null;
		try {
			allRepoFileCount = (HashMap<String,Integer>)in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// System.out.println("OK.. Deserialized");
		 return allRepoFileCount;
	}


}
