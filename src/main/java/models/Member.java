package models;

public class Member extends User {
    private String address;

    public Member() {}

    public Member(String userId, String name, String email, String phoneNumber, String address) {
        super(userId, name, email, phoneNumber);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
