package model.Clients;

import java.util.LinkedHashMap;

/**
 * Created by shuklv1 on 30-Sep-17.
 */
public class ClientResults {

    //Maps CLIENT_IP to RESULT
    static LinkedHashMap<String, ClientResult> ClientIP_Result;
    static{
        ClientIP_Result=new LinkedHashMap<>();
    }
    public void addClientResult(String CLIENT_IP){

        if(!ClientIP_Result.keySet().contains(CLIENT_IP)){
            ClientResult resultObject=new ClientResult();
            ClientIP_Result.put(CLIENT_IP,resultObject);
            System.out.println("\nNew Result object: "+CLIENT_IP+"\n");
        }
        else{
            System.out.println("\nExisting Result object: "+CLIENT_IP+"\n");
        }
    }
    public ClientResult getClientResult(String CLIENT_IP){
        return ClientIP_Result.get(CLIENT_IP);
    }
    public void removeClientResultObject(String CLIENT_IP){
        if(ClientIP_Result.containsKey(CLIENT_IP)){
            ClientIP_Result.remove(CLIENT_IP);
               System.out.println("Client Result object removed: "+CLIENT_IP);
        
        }
    }
}

