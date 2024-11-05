package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.net.URL;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        barChart();
        setUserAvatar();

    }

    public void setUserAvatar() {
        Image im = new Image(getClass().getResource("/images/avatar_img.png").toExternalForm(), false);
        avatar_circle.setFill(new ImagePattern(im));
    }

    public void setAdminInfo(String firstName, String lastName, String username){
        label_adminName.setText( firstName + " " + lastName );
        label_accType.setText("      Admin");
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

