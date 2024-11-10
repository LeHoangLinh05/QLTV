package models;

import java.util.Date;
import java.util.Optional;

public class ReturnBookService {

    public static void returnBook(String loanId, Date returnDate, Library library) {
        Optional<Loan> loanOpt = library.getLoans().stream()
                .filter(loan -> loan.getLoanId().equals(loanId))
                .findFirst();

        loanOpt.ifPresent(loan -> {

            loan.setReturnDate(returnDate);

            Book book = loan.getBook();
            book.setQuantity(book.getQuantity() + 1);

            Member member = loan.getMember();
            member.addReturnHistory(loan);
        });
    }
}
