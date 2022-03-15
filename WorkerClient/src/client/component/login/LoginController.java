package client.component.login;

import client.component.main.mainSceneController;
import client.util.Constants;
import client.util.http.HttpClientUtil;

import client.util.http.HttpClientUtil;
import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import sun.awt.HKSCS;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class LoginController implements Initializable {
    private static final String USER = "User";
    FileChooser fileChooser = new FileChooser();
    private static final int MAX_THREADS = 5;


    @FXML private TextArea alertTextArea;
    @FXML private Label errorMessageLabel;
    @FXML private TextField userNameTextField;
    @FXML private MenuButton threadNumMenu;


    @FXML
    void loginPressed(ActionEvent event) {
        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }
        if(threadNumMenu.getText().equals("-")){
            errorMessageProperty.set("You have not selected the number of threads your`e willing to allocate");
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .addQueryParameter("role",USER)
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
                            System.out.println(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        changeScene(event, Constants.MAIN_PAGE_FXML_RESOURCE_LOCATION);
                    });
                }
            }
        });
    }




    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        initThreadMenu();
    }

    private void initThreadMenu() {

        for (int i = 0; i < MAX_THREADS; i++) {
            MenuItem newMenuItem = new MenuItem(String.valueOf(i + 1));
            int currentI = i;
            newMenuItem.setOnAction(e -> {
                threadNumMenu.setText((String.valueOf(currentI + 1)));
                return;
            });
            threadNumMenu.getItems().add(newMenuItem);
        }
    }

    //When this method is called, it will change the Scene
    public void changeScene(ActionEvent event, String sceneFile){
        try {
            /*
*/

            URL PageUrl = getClass().getResource(Constants.MAIN_PAGE_FXML_RESOURCE_LOCATION);

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(PageUrl);
            Parent mainSceneParent =  fxmlLoader.load();;

            Scene mainScene = new Scene(mainSceneParent);
            //Getting the Stage information
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            mainSceneController mainCont = fxmlLoader.getController();
            mainCont.updateUserName(userNameTextField.getText());
            mainCont.setNumberOfThreads(Integer.parseInt(threadNumMenu.getText()));
            window.setScene((mainScene));
            window.show();




        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
