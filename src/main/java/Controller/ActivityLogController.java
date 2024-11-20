package Controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import models.ActivityLog;
import javafx.fxml.FXML;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class ActivityLogController implements Initializable {

    @FXML
    private Label activity_text;

    @FXML
    private Label date_text;

    @FXML
    private HBox logBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setData(ActivityLog log) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = dateFormat.format(log.getDate());

        date_text.setText(formattedDate);
        activity_text.setText(log.getActivity());
    }
}
