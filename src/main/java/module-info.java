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
    requires java.naming;

    opens it.polimi.ingsw.view.gui to javafx.fxml;
    exports it.polimi.ingsw.view.gui;

    opens it.polimi.ingsw.model.cards to com.fasterxml.jackson.databind;
    exports it.polimi.ingsw.model.cards to com.fasterxml.jackson.databind;

    opens it.polimi.ingsw.model.good to com.fasterxml.jackson.databind;
    exports it.polimi.ingsw.model.good to com.fasterxml.jackson.databind;

    opens it.polimi.ingsw.model.cards.hits to com.fasterxml.jackson.databind;
}