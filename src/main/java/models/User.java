package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

/**
 * Abstract class representing a user in the library management system.
 */
public abstract class User {
    private int id;
    private String fname;
    private String lname;
    private String dateOfBirth;
    private String email;
    private String username;
    private String password;
    private CheckBox selected;
    private StringProperty imagePath = new SimpleStringProperty();;

    /**
     * Constructs a User with the specified details.
     *
     * @param id the ID of the user
     * @param fname the first name of the user
     * @param lname the last name of the user
     * @param dateOfBirth the date of birth of the user
     * @param email the email of the user
     * @param username the username of the user
     * @param password the password of the user
     * @param imagePath the image path of the user
     */
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

    /**
     * Constructs a User with the specified details.
     *
     * @param id the ID of the user
     * @param fname the first name of the user
     * @param lname the last name of the user
     * @param dateOfBirth the date of birth of the user
     * @param email the email of the user
     * @param username the username of the user
     * @param password the password of the user
     */
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

    /**
     * Constructs a User with the specified username, password, first name, and last name.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @param fname the first name of the user
     * @param lname the last name of the user
     */
    public User(String username, String password, String fname, String lname) {
        this.username = username;
        this.password = password;
        this.fname = fname;
        this.lname = lname;
        this.selected = new CheckBox();
        this.imagePath = new SimpleStringProperty();
    }

    /**
     * Constructs a User with the specified username, password, first name, and last name.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @param fname the first name of the user
     * @param lname the last name of the user
     * @param imagePath the avatar path of the user
     */
    public User(String username, String password, String fname, String lname, String imagePath) {
        this.username = username;
        this.password = password;
        this.fname = fname;
        this.lname = lname;
        this.selected = new CheckBox();
        this.imagePath = new SimpleStringProperty(imagePath);
    }

    /**
     * Constructs a User with the specified ID, first name, and last name.
     *
     * @param id the ID of the user
     * @param fname the first name of the user
     * @param lname the last name of the user
     */
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

    /**
     * Constructs a User with the specified ID, first name, last name and imagePath.
     *
     * @param id the ID of the user
     * @param fname the first name of the user
     * @param lname the last name of the user
     * @param imagePath the image path of the user
     */
    public User(int id, String fname, String lname, String imagePath) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.dateOfBirth = "";
        this.email = "";
        this.username = "";
        this.password = "";
        this.selected = new CheckBox();
        this.imagePath = new SimpleStringProperty(imagePath);
    }

    /**
     * Constructs a User with the specified username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     */
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

    /**
     * Gets the image path of the user.
     *
     * @return the image path
     */
    public String getImagePath() {
        return imagePath.get();
    }

    /**
     * Sets the image path of the user.
     *
     * @param imagePath the image path to set
     */
    public void setImagePath(String imagePath) {
        System.out.println("Setting imagePath: " + imagePath);
        this.imagePath.set(imagePath);
    }

    /**
     * Gets the image path property of the user.
     *
     * @return the image path property
     */
    public StringProperty imagePathProperty() {
        return imagePath;
    }

    /**
     * Gets the ID of the user.
     *
     * @return the ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the first name of the user.
     *
     * @return the first name
     */
    public String getFName() {
        return fname;
    }

    /**
     * Gets the last name of the user.
     *
     * @return the last name
     */
    public String getLname() {
        return lname;
    }

    /**
     * Gets the date of birth of the user.
     *
     * @return the date of birth
     */
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Gets the email of the user.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the username of the user.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password of the user.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the selected CheckBox of the user.
     *
     * @return the selected CheckBox
     */
    public CheckBox getSelected() {
        return selected;
    }

    /**
     * Sets the selected state of the CheckBox.
     *
     * @param isSelected the selected state to set
     */
    public void setSelected(boolean isSelected) {
        this.selected.setSelected(isSelected);
    }

    /**
     * Checks if the CheckBox is selected.
     *
     * @return true if selected, false otherwise
     */
    public boolean isSelected() {
        return selected.isSelected();
    }

    /**
     * Sets the first name of the user.
     *
     * @param text the first name to set
     */
    public void setFName(String text) {
        this.fname = text;
    }

    /**
     * Sets the date of birth of the user.
     *
     * @param dateOfBirth the date of birth to set
     */
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Sets the email of the user.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the ID of the user.
     *
     * @param generatedId the ID to set
     */
    public void setId(int generatedId) {
        this.id = generatedId;
    }

    /**
     * Gets the full name of the user.
     *
     * @return the full name
     */
    public String getName() {
        return fname + " " + lname;
    }

    /**
     * Gets the role of the user.
     *
     * @return the role
     */
    public abstract String getRole();

    /**
     * Sets the last name of the user.
     *
     * @param text the last name to set
     */
    public void setLname(String text) {
        this.lname = text;
    }
}
