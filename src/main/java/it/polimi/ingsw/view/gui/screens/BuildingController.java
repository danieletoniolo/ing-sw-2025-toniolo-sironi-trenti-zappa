package it.polimi.ingsw.view.gui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.deck.PickLeaveDeck;
import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromBoard;
import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromReserve;
import it.polimi.ingsw.event.game.clientToServer.placeTile.PlaceTileToBoard;
import it.polimi.ingsw.event.game.clientToServer.placeTile.PlaceTileToReserve;
import it.polimi.ingsw.event.game.clientToServer.placeTile.PlaceTileToSpaceship;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.player.PlaceMarker;
import it.polimi.ingsw.event.game.clientToServer.rotateTile.RotateTile;
import it.polimi.ingsw.event.game.clientToServer.timer.FlipTimer;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.gui.controllers.board.BoardController;
import it.polimi.ingsw.view.gui.controllers.components.ComponentController;
import it.polimi.ingsw.view.gui.controllers.misc.MessageController;
import it.polimi.ingsw.view.gui.controllers.misc.ViewablePileController;
import it.polimi.ingsw.view.gui.controllers.ship.SpaceShipController;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.javatuples.Pair;

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
                @FXML private HBox upperRightHBox;
                    @FXML private StackPane upperRightStackPane;
                        @FXML private VBox handVBox;
                            @FXML private StackPane handComponent;
                            @FXML private Button rotateButton;
                @FXML private StackPane lowerRightStackPane;
        @FXML private HBox lowerHBox;
            @FXML private List<Button> otherPlayersButtons;
            @FXML private Button placeMarkerButton;
            @FXML private Button hiddenTileButton;
            @FXML private Button putTileInPileButton;
            
    private final MiniModel mm = MiniModel.getInstance();
    int pos = 0;

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

        Pair<Node, BoardController> board = mm.getBoardView().getNode();
        BoardController boardController = board.getValue1();

        if (mm.getBoardView().getLevel() == LevelView.SECOND) {
            for (int i = 0; i < boardController.getDeckControllers().size(); i++) {
                int finalI = i;
                boardController.getDeckControllers().get(i).getParent().setOnMouseClicked(e -> {
                    StatusEvent status = PickLeaveDeck.requester(Client.transceiver, new Object()).request(new PickLeaveDeck(mm.getUserID(), 0, finalI));
                    if (status.get().equals(mm.getErrorCode())) {
                        Stage currentStage = (Stage) parent.getScene().getWindow();
                        MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                    }
                });
            }

            boardController.getTimerController().getParent().setOnMouseClicked(e -> {
                StatusEvent status = FlipTimer.requester(Client.transceiver, new Object()).request(new FlipTimer(mm.getUserID()));
                if (status.get().equals(mm.getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
            });
        }

        for (int i = 0; i < boardController.getStepsNodes().size(); i++) {
            int finalI = i;
            Node stepNode = boardController.getStepsNodes().get(i);
            //stepNode.setStyle("-fx-background-color: rgba(255,176,56,0.1);");

            stepNode.setOnMouseClicked(e -> {
                StatusEvent status = PlaceMarker.requester(Client.transceiver, new Object())
                        .request(new PlaceMarker(MiniModel.getInstance().getUserID(), finalI));
                if (status.get().equals(mm.getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
            });
        }

        placeMarkerButton.setText("Place marker, pos: " + pos);
        placeMarkerButton.setOnMouseClicked(e -> {
            StatusEvent status = PlaceMarker.requester(Client.transceiver, new Object())
                    .request(new PlaceMarker(MiniModel.getInstance().getUserID(), pos));
            if (status.get().equals(mm.getErrorCode())) {
                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
            }
            else {
                status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(mm.getUserID()));
                if (status.get().equals(mm.getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
            }
            pos++;
            if (pos >= boardController.getStepsNodes().size()) {
                pos = 0;
            }
        });

        hiddenTileButton.setText("Pick a hidden tile from the pile");
        hiddenTileButton.setOnMouseClicked(e -> {
            StatusEvent status = PickTileFromBoard.requester(Client.transceiver, new Object()).request(new PickTileFromBoard(MiniModel.getInstance().getUserID(), -1));
            if (status.get().equals(mm.getErrorCode())) {
                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
            }
        });

        putTileInPileButton.setText("Put tile in the pile");
        putTileInPileButton.setOnMouseClicked(e -> {;
            StatusEvent status = PlaceTileToBoard.requester(Client.transceiver, new Object()).request(new PlaceTileToBoard(MiniModel.getInstance().getUserID()));
            if (status.get().equals(mm.getErrorCode())) {
                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
            }
        });

        rotateButton.setText("Rotate component");
        rotateButton.setOnMouseClicked(e -> {
            StatusEvent status = RotateTile.requester(Client.transceiver, new Object()).request(new RotateTile(MiniModel.getInstance().getUserID(), mm.getClientPlayer().getHand().getID()));
            if (status.get().equals(mm.getErrorCode())) {
                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
            }
        });
    }

    @Override
    public void react() {
        Pair<Node, SpaceShipController> spaceShipPair = mm.getClientPlayer().getShip().getNode();
        SpaceShipController spaceShipController = spaceShipPair.getValue1();

        for (ComponentController component : spaceShipController.getShipComponentControllers()) {
            component.getParent().setOnMouseClicked(event -> {
                StatusEvent status = PlaceTileToSpaceship.requester(Client.transceiver, new Object()).request(new PlaceTileToSpaceship(mm.getUserID(), component.getComponentView().getRow() - 1, component.getComponentView().getCol() - 1));
                if (status.get().equals(mm.getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
            });
        }

        for (ComponentController component : spaceShipController.getReservedComponentControllers()) {
            component.getParent().setOnMouseClicked(event -> {
                StatusEvent status = PickTileFromReserve.requester(Client.transceiver, new Object()).request(new PickTileFromReserve(mm.getUserID(), component.getComponentView().getID()));
                if (status.get().equals(mm.getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
            });
        }

        spaceShipController.getReserveLostGrid().setOnMouseClicked(event -> {
            StatusEvent status = PlaceTileToReserve.requester(Client.transceiver, new Object()).request(new PlaceTileToReserve(mm.getUserID()));
            if (status.get().equals(mm.getErrorCode())) {
                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
            }
        });

        Pair<Node, ViewablePileController> viewablePile = mm.getViewablePile().getNode();
        ViewablePileController viewablePileCtrl = viewablePile.getValue1();

        for (int i = 0; i < viewablePileCtrl.getComponentControllers().size(); i++) {
            int finalI = i;

            viewablePileCtrl.getComponentControllers().get(i).getParent().setOnMouseClicked(e-> {
                int ID = mm.getViewablePile().getViewableComponents().stream()
                        .skip(finalI)
                        .map(ComponentView::getID)
                        .findFirst()
                        .orElse(-1);

                // Send the request to pick the tile from the board
                StatusEvent status = PickTileFromBoard.requester(Client.transceiver, new Object())
                        .request(new PickTileFromBoard(MiniModel.getInstance().getUserID(), ID));
                if (status.get().equals(mm.getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
            });
        }

        Platform.runLater(() -> {
            lowerRightStackPane.getChildren().clear();
            lowerRightStackPane.getChildren().add(mm.getClientPlayer().getShip().getNode().getValue0());

            handComponent.getChildren().clear();
            handComponent.getChildren().add(mm.getClientPlayer().getHand().getNode().getValue0());

            upperRightStackPane.getChildren().clear();
            upperRightStackPane.getChildren().add(mm.getViewablePile().getNode().getValue0());

            lowerLeftStackPane.getChildren().clear();
            lowerLeftStackPane.getChildren().add(mm.getBoardView().getNode().getValue0());
        });

    }
}
