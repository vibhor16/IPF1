package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Clients.Client;
import model.Clients.ClientResult;
import model.Clients.ClientResults;
import model.Clients.ClientFactory;
import model.Clients.ClientThread;
import model.Utilities.Messages;
import play.libs.Json;
import play.mvc.*;

import views.html.*;

import java.io.IOException;


public class Application extends Controller {

    static ClientFactory clientFactoryObject;
    static ClientResults clientResultsObject;

    static {
        clientFactoryObject = new ClientFactory();
        clientResultsObject = new ClientResults();
    }

    public static void handleIncomingRequest() {
        String CLIENT_IP = request().remoteAddress().trim();
        clientFactoryObject.addClient(CLIENT_IP);
        ClientThread clientThreadobj = getClientThreadObject();

        //If only one instance is running
        if (getClientObject(CLIENT_IP).getClientInstancesRunning() == 1) {
            System.out.println("\nThread Started: " + clientThreadobj.getThreadName());
            clientFactoryObject.activateClient(clientThreadobj);
            clientResultsObject.addClientResult(CLIENT_IP);
        }

    }

    public static void handleClientTerminateRequest(String CLIENT_IP) {
        Client clientObject = getClientObject(CLIENT_IP);
        clientFactoryObject.removeClient(CLIENT_IP);
        clientResultsObject.removeClientResultObject(CLIENT_IP);
        clientObject.setClientInstancesRunning(clientObject.getClientInstancesRunning() * -1); // should be less than 1 so that new search tab opens up
    }

    public static ClientThread getClientThreadObject() {
        String CLIENT_IP = request().remoteAddress().trim();
        return clientFactoryObject.getClientThreadObject(CLIENT_IP);
    }

    public static Client getClientObject() {
        String CLIENT_IP = request().remoteAddress().trim();
        return clientFactoryObject.getClientThreadObject(CLIENT_IP).getClientObject();
    }

    public static Client getClientObject(String CLIENT_IP) {
        return clientFactoryObject.getClientThreadObject(CLIENT_IP).getClientObject();
    }

    public static ClientResult getClientResultObject() {
        String CLIENT_IP = request().remoteAddress().trim();
        return clientResultsObject.getClientResult(CLIENT_IP);
    }

    public static ClientResult getClientResultObject(String CLIENT_IP) {
        return clientResultsObject.getClientResult(CLIENT_IP);
    }

    public static Result initFrontPage() {
        String CLIENT_IP = request().remoteAddress().trim();
        handleIncomingRequest();
        Client clientObject = getClientObject(CLIENT_IP);

        //This client is already searching
        if (clientObject.getClientInstancesRunning() > 1) {
            return ok(error.render(Messages.ERROR_CLIENT_EXISTS, CLIENT_IP));
        }

        return ok(initFront.render(CLIENT_IP));
    }

    public static int initFrontPageLoading(String CLIENT_IP) {
        System.out.println("loading: " + CLIENT_IP);
        return getClientObject().getPercentComplete();
    }

