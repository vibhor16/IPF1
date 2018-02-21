package model.Multithreading;

import model.Clients.Client;
import model.Logic.GetFileCountInAllRepos;
import model.Time.TimeDurationOfSearch;
import model.Utilities.Utility;

import java.io.IOException;

class FileCountRepoThread implements Runnable {

    Thread t = null;
    GetFileCountInAllRepos gfcrObject;
    Client clientObject;

    FileCountRepoThread(String name, Client clientObject) {
        this.clientObject = clientObject;
        gfcrObject = this.clientObject.getGetFileCountInAllReposObject();
        if (t == null) {
            t = new Thread(this, name);
        }
    }

    public void start() {
        // TODO Auto-generated method stub

        if (!t.isAlive())
            t.start();

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

        clientObject.FileCountRepoThread_lck = 1;
        System.out.println("[INFO]\t<NEW Thread -> " + t.getName() + ">");

        try {
            gfcrObject.init();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[INFO]\t<TERMINATED Thread -> " + t.getName() + ">");
        clientObject.FileCountRepoThread_lck = 0;
    }

}

class timeDurationThread implements Runnable {

    Thread t = null;
    TimeDurationOfSearch timeDurationOfSearchObject;
    Client clientObject;

    timeDurationThread(String name, Client clientObject) {
        this.clientObject = clientObject;
        timeDurationOfSearchObject = this.clientObject.getTimeDurationOfSearchObject();
        if (t == null) {
            t = new Thread(this, name);
        }
    }

    public void start() {
        // TODO Auto-generated method stub

        if (!t.isAlive())
            t.start();

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

        System.out.println("[INFO]\t<NEW Thread -> " + t.getName() + ">");
        timeDurationOfSearchObject.getElapsedTime(clientObject);
        System.out.println("[INFO]\t<TERMINATED Thread -> " + t.getName() + ">");
    }

}

class UtilityThread implements Runnable {

    Thread t = null;
    Utility utl_obj;
    String repoName = "se-rms-16_0_x1";
    Client clientObject;

    UtilityThread(String name, Utility utl_obj, Client clientObject) {
        this.clientObject = clientObject;
        if (t == null) {
            this.utl_obj = utl_obj;
            t = new Thread(this, name);
        }
    }

    public void start() {
        // TODO Auto-generated method stub

        if (!t.isAlive())
            t.start();

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        clientObject.UtilityThread_lck = 1;
        System.out.println("[INFO]\t<NEW Thread -> " + t.getName() + ">");

        utl_obj.startPkgNmeFileSearch(repoName);
        System.out.println("[INFO]\t<TERMINATED Thread -> " + t.getName() + ">");
        clientObject.UtilityThread_lck = 0;
    }

}

public class Threading {

    Client clientObject;

    public Threading(Client clientObject) {
        this.clientObject = clientObject;
    }

    public void newFileCountRepoThread() {
        FileCountRepoThread t = new FileCountRepoThread("NumFilesInRepos_thread", clientObject);
        t.start();

    }

    public void newTimeElapsedThread() {
        timeDurationThread t = new timeDurationThread("TimeDuration_thread", clientObject);
        t.start();
    }

    public void newUtilityThread(Utility utl_obj) {
        UtilityThread t = new UtilityThread("UtilityThread_thread", utl_obj, clientObject);
        t.start();
    }

}
