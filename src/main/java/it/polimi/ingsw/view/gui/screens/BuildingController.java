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
import java.util.List;
import java.util.ResourceBundle;

public class BuildingController implements MiniModelObserver, Initializable {

    @FXML private StackPane parent;

    @FXML private VBox mainVBox;
        @FXML private HBox upperHBox;
            @FXML private VBox leftVBox;
                @FXML private StackPane upperLeftStackPane;
                    @FXML private Label titleLabel;
                @FXML private StackPane lowerLeftStackPane;
            @FXML private VBox rightVBox;
                @FXML private StackPane upperRightStackPane;
                @FXML private StackPane lowerRightStackPane;

        @FXML private HBox lowerHBox;
            @FXML private List<Button> otherPlayersButtons;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        URL imageUrl = getClass().getResource("/image/background/background2.png");
        if (imageUrl != null) {
            String cssBackground = "-fx-background-image: url('" + imageUrl.toExternalForm() + "'); " +
                    "-fx-background-size: cover; " +
                    "-fx-background-position: center center; " +
                    "-fx-background-repeat: no-repeat;";
            parent.setStyle(cssBackground);
        }



    }



    @Override
    public void react() {

    }
}
