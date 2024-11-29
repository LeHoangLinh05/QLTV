package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import models.ActivityLog;
import models.Admin;
import repository.BookRepository;
import repository.LoanRepository;
import repository.UserRepository;
import services.BookService;
import services.LoanService;
import services.UserService;
import ui_helper.CardHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

/**
 * Controller class for managing the dashboard view.
 */
public class DashboardController implements Initializable {

    @FXML
    private AnchorPane dashboard_anchorpane;

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

    private UserService userService;
    private BookService bookService;
    private LoanService loanService;
    private static final BookRepository bookRepository = new BookRepository();
    private static final UserRepository userRepository = new UserRepository();
    private static final LoanRepository loanRepository = new LoanRepository();

    /**
     * Initializes the controller class.
     *
     * @param url the location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle the resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.userService = new UserService(userRepository);
        this.bookService = new BookService(bookRepository);
        this.loanService = new LoanService(loanRepository);

        barChart();
        try {
            setNumOfBooks();
            setNumOfUsers();
            setNumOfLoans();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            loadActivityLog();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Circle clip = new Circle(50);
        clip.setCenterX(72);
        clip.setCenterY(70);
        avatar.setClip(clip);
    }

    /**
     * Sets the admin information on the dashboard.
     *
     * @param admin the admin whose information is to be displayed.
     */
    public void setAdminInfo(Admin admin) {
        label_adminName.setText(admin.getFName() + " " + admin.getLname());
        label_accType.setText("Admin");
        String avatarPath = admin.getImagePath();
        System.out.println(avatarPath);
        if (avatarPath != null && !avatarPath.isEmpty()) {
            File avatarFile = new File(avatarPath);

            if (avatarFile.exists()) {
                try {
                    Image avatarImage = new Image(new FileInputStream(avatarFile));
                    avatar.setImage(avatarImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("Avatar file not found at path: " + avatarPath);
                }
            } else {
                System.out.println("Avatar path does not exist: " + avatarPath);
            }
        } else {
            System.out.println("Avatar path is empty or null.");
        }
    }

    /**
     * Sets the number of books on the dashboard.
     *
     * @throws SQLException if a database access error occurs.
     */
    public void setNumOfBooks() throws SQLException {
        int count = bookService.countBookRecords();
        numOfBooks.setText(String.valueOf(count));
    }

    /**
     * Sets the number of users on the dashboard.
     *
     * @throws SQLException if a database access error occurs.
     */
    public void setNumOfUsers() throws SQLException {
        int count = userService.countUserRecords();
        numOfUsers.setText(String.valueOf(count));
    }

    /**
     * Sets the number of loans on the dashboard.
     *
     * @throws SQLException if a database access error occurs.
     */
    public void setNumOfLoans() throws SQLException {
        int count = loanService.countLoanRecords();
        numOfLoans.setText(String.valueOf(count));
    }

    /**
     * Configures the bar chart with borrowing and returning data.
     */
    public void barChart() {
        Series series1 = new Series<>();
        series1.setName("Borrowings");

        Series series2 = new Series<>();
        series2.setName("Returnings");

        Map<String, Integer> borrowData = loanService.getBorrowData();
        Map<String, Integer> returnData = loanService.getReturnData();

        List<String> daysOfTheWeek = Arrays.asList("Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday");

        for (String day : daysOfTheWeek) {
            series1.getData().add(new XYChart.Data<>(day, borrowData.getOrDefault(day, 0)));
            series2.getData().add(new XYChart.Data<>(day, returnData.getOrDefault(day, 0)));
        }

        barChart.getData().clear();
        barChart.getData().add(series1);
        barChart.getData().add(series2);
    }

    /**
     * Loads the activity log into the dashboard.
     *
     * @throws SQLException if a database access error occurs.
     */
    public void loadActivityLog() throws SQLException {
        List<ActivityLog> logs = loanService.getActivityLogs();

        try {
            logVBox.getChildren().clear();

            for (ActivityLog log : logs) {
                HBox logBox = CardHelper.displayLogCard(log);
                logVBox.getChildren().add(logBox);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

