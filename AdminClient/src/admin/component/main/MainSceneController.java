package admin.component.main;
import admin.component.missions.MissionListController;
import admin.component.users.UsersListController;
import admin.util.http.HttpClientUtil;
import admin.component.graphs.GraphListController;
import admin.util.Constants;
import com.google.gson.Gson;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logic.DTO.GraphDTO;
import logic.DTO.TargetDTO;
import logic.system.Graph;
import logic.system.Target;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

import static admin.util.Constants.*;


public class MainSceneController implements Initializable, Closeable {

    @FXML private AnchorPane userListAnchorPane;
    @FXML private AnchorPane missionsAnchorPane;
    @FXML private AnchorPane addTaskGraphListAnchorPane;
    @FXML private AnchorPane anchorForFindRoute;
    @FXML private AnchorPane findCycleAnchorPane;
    @FXML private AnchorPane anchorPane;
    @FXML private AnchorPane whatIfAnchorPane;
    @FXML private AnchorPane newTaskButtomAnchorPane;
    @FXML private AnchorPane taskInfoAnchorPane;
    @FXML private AnchorPane simulationTaskAnchorPane;
    @FXML private AnchorPane compilationTaskAnchorPane;
    @FXML private RadioButton simulationRadioButton;
    @FXML private RadioButton compilationRadioButton;
    @FXML private RadioButton depOnRadioButton;
    @FXML private RadioButton reqForRadioButton;
    @FXML private RadioButton whatIfdepOnRadioButton;
    @FXML private RadioButton whatIfreqForRadioButton;
    @FXML private MenuButton fromMenuButon;
    @FXML private MenuButton toMenuButon;
    @FXML private MenuButton whatIfMenuButton;
    @FXML private MenuButton targetMenuButton;
    @FXML private Label targetsInSystemTextLabel;
    @FXML private Label numberOfTargetsLabel;
    @FXML private Label indiTextLabel;
    @FXML private Label leafTextLabel;
    @FXML private Label rootTextLabel;
    @FXML private Label midTextLabel;
    @FXML private Label leafNumLabel;
    @FXML private Label rootNumLabel;
    @FXML private Label midNumLabel;
    @FXML private Label indiNumLabel;
    @FXML private Label targetSelectedLabelWhatIf;
    @FXML private Label serialSetsInSystemTextLabel;
    @FXML private Label numberOfSetsLabel;
    @FXML private Label srcTargetLabel;
    @FXML private Label destTargetLabel;
    @FXML private Label targetSelectedLabel;
    @FXML private Label errorLabelAddTask;
    @FXML private Label processingTimeLabel;
    @FXML private Label randOrConstLabel;
    @FXML private Label successProbLabel;
    @FXML private Label warningProbLabel;
    @FXML private Button findRouteButton;
    @FXML private Button findCycleButton;
    @FXML private Button whatIfButton;
    @FXML private Button runCompilationButton;
    @FXML private Button runTaskButton;
    @FXML private Button findRouteExecButton;
    @FXML private Button addTaskButton;
    @FXML private ListView<String> whatIfListView;
    @FXML private ListView<String> findRouteList;
    @FXML private ListView<String> findCycleList;
    @FXML private TextField taskNameTextField;
    @FXML private Tab taskInfoTab;
    @FXML private TextField msField;
    @FXML private TextField successField;
    @FXML private TextField warningField;
    @FXML private RadioButton randRadioButton;
    @FXML private RadioButton constRadioButton;


    ////////////////////////Graph Info Tab/////////////////////////////////////////
    @FXML private ListView<GraphDTO> graphListView;
    @FXML private ListView<GraphDTO> graphListViewForTaskTab;
    @FXML private Label selectGraphLabel;
    @FXML private TableView<TargetDTO> targetTableNewTask;
    @FXML private TableColumn<TargetDTO, String> targetNameCol;
    @FXML private TableColumn<TargetDTO, Boolean> selectCol;
    @FXML private AnchorPane targetTableAnchorPane;

