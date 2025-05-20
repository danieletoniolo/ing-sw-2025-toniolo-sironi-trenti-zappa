module demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javatuples;
    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.rmi;
    requires org.jline;

    opens view.gui to javafx.fxml;
    exports view.gui;
}