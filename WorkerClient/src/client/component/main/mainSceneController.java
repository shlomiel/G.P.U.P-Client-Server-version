package client.component.main;

import client.component.missions.MissionListController;
import client.component.missions.MissionListRefresher;
import client.component.users.UsersListController;
import client.util.http.HttpClientUtil;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import logic.DTO.MissionDTO;
import logic.DTO.TargetDTO;
import logic.system.Target;
import logic.tasks.CompilationRunTask;
import logic.tasks.SimulationRunTask;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static client.util.Constants.*;

public class mainSceneController implements Initializable, Closeable {


    private UsersListController usersController;
    private MissionListController missionsController;
    private final StringProperty currentUserName;
    private final StringProperty errorMessageProperty = new SimpleStringProperty();
    private Parent usersComponent;
    private Parent missionsComponent;
    private List<TargetDTO> targetsDoneByMe = new ArrayList<>();
    private List<MissionDTO> currentMissions = new ArrayList<>();
    private IntegerProperty numOfThreadsAvailable = new SimpleIntegerProperty();


    @FXML
    private Button signUpButton;
    @FXML
    private Tab dashboardTab;
    @FXML
    private Tab tasksTab;
    @FXML
    private AnchorPane missionsAnchorPane;
    @FXML
    private AnchorPane userListAnchorPane;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private Label userGreetingLabel;
    @FXML
    private AnchorPane graphListAnchorPane;
    @FXML
    private Label totalCreditsLabel;
    @FXML
    private MenuButton threadNumMenu;


    private List<TargetDTO> targetsRunList = new ArrayList<>();


    public mainSceneController() {
        currentUserName = new SimpleStringProperty(JHON_DOE);
        autoUpdate = new SimpleBooleanProperty();
        autoUpdate.setValue(true);
        totalMissions = new SimpleIntegerProperty();
    }

    public void updateUserName(String userName) {
        currentUserName.set(userName);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
        userGreetingLabel.textProperty().bind(Bindings.concat("Hello ", currentUserName));

        loadUserList();
        usersController.startListRefresher();
        loadMissionsList();
        missionsController.startListRefresher();

        initThreadMenu();

        //table
        //priceRecievedCol.setCellValueFactory(new PropertyValueFactory<>());
        targetStatusCol.setCellValueFactory(new PropertyValueFactory<>("runningStatus"));
        taskNameCol.setCellValueFactory(new PropertyValueFactory<>("missionName"));
        taskTypeCol.setCellValueFactory(new PropertyValueFactory<>("taskType"));
        tragetNameCol.setCellValueFactory(new PropertyValueFactory<>("targetName"));
        priceRecievedCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        logsCol.setCellValueFactory(new PropertyValueFactory<>("mylog"));


        taskNameMissionTableCol.setCellValueFactory(new PropertyValueFactory<>("missionName"));
        totalWorkersCol.setCellValueFactory(new PropertyValueFactory<>("numOfWorkers"));

        myMissionsTableView.setItems(FXCollections.observableList(currentMissions));
    }


