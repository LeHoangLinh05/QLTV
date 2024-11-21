package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import models.DB;
import models.User;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
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
    private Button printButton;
    @FXML
    private Button addUserButton;
    @FXML
    private AnchorPane usermanagement_anchorpane;
    @FXML
    private ComboBox<String> sortByComboBox;


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

    private void sortTableByCriteria(String criteria) {
        Comparator<User> comparator = null;

        switch (criteria) {
            case "ID":
                comparator = Comparator.comparing(User::getId, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case "Name":
                comparator = Comparator.comparing(User::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                break;
            case "Date of Birth":
                comparator = Comparator.comparing(
                        User::getDateOfBirth,
                        Comparator.nullsLast(Comparator.naturalOrder()) // Sắp xếp null ở cuối
                );
                break;
            case "Email":
                comparator = Comparator.comparing(
                        User::getEmail,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER) // Sắp xếp email null ở cuối
                );
                break;
            default:
                return; // Không làm gì nếu tiêu chí không hợp lệ
        }

        if (comparator != null) {
            FXCollections.sort(userList, comparator);
            tableView.refresh();
        }
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
                    setAlignment(Pos.CENTER);
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

            return user.getName().toLowerCase().contains(searchQuery);
        });
    }


    @FXML
    private void handlePrintButton() {
        try {
            // Tạo Workbook và Sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("User List");

            // Tạo hàng tiêu đề
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID Number");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Date of Birth");
            headerRow.createCell(3).setCellValue("Email");

            // Ghi dữ liệu từ userList vào Sheet
            int rowIndex = 1;
            for (User user : userList) {
                Row dataRow = sheet.createRow(rowIndex++);
                dataRow.createCell(0).setCellValue(user.getId());
                dataRow.createCell(1).setCellValue(user.getName());
                dataRow.createCell(2).setCellValue(user.getDateOfBirth()); // Thay bằng thông tin liên lạc nếu có
                dataRow.createCell(3).setCellValue(user.getEmail()); // Thay bằng số ID nếu có
            }

            // Tự động căn chỉnh cột
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            // Tạo file tạm để lưu workbook
            File tempFile = File.createTempFile("UserList", ".xlsx");
            try (FileOutputStream fileOut = new FileOutputStream(tempFile)) {
                workbook.write(fileOut);
            }
            workbook.close();

            // Mở file Excel vừa tạo bằng ứng dụng mặc định (Microsoft Excel)
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(tempFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Hiển thị thông báo lỗi
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Export Failed");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Failed to export user list to Excel.");
            errorAlert.showAndWait();
        }
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
