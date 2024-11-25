package models;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class profileTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the main FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Profile.fxml"));
            Parent root = loader.load();

            // Set up the scene with the loaded FXML file
            Scene scene = new Scene(root, 920, 700); // Adjust the dimensions as needed
            primaryStage.setTitle("Library Management System"); // Set the title of the window
            primaryStage.setScene(scene); // Set the scene for the primary stage
            primaryStage.show(); // Display the stage
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
