package admin.component.main;

import admin.util.Constants;
import admin.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import logic.DTO.GraphDTO;
import logic.DTO.MissionDTO;
import logic.DTO.TargetDTO;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static admin.util.Constants.*;

public class TaskTabController implements Initializable {

    public MissionDTO getMyTask() {
        return myTask;
    }

    public void setMyTask(MissionDTO myTask) {
        this.myTask = myTask;
    }

    private MissionDTO myTask;
    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    @FXML private AnchorPane topAnchorPane;
    @FXML private Label currentMissionLabel;
    @FXML private Label missionGraphNameLabel;
    @FXML private Label selectMissionLabel;
    @FXML private Label myMissionsLabel;
    @FXML private Label errorMessageLabel;

    public ListView<MissionDTO> getMissionListView() {
        return missionListView;
    }

    @FXML private ListView<MissionDTO> missionListView;
    @FXML private AnchorPane buttomAnchorPane;
    @FXML public ListView<String> frozenListt;
    @FXML private Tooltip frozenListToolTip;
    @FXML private Label frozenLabel;
    @FXML public ListView<String> skippedList;
    @FXML private Label skippedLabel;
    @FXML private Label waitingLabel;
    @FXML public ListView<String> waitingList;
    @FXML public ListView<String> inProcessList;
    @FXML private Label inProcessLabel;
    @FXML private Label finishedLabel;
    @FXML public ListView<String> finishedList;
    @FXML private ProgressBar taskProgressBar;
    @FXML private Label progressPercentLabel;
    @FXML private Label numberOfTargetsLabel;
    @FXML private Label IndependentsLabel;
    @FXML private Label leafLabel;
    @FXML private Label middleLabel;
    @FXML private Label rootLabel;
    @FXML private Label numberOfWorkersLabel;
    @FXML private Label waitingInLineLabel;
    @FXML private Button playButton;
    @FXML private Button pauseButton;
    @FXML private Button resumeButton;
    @FXML private Button stopButton;
    @FXML private TextArea midRunTextArea;





    @FXML void pauseButtonPressed(ActionEvent event) {
        String missionName = currentMissionLabel.getText().split(" ")[2];
        startMissionRequest(missionName,"Pause");
    }

    @FXML
    void playButtonPressed(ActionEvent event) {

        //Form running queue or set at the relevant mission object
        String missionName = currentMissionLabel.getText().split(" ")[2];
        startMissionRequest(missionName,"Start");


    }

