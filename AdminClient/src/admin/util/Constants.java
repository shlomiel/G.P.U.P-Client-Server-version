package admin.util;

import com.google.gson.Gson;

public class Constants {

    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static String JHON_DOE = "<Anonymous>";
    public final static int REFRESH_RATE = 2000;
    public final static String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %.10s: %s%n";

    // fxml locations
    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/admin/component/main/mainScene.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/admin/component/login/login.fxml";
    public final static String USERS_FXML_RESOURCE_LOCATION = "/admin/component/users/users.fxml";
    public final static String GRAPHS_FXML_RESOURCE_LOCATION = "/admin/component/graphs/graphs.fxml";
    public final static String MISSIONS_FXML_RESOURCE_LOCATION = "/admin/component/missions/missions.fxml";
    public final static String TASK_TAB_FXML_RESOURCE_LOCATION = "/admin/component/main/taskTab.fxml";
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
    public static final String GET_MISSION_PAGE = FULL_SERVER_PATH +"/mission";
    public static final String MISSION_CONTROL_PAGE = FULL_SERVER_PATH +"/missionControl";
    public static final String GET_FIND_ROUTE = FULL_SERVER_PATH + "/findroute";
    public static final String ADD_TASK_PAGE = FULL_SERVER_PATH + "/addtask";
    public static final String GET_MISSION_INFO = FULL_SERVER_PATH + "/mission-info";


    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
