module com {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.formsfx;
    requires java.desktop;
    requires java.sql;

    opens com to javafx.fxml;
    
    exports com;
}