package models;

import java.util.List;
import java.util.ArrayList;

public class Member extends User {
    private String address;
    private List<Loan> returnHistory;

    public Member() {
        returnHistory = new ArrayList<>();
    }

    public Member(String userId, String name, String email, String phoneNumber, String address) {
        super(userId, name, email, phoneNumber);
        this.address = address;
        this.returnHistory = new ArrayList<>();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Loan> getReturnHistory() {
        return returnHistory;
    }

    public void addReturnHistory(Loan loan) {
        returnHistory.add(loan);
    }
}
