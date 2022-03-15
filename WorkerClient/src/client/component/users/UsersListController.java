package client.component.users;

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
import javafx.scene.control.cell.PropertyValueFactory;
import logic.users.User;

import java.io.Closeable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static client.util.Constants.REFRESH_RATE;


public class UsersListController implements Closeable {

    private Timer timer;
    private TimerTask listRefresher;
    private final BooleanProperty autoUpdate;
    private final IntegerProperty totalUsers;

    @FXML private TableView<User> userTableView;
    @FXML private TableColumn<User,String> usernameCol;
    @FXML private TableColumn<User,String> roleCol;
    @FXML private Label chatUsersLabel;

    public UsersListController() {
        autoUpdate = new SimpleBooleanProperty();
        autoUpdate.setValue(true);
        totalUsers = new SimpleIntegerProperty();
    }

    @FXML
    public void initialize() {
        chatUsersLabel.textProperty().bind(Bindings.concat("Users: (", totalUsers.asString(), ")"));

        //table
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
    }


    public BooleanProperty autoUpdatesProperty() {
        return autoUpdate;
    }

    private void updateUsersList(List<User> usersNames) {
        Platform.runLater(() -> {
            ObservableList<User> items = userTableView.getItems();
            items.clear();
            items.addAll(usersNames);
            userTableView.setItems(items);
            totalUsers.set(usersNames.size());
        });
    }

    public void startListRefresher() {
        listRefresher = new UserListRefresher(
                autoUpdate,
                //httpStatusUpdate::updateHttpLine,
                this::updateUsersList);
        timer = new Timer();
        timer.schedule(listRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    @Override
    public void close() {
        userTableView.getItems().clear();
        totalUsers.set(0);
        if (listRefresher != null && timer != null) {
            listRefresher.cancel();
            timer.cancel();
        }
    }
}
