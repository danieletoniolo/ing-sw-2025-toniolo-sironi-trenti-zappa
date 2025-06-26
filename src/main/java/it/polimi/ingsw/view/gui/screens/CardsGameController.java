package it.polimi.ingsw.view.gui.screens;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CardsGameController implements MiniModelObserver, Initializable {

    @FXML private StackPane parent;
    @FXML private VBox mainVBox;
    @FXML private HBox upperHBox;
    @FXML private VBox upperLeftVBox;
    @FXML private HBox titleCardHBox;
    @FXML private Label titleLabel;
    @FXML private StackPane currentCard;
    @FXML private StackPane board;
    @FXML private VBox upperRightVBox;
    @FXML private StackPane infos;
    @FXML private StackPane clientSpaceShip;
    @FXML private HBox lowerHBox;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    @Override
    public void react() {

    }
}
