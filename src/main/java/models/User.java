package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class User {
    private int id;
    private String name;
    private String dateOfBirth;
    private String email;
    private String username;                // Username
    private String password;
    private CheckBox selected;
    private StringProperty imagePath; // Property for storing the image path

    // Constructor
    public User(int id, String name, String dateOfBirth, String email, String username, String password, String imagePath) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.username = username;
        this.password = password;
        this.selected = new CheckBox(); // Initialize the CheckBox
        this.imagePath = new SimpleStringProperty(imagePath); // Initialize the StringProperty
    }

    public User(int id, String name, String dateOfBirth, String email, String username, String password) {
        this(id, name, dateOfBirth, email, username, password, ""); // Default avatarPath to an empty string
    }


    // Getter and setter for imagePath
    public String getImagePath() {
        return imagePath.get();
    }

    public void setImagePath(String imagePath) {
        this.imagePath.set(imagePath);
    }

    public StringProperty imagePathProperty() {
        return imagePath;
    }

    // Other getters and setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public void setName(String text) {
        this.name = text;
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
}
