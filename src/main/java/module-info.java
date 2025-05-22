module demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javatuples;
    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.rmi;
    requires org.jline;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens view.gui to javafx.fxml;
    exports view.gui;
}