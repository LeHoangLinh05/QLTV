module com.example.qltv {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.formsfx;
    requires java.desktop;
    requires java.sql;

    opens com.example.qltv to javafx.fxml;
    
    exports com.example.qltv;
}