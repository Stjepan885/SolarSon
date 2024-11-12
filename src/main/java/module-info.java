module com.example.projektoop {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;
    requires java.prefs;
    requires ewelink.api.java;
    requires com.google.gson;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;

    opens com.example.projektoop to javafx.fxml;
    exports com.example.projektoop;
    exports com.example.projektoop.controllers;
    opens com.example.projektoop.controllers to javafx.fxml;
    exports com.example.projektoop.controllers.pane;
    opens com.example.projektoop.controllers.pane to javafx.fxml;
    exports com.example.projektoop.models;
    opens com.example.projektoop.models to com.google.gson;
    exports com.example.projektoop.preferences;
    opens com.example.projektoop.preferences to com.fasterxml.jackson.databind;
    exports com.example.projektoop.device;

}