    public static int initFrontPageLoaded(String CLIENT_IP) {
        System.out.println("loaded: " + getClientObject().getPercentComplete());
        if (getClientObject().getPercentComplete() >= 100) {
            try {
                getClientObject().getGetFileCountInAllReposObject().serialize(getClientObject().allRepoFileCount);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getClientObject().getPercentComplete();
    }


    static ObjectMapper objMapper = new ObjectMapper();

    public static Result AjaxCalls(String callRequestURI) {

        String ajax_CLIENT_IP = callRequestURI.trim().substring(0, callRequestURI.indexOf('/'));
        String[] temp_requestURI_array = callRequestURI.split("/");
        Client clientObject;
        ClientResult clientResultObject;

        if (callRequestURI.contains("terminateClient")) {
            System.out.println("terminate client");
            handleClientTerminateRequest(ajax_CLIENT_IP);
            return ok(Json.toJson("Terminated"));

        }

        clientObject = getClientObject(ajax_CLIENT_IP);
        clientResultObject = getClientResultObject();

        if (callRequestURI.contains("indexLoaded")) {

            return ok(Json.toJson(initFrontPageLoaded(ajax_CLIENT_IP) + ""));

        }
        else if (callRequestURI.contains(("indexLoading"))) {

            System.out.println("call request: " + ajax_CLIENT_IP + " indexLoading");
            return ok(Json.toJson(initFrontPageLoading(ajax_CLIENT_IP) + ""));

        }
        else if (callRequestURI.contains("mainSearch")) {          //type: /ajax/:client_ip/mainSearch/1/a/a

            String visitNum = temp_requestURI_array[2];
            String SEARCH_STRING = temp_requestURI_array[3];
            String selectedRepository = temp_requestURI_array[4];
            clientObject.setSearchString(SEARCH_STRING);
            clientObject.mainSearch(visitNum, SEARCH_STRING, selectedRepository, clientResultObject);
            return ok(vibhor.render(
                    clientResultObject.FILE_DIRECTORY_PATH,
                    clientResultObject.selectedRepository,
                    null,
                    clientObject.GLOBAL_FILE_COUNT,
                    clientResultObject.selectRepoList,
                    clientResultObject.FINAL_ALL_PATHS,
                    clientResultObject.colorSelectedFunctions,
                    clientResultObject.displayTillLevel,
                    clientResultObject.CURRENT_PATH,
                    clientResultObject.ALL_SEARCH_STRINGS,
                    clientResultObject.ALL_REFINED_HM,
                    clientResultObject.ALL_TYPE_COUNTS,
                    ajax_CLIENT_IP

            ));
        }
        if (callRequestURI.contains("SystemReady")) {

            int result = getClientObject().FileCountRepoThread_lck | getClientObject().UtilityThread_lck;  // 0 - Ready  1 - Busy
            return ok(Json.toJson(result));

        }
        else if (callRequestURI.contains("isStringAlreadySearched")) {

            return ok(Json.toJson(clientObject.isStringAlreadySearched(temp_requestURI_array[2])));

        }
        else if (callRequestURI.contains("progress")) {

            clientObject.allRepoFileCount = clientObject.getGetFileCountInAllReposObject().deserialize();
            String thisRepoPath = clientObject.getDriverClassObject().getThisRepoPath();
            if (thisRepoPath == null) {
                return ok(Json.toJson("-1"));
            }
            int numFilesInThisRepo = clientObject.allRepoFileCount.get(thisRepoPath);
            double percent = (double) clientObject.GLOBAL_FILE_COUNT * 100 / numFilesInThisRepo;
            String currentSearchString = clientObject.currentSearchString;
            return ok(Json.toJson(percent + ":" + currentSearchString + ":" + clientObject.typeOfThisSearch));

        }
        else if (callRequestURI.contains("test")) {
            try {
                return ok(Json.toJson(objMapper.writeValueAsString(clientResultObject)));
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        else if (callRequestURI.contains("pathterminators")) {
            String level = temp_requestURI_array[2];
            String file_name = temp_requestURI_array[3];
            clientObject.pathTerminators(level, file_name, clientResultObject);
            try {
                return ok(Json.toJson(objMapper.writeValueAsString(clientResultObject)));
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        else if (callRequestURI.contains("stopSearching")){

            clientObject.stop_searching_ind = 1;
            clientObject.searching_goingOn_ind = 0;
            return ok(Json.toJson("Stopped"));
        }
        else if (callRequestURI.contains("getElapsedTime")){

            return ok(Json.toJson(clientObject.elapsedTime));
        }
        else if (callRequestURI.contains("graph")){

            return ok(graph.render(clientObject.ALL_SEARCH_STRINGS.get(0), ajax_CLIENT_IP));
        }
        else if (callRequestURI.contains("getGraph")) {

            return ok(Json.toJson(clientObject.getMap()));
        }
        else if (callRequestURI.contains("newsearch")) {

            String level = temp_requestURI_array[2];
            String pls_funcName = temp_requestURI_array[3];
            clientObject.newSearch(level, pls_funcName, clientResultObject);
            clientResultObject.displayTillLevel = clientObject.displayTillLevel;

            if (clientObject.popupText.equals("AlreadySearched")) {
                return ok(Json.toJson("Already searched - '" + pls_funcName + "'"));
            }
        }
        else if (callRequestURI.contains("showFileContents")) {
            String fileName = temp_requestURI_array[2];
            clientObject.showFileContents(fileName);
            return ok(fileContent.render(fileName, clientObject.getViewFilePath(), clientObject.getViewFileContents()));
        }
        else if (callRequestURI.contains("filterOptions")) {
            String operation = temp_requestURI_array[2];
            String userFilters = temp_requestURI_array[3];
            clientObject.Filters_options(operation, userFilters);
            return ok(Json.toJson("Filters set - " + userFilters));
        }
        return ok(Json.toJson("Bad Request: " + callRequestURI));
    }


}
