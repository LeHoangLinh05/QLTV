package services;

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

    public int countLoanRecords() throws SQLException {
        return loanRepository.countLoanRecords();
    }

    public ObservableList<Loan> getLoansByMemberId(int memberId) throws SQLException {
        return loanRepository.getLoansByMemberId(memberId);
    }

    public boolean updateLoanAfterReturned(int memberId, int bookId) throws SQLException {
        return loanRepository.updateLoan(memberId, bookId);
    }

    public boolean createNewLoan(Loan loan) throws SQLException {
        return loanRepository.createLoan(loan.getMember().getId(), loan.getBook().getId(), LocalDate.now(), loan.getDueDate());
    }

    public List<Loan> getBorrowingLoans(int memberId) throws SQLException {
        return loanRepository.getBorrowingLoansByMemberId(memberId);
    }

    public List<Loan> getReturnedLoans(int memberId) throws SQLException {
        return loanRepository.getReturnedLoansByMemberId(memberId);
    }

    public void updateBookQuantityAfterBorrow(Book book) throws SQLException {
        loanRepository.updateQuantityAfterBorrow(book);
    }

    public void updateBookQuantityAfterReturn(Book book) throws SQLException {
        loanRepository.updateQuantityAfterReturn(book);
    }

    public boolean isBookAvailable(Book book) throws SQLException {
        int quantity = loanRepository.checkBookQuantity(book);
        if (quantity < 1) return false;
        return true;
    }

    public List<ActivityLog> getActivityLogs() throws SQLException {
        return loanRepository.fetchActivityLog();
    }

    public Map<String, Integer> getBorrowData() {
        try {
            return loanRepository.getBorrowData();
        } catch (Exception e) {
            System.out.println("Error in LoanService while fetching borrow data: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    public Map<String, Integer> getReturnData() {
        try {
            return loanRepository.getReturnData();
        } catch (Exception e) {
            System.out.println("Error in LoanService while fetching return data: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

}
