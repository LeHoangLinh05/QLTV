package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private AnchorPane dashboard_anchorpane;

    @FXML
    private Circle avatar_circle;

    @FXML
    private AnchorPane main_pane;

    @FXML
    private Pane pane_1;

    @FXML
    private Pane pane_2;

    @FXML
    private Pane pane_3;

    @FXML
    private Pane pane_4;

    @FXML
    private BarChart<String, ?> barChart;

    @FXML
    private Label label_adminName;

    @FXML
    private Label label_accType;

    @FXML
    private ImageView avatar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        barChart();

    }


    public void setAdminInfo(String firstName, String lastName, String username, String role, String avatar_path){
        label_adminName.setText( firstName + " " + lastName );
        if (Objects.equals(role, "Admin")) {
            label_accType.setText("      Admin");
        }
        if (Objects.equals(role, "User")) {
            label_accType.setText("       User");
        }
        if (avatar_path != null && !avatar_path.isEmpty()) {
            File avatarFile = new File(avatar_path);

            if (avatarFile.exists()) {
                try {
                    Image avatarImage = new Image(new FileInputStream(avatarFile));
                    avatar.setImage(avatarImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("Avatar file not found at path: " + avatar_path);
                }
            } else {
                System.out.println("Avatar path does not exist: " + avatar_path);
            }
        } else {
            System.out.println("Avatar path is empty or null.");
        }
    }

    public void barChart() {
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Visitors");
        series1.getData().add(new XYChart.Data("SAT", 10));
        series1.getData().add(new XYChart.Data("SUN", 20));
        series1.getData().add(new XYChart.Data("MON", 45));
        series1.getData().add(new XYChart.Data("TUE", 30));
        series1.getData().add(new XYChart.Data("WED", 25));
        series1.getData().add(new XYChart.Data("THU", 10));
        series1.getData().add(new XYChart.Data("FRI", 5));

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Borrowers");
        series2.getData().add(new XYChart.Data("SAT", 20));
        series2.getData().add(new XYChart.Data("SUN", 20));
        series2.getData().add(new XYChart.Data("MON", 55));
        series2.getData().add(new XYChart.Data("TUE", 40));
        series2.getData().add(new XYChart.Data("WED", 35));
        series2.getData().add(new XYChart.Data("THU", 20));
        series2.getData().add(new XYChart.Data("FRI", 50));

        barChart.getData().addAll(series1, series2);
    }

}

