package models;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class ReturnedLoansController {

    @FXML
    private TableView<Loan> returnedLoansTable;

    @FXML
    private TableColumn<Loan, String> titleColumn;

    @FXML
    private TableColumn<Loan, String> issueDateColumn;

    @FXML
    private TableColumn<Loan, String> dueDateColumn;

    @FXML
    private TableColumn<Loan, String> returnDateColumn;

    public void initialize() {
        // Cấu hình các cột trong bảng
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("book.title"));
        issueDateColumn.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        
        loadReturnedLoans();
    }

    private void loadReturnedLoans() {
        List<Loan> loans = LoanService.getLoans(); 

        List<Loan> returnedLoans = loans.stream()
                .filter(loan -> loan.getReturnDate() != null)
                .collect(Collectors.toList());

        
        returnedLoansTable.getItems().setAll(returnedLoans);
    }
}
