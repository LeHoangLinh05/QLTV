package models;

public abstract class User {
    protected String name;
    protected String email;
    protected String phonenumber;
    protected String role;

    public User(String name) {
        this.name = name;
    }

    public User(String name, String email, String phonenumber) {
        this.name = name;
        this.email = email;
        this.phonenumber = phonenumber;
    }

    // Abstract method to be implemented by subclasses (NormalUser and Admin)
    public abstract void menu(DB database, User user);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getRole() {
        return role;
    }

    protected void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return name + "<N/>" + email + "<N/>" + phonenumber + "<N/>" + role;
    }
}
