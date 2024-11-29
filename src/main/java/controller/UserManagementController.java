package controller;

import exceptions.InvalidDataException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import models.Admin;
import models.Member;
import models.User;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import repository.UserRepository;
import services.UserService;
import ui_helper.AlertHelper;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.ResourceBundle;

/**
 * Controller class for managing user data in the user management view.
 */
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
    private Button printButton;
    @FXML
    private Button addUserButton;
    @FXML
    private AnchorPane usermanagement_anchorpane;
    @FXML
    private ComboBox<String> sortByComboBox;

    private Admin admin;
    private UserService userService;
    private static final UserRepository userRepository = new UserRepository();

    private String username;
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private FilteredList<User> filteredList;

    /**
     * Gets the admin.
     *
     * @return the admin
     */
    public Admin getAdmin() {
        return this.admin;
    }

    /**
     * Sets the admin.
     *
     * @param admin the admin to set
     */
    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    /**
     * Initializes the controller class.
     *
     * @param url the location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle the resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.userService = new UserService(userRepository);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFName() + " " + cellData.getValue().getLname()));

        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        checkBoxColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));

        userList.forEach(user -> user.getSelected().selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateDeleteButtonVisibility();
        }));

        filteredList = new FilteredList<>(userList, p -> true);
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
        sortByComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                sortTableByCriteria(newValue);
            }
        });
        usermanagement_anchorpane.setOnMouseClicked(event -> {
            if (!(event.getTarget() instanceof TextField || event.getTarget() instanceof TableView)) {
                if (tableView.getSelectionModel().getSelectedItem() != null) {
                    tableView.getSelectionModel().clearSelection();
                }
                searchBar.getParent().requestFocus();
            }
        });
    }

    /**
     * Sorts the table by the specified criteria.
     *
     * @param criteria the criteria to sort by
     */
    private void sortTableByCriteria(String criteria) {
        Comparator<User> comparator = null;

        switch (criteria) {
            case "ID":
                comparator = Comparator.comparing(User::getId, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case "Name":
                comparator = Comparator.comparing(User::getFName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                break;
            case "Date of Birth":
                comparator = Comparator.comparing(
                        User::getDateOfBirth,
                        Comparator.nullsLast(Comparator.naturalOrder())
                );
                break;
            case "Email":
                comparator = Comparator.comparing(
                        User::getEmail,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                );
                break;
            default:
                return;
        }

        if (comparator != null) {
            FXCollections.sort(userList, comparator);
            tableView.refresh();
        }
    }

    /**
     * Updates the visibility of the delete button based on whether any users are selected.
     */
    private void updateDeleteButtonVisibility() {
        boolean anySelected = userList.stream().anyMatch(User::isSelected);
        deleteButton.setVisible(anySelected);
    }

    /**
     * Handles the delete button action.
     *
     * @param event the action event
     */
    @FXML
    private void handleDeleteButton(ActionEvent event) {
        ObservableList<User> selectedUsers = FXCollections.observableArrayList();
        for (User user : userList) {
            if (user.isSelected()) {
                selectedUsers.add(user);
            }
        }

        if (selectedUsers.isEmpty()) {
            AlertHelper.showWarning("No Selection", "Please select at least one user to delete.");
            return;
        }

        boolean isConfirmed = AlertHelper.showConfirmation("Confirm Deletion", "Are you sure you want to delete the selected user(s)?");

        if (isConfirmed) {
            for (User user : selectedUsers) {
                deleteUserFromDatabase(user.getId());
            }

            userList.removeAll(selectedUsers);
            tableView.refresh();
            updateDeleteButtonVisibility();
            AlertHelper.showInformation("Deletion Successful", "Selected user(s) have been deleted successfully.");
        }
    }

    /**
     * Deletes a user from the database.
     *
     * @param userId the ID of the user to delete
     */
    private void deleteUserFromDatabase(int userId) {
        System.out.println("Attempting to delete user with ID: " + userId);
        try {
            admin.removeMember(userId);
        } catch (InvalidDataException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Successfully deleted user with ID: " + userId);
    }

    /**
     * Loads user data from the database.
     */
    private void loadUserData() {
        refreshTable();
        try {
            ResultSet rs = userService.getAllUsers();

            while (rs.next()) {
                int id = rs.getInt("id");
                String fName = rs.getString("fName");
                String lName = rs.getString("lName");
                String dateOfBirth = rs.getString("date_of_birth");
                String email = rs.getString("email");
                String imagePath = rs.getString("avatar_path");

                User user = new Member(id, fName, lName, dateOfBirth, email, "", "");
                user.getSelected().selectedProperty().addListener((observable, oldValue, newValue) -> {
                    updateDeleteButtonVisibility();
                });

                userList.add(user);
                System.out.println("Loaded user: " + user.getFName() + " " + user.getLname()); // Debugging print
            }
            rs.close();
            tableView.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableView.setItems(userList);

    }

    /**
     * Adds an edit button to the table.
     */
    private void addEditButtonToTable() {
        editColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button();

            {
                Image pencilImage = new Image(getClass().getResourceAsStream("/images/managebook_button.png"));
                ImageView pencilIcon = new ImageView(pencilImage);
                pencilIcon.setFitWidth(19);
                pencilIcon.setFitHeight(19);
                pencilIcon.setPreserveRatio(true);
                editButton.setGraphic(pencilIcon);
                editButton.getStyleClass().add("edit-button");

                editButton.setOnAction(event -> {
                    Member selectedUser = (Member) getTableView().getItems().get(getIndex());
                    boolean isEdited = EditUserDialogController.openEditDialog(selectedUser,admin);

                    if (isEdited) {
                        UserRepository.updateUser(selectedUser);
                        tableView.refresh();
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
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    /**
     * Refreshes the table by clearing the user list.
     */
    private void refreshTable() {
        userList.clear();
    }

    /**
     * Filters the user list based on the search query.
     */
    private void filterUserList() {
        String searchQuery = searchBar.getText().toLowerCase();

        filteredList.setPredicate(user -> {
            if (searchQuery == null || searchQuery.isEmpty()) {
                return true;
            }

            return user.getFName().toLowerCase().contains(searchQuery) || user.getLname().toLowerCase().contains(searchQuery);
        });
    }

    /**
     * Handles the print button action.
     */
    @FXML
    private void handlePrintButton() {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("User List");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID Number");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Date of Birth");
            headerRow.createCell(3).setCellValue("Email");

            int rowIndex = 1;
            for (User user : userList) {
                Row dataRow = sheet.createRow(rowIndex++);
                dataRow.createCell(0).setCellValue(user.getId());
                dataRow.createCell(1).setCellValue(String.valueOf(user.getFName() + " " + user.getLname()));
                dataRow.createCell(2).setCellValue(user.getDateOfBirth());
                dataRow.createCell(3).setCellValue(user.getEmail());
            }

            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            File tempFile = File.createTempFile("UserList", ".xlsx");
            try (FileOutputStream fileOut = new FileOutputStream(tempFile)) {
                workbook.write(fileOut);
            }
            workbook.close();

            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(tempFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Export Failed", "Failed to export user list to Excel.");
        }
    }

    /**
     * Handles the add user button action.
     */
    @FXML
    private void handleAddUserButton() {
        User newUser = AddUserDialogController.openAddDialog(admin);

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