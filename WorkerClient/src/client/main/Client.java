package client.main;

import client.util.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application{
        @Override
        public void start(Stage primaryStage) throws Exception {
            primaryStage.setTitle("GPUP-Worker");
            Parent rootContainer = FXMLLoader.load(getClass().getResource(Constants.LOGIN_PAGE_FXML_RESOURCE_LOCATION));
            primaryStage.setScene(new Scene(rootContainer, 600, 400));
            primaryStage.show();
        }

        public static void main(String[] args) {
            Thread.currentThread().setName("Main");
            launch(args);
        }
}