    //////Controllers///////
    private UsersListController usersController;
    private GraphListController graphsController;
    private GraphListController graphsControllerForTaskTab;
    private MissionListController missionsController;
    private TaskTabController taskController;
    //

    private final StringProperty errorMessagePropertyForTaskTab = new SimpleStringProperty();


    @FXML private TableView<TargetDTO> targetTable;
    @FXML private TableColumn<TargetDTO, String> nameCol;
    @FXML private TableColumn<TargetDTO, Target.GraphStatus> locationCol;
    @FXML private TableColumn<TargetDTO, ArrayList<String>> directDepOnCol;
    @FXML private TableColumn<TargetDTO, ArrayList<String>> allDepOnCol;
    @FXML private TableColumn<TargetDTO, ArrayList<String>> directReqForCol;
    @FXML private TableColumn<TargetDTO, ArrayList<String>> allReqForCol;
    @FXML private TableColumn<TargetDTO, String> freeInfoCol;
    private String selectedGraphForTask;


    public void setCurrentGraphToPresent(GraphDTO currentGraphToPresent) {
        this.currentGraphToPresent = currentGraphToPresent;
    }

    private GraphDTO currentGraphToPresent;

    @FXML
    void toMenuOptionSelected(ActionEvent event) {

    }

    @FXML
    void fromMenuOptionSelected(ActionEvent event) {

    }

    @FXML
    void runCompilationButtonPressed(ActionEvent event) {

    }
    @FXML
    void runTaskButtonPressed(ActionEvent event) {
        changeScene(event, "userinterface/taskScene.fxml");
    }


    @FXML void simulationTaskRadioButtonSelected(ActionEvent event){
        simulationTaskAnchorPane.setDisable(false);
        compilationTaskAnchorPane.setDisable(true);
    }

    @FXML void compilationTaskRadioButtonSelected(ActionEvent event){
        compilationTaskAnchorPane.setDisable(false);
        simulationTaskAnchorPane.setDisable(true);
    }

    @FXML
    void whatIfButtonPressed(ActionEvent event) {
        findCycleAnchorPane.setDisable(true);
        anchorForFindRoute.setDisable(true);
        findRouteList.setDisable(true);
        findCycleList.setDisable(true);

        whatIfAnchorPane.setDisable(false);
        whatIfListView.setDisable(false);
        ToggleGroup group = new ToggleGroup();
        whatIfdepOnRadioButton.setToggleGroup(group);
        whatIfreqForRadioButton.setToggleGroup(group);


        for (TargetDTO target : currentGraphToPresent.getTargets()) {
            MenuItem newMenuItem = new MenuItem(target.getTargetName());
            newMenuItem.setOnAction(e -> {
                targetSelectedLabelWhatIf.setText(target.getTargetName());
                ObservableList<String> observableList;

                if(whatIfdepOnRadioButton.isSelected()){
                    List<String> allNames = currentGraphToPresent.getTargets().
                            get(currentGraphToPresent.getTargets().indexOf(target)).getAllDepOn();

                    if(allNames == null){
                        ArrayList<String> empty = new ArrayList<>();
                        empty.add("No depends on");
                        observableList  = FXCollections.observableList(empty);
                        whatIfListView.setItems(observableList);
                        return;
                    }

                    observableList  = FXCollections.observableList(allNames);
                    whatIfListView.setItems(observableList);
                }

                else if(whatIfreqForRadioButton.isSelected()){
                    List<String> allNames = currentGraphToPresent.getTargets().
                            get(currentGraphToPresent.getTargets().indexOf(target)).getAllReqFor();

                    if(allNames == null){
                        ArrayList<String> empty = new ArrayList<>();
                        empty.add("No required for");
                        observableList  = FXCollections.observableList(empty);
                        whatIfListView.setItems(observableList);
                        return;
                    }
                    observableList  = FXCollections.observableList(allNames);
                    whatIfListView.setItems(observableList);
                }
                else{return;}
            });


            whatIfMenuButton.getItems().add(newMenuItem);
        }

    }

