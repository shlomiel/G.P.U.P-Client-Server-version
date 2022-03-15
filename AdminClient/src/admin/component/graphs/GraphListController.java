package admin.component.graphs;

import admin.util.Constants;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import logic.DTO.GraphDTO;
import logic.system.Target;

import java.io.Closeable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GraphListController implements Closeable {

    private Timer timer;
    private TimerTask graphRefresher;
    private final BooleanProperty autoUpdate;
    private final IntegerProperty totalGraphs;
    //private HttpStatusUpdate httpStatusUpdate;

    //@FXML private ListView<String> usersListView;
    @FXML private Label chatUsersLabel;
    @FXML private ListView<GraphDTO> ListViewComponent;


    @FXML
    private TableView<GraphDTO> graphTable;
    @FXML
    private TableColumn<GraphDTO, String> nameCol;
    @FXML
    private TableColumn<GraphDTO, Target.GraphStatus> numOfTargetsCol;
    @FXML
    private TableColumn<GraphDTO, Integer> indiCol;
    @FXML
    private TableColumn<GraphDTO, String> userCol;
    @FXML
    private TableColumn<GraphDTO , Integer> leafCol;
    @FXML
    private TableColumn<GraphDTO, Integer> rootCol;
    @FXML
    private TableColumn<GraphDTO, Integer> middleCol;
    @FXML
    private TableColumn<GraphDTO, Integer> simPriceCol;
    @FXML
    private TableColumn<GraphDTO, Integer> comPriceCol;


    public synchronized TableView<GraphDTO> getGraphTable() {
        return graphTable;
    }

    public void setGraphTable(TableView<GraphDTO> graphTable) {
        this.graphTable = graphTable;
    }

    public GraphListController() {
        autoUpdate = new SimpleBooleanProperty();
        autoUpdate.setValue(true);
        totalGraphs = new SimpleIntegerProperty();
    }

    @FXML
    public void initialize() {
        chatUsersLabel.textProperty().bind(Bindings.concat("Graphs: (", totalGraphs.asString(), ")"));


        //table
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        numOfTargetsCol.setCellValueFactory(new PropertyValueFactory<>("numOfNodes"));
        indiCol.setCellValueFactory(new PropertyValueFactory<>("numOfIndependent"));
        rootCol.setCellValueFactory(new PropertyValueFactory<>("numOfRoot"));
        middleCol.setCellValueFactory(new PropertyValueFactory<>("numOfMiddle"));
        leafCol.setCellValueFactory(new PropertyValueFactory<>("numOfLeaf"));
        simPriceCol.setCellValueFactory(new PropertyValueFactory<>("priceSimulation"));
        comPriceCol.setCellValueFactory(new PropertyValueFactory<>("priceCompilation"));
    }


    public BooleanProperty autoUpdatesProperty() {
        return autoUpdate;
    }

    private void updateGraphList(List<GraphDTO> graphs) {
        Platform.runLater(() -> {
          //  graphTable.setItems(graphs);
            ObservableList<GraphDTO> items = graphTable.getItems();
            items.clear();
            items.addAll(graphs);
            graphTable.setItems(items);
            totalGraphs.set(graphs.size());
            if(ListViewComponent == null)
                return;
            ListViewComponent.setItems(items);
        });
    }

    public void startListRefresher() {
        graphRefresher = new GraphListRefresher(
                autoUpdate,
                //httpStatusUpdate::updateHttpLine,
                this::updateGraphList);
        timer = new Timer();
        timer.schedule(graphRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    @Override
    public void close() {
        graphTable.getItems().clear();
        totalGraphs.set(0);
        if (graphRefresher != null && timer != null) {
            graphRefresher.cancel();
            timer.cancel();
        }
    }

    public void setListViewComponent(ListView<GraphDTO> graphListView) {
        this.ListViewComponent = graphListView;
    }
}
