package controller;

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
import services.LoanService;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

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
    private final ObservableList<Loan> loanList = FXCollections.observableArrayList();
    private boolean isSaved = false;
    private Admin admin;

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

    private void loadLoanHistory(Member user) {
        //loanList.clear();
        //loanList.addAll(DB.getLoansByMemberId(userId));
        List<Loan> loans = user.getMemberHistory();
        loanTable.setItems((ObservableList<Loan>) loans);
        loanTable.refresh();
    }

    // Method to set the user and populate the fields
    public void setUser(Member user) {
        this.user = user;
        if (user != null) {
            nameField.setText(user.getFName() + " " + user.getLname());
            dobField.setText(user.getDateOfBirth());
            emailField.setText(user.getEmail());
            loadLoanHistory(user);
            if (user.imagePathProperty() != null && user.imagePathProperty().get() != null && !user.imagePathProperty().get().isEmpty()) {
                File imageFile = new File(user.imagePathProperty().get()); // Use .get() to get the String value
                if (imageFile.exists()) {
                    profileImageView.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    System.out.println("Image file does not exist: " + user.imagePathProperty().get());
                }
            }
        }
    }

    // Returns whether the changes were saved
    public boolean isSaved() {
        return isSaved;
    }

    @FXML
    private void handleSave() {
        user.setFName(nameField.getText());
        user.setDateOfBirth(dobField.getText());
        user.setEmail(emailField.getText());
        user.setImagePath(user.getImagePath());
        try {
            admin.editMember(user); // Update the database
            System.out.println("User updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Close the dialog
        ((Stage) nameField.getScene().getWindow()).close();
    }

    @FXML
    private void handleCancel() {
        // Close the dialog without saving
        ((Stage) nameField.getScene().getWindow()).close();
    }

    // Method to open the edit dialog
    public static boolean openEditDialog(Member user) {
        try {
            FXMLLoader loader = new FXMLLoader(EditUserDialogController.class.getResource("/view/EditUserDialog.fxml"));
            Parent root = loader.load();

            EditUserDialogController controller = loader.getController();
            controller.setUser(user);

            Stage stage = new Stage();
            stage.setTitle("Edit User");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Make it a modal dialog
            stage.showAndWait();

            return controller.isSaved();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