    @FXML
    void addTaskButtonPressed(ActionEvent event){
        //if(mission)
        Graph.RunningTimeType runType;
        ArrayList<TargetDTO> targetsToRun = getTargetsToRunOn();
        if(targetsToRun.isEmpty()){
            errorMessagePropertyForTaskTab.setValue("Please select targets from table");
            return;
        }
        if(taskNameTextField.getText().isEmpty()){
            errorMessagePropertyForTaskTab.setValue("Please enter a name for the task");
            return;
        }
        if(!simulationRadioButton.isSelected() && !compilationRadioButton.isSelected()){
            errorMessagePropertyForTaskTab.setValue("Please select mission type");
            return;
        }
        errorMessagePropertyForTaskTab.setValue("");

        if(compilationRadioButton.isSelected()) {
            if(destPath == null || sourcePath == null){
                errorMessagePropertyForTaskTab.setValue("Please select directory path");
            }
           // addTaskToServer(targetsToRun, this.taskNameTextField.getText(), "Compliation");
            addTaskToServerCompilation(targetsToRun, this.taskNameTextField.getText(), "Compilation");
            return;
        }

        else if(simulationRadioButton.isSelected()) {
            if (randRadioButton.isSelected())
                runType = Graph.RunningTimeType.RANDOM;

            else if (constRadioButton.isSelected())
                runType = Graph.RunningTimeType.FIXED;

            else {
                errorMessagePropertyForTaskTab.set("no run time type selection detected ");
                return;
            }


            addTaskToServer(targetsToRun, this.taskNameTextField.getText(), "Simulation",
                    Integer.parseInt(msField.getText()), runType,
                    Double.parseDouble(successField.getText()), Double.parseDouble(warningField.getText()));
        }
    }

