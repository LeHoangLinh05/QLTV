package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

import static java.awt.SystemColor.text;

public abstract class User {
    private int id;
    private String fname;
    private String lname;
    private String dateOfBirth;
    private String email;
    private String username;
    private String password;
    private CheckBox selected;
    private StringProperty imagePath;

    public User(int id, String fname, String lname, String dateOfBirth, String email, String username, String password, String imagePath) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.username = username;
        this.password = password;
        this.selected = new CheckBox();
        this.imagePath = new SimpleStringProperty(imagePath);
    }

    public User(int id, String fname, String lname, String dateOfBirth, String email, String username, String password) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.username = username;
        this.password = password;
        this.selected = new CheckBox();
        this.imagePath = new SimpleStringProperty();
    }

    public User(String username, String password, String fname, String lname) {
        this.username = username;
        this.password = password;
        this.fname = fname;
        this.lname = lname;
        this.selected = new CheckBox();
        this.imagePath = new SimpleStringProperty();
    }

    public User(int id, String fname, String lname) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.dateOfBirth = "";
        this.email = "";
        this.username = "";
        this.password = "";
        this.selected = new CheckBox();
        this.imagePath = new SimpleStringProperty();
    }

    public User(String username, String password) {
        this.id = Integer.parseInt(null);
        this.fname = "";
        this.lname = "";
        this.dateOfBirth = "";
        this.email = "";
        this.username = username;
        this.password = password;
        this.selected = new CheckBox();
        this.imagePath = new SimpleStringProperty();
    }

    public String getImagePath() {
        return imagePath.get();
    }

    public void setImagePath(String imagePath) {
        this.imagePath.set(imagePath);
    }

    public StringProperty imagePathProperty() {
        return imagePath;
    }

    public int getId() {
        return id;
    }

    public String getFName() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CheckBox getSelected() {
        return selected;
    }

    public void setSelected(boolean isSelected) {
        this.selected.setSelected(isSelected);
    }

    public boolean isSelected() {
        return selected.isSelected();
    }

    public void setFName(String text) {
        this.fname = text;
    }


    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(int generatedId) {
        this.id = generatedId;
    }

    public String getName() {
        return fname + " " + lname;
    }

    public abstract String getRole();


    public void setLname(String text) {
        this.lname = text;
    }
}