    private void loadMissionsList() {
        URL missionsPageUrl = getClass().getResource(MISSIONS_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(missionsPageUrl);
            missionsComponent = fxmlLoader.load();
            missionsAnchorPane.getChildren().add(missionsComponent);
            missionsController = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void close() throws IOException {
        usersController.close();
    }


    private void loadUserList() {

        URL usersPageUrl = getClass().getResource(USERS_FXML_RESOURCE_LOCATION);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(usersPageUrl);
            usersComponent = fxmlLoader.load();
            userListAnchorPane.getChildren().add(usersComponent);
            usersController = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void signUpPresssed(ActionEvent event) {
        String taskName = "";
        for (MissionDTO msn : missionsController.getItems()) {
            if (missionsController.getSelected().get(msn.getMissionName()).getValue() == true) {
                taskName = msn.getMissionName();
            }
        }

        if (taskName.isEmpty()) {
            errorMessageProperty.set("Please select task");
            return;
        }

        String finalUrl = HttpUrl
                .parse(SIGN_UP_TASK_PAGE)
                .newBuilder()
                .addQueryParameter("taskName", taskName)
                .addQueryParameter("userName", currentUserName.getValue())
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {


            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    errorMessageLabel.setTextFill((Paint) Color.RED);
                    errorMessageProperty.set("Something went wrong: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        errorMessageLabel.setTextFill((Paint) Color.RED);
                        errorMessageProperty.set("Something went wrong: " + responseBody);
                        System.out.println(responseBody);
                    });
                } else {

                    Platform.runLater(() -> {
                        try {
                            String res = response.body().string();

                            //      TargetDTO[] targets = GSON_INSTANCE.fromJson(res, TargetDTO[].class);
                            //        myTargetTableView.setItems(FXCollections.observableList(Arrays.asList(targets)));

                            startTargetRefresher();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });

    }

    private final BooleanProperty autoUpdate;
    private final IntegerProperty totalMissions;
    Map<String, SimpleBooleanProperty> selected = new HashMap<>();
    private Timer timer;
    private TimerTask targetRefresher;
    private TargetListRefresher targetListRefresher;

    private void updateTargetTable(List<TargetDTO> targets) {
        Platform.runLater(() -> {
            ObservableList<TargetDTO> items = myTargetTableView.getItems();
            items.clear();
            items.addAll(targets);
            for (TargetDTO t : items) {
                selected.putIfAbsent(t.getTargetName(), new SimpleBooleanProperty());
            }

          //  myTargetTableView.setItems(items);
            totalMissions.set(targets.size());
        });
    }


    private void startTargetRefresher() {
        targetRefresher = new TargetListRefresher(
                autoUpdate,
                this::updateTargetTable,
                this.currentUserName.getValue(),
                this.numOfThreadsAvailable,
                this);
        timer = new Timer();
        timer.schedule(targetRefresher, REFRESH_RATE, REFRESH_RATE);
    }


    //////////////////////////////  My Tasks Tab //////////////////////////

    @FXML
    private TableView<TargetDTO> myTargetTableView;

    @FXML
    private TableColumn<TargetDTO, String> taskNameCol;

    @FXML
    private TableColumn<TargetDTO, String> taskTypeCol;

    @FXML
    private TableColumn<TargetDTO, String> tragetNameCol;

    @FXML
    private TableColumn<TargetDTO, Target.RunningStatus> targetStatusCol;

    @FXML
    private TableColumn<TargetDTO, Integer> priceRecievedCol;

    @FXML
    private TableColumn<TargetDTO, String> logsCol;

    @FXML
    TableView<MissionDTO> myMissionsTableView;

    @FXML
    TableColumn<MissionDTO, String> taskNameMissionTableCol;

    @FXML
    TableColumn<MissionDTO, Integer> totalWorkersCol;

    @FXML
    TableColumn<MissionDTO, String> taskProgressCol;

    @FXML
    TableColumn<?, Integer> targetsProccessedByMeCol;

    @FXML
    TableColumn<?, Integer> creditEarnedCol;

    @FXML
    Label selectedTaskLabel;

    @FXML
    Button pauseButton;

    @FXML
    Button resumeButton;

    @FXML
    Button stopButton;


    private void initThreadMenu() {

        for (int i = 0; i < numOfThreadsAvailable.getValue(); i++) {
            MenuItem newMenuItem = new MenuItem(String.valueOf(i + 1));
            int currentI = i;
            newMenuItem.setOnAction(e -> {
                threadNumMenu.setText((String.valueOf(currentI + 1)));
                return;
            });
            threadNumMenu.getItems().add(newMenuItem);
        }
    }


    public void setNumberOfThreads(int maxThreads) {
        this.numOfThreadsAvailable.setValue(maxThreads);
        executor = Executors.newFixedThreadPool(maxThreads);
    }

    @FXML
    public void pauseButtonPressed(ActionEvent event) {
    }

    @FXML
    public void resumeButtonPressed(ActionEvent event) {
    }

    @FXML
    public void stopButtonPressed(ActionEvent event) {
    }

    public synchronized void addTargetsToRunTargets(TargetDTO[] targets) {
        if(targets.length == 0){
            return;
        }
        this.targetsRunList.addAll(Arrays.asList(targets));
        this.targetsDoneByMe.addAll(Arrays.asList(targets));

        //myTargetTableView.getItems().clear();
        myTargetTableView.setItems(FXCollections.observableList(targetsDoneByMe));
    }

    public synchronized  void addTargetsToRunTargets(TargetDTO t){
        this.targetsRunList.add(t);
        myTargetTableView.setItems(FXCollections.observableList(targetsRunList));
    }


    protected ExecutorService executor;

    public synchronized void runTargets() {
        //Steps:
        //1) Create a thread pool with MaxThreads - DONE

        //2) Execute each target in targetsRunList
        SimulationRunTask simWorker;
        CompilationRunTask compWorker;
        while (!targetsRunList.isEmpty()) {
            TargetDTO t = targetsRunList.get(0);
            targetsRunList.remove(t);
            //System.out.println("In run targets");
            if(t.getMission().getMissionType().equals("Simulation")) {
                simWorker = new SimulationRunTask(t, t.getMission(), this);
                executor.execute(simWorker);//calling execute method of ExecutorService
            }

            else{
                compWorker = new CompilationRunTask(t,t.getMission(),this);
                executor.execute(compWorker);
            }
        }
    }


    public void updateRunStatusInServer(TargetDTO t)
    {
        Gson json = new Gson();

        String finalUrl = HttpUrl
            .parse(UPDATE_TARGET_RUN_RESULT)
            .newBuilder()
            .addQueryParameter("target", json.toJson(t))
            //.addQueryParameter("userName", currentUserName.getValue())
            .build()
            .toString();


        HttpClientUtil.runAsync(finalUrl,new Callback() {


        @Override
        public void onFailure (@NotNull Call call, @NotNull IOException e){
            Platform.runLater(() -> {
                errorMessageLabel.setTextFill((Paint) Color.RED);
                errorMessageProperty.set("Something went wrong: " + e.getMessage());
            });
        }

        @Override
        public void onResponse (@NotNull Call call, @NotNull Response response) throws IOException {
            if (response.code() != 200) {
                String responseBody = response.body().string();
                Platform.runLater(() -> {
                    errorMessageLabel.setTextFill((Paint) Color.RED);
                    errorMessageProperty.set("Something went wrong: " + responseBody);
                    System.out.println(responseBody);
                });
            } else {

                Platform.runLater(() -> {
                    try {
                        String res = response.body().string();


                        startTargetRefresher();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    });
}


    private List<String> myMissions = new ArrayList<>();

    public synchronized void addTaskToMyTasks(MissionDTO mission) {
        if(myMissions.contains(mission.getMissionName()))
            return;
        currentMissions.add(mission);
        myMissions.add(mission.getMissionName());
        //myMissionsTableView.getItems().clear();
        myMissionsTableView.refresh();

    }

    public synchronized void updateRunStatusInUI(TargetDTO dtOtarget) {// change the status of the targets, and add the price
      //  myTargetTableView.getItems().clear();
        //myTargetTableView.setItems(FXCollections.observableList(targetsDoneByMe));
        TargetDTO t = targetsDoneByMe.get(targetsDoneByMe.indexOf(dtOtarget));
        if(t.getRunningStatus().equals(Target.RunningStatus.FINISHED) && t.getLastRunResult()!= Target.RunResult.FAILURE) {
            t.setPrice(t.getMission().getSinglePrice());
            totalCreditsLabel.setText(String.valueOf(
                    Integer.parseInt(totalCreditsLabel.getText() + t.getPrice())));
        }

        myTargetTableView.refresh();
        myMissionsTableView.refresh();

        if(t.getMission().getFinishedList().size() == t.getMission().getNumOfTargets()){
            t.getMission().setStatus("FINISHED");
        }
    }
}