    private void addTaskToServerCompilation(ArrayList<TargetDTO> targetsToRun, String taskName, String taskType) {
        Gson gson = new Gson();
        String targets = gson.toJson(targetsToRun);
        String finalUrl = null;
        if(taskType.equals("Compilation")) {
            finalUrl = HttpUrl
                    .parse(Constants.ADD_TASK_PAGE)
                    .newBuilder()
                    .addQueryParameter("targets", targets)
                    .addQueryParameter("taskName", taskName)
                    .addQueryParameter("taskType", taskType)
                    .addQueryParameter("username", this.currentUserName.getValue())
                    .addQueryParameter("graphName", selectedGraphForTask)
                    .addQueryParameter("source", sourcePath)
                    .addQueryParameter("dest",destPath)

                    .build()
                    .toString();
        }


        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessagePropertyForTaskTab.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->{
                        errorMessagePropertyForTaskTab.set("Something went wrong: " + responseBody);
                        System.out.println(responseBody);
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            String out = response.body().string();
                            System.out.println(out);
                            errorMessagePropertyForTaskTab.setValue(out);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }






    private void addTaskToServer(ArrayList<TargetDTO> targetsToRun,String taskName, String taskType,
                                 int ms, Graph.RunningTimeType runTimeSelectionEnum,
                                 double successProb, double successWithWarningsProb/*Incorscratch/*/ ) {
        Gson gson = new Gson();
        String targets = gson.toJson(targetsToRun);
        String finalUrl = null;
        if(taskType.equals("Simulation")) {
            finalUrl = HttpUrl
                    .parse(Constants.ADD_TASK_PAGE)
                    .newBuilder()
                    .addQueryParameter("targets", targets)
                    .addQueryParameter("taskName", taskName)
                    .addQueryParameter("taskType", taskType)
                    .addQueryParameter("username", this.currentUserName.getValue())
                    .addQueryParameter("graphName", selectedGraphForTask)
                    .addQueryParameter("ms", String.valueOf(ms))
                    .addQueryParameter("runTimeSelectionEnum",runTimeSelectionEnum.toString())
                    .addQueryParameter("successProb", String.valueOf(successProb))
                    .addQueryParameter("warningProb", String.valueOf(successWithWarningsProb))
                    .build()
                    .toString();
        }


        
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessagePropertyForTaskTab.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->{
                        errorMessagePropertyForTaskTab.set("Something went wrong: " + responseBody);
                        System.out.println(responseBody);
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            String out = response.body().string();
                            System.out.println(out);
                            errorMessagePropertyForTaskTab.setValue(out);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }


    private ArrayList<TargetDTO> getTargetsToRunOn() {
        ArrayList<TargetDTO> res = new ArrayList<>();
            for (TargetDTO t : targetTableNewTask.getItems()){
                if(selected.get(t.getTargetName()).getValue() == true)
                    res.add(t);
            }
        return res;
    }

    @FXML
    void findCycleButtonPressed(ActionEvent event) {
        whatIfAnchorPane.setDisable(true);
        whatIfListView.setDisable(true);
        anchorForFindRoute.setDisable(true);
        findRouteList.setDisable(true);

        findCycleAnchorPane.setDisable(false);
        findCycleList.setDisable(false);


        if(  !targetMenuButton.getItems().isEmpty())
            return;

        for (TargetDTO target : currentGraphToPresent.getTargets()) {
            MenuItem newMenuItem = new MenuItem(target.getTargetName());
            newMenuItem.setOnAction(e -> {
                findCycleList.getItems().clear();
                targetSelectedLabel.setText(target.getTargetName());
            });
            targetMenuButton.getItems().add(newMenuItem);
        }

    }




    private final StringProperty currentUserName;

    public MainSceneController() {
        currentUserName = new SimpleStringProperty(JHON_DOE);
    }

    public void updateUserName(String userName) {
        currentUserName.set(userName);
    }

    @FXML
    void findRouteButtonPresed(ActionEvent event) {
        findCycleAnchorPane.setDisable(true);
        findCycleList.setDisable(true);
        whatIfAnchorPane.setDisable(true);
        whatIfListView.setDisable(true);

        anchorForFindRoute.setDisable(false);
        findRouteList.setDisable(false);

        ToggleGroup group = new ToggleGroup();
        depOnRadioButton.setToggleGroup(group);
        reqForRadioButton.setToggleGroup(group);


        if(  ! (fromMenuButon.getItems().isEmpty() && toMenuButon.getItems().isEmpty()) )
            return;

        for (TargetDTO target: currentGraphToPresent.getTargets()) {
            MenuItem newMenuItemFrom = new MenuItem(target.getTargetName());
            MenuItem newMenuItemTo = new MenuItem(target.getTargetName());
            newMenuItemFrom.setOnAction(e->{
                srcTargetLabel.setText(target.getTargetName());
            });

            newMenuItemTo.setOnAction(e->{
                destTargetLabel.setText(target.getTargetName());
            });

            fromMenuButon.getItems().add(newMenuItemFrom);
            toMenuButon.getItems().add(newMenuItemTo);
        }


    }


    @FXML
    void findCycleExecButtonPressed(ActionEvent event) {
     getFindRouteResFromServer(targetSelectedLabel.getText(),targetSelectedLabel.getText(),
                Graph.RoutSelection.DEPENDS_ON);
        ObservableList<String> observableList;

        if(routesList.isEmpty()){
            List<String> firstList = new ArrayList<>();
            firstList.add("No cycle found.");
            observableList = FXCollections.observableList(firstList);
            findCycleList.setItems(observableList);
        }
    }


    @FXML
    void findRouteExecButtonPressed(ActionEvent event) throws InterruptedException {
        List<String> allPaths = null;
        ObservableList<String> observableList;

        if(depOnRadioButton.isSelected()) {
           getFindRouteResFromServer(srcTargetLabel.getText(),destTargetLabel.getText(),
                    Graph.RoutSelection.DEPENDS_ON);
        }
        else if(reqForRadioButton.isSelected()){
            getFindRouteResFromServer(srcTargetLabel.getText(),destTargetLabel.getText(),
                    Graph.RoutSelection.REQUIRED_FOR);
        }
    }

    private void getFindRouteResFromServer(String from, String to, Graph.RoutSelection rout) {
        String finalUrl = HttpUrl
                .parse(Constants.GET_FIND_ROUTE)
                .newBuilder()
                .addQueryParameter("graphName",currentGraphToPresent.getGraphName())
                .addQueryParameter("from", from)
                .addQueryParameter("to", to)
                .addQueryParameter("RoutSelection",rout.toString())
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
                            String[] routes = GSON_INSTANCE.fromJson(response.body().string(), String[].class);
                            setRoutesList(Arrays.asList(routes));
                            updateLists();


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                }
            }});

        }

    private void updateLists() {
        ObservableList<String> observableList;

        if(!anchorForFindRoute.isDisabled()) { // set route component list
            if (routesList.isEmpty()) {
                List<String> lst = new ArrayList<>();
                lst.add("No path found.");
                observableList = FXCollections.observableList(lst);
          //      findRouteList.getItems().clear();
                findRouteList.setItems(observableList);
                return;
            }
            observableList = FXCollections.observableList(routesList);
          //  findRouteList.getItems().clear();
            findRouteList.setItems(observableList);
        }

        else if(!findCycleAnchorPane.isDisabled()){
            if (routesList.isEmpty()) {
                List<String> lst = new ArrayList<>();
                lst.add("No cycle found.");
                observableList = FXCollections.observableList(lst);
                findCycleList.setItems(observableList);
                return;
            }
            observableList = FXCollections.observableList(routesList);
            findCycleList.setItems(observableList);
        }

    }


    List<String> routesList;

    public void setRoutesList(List<String> routesList) {
        this.routesList = routesList;
    }

    @FXML
    private Label userGreetingLabel;

    Map<String,SimpleBooleanProperty> selected = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
        userGreetingLabel.textProperty().bind(Bindings.concat("Hello ", currentUserName));
        errorLabelAddTask.textProperty().bind(errorMessagePropertyForTaskTab);

        //Targets table
        nameCol.setCellValueFactory(new PropertyValueFactory<>("targetName"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        directDepOnCol.setCellValueFactory(new PropertyValueFactory<>("directDepOn"));
        directReqForCol.setCellValueFactory(new PropertyValueFactory<>("directReqFor"));
        allDepOnCol.setCellValueFactory(new PropertyValueFactory<>("allDepOn"));
        allReqForCol.setCellValueFactory(new PropertyValueFactory<>("allReqFor"));
        freeInfoCol.setCellValueFactory(new PropertyValueFactory<>("freeInfo"));

        //

        ToggleGroup group = new ToggleGroup(),group3 = new ToggleGroup();
        compilationRadioButton.setToggleGroup(group);
        simulationRadioButton.setToggleGroup(group);

        randRadioButton.setToggleGroup(group3);
        constRadioButton.setToggleGroup(group3);

        targetNameCol.setCellValueFactory(new PropertyValueFactory<>("targetName"));

        loadTaskTab();

        loadUserList();
        usersController.startListRefresher();
        loadGraphList();
        graphsController.startListRefresher();
        loadGraphListForTaskTab();
        graphsControllerForTaskTab.startListRefresher();
        loadMissionsList();
        missionsController.startListRefresher();

        setListsOnSelection();


    }

    private void loadTaskTab() {
        URL taskTabPageUrl = getClass().getResource(TASK_TAB_FXML_RESOURCE_LOCATION);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(taskTabPageUrl);
            taskComponent = fxmlLoader.load();
            taskInfoAnchorPane.getChildren().add(taskComponent);
            taskController  = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setListsOnSelection() {
        graphListView.setCellFactory(lv -> new ListCell<GraphDTO>() {
            @Override
            protected void updateItem(GraphDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? null : item.getGraphName() );
            }
        });

        graphListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Your action here
            if (newValue == null)
                return;
            loadGraph(newValue);
        });



        //Task table in add task tab
        targetTableNewTask.setEditable(true);
        selectCol.setCellFactory(column -> new CheckBoxTableCell<>());
        selectCol.setCellValueFactory(cellData -> {
                TargetDTO cellValue = cellData.getValue();
                BooleanProperty property = selected.get(cellValue.getTargetName());
                // Add listener to handler change
                property.addListener((observable, oldValue, newValue) -> property.setValue(newValue));
                return property;
        });
        graphsControllerForTaskTab.getGraphTable().getSelectionModel().selectedItemProperty().addListener(
                        (Observable, oldValue, newValue) -> {
                if (newValue == null)
                    return;
                targetTableNewTask.getItems().clear();
                ObservableList<TargetDTO> observableList = FXCollections.observableList(newValue.getTargets());
                targetTableNewTask.setItems(observableList);
                buildTargetToBooleanMap();
                this.selectedGraphForTask = newValue.getGraphName();
            } ) ;

        //mission table in dashboard tab

    }

    private void buildTargetToBooleanMap() {
        selected.clear();
        for (TargetDTO t : targetTableNewTask.getItems()){
            selected.putIfAbsent(t.getTargetName(), new SimpleBooleanProperty());
        }
    }


    @FXML
    private AnchorPane graphListAnchorPane;

    private void loadGraphList() {
        URL graphsPageUrl = getClass().getResource(GRAPHS_FXML_RESOURCE_LOCATION);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(graphsPageUrl);
            graphsComponent = fxmlLoader.load();
            graphListAnchorPane.getChildren().add(graphsComponent);
            graphsController  = fxmlLoader.getController();
            graphsController.setListViewComponent(graphListView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGraphListForTaskTab() {
        URL graphsPageUrl = getClass().getResource(GRAPHS_FXML_RESOURCE_LOCATION);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(graphsPageUrl);
            graphsComponentForTaskTab = fxmlLoader.load();
            addTaskGraphListAnchorPane.getChildren().add(graphsComponentForTaskTab);
            graphsControllerForTaskTab  = fxmlLoader.getController();
            //graphsControllerForTaskTab.setListViewComponent(graphListViewForTaskTab);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void loadMissionsList() {
        URL missionsPageUrl = getClass().getResource(MISSIONS_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(missionsPageUrl);
            missionsComponent = fxmlLoader.load();
            missionsAnchorPane.getChildren().add(missionsComponent);
            missionsController  = fxmlLoader.getController();
            missionsController.setListViewComponent(taskController.getMissionListView());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void loadUserList() {

        URL usersPageUrl = getClass().getResource(USERS_FXML_RESOURCE_LOCATION);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(usersPageUrl);
            usersComponent = fxmlLoader.load();
            userListAnchorPane.getChildren().add(usersComponent);
            usersController  = fxmlLoader.getController();
            //chatRoomComponentController.setChatAppMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //When this method is called, it will change the Scene
    public void changeScene(ActionEvent event, String sceneFile) {
        try {
            /*Parent mainSceneParent = FXMLLoader.load(getClass().getResource(sceneFile));
            Scene mainScene = new Scene(mainSceneParent);
            //Getting the Stage information
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

            window.setScene((mainScene));
            window.show();
*/          URL PageUrl = getClass().getResource(MAIN_PAGE_FXML_RESOURCE_LOCATION);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(PageUrl);
            //usersComponent =
                    fxmlLoader.load();
            //userListAnchorPane.getChildren().add(usersComponent);
            //mainSceneController  = fxmlLoader.getController();
            //chatRoomComponentController.setChatAppMainController(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
///////////////////////////////////////EX3//////////////////////////////////////


        private Parent usersComponent;
        private Parent graphsComponent;
        private Parent graphsComponentForTaskTab;
        private Parent missionsComponent;
        private Parent taskComponent;



        @FXML private Button sourceButton;
        @FXML private Button destButton;

        @FXML void sourceButtonPressed(ActionEvent event){
            DirectoryChooser directoryChooser = new DirectoryChooser();
            //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
            File file = directoryChooser.showDialog(new Stage());
            this.sourcePath = file.getPath();
            this.labelForSourcePath.setText(sourcePath);
        }

        @FXML void destButtonPressed(ActionEvent event){
            DirectoryChooser directoryChooser = new DirectoryChooser();
            //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
            File file = directoryChooser.showDialog(new Stage());
            this.destPath = file.getPath();
            this.labelForDestPath.setText(destPath);
        }

    private String sourcePath;
    private String destPath;
@FXML
private Button uploadFileButton;


    @FXML private Label labelForSourcePath;
    @FXML private Label labelForDestPath;
    

    @FXML
    private Label errorMessageLabel;
    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    @FXML MenuButton selectGraphMenuButton;


    @FXML
    void uploadFilePressed(ActionEvent event) {


        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File file = fileChooser.showOpenDialog(new Stage());


        OkHttpClient client = new OkHttpClient().
                newBuilder()
                .build();
        MediaType media = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).
                addFormDataPart(currentUserName.getValue(), currentUserName.getValue(), RequestBody.create(MediaType.parse("application/octet-stream"),
                        file)).build();



        Request request = new Request.Builder().url(FILE_UPLOAD_PAGE).method("POST", body).build();
        HttpClientUtil.runAsyncRequest(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    errorMessageLabel.setTextFill((Paint)Color.RED);
                    errorMessageProperty.set("Something went wrong: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        errorMessageLabel.setTextFill((Paint)Color.RED);
                        errorMessageProperty.set("Something went wrong: " + responseBody);
                        System.out.println(responseBody);
                    });
                } else {

                    Platform.runLater(() -> {
                        try {
                            String res = response.body().string();
                            System.out.println(res);
                            errorMessageLabel.setTextFill((Paint)Color.GREEN);
                            errorMessageProperty.set(res);
                      //      Gson gson = new Gson();
                        //    GraphDTO graph = gson.fromJson(res, GraphDTO.class);


                          //  ArrayList<GraphDTO> lst = new ArrayList<GraphDTO>();
                            //lst.add(graph);
                            //ObservableList<GraphDTO> observableList = FXCollections.observableList(lst);
                            //updateGraphTableComponent(observableList);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }





        @Override
        public void close() throws IOException {
            usersController.close();
        }

    private Map<String,GraphDTO> myGraphs = new HashMap<>();

    void loadGraph(GraphDTO newValue) {
        getGraphFromServer(newValue.getGraphName());
    }

    private void getGraphFromServer(String graphName) {

        String finalUrl = HttpUrl
                .parse(Constants.GET_GRAPH_PAGE)
                .newBuilder()
                .addQueryParameter("graphname", graphName)
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
                    Platform.runLater(() ->{
                        errorMessageProperty.set("Something went wrong: " + responseBody);
                        System.out.println(responseBody);
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                        GraphDTO graph = GSON_INSTANCE.fromJson(response.body().string(), GraphDTO.class);
                        setCurrentGraphToPresent(graph);
                        //myGraphs.putIfAbsent(graphName,graph);

                        //labels
                            numberOfTargetsLabel.setText(Integer.toString(currentGraphToPresent.getNumOfNodes()));
                            indiNumLabel.setText(Integer.toString(currentGraphToPresent.getNumOfIndependent()));
                            leafNumLabel.setText(Integer.toString(currentGraphToPresent.getNumOfLeaf()));
                            rootNumLabel.setText(Integer.toString(currentGraphToPresent.getNumOfRoot()));
                            midNumLabel.setText(Integer.toString(currentGraphToPresent.getNumOfMiddle()));

                            targetTable.getItems().clear();
                            ObservableList<TargetDTO> observableList = FXCollections.observableList(currentGraphToPresent.getTargets());

                            fromMenuButon.getItems().clear();
                            toMenuButon.getItems().clear();
                       //     findRouteList.getItems().clear();

                            anchorForFindRoute.setDisable(true);
                            findCycleAnchorPane.setDisable(true);
                            whatIfAnchorPane.setDisable(true);

                            targetTable.setItems(observableList);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                }
            }
        });
    }




}
