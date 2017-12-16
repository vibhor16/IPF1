package model.Clients;

/**
 * Created by shuklv1 on 27-Sep-17.
 */
public class ClientThread implements Runnable{
    Thread t;
    String clientThreadName="";
    Client clientObject=null;
    public ClientThread(String name){
        this.clientThreadName=name;
        clientObject=new Client(name);
    }
    public String getThreadName(){
        return this.clientThreadName;
    }
    public Client getClientObject(){
        return this.clientObject;
    }
    @Override
    public void run() {
        clientObject.init();
        clientObject.countFileNumInAllRepos();
    }
    public void start(){
        if(t==null){
            t=new Thread(this,clientThreadName);
            t.start();
        }
    }
}
