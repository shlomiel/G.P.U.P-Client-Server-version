package client.util;

import com.google.gson.Gson;

public class Constants {

    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static String JHON_DOE = "<Anonymous>";
    public final static int REFRESH_RATE = 2000;
    public final static String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %.10s: %s%n";

    // fxml locations
    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/client/component/main/mainSceneClient.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/client/component/login/login.fxml";
    public final static String USERS_FXML_RESOURCE_LOCATION = "/client/component/users/users.fxml";
    public final static String GRAPHS_FXML_RESOURCE_LOCATION = "/client/component/graphs/graphs.fxml";
    public final static String MISSIONS_FXML_RESOURCE_LOCATION = "/client/component/missions/missions.fxml";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/GPUP";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String FILE_UPLOAD_PAGE = FULL_SERVER_PATH + "/upload-file";
    public final static String USERS_LIST = FULL_SERVER_PATH + "/userslist";
    public final static String GRAPHS_LIST = FULL_SERVER_PATH + "/graphlist";
    public static final String MISSIONS_LIST = FULL_SERVER_PATH + "/missionlist";
    public static final String GET_GRAPH_PAGE = FULL_SERVER_PATH + "/graph"; ;
    public static final String GET_FIND_ROUTE = FULL_SERVER_PATH + "/findroute";
    public static final String ADD_TASK_PAGE = FULL_SERVER_PATH + "/addtask";
    public final static String SIGN_UP_TASK_PAGE = FULL_SERVER_PATH + "/sign-up-task";
    public static final String GET_TARGETS_TO_WORK = FULL_SERVER_PATH + "/get-targets";
    public static final String UPDATE_TARGET_RUN_RESULT = FULL_SERVER_PATH + "/update-run-result";




    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
