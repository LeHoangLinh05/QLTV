package controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import models.ActivityLog;
import javafx.fxml.FXML;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

/**
 * Controller class for managing the activity log view.
 */
public class ActivityLogController implements Initializable {

    @FXML
    private Label activity_text;

    @FXML
    private Label date_text;

    @FXML
    private HBox logBox;

    /**
     * Initializes the controller class.
     *
     * @param url the location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle the resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    /**
     * Sets the data for the activity log.
     *
     * @param log the activity log to be displayed.
     */
    public void setData(ActivityLog log) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = dateFormat.format(log.getDate());

        date_text.setText(formattedDate);
        activity_text.setText(log.getActivity());
    }
}
