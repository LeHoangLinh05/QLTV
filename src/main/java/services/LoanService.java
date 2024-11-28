package services;

import exceptions.DatabaseException;
import javafx.collections.ObservableList;
import models.ActivityLog;
import models.Book;
import models.Loan;
import repository.LoanRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LoanService {
    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public int countLoanRecords() throws DatabaseException {
        return loanRepository.countLoanRecords();
    }

    public ObservableList<Loan> getLoansByMemberId(int memberId) throws DatabaseException, SQLException {
        return loanRepository.getLoansByMemberId(memberId);
    }

    public boolean updateLoanAfterReturned(int memberId, int bookId) throws DatabaseException, SQLException {
        return loanRepository.updateLoan(memberId, bookId);
    }

    public boolean createNewLoan(Loan loan) throws DatabaseException, SQLException {
        return loanRepository.createLoan(loan.getMember().getId(), loan.getBook().getId(), LocalDate.now(), loan.getDueDate());
    }

    public ObservableList<Loan> getBorrowingLoans(int memberId) throws DatabaseException, SQLException {
        ObservableList<Loan> loans = loanRepository.getBorrowingLoansByMemberId(memberId);
        if (loans == null) {
            throw new DatabaseException("No borrowing loans found for member with ID: " + memberId);
        }
        return loans;
    }

    public ObservableList<Loan> getReturnedLoans(int memberId) throws DatabaseException, SQLException {
        ObservableList<Loan> loans = loanRepository.getReturnedLoansByMemberId(memberId);
        if (loans == null) {
            throw new DatabaseException("No returned loans found for member with ID: " + memberId);
        }
        return loans;
    }

    public void updateBookQuantityAfterBorrow(Book book) throws DatabaseException, SQLException {
        loanRepository.updateQuantityAfterBorrow(book);
    }

    public void updateBookQuantityAfterReturn(Book book) throws DatabaseException, SQLException {
        loanRepository.updateQuantityAfterReturn(book);
    }

    public boolean isBookAvailable(Book book) throws DatabaseException, SQLException {
        int quantity = loanRepository.checkBookQuantity(book);
        if (quantity < 1) return false;
        return true;
    }

    public List<ActivityLog> getActivityLogs() throws DatabaseException {
        return loanRepository.fetchActivityLog();
    }

    public Map<String, Integer> getBorrowData() throws DatabaseException {
        try {
            return loanRepository.getBorrowData();
        } catch (Exception e) {
            System.out.println("Error in LoanService while fetching borrow data: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    public Map<String, Integer> getReturnData() throws DatabaseException {
        try {
            return loanRepository.getReturnData();
        } catch (Exception e) {
            System.out.println("Error in LoanService while fetching return data: " + e.getMessage());
            return Collections.emptyMap();
        }
    }


}
