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
    requires java.desktop;

    exports it.polimi.ingsw.view.gui.controllers.components;
    opens it.polimi.ingsw.view.gui.controllers.components to javafx.fxml;

    exports it.polimi.ingsw.view.gui.controllers.cards;
    opens it.polimi.ingsw.view.gui.controllers.cards to javafx.fxml;

    opens it.polimi.ingsw.view.gui to javafx.fxml;
    exports it.polimi.ingsw.view.gui;

    opens it.polimi.ingsw.view.gui.controllers.ship to javafx.fxml;
    exports it.polimi.ingsw.view.gui.controllers.ship;

    exports it.polimi.ingsw.view.gui.screens;
    opens it.polimi.ingsw.view.gui.screens to javafx.fxml;

    opens it.polimi.ingsw.model.cards to com.fasterxml.jackson.databind;
    exports it.polimi.ingsw.model.cards to com.fasterxml.jackson.databind;

    opens it.polimi.ingsw.model.good to com.fasterxml.jackson.databind;
    exports it.polimi.ingsw.model.good to com.fasterxml.jackson.databind;

    opens it.polimi.ingsw.model.cards.hits to com.fasterxml.jackson.databind;
}
