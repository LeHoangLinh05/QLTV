package controller;

import exceptions.InvalidDataException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx. scene. image. ImageView;
import models.*;
import repository.UserRepository;
import services.LoanService;
import services.UserService;
import ui_helper.AlertHelper;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class for managing the edit user dialog.
 */
public class EditUserDialogController implements Initializable {
    @FXML
    private TextField nameField;
    @FXML
    private TextField dobField;
    @FXML
    private TextField emailField;
    @FXML
    private ImageView profileImageView;
    @FXML
    private TableView<Loan> loanTable;

    @FXML
    private TableColumn<Loan, String> loanIdColumn;

    @FXML
    private TableColumn<Loan, String> bookIdColumn;

    @FXML
    private TableColumn<Loan, String> issueDateColumn;

    @FXML
    private TableColumn<Loan, String> dueDateColumn;

    @FXML
    private TableColumn<Loan, String> returnDateColumn;
    @FXML
    private AnchorPane rootPane;

    private User user;
    private ObservableList<Loan> loanList = FXCollections.observableArrayList();
    private boolean isSaved = false;
    private Admin admin;

    /**
     * Initializes the controller class.
     *
     * @param url the location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle the resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loanIdColumn.setCellValueFactory(data -> data.getValue().loanIdProperty());
        bookIdColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBook() != null ? data.getValue().getBook().getTitle() : "Unknown"));
        issueDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIssueDate().toString()));
        dueDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDueDate().toString()));
        returnDateColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getReturnDate() != null ? data.getValue().getReturnDate().toString() : "Not Returned"));

        loanTable.setItems(loanList);
        rootPane.setOnMouseClicked(event -> {
            if (!(event.getTarget() instanceof TextField || event.getTarget() instanceof TableView)) {
                if (loanTable.getSelectionModel().getSelectedItem() != null) {
                    loanTable.getSelectionModel().clearSelection();
                }
                nameField.getParent().requestFocus();
            }
        });
    }

    /**
     * Loads the loan history of the user.
     *
     * @param user the user whose loan history is to be loaded.
     * @throws SQLException if a database access error occurs.
     */
    private void loadLoanHistory(Member user) throws SQLException {
        List<Loan> loans = user.getMemberHistory();
        loanList = FXCollections.observableArrayList(loans);
        loanTable.setItems(loanList);
        loanTable.refresh();
    }

    /**
     * Sets the user data in the dialog.
     *
     * @param user the user whose data is to be set.
     * @throws SQLException if a database access error occurs.
     */
    public void setUser(Member user) throws SQLException {
        this.user = user;
        if (user != null) {
            nameField.setText(user.getFName() + " " + user.getLname());
            dobField.setText(user.getDateOfBirth());
            emailField.setText(user.getEmail());
            loadLoanHistory(user);
            if (user.getImagePath() != null && !user.getImagePath().isEmpty()) {
                File imageFile = new File(user.getImagePath());
                if (imageFile.exists()) {
                    profileImageView.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    System.out.println("Image file does not exist: " + user.imagePathProperty().get());
                }
            }
        }
    }

    /**
     * Checks if the user data is saved.
     *
     * @return true if the user data is saved, false otherwise.
     */
    public boolean isSaved() {
        return isSaved;
    }

    /**
     * Handles the save action.
     */
    @FXML
    private void handleSave() {
        String fullName = nameField.getText();
        String[] nameParts = fullName.split(" ");
        String fName = nameParts[0];
        String lName = nameParts.length > 1 ? nameParts[nameParts.length - 1] : "";
        user.setFName(fName);
        user.setLname(lName);
        user.setDateOfBirth(dobField.getText());
        user.setEmail(emailField.getText());
        user.setImagePath(user.getImagePath());
        try {
            UserService.hasTheRightFormat(user.getDateOfBirth());
            admin.editMember((Member) user);
            System.out.println("User updated successfully!");
            AlertHelper.showInformation("Update Successful", "User updated successfully!");

            ((Stage) nameField.getScene().getWindow()).close();
        } catch (InvalidDataException e) {
            AlertHelper.showError("Invalid Data", "Date of Birth must be in the format yyyy-MM-dd.");
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Error", "An unexpected error occurred.");
        }

        ((Stage) nameField.getScene().getWindow()).close();
    }

    /**
     * Handles the cancel action.
     */
    @FXML
    private void handleCancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    /**
     * Sets the admin data in the dialog.
     *
     * @param admin the admin whose data is to be set.
     */
    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    /**
     * Opens the edit user dialog.
     *
     * @param user the user to be edited.
     * @param admin the admin performing the edit.
     * @return true if the user data is saved, false otherwise.
     */
    public static boolean openEditDialog(Member user, Admin admin) {
        try {
            FXMLLoader loader = new FXMLLoader(EditUserDialogController.class.getResource("/view/EditUserDialog.fxml"));
            Parent root = loader.load();

            EditUserDialogController controller = loader.getController();
            controller.setUser(user);
            controller.setAdmin(admin);
            Stage stage = new Stage();
            stage.setTitle("Edit User");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            return controller.isSaved();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}