    private void startMissionRequest(String missionName,String reqType) {
        java.lang.String finalUrl = HttpUrl
                .parse(Constants.MISSION_CONTROL_PAGE)
                .newBuilder()
                .addQueryParameter("reqType", reqType)
                .addQueryParameter("missionName", missionName)
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }


            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        errorMessageProperty.set("Something went wrong: " + responseBody);
                        System.out.println(responseBody);
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            errorMessageProperty.set(response.body().string());
                            //updateLists(missionName);
                            startTargetRefresher(missionName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                }
            }
        });
    }


    private void startTargetRefresher(String missionName) {
        targetRefresher = new TaskListRefresher(
                autoUpdate,
              //  this::updateTargetTable,
                missionName,
                this);
        timer = new Timer();
        timer.schedule(targetRefresher, REFRESH_RATE+2000, REFRESH_RATE+2000);
    }

    private Timer timer;
    private TimerTask targetRefresher;

    private synchronized void updateLists(MissionDTO missionName) {
    }

    @FXML
    void resumeButtonPressed(ActionEvent event) {
        String missionName = currentMissionLabel.getText().split(" ")[2];
        startMissionRequest(missionName,"Resume");

    }

    @FXML
    void stopButtonPressed(ActionEvent event) {
        String missionName = currentMissionLabel.getText().split(" ")[2];
        startMissionRequest(missionName,"Stop");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        missionListView.setCellFactory(lv -> new ListCell<MissionDTO>() {
            @Override
            protected void updateItem(MissionDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? null : item.getMissionName() );
            }
        });

        missionListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Your action here
            if (newValue == null)
                return;
            loadMission(newValue.getMissionName()); //get mission from server
        });


        autoUpdate = new SimpleBooleanProperty();
        autoUpdate.setValue(true);

    }
    private  BooleanProperty autoUpdate;
    private void loadMission(java.lang.String missionName) {

        java.lang.String finalUrl = HttpUrl
                .parse(Constants.GET_MISSION_PAGE)
                .newBuilder()
                .addQueryParameter("missionName", missionName)
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }


            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        errorMessageProperty.set("Something went wrong: " + responseBody);
                        System.out.println(responseBody);
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            MissionDTO task = GSON_INSTANCE.fromJson(response.body().string(), MissionDTO.class);
                            setMyTask(task);
                            initLabels();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                }
            }
        });
    }

    private void initLabels() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
        numberOfTargetsLabel.textProperty().bind(Bindings.concat("Number of targets: ",
                myTask.getNumOfTargets()));
        currentMissionLabel.textProperty().bind(Bindings.concat("Current mission: ",
                myTask.getMissionName()));
        missionGraphNameLabel.textProperty().bind(Bindings.concat("Mission graph name: ",
                myTask.getGraphName()));
        IndependentsLabel.textProperty().bind(Bindings.concat("Independents: ",
                myTask.getNumOfIndis()));
        leafLabel.textProperty().bind(Bindings.concat("Leaves : ", myTask.getNumOfLeafs()));
        rootLabel.textProperty().bind(Bindings.concat("Roots: ",myTask.getNumOfRoots()));
        middleLabel.textProperty().bind(Bindings.concat("Middle: ", myTask.getNumOfMiddle()));
        numberOfWorkersLabel.textProperty().bind(Bindings.concat("Number of workers assigned: ",
                myTask.getNumOfWorkers()));


        frozenListt.setItems(FXCollections.observableArrayList(myTask.getFrozenList()));
        inProcessList.setItems(FXCollections.observableArrayList(myTask.getInProcessList()));
        waitingList.setItems(FXCollections.observableArrayList(myTask.getWaitingList()));
        skippedList.setItems(FXCollections.observableArrayList(myTask.getSkippedList()));
        finishedList.setItems(FXCollections.observableArrayList(myTask.getFinishedList()));
        //waitingInLineLabel.textProperty().bind(Bindings.concat("Targets waiting in line: "),myTask.get);

    }


    private UpdateListConsumerClass UIconsumer = new UpdateListConsumerClass(this);

    public synchronized void updateLists() {
        frozenListt.refresh();
        waitingList.refresh();
        inProcessList.refresh();
        skippedList.refresh();
        finishedList.refresh();
      //  frozenListt.setItems(FXCollections.observableList(myTask.getFrozenList()));
        //waitingList.setItems(FXCollections.observableList(myTask.getWaitingList()));
        //inProcessList.setItems(FXCollections.observableList(myTask.getInProcessList()));
        //skippedList.setItems(FXCollections.observableList(myTask.getSkippedList()));
        //finishedList.setItems(FXCollections.observableList(myTask.getFinishedList()));
        midRunTextArea.setText(myTask.getMyLog());
        taskProgressBar.setProgress(finishedList.getItems().size()+skippedList.getItems().size()
                /myTask.getNumOfTargets());

        midRunTextArea.setText("in updateLists");
    //    numberOfTargetsLabel.setText(String.valueOf(myTask.getNumOfTargets()));
   //     leafLabel.setText(String.valueOf(myTask.getNumOfLeafs()));
   //     middleLabel.setText(String.valueOf(myTask.getNumOfMiddle()));
   //     rootLabel.setText(String.valueOf(myTask.getNumOfMiddle()));
   //     IndependentsLabel.setText(String.valueOf(myTask.getNumOfIndis()));
   //     numberOfWorkersLabel.setText(String.valueOf(myTask.getNumOfWorkers()));
   //     waitingInLineLabel.setText(String.valueOf(myTask.getWaitingList().size()));
      //  updateProgressBar();
    }


    public synchronized void updateProgressBar(){
        taskProgressBar.setProgress(finishedList.getItems().size()+skippedList.getItems().size()
                /myTask.getNumOfTargets());
    }



}
