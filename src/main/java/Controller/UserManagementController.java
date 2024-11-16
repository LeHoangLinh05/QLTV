package Controller;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import models.DB;
import models.User;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserManagementController implements Initializable {
    @FXML
    private TableView<User> tableView;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> dobColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private CheckBox checkBox;
    @FXML
    private TableColumn<User, Boolean> checkBoxColumn; // New column for checkboxes
    @FXML
    private Pagination pagination;
    @FXML
    private Button deleteButton; // Button for deleting users

    private String username;
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private static final int ROWS_PER_PAGE = 10; // Number of rows per page

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        checkBoxColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));

        userList.forEach(user -> user.getSelected().selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateDeleteButtonVisibility();
        }));
        loadUserData();
        updateDeleteButtonVisibility();

    }



    private void updateDeleteButtonVisibility() {
        boolean anySelected = userList.stream().anyMatch(User::isSelected);
        deleteButton.setVisible(anySelected);
    }


    @FXML
    private void handleDeleteButton(ActionEvent event) {
        // Filter the list to get selected users
        ObservableList<User> selectedUsers = FXCollections.observableArrayList();
        for (User user : userList) {
            if (user.isSelected()) { // Check if the user is selected
                selectedUsers.add(user);
            }
        }

        if (selectedUsers.isEmpty()) {
            // Show an alert if no users are selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select at least one user to delete.");
            alert.showAndWait();
            return;
        }

        // Confirm deletion with the user
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete the selected user(s)?");

        // Proceed with deletion only if the user confirms
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            for (User user : selectedUsers) {
                // Delete user from the database
                deleteUserFromDatabase(user.getId());
            }

            // Remove the selected users from the ObservableList
            userList.removeAll(selectedUsers);

            // Refresh the TableView
            tableView.refresh();

            // Update the Delete button visibility
            updateDeleteButtonVisibility();

            // Show success message
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Deletion Successful");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Selected user(s) have been deleted successfully.");
            successAlert.showAndWait();
        }
    }





    private void deleteUserFromDatabase(int userId) {
        try {
            DB.deleteUser(userId); // Assuming you have a deleteUser method in DB class
            System.out.println("Deleted user with ID: " + userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to load data from the database and populate the TableView
    private void loadUserData() {
        try {
            ResultSet rs = DB.getAllUsers();

            while (rs.next()) {
                int id = rs.getInt("id");
                String fName = rs.getString("fName");
                String lName = rs.getString("lName");
                String dateOfBirth = rs.getString("date_of_birth");
                String email = rs.getString("email");

                User user = new User(id, fName + " " + lName, dateOfBirth, email);
                user.getSelected().selectedProperty().addListener((observable, oldValue, newValue) -> {
                    updateDeleteButtonVisibility();
                });

                userList.add(user);
                System.out.println("Loaded user: " + user.getName()); // Debugging print
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableView.setItems(userList);
    }



}
