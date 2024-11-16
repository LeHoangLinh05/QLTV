package models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.CheckBox;

public class User {
    private int id;
    private String name;
    private String fname;
    private String lname;
    private String dateOfBirth;
    private String email;
    private CheckBox selected;


    public User(int id, String name, String dateOfBirth, String email) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.selected = new CheckBox(); // Initialize with unchecked
    }

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
}
