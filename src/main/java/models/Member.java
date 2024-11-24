package models;

import java.util.List;
import java.util.ArrayList;

public class Member  {
    private String address;
    private List<Loan> returnHistory;
    private String name;
    private String memberId;

    public Member() {
        returnHistory = new ArrayList<>();
    }

    public Member(String name, String memberId) {
        this.name = name;
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public Member(String userId, String name, String email, String phoneNumber, String address) {
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

    public String getMemberId() {return memberId;}

    public void setMemberId(String memberId) {this.memberId = memberId;}
}
