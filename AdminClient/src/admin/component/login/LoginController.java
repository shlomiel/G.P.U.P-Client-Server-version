package admin.component.login;

import admin.component.main.MainSceneController;
import admin.util.Constants;
import admin.util.http.HttpClientUtil;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable {
    private static final String ADMIN = "Admin";
    FileChooser fileChooser = new FileChooser();


    @FXML private TextArea alertTextArea;
    @FXML private Label errorMessageLabel;
    @FXML private TextField userNameTextField;


    @FXML
    void loginPressed(ActionEvent event) {
        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .addQueryParameter("role",ADMIN)
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
                        //TO DO : update user name
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
            MainSceneController mainCont = fxmlLoader.getController();
            mainCont.updateUserName(userNameTextField.getText());
            window.setScene((mainScene));
            window.show();




        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
