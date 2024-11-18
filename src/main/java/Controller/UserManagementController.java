package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx. scene. layout. VBox;
import models.DB;
import models.User;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static models.DB.updateUser;

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
    private TableColumn<User, Void> editColumn;
    @FXML
    private TableColumn<User, Boolean> checkBoxColumn;
    @FXML
    private Button deleteButton;
    @FXML
    private TextField searchBar;
    @FXML
    private Button searchButton;
    @FXML
    private Button printButton;
    @FXML
    private Button addUserButton;

    private String username;
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private FilteredList<User> filteredList;

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

        filteredList = new FilteredList<>(userList, p -> true);

        // Add action handler to the search button
        //searchButton.setOnAction(event -> filterUserList());
        printButton.setOnAction(event -> handlePrintButton());

        loadUserData();
        addUserButton.setOnAction(event -> handleAddUserButton());
        tableView.setItems(filteredList);
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> filterUserList());
        userList.forEach(user -> user.getSelected().selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateDeleteButtonVisibility();
        }));
        updateDeleteButtonVisibility();
        addEditButtonToTable();
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
        System.out.println("Attempting to delete user with ID: " + userId);
        try {
            DB.deleteUser(userId);
            System.out.println("Successfully deleted user with ID: " + userId);
        } catch (SQLException e) {
            System.err.println("Failed to delete user with ID: " + userId);
            e.printStackTrace();
        }
    }

    private void loadUserData() {
        refreshTable();
        try {

            ResultSet rs = DB.getAllUsers();

            while (rs.next()) {
                int id = rs.getInt("id");
                String fName = rs.getString("fName");
                String lName = rs.getString("lName");
                String dateOfBirth = rs.getString("date_of_birth");
                String email = rs.getString("email");
                String imagePath = rs.getString("avatar_path");

                User user = new User(id, fName + " " + lName, dateOfBirth, email, "", "", imagePath);
                user.getSelected().selectedProperty().addListener((observable, oldValue, newValue) -> {
                    updateDeleteButtonVisibility();
                });

                userList.add(user);
                System.out.println("Loaded user: " + user.getName()); // Debugging print
            }
            rs.close();
            tableView.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableView.setItems(userList);

    }

    private void addEditButtonToTable() {
        editColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button();

            {
                Image pencilImage = new Image(getClass().getResourceAsStream("/images/managebook_button.png"));
                ImageView pencilIcon = new ImageView(pencilImage);
                pencilIcon.setFitWidth(19);  // Adjust the width of the icon
                pencilIcon.setFitHeight(19); // Adjust the height of the icon
                pencilIcon.setPreserveRatio(true);
                editButton.setGraphic(pencilIcon);
                editButton.getStyleClass().add("edit-button");

                editButton.setOnAction(event -> {
                    User selectedUser = getTableView().getItems().get(getIndex());
                    boolean isEdited = EditUserDialogController.openEditDialog(selectedUser);

                    if (isEdited) {
                        try {
                            updateUser(selectedUser);
                            tableView.refresh();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    tableView.refresh();
                });
            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });
    }

    private void refreshTable() {
        userList.clear();

    }

    private void filterUserList() {
        String searchQuery = searchBar.getText().toLowerCase();

        filteredList.setPredicate(user -> {
            if (searchQuery == null || searchQuery.isEmpty()) {
                return true;
            }

            // Match user name or email with the search query
            return user.getName().toLowerCase().contains(searchQuery);
        });
    }



    @FXML
    private void handlePrintButton() {
        // Create a VBox to hold the header and user data
        VBox content = new VBox(10); // Spacing between rows
        content.setStyle("-fx-padding: 20;"); // Add some padding

        // Add the title
        Label title = new Label("Users");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 0 0 20 0;");
        content.getChildren().add(title);

        // Add the header row
        HBox headerRow = new HBox(20); // Spacing between columns
        headerRow.setStyle("-fx-font-weight: bold; -fx-border-width: 0 0 2 0; -fx-border-color: black;");
        headerRow.getChildren().addAll(
                createCell("Membership Number", 150),
                createCell("Name", 200),
                createCell("Contact", 150),
                createCell("ID Number", 150)
        );
        content.getChildren().add(headerRow);

        // Add user data rows
        for (User user : userList) {
            HBox dataRow = new HBox(20); // Spacing between columns
            dataRow.getChildren().addAll(
                    createCell(String.valueOf(user.getId()), 150),
                    createCell(user.getName(), 200),
                    createCell("N/A", 150), // Replace with actual contact info if available
                    createCell("N/A", 150)  // Replace with actual ID number if available
            );
            content.getChildren().add(dataRow);
        }

        // Create a PrinterJob
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        if (printerJob != null) {
            boolean proceed = printerJob.showPrintDialog(tableView.getScene().getWindow()); // Show print dialog

            if (proceed) {
                boolean success = printerJob.printPage(content); // Print the VBox content
                if (success) {
                    printerJob.endJob();
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Print Successful");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("User list printed successfully.");
                    successAlert.showAndWait();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Print Failed");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Failed to print the user list.");
                    errorAlert.showAndWait();
                }
            }
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Print Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Could not create a printer job.");
            errorAlert.showAndWait();
        }
    }

    // Helper method to create a styled cell
    private Label createCell(String text, double width) {
        Label cell = new Label(text);
        cell.setPrefWidth(width);
        cell.setStyle("-fx-padding: 5; -fx-border-width: 0 0 1 0; -fx-border-color: lightgray;");
        return cell;
    }

    @FXML
    private void handleAddUserButton() {
        User newUser = AddUserDialogController.openAddDialog();

        if (newUser != null) {

            userList.add(newUser);

            newUser.getSelected().selectedProperty().addListener((observable, oldValue, newValue) -> {
                updateDeleteButtonVisibility();
            });
            tableView.refresh();
            searchBar.clear();

        }
        tableView.refresh();
    }




}
