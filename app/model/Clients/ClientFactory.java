package model.Clients;

import java.util.LinkedHashMap;

/**
 * Created by shuklv1 on 29-Sep-17.
 */
public class ClientFactory {
    static LinkedHashMap<String, ClientThread> NewRequest_ThreadName_hm=new LinkedHashMap<>(); //CLIENT_IP ---> Thread object
    public void addClient(String CLIENT_IP){
        Client clientObject;
        if(!NewRequest_ThreadName_hm.keySet().contains(CLIENT_IP)){
            ClientThread obj=new ClientThread(CLIENT_IP);
            NewRequest_ThreadName_hm.put(CLIENT_IP,obj);
            System.out.println("\nNew request: "+CLIENT_IP+"\n");
        }
        else{
            System.out.println("\nExisting request: "+CLIENT_IP+"\n");
        }
            clientObject=getClientObject(CLIENT_IP);
            clientObject.setClientInstancesRunning(1);
            // System.out.println("Client Instances: "+clientObject.getClientInstancesRunning());
   
    }
    public void activateClient(ClientThread clientThreadobj){
        clientThreadobj.start();
    }
    public void removeClient(String CLIENT_IP){
        if(NewRequest_ThreadName_hm.containsKey(CLIENT_IP)){
            NewRequest_ThreadName_hm.remove(CLIENT_IP);
            System.out.println("Client Thread removed: "+CLIENT_IP);
        }
    }
    public static Client getClientObject(String CLIENT_IP){
        return NewRequest_ThreadName_hm.get(CLIENT_IP).clientObject;
    }
    public static ClientThread getClientThreadObject(String CLIENT_IP){
        return NewRequest_ThreadName_hm.get(CLIENT_IP);
    }

}
