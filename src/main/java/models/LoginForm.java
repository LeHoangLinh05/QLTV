package models;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginForm extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DB.class.getResource("/view/Login.fxml"));
        Parent root = fxmlLoader.load();
        stage.setTitle("Quản lý thư viện");
        try{
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
        }catch (Exception e){
            e.printStackTrace();
        }
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}