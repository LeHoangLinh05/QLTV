package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import models.ActivityLog;
import models.DB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class DashboardController implements Initializable {

    @FXML
    private AnchorPane dashboard_anchorpane;

    @FXML
    private Circle avatar_circle;

    @FXML
    private AnchorPane main_pane;

    @FXML
    private ScrollPane stat_scrollpane;

    @FXML
    private Pane pane_1;

    @FXML
    private Pane pane_2;

    @FXML
    private Pane pane_3;

    @FXML
    private Pane pane_4;

    @FXML
    private AnchorPane activityLogPane;

    @FXML
    private BarChart<String, ?> barChart;

    @FXML
    private Label label_adminName;

    @FXML
    private Label label_accType;

    @FXML
    private ImageView avatar;

    @FXML
    private Label numOfBooks;

    @FXML
    private Label numOfUsers;

    @FXML
    private Label numOfLoans;

    @FXML
    private VBox logVBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        barChart();
        setNumOfBooks();
        setNumOfUsers();
        setNumOfLoans();
        loadActivityLog();
        Circle clip = new Circle(50); // Adjust radius as needed
        clip.setCenterX(72); // Center X coordinate
        clip.setCenterY(70); // Center Y coordinate
        avatar.setClip(clip);
    }


    public void setAdminInfo(String firstName, String lastName, String username, String role, String avatar_path){
        label_adminName.setText( firstName + " " + lastName );
        if (Objects.equals(role, "Admin")) {
            label_accType.setText("Admin");
        }
        if (Objects.equals(role, "User")) {
            label_accType.setText("User");
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

    public void setNumOfBooks() {
        int count = 0;

        String query = "SELECT COUNT(*) AS total FROM books";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        numOfBooks.setText(String.valueOf(count));
    }

    public void setNumOfUsers() {
        int count = 0;

        String query = "SELECT COUNT(*) AS total FROM userdetail";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        numOfUsers.setText(String.valueOf(count));
    }

    public void setNumOfLoans() {
        int count = 0;

        String query = "SELECT COUNT(*) AS total FROM loans";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        numOfLoans.setText(String.valueOf(count));
    }

    public void barChart() {
        Series series1 = new Series<>();
        series1.setName("Borrowings");

        Series series2 = new Series<>();
        series2.setName("Returnings");

        Map<String, Integer> borrowData = DB.getBorrowData();
        Map<String, Integer> returnData = DB.getReturnData();

        // Danh sách các ngày trong tuần
        List<String> daysOfTheWeek = Arrays.asList("Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday");

        for (String day : daysOfTheWeek) {
            series1.getData().add(new XYChart.Data<>(day, borrowData.getOrDefault(day, 0)));
            series2.getData().add(new XYChart.Data<>(day, returnData.getOrDefault(day, 0)));
        }

        // Thêm dữ liệu vào biểu đồ
        barChart.getData().clear();
        barChart.getData().add(series1);
        barChart.getData().add(series2);
    }

    public void loadActivityLog() {
        List<ActivityLog> logs = DB.fetchActivityLog();

        try {
            logVBox.getChildren().clear();

            for (ActivityLog log : logs) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/LogCard.fxml"));
                HBox logBox = fxmlLoader.load();
                ActivityLogController logController = fxmlLoader.getController();
                logController.setData(log);

                logVBox.getChildren().add(logBox);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

