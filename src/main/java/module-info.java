module models {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.formsfx;
    requires java.sql;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires org.apache.poi.ooxml;
    requires java.desktop;
    exports models;
    opens models to javafx.fxml;
    exports controller;
    opens controller to javafx.fxml;
}