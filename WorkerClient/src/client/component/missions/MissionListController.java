package client.component.missions;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import logic.DTO.MissionDTO;

import java.io.Closeable;
import java.util.*;

import static client.util.Constants.REFRESH_RATE;


public class MissionListController implements Closeable {

    private Timer timer;
    private TimerTask missionsRefresher;
    private final BooleanProperty autoUpdate;
    private final IntegerProperty totalMissions;

    public Map<String, SimpleBooleanProperty> getSelected() {
        return selected;
    }

    Map<String,SimpleBooleanProperty> selected = new HashMap<>();




    @FXML private Label missionsLabel;
    @FXML private TableView<MissionDTO> missionsTable;
    @FXML private TableColumn<MissionDTO, Boolean> selectCol;
    @FXML private TableColumn<MissionDTO, String> missionNameCol;
    @FXML private TableColumn<MissionDTO, String> graphNameCol;
    @FXML private TableColumn<MissionDTO, String> createdByCol;
    @FXML private TableColumn<MissionDTO, Integer> leafCol;
    @FXML private TableColumn<MissionDTO, Integer> rootCol;
    @FXML private TableColumn<MissionDTO, Integer> middleCol;
    @FXML private TableColumn<MissionDTO, Integer> indiCol;
    @FXML private TableColumn<MissionDTO, Integer> priceCol;
    @FXML private TableColumn<MissionDTO, Integer> numOfWorkersCol;
    @FXML private TableColumn<MissionDTO, Integer> numOfTargetsCol;
    @FXML private TableColumn<MissionDTO, String> statusCol;



    public MissionListController() {
        autoUpdate = new SimpleBooleanProperty();
        autoUpdate.setValue(true);
        totalMissions = new SimpleIntegerProperty();
    }

    public ObservableList<MissionDTO> getItems() {
        return this.missionsTable.getItems();
    }

    @FXML
    public void initialize() {
        missionsLabel.textProperty().bind(Bindings.concat("Missions: (", totalMissions.asString(), ")"));

        //table
        missionNameCol.setCellValueFactory(new PropertyValueFactory<>("missionName"));
        graphNameCol.setCellValueFactory(new PropertyValueFactory<>("graphName"));
        createdByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        indiCol.setCellValueFactory(new PropertyValueFactory<>("numOfIndis"));
        rootCol.setCellValueFactory(new PropertyValueFactory<>("numOfRoots"));
        middleCol.setCellValueFactory(new PropertyValueFactory<>("numOfMiddle"));
        leafCol.setCellValueFactory(new PropertyValueFactory<>("numOfLeafs"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        numOfWorkersCol.setCellValueFactory(new PropertyValueFactory<>("numOfWorkers"));
        numOfTargetsCol.setCellValueFactory(new PropertyValueFactory<>("numOfTargets"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

       // targetTableNewTask.setEditable(true);
        missionsTable.setEditable(true);
        selectCol.setCellFactory(column -> new CheckBoxTableCell<>());
        selectCol.setCellValueFactory(cellData -> {
            MissionDTO cellValue = cellData.getValue();
            BooleanProperty property = selected.get(cellValue.getMissionName());
            // Add listener to handler change
            property.addListener((observable, oldValue, newValue) -> property.setValue(newValue));
            return property;
        });

    }


    public BooleanProperty autoUpdatesProperty() {
        return autoUpdate;
    }

    private void updateMissionTable(List<MissionDTO> missions) {
        Platform.runLater(() -> {
          //  graphTable.setItems(graphs);
            ObservableList<MissionDTO> items = missionsTable.getItems();
            items.clear();
            items.addAll(missions);
            for (MissionDTO m:items){
                selected.putIfAbsent(m.getMissionName(),new SimpleBooleanProperty());
            }

            missionsTable.setItems(items);
            totalMissions.set(missions.size());
        });
    }

    public void startListRefresher() {
        missionsRefresher = new MissionListRefresher(
                autoUpdate,
                this::updateMissionTable);
        timer = new Timer();
        timer.schedule(missionsRefresher, REFRESH_RATE , REFRESH_RATE );
    }

    @Override
    public void close() {
        missionsTable.getItems().clear();
        totalMissions.set(0);
        if (missionsRefresher != null && timer != null) {
            missionsRefresher.cancel();
            timer.cancel();
        }
    }

}
