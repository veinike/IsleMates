module com.palsandpalms {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.media;
    requires com.google.gson;

    opens com.palsandpalms to javafx.graphics;
    opens com.palsandpalms.ui to javafx.graphics;
    opens com.palsandpalms.model to com.google.gson;
    opens com.palsandpalms.persistence to com.google.gson;
    opens com.palsandpalms.ui.screens to javafx.fxml;

    exports com.palsandpalms;
    exports com.palsandpalms.model;
    exports com.palsandpalms.engine;
    exports com.palsandpalms.world;
    exports com.palsandpalms.persistence;
    exports com.palsandpalms.ui;
    exports com.palsandpalms.ui.screens;
    exports com.palsandpalms.ui.components;
    exports com.palsandpalms.input;
}
