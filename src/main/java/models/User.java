package models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.CheckBox;

public class User {
    private int id;
    private String name;
    private String dateOfBirth;
    private String email;
    private CheckBox selected;
   // private final BooleanProperty selected = new SimpleBooleanProperty(false);

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
//   public BooleanProperty selectedProperty() {
//       return selected;
//   }
    public boolean isSelected() {
        return selected.isSelected();
    }
//
//    public void setSelected(boolean selected) {
//        this.selected.set(selected);
//    }
}
