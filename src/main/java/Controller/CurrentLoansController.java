package models;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.util.stream.Collectors;

public class CurrentLoansController {

    @FXML
    private TableView<Loan> currentLoansTable;

    @FXML
    private TableColumn<Loan, String> titleColumn;

    @FXML
    private TableColumn<Loan, String> issueDateColumn;

    @FXML
    private TableColumn<Loan, String> dueDateColumn;

    public void initialize() {
       
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("book.title"));
        issueDateColumn.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        
        loadCurrentLoans();
    }

    private void loadCurrentLoans() {
        
        List<Loan> loans = LoanService.getLoans(); 

        
        List<Loan> currentLoans = loans.stream()
                .filter(loan -> loan.getReturnDate() == null)
                .collect(Collectors.toList());

        
        currentLoansTable.getItems().setAll(currentLoans);
    }
}