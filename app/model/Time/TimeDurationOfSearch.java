package model.Time;


import model.Clients.Client;

public class TimeDurationOfSearch
{

	public static void getElapsedTime(Client clientObject)
	{
		while(clientObject.searching_goingOn_ind==1)
		{
			long startTime=clientObject.startTime;
			long endTime=System.currentTimeMillis();
			long milliseconds=(endTime-startTime);
		 //	System.out.println("start: "+startTime+" end: "+endTime);
			String seconds = ""+(int) (milliseconds / 1000) % 60 ;
			String minutes = ""+(int) ((milliseconds / (1000*60)) % 60);
			String hours   = ""+(int) ((milliseconds / (1000*60*60)) % 24);

			if(Integer.parseInt(seconds)<10)
				seconds="0"+seconds;
			if(Integer.parseInt(minutes)<10)
				minutes="0"+minutes;
			if(Integer.parseInt(hours)<10)
				hours="0"+hours;
			clientObject.elapsedTime=hours+":"+minutes+":"+seconds;
		}
	}


}