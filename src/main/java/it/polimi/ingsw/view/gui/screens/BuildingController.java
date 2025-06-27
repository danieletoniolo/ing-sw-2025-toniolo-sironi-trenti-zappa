package it.polimi.ingsw.view.gui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.cheatCode.CheatCode;
import it.polimi.ingsw.event.game.clientToServer.deck.PickLeaveDeck;
import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromBoard;
import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromReserve;
import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromSpaceship;
import it.polimi.ingsw.event.game.clientToServer.placeTile.PlaceTileToBoard;
import it.polimi.ingsw.event.game.clientToServer.placeTile.PlaceTileToReserve;
import it.polimi.ingsw.event.game.clientToServer.placeTile.PlaceTileToSpaceship;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.player.PlaceMarker;
import it.polimi.ingsw.event.game.clientToServer.rotateTile.RotateTile;
import it.polimi.ingsw.event.game.clientToServer.timer.FlipTimer;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.view.gui.controllers.board.BoardController;
import it.polimi.ingsw.view.gui.controllers.components.ComponentController;
import it.polimi.ingsw.view.gui.controllers.misc.MessageController;
import it.polimi.ingsw.view.gui.controllers.misc.ViewablePileController;
import it.polimi.ingsw.view.gui.controllers.ship.SpaceShipController;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.miniModel.components.ComponentTypeView;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.javatuples.Pair;

import java.net.URL;
import java.util.ResourceBundle;

public class BuildingController implements MiniModelObserver, Initializable {

    @FXML private StackPane parent;

    @FXML private VBox mainVBox;
    @FXML private StackPane lowerLeftStackPane;
    @FXML private StackPane handComponent;
    @FXML private Button rotateButton;
    @FXML private Button pickNewTile;
    @FXML private StackPane upperRightStackPane;
    @FXML private StackPane lowerRightStackPane;
    @FXML private HBox lowerHBox;

    @FXML private Group resizeGroup;

    private StackPane newOtherPlayerPane;

    private final MiniModel mm = MiniModel.getInstance();

    private final double ORIGINAL_MAIN_VBOX_WIDTH = 1600.0;
    private final double ORIGINAL_MAIN_VBOX_HEIGHT = 900.0;

    private boolean pKeyPressed = false;


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

        StackPane.setAlignment(mainVBox, Pos.CENTER);

        ChangeListener<Number> resizeListener = createResizeListener();
        parent.widthProperty().addListener(resizeListener);
        parent.heightProperty().addListener(resizeListener);

        mainVBox.sceneProperty().addListener((_, _, newScene) -> {
            if (newScene != null) {
                newScene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.P) {
                        pKeyPressed = true;
                        return;
                    }

                    if (pKeyPressed) {
                        int cheatIndex = -1;
                        boolean isLevelSecond = mm.getBoardView().getLevel() == LevelView.SECOND;
                        switch (event.getCode()) {
                            case DIGIT1:
                            case NUMPAD1:
                                cheatIndex = 0;
                                break;
                            case DIGIT2:
                            case NUMPAD2:
                                cheatIndex = 1;
                                break;
                            case DIGIT3:
                            case NUMPAD3:
                                if (isLevelSecond) {
                                    cheatIndex = 2;
                                }
                                break;
                            case DIGIT4:
                            case NUMPAD4:
                                if (isLevelSecond) {
                                    cheatIndex = 3;
                                }
                                break;
                            default:
                                // Not a cheat key, do nothing
                                break;
                        }

                        if (cheatIndex != -1) {
                            CheatCode.requester(Client.transceiver, new Object()).request(new CheatCode(mm.getUserID(), cheatIndex));
                        }
                        pKeyPressed = false; // Reset after any key press following 'P'
                    }
                });

                Platform.runLater(() -> {
                    resizeListener.changed(null, null, null);
                    newScene.windowProperty().addListener((_, _, newWin) -> {
                        if (newWin != null) {
                            Platform.runLater(() -> resizeListener.changed(null, null, null));
                        }
                    });
                });
            }
        });

        // Initialize the board logic

        Pair<Node, BoardController> board = mm.getBoardView().getNode();
        BoardController boardController = board.getValue1();

        if (mm.getBoardView().getLevel() == LevelView.SECOND) {
            for (int i = 0; i < boardController.getDeckControllers().size(); i++) {
                int finalI = i;
                boardController.getDeckControllers().get(i).getParent().setOnMouseClicked(_ -> {
                    StatusEvent status = PickLeaveDeck.requester(Client.transceiver, new Object()).request(new PickLeaveDeck(mm.getUserID(), 0, finalI));
                    if (status.get().equals(mm.getErrorCode())) {
                        Stage currentStage = (Stage) parent.getScene().getWindow();
                        MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                    } else {
                        showDeckView(mm.getDeckViews().getValue0()[finalI], finalI);
                    }
                });
            }

            boardController.getTimerController().getParent().setOnMouseClicked(_ -> {
                StatusEvent status = FlipTimer.requester(Client.transceiver, new Object()).request(new FlipTimer(mm.getUserID()));
                if (status.get().equals(mm.getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
            });
        }

        // Initialize lower buttons and interactions

        Button placeMarkerButton = new Button("Place marker");
        placeMarkerButton.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold");
        placeMarkerButton.setOnMouseClicked(_ -> showMarkerPositionSelector());
        lowerHBox.getChildren().add(placeMarkerButton);

        Button flipTimerButton = new Button("Flip timer");
        flipTimerButton.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold");
        flipTimerButton.setOnMouseClicked(_ -> {
            StatusEvent status = FlipTimer.requester(Client.transceiver, new Object()).request(new FlipTimer(mm.getUserID()));
            if (status.get().equals(mm.getErrorCode())) {
                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
            }
        });
        lowerHBox.getChildren().add(flipTimerButton);

        int numOfButtons = mm.getOtherPlayers().size() + 2;
        for (PlayerDataView playerDataView : mm.getOtherPlayers()) {
            Button playerButton = new Button("View " + playerDataView.getUsername() + "'s spaceship");
            playerButton.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold");
            playerButton.setOnMouseClicked(_ -> showOtherPlayer(playerDataView));
            playerButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(numOfButtons));
            lowerHBox.getChildren().add(playerButton);
        }
        placeMarkerButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(numOfButtons));
        flipTimerButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(numOfButtons));

        // Initialize upper buttons and interactions

        rotateButton.setText("Rotate component");
        rotateButton.setOnMouseClicked(_ -> {
            StatusEvent status = RotateTile.requester(Client.transceiver, new Object()).request(new RotateTile(MiniModel.getInstance().getUserID(), mm.getClientPlayer().getHand().getID()));
            if (status.get().equals(mm.getErrorCode())) {
                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
            }
        });

        pickNewTile.setOnAction(_ -> {
            StatusEvent status = PickTileFromBoard.requester(Client.transceiver, new Object()).request(new PickTileFromBoard(MiniModel.getInstance().getUserID(), -1));
            if (status.get().equals(mm.getErrorCode())) {
                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
            }
        });

        if (parent.getScene() != null && parent.getWidth() > 0) {
            Platform.runLater(() -> resizeListener.changed(null, null, null));
        }
    }

    private ChangeListener<Number> createResizeListener() {
        return (_, _, _) -> {
            if (parent.getWidth() <= 0 || parent.getHeight() <= 0) {
                return;
            }

            double scaleX = parent.getWidth() / ORIGINAL_MAIN_VBOX_WIDTH;
            double scaleY = parent.getHeight() / ORIGINAL_MAIN_VBOX_HEIGHT;
            double scale = Math.min(scaleX, scaleY);

            resizeGroup.setScaleX(scale);
            resizeGroup.setScaleY(scale);
        };
    }

    private void showDeckView(DeckView deck, int position) {
        StackPane deckViewPane = new StackPane();
        deckViewPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        // Copre tutto il parent
        deckViewPane.prefWidthProperty().bind(parent.widthProperty());
        deckViewPane.prefHeightProperty().bind(parent.heightProperty());
        deckViewPane.maxWidthProperty().bind(parent.widthProperty());
        deckViewPane.maxHeightProperty().bind(parent.heightProperty());

        VBox deckVBox = new VBox(20);
        deckVBox.setAlignment(Pos.CENTER);

        deckVBox.prefWidthProperty().bind(mainVBox.widthProperty().multiply(0.8));
        deckVBox.prefHeightProperty().bind(mainVBox.heightProperty().multiply(0.7));

        Label titleLabel = new Label("Deck Cards");
        titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #f2ff00;");
        titleLabel.setEffect(new DropShadow());

        HBox cardsHBox = new HBox(20);
        cardsHBox.setAlignment(Pos.CENTER);

        int cardCount = Math.min(3, deck.getDeck().size());
        for (int i = 0; i < cardCount; i++) {
            Node cardNode = deck.getDeck().get(i).getNode().getValue0();

            StackPane cardContainer = new StackPane();
            cardContainer.prefWidthProperty().bind(mainVBox.widthProperty().multiply(0.2));
            cardContainer.prefHeightProperty().bind(mainVBox.heightProperty().multiply(0.4));
            cardContainer.getChildren().add(cardNode);

            cardsHBox.getChildren().add(cardContainer);
        }

        Button closeButton = new Button("Close");
        closeButton.setPrefSize(200, 60);
        closeButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10;");
        closeButton.setOnAction(_ -> {
            StatusEvent status = PickLeaveDeck.requester(Client.transceiver, new Object()).request(new PickLeaveDeck(mm.getUserID(), 1, position));
            if (status.get().equals(mm.getErrorCode())) {
                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
            } else {
                hideOverlay(deckViewPane);
            }
        });

        deckVBox.getChildren().addAll(titleLabel, cardsHBox, closeButton);
        deckViewPane.getChildren().add(deckVBox);
        StackPane.setAlignment(deckVBox, Pos.CENTER);

        parent.getChildren().add(deckViewPane);
        deckViewPane.setVisible(false);

        Platform.runLater(() -> {
            deckViewPane.setVisible(true);
            deckViewPane.toFront();
            parent.layout();

            deckViewPane.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), deckViewPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
    }

    private void showMarkerPositionSelector() {
        StackPane markerSelectorPane = new StackPane();
        markerSelectorPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        markerSelectorPane.prefWidthProperty().bind(parent.widthProperty());
        markerSelectorPane.prefHeightProperty().bind(parent.heightProperty());
        markerSelectorPane.maxWidthProperty().bind(parent.widthProperty());
        markerSelectorPane.maxHeightProperty().bind(parent.heightProperty());

        VBox selectorVBox = new VBox(20);
        selectorVBox.setAlignment(Pos.CENTER);

        selectorVBox.prefWidthProperty().bind(mainVBox.widthProperty().multiply(0.5));
        selectorVBox.prefHeightProperty().bind(mainVBox.heightProperty().multiply(0.6));

        Label titleLabel = new Label("Select Marker Position");
        titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #f2ff00;");
        titleLabel.setEffect(new DropShadow());

        VBox buttonsVBox = new VBox(15);
        buttonsVBox.setAlignment(Pos.CENTER);

        for (int i = 1; i <= 4; i++) {
            Button positionButton = new Button("Position " + i);
            positionButton.setPrefSize(300, 60);
            positionButton.setStyle("-fx-background-color: rgba(251,197,9, 0.8); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 18px;");

            int position = i - 1;
            positionButton.setOnAction(_ -> {
                hideOverlay(markerSelectorPane);
                placeMarkerAtPosition(position);
            });

            buttonsVBox.getChildren().add(positionButton);
        }

        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefSize(300, 60);
        cancelButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10;");
        cancelButton.setOnAction(_ -> hideOverlay(markerSelectorPane));

        selectorVBox.getChildren().addAll(titleLabel, buttonsVBox, cancelButton);
        markerSelectorPane.getChildren().add(selectorVBox);
        StackPane.setAlignment(selectorVBox, Pos.CENTER);

        // Aggiungi al parent
        parent.getChildren().add(markerSelectorPane);
        markerSelectorPane.setVisible(false);

        Platform.runLater(() -> {
            markerSelectorPane.setVisible(true);
            markerSelectorPane.toFront();
            parent.layout();

            markerSelectorPane.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), markerSelectorPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
    }

    private void placeMarkerAtPosition(int position) {
        StatusEvent status = PlaceMarker.requester(Client.transceiver, new Object())
                .request(new PlaceMarker(MiniModel.getInstance().getUserID(), position));
        if (status.get().equals(mm.getErrorCode())) {
            Stage currentStage = (Stage) parent.getScene().getWindow();
            MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
        } else {
            status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(mm.getUserID()));
            if (status.get().equals(mm.getErrorCode())) {
                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
            }
        }
    }

    private void showOtherPlayer(PlayerDataView player) {
        newOtherPlayerPane = new StackPane();
        newOtherPlayerPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        newOtherPlayerPane.prefWidthProperty().bind(parent.widthProperty());
        newOtherPlayerPane.prefHeightProperty().bind(parent.heightProperty());
        newOtherPlayerPane.maxWidthProperty().bind(parent.widthProperty());
        newOtherPlayerPane.maxHeightProperty().bind(parent.heightProperty());

        VBox newOtherPlayerVBox = new VBox(15);
        newOtherPlayerVBox.setAlignment(Pos.CENTER);

        newOtherPlayerVBox.prefWidthProperty().bind(mainVBox.widthProperty().multiply(0.8));
        newOtherPlayerVBox.prefHeightProperty().bind(mainVBox.heightProperty().multiply(0.8));
        newOtherPlayerVBox.maxWidthProperty().bind(mainVBox.widthProperty().multiply(0.8));
        newOtherPlayerVBox.maxHeightProperty().bind(mainVBox.heightProperty().multiply(0.8));
        newOtherPlayerVBox.setStyle("-fx-background-color: transparent;");

        Label titleLabel = new Label(player.getUsername() + "'s spaceship");
        titleLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #f2ff00;");
        titleLabel.setEffect(new DropShadow());


        StackPane otherShip = new StackPane();
        otherShip.prefWidthProperty().bind(mainVBox.widthProperty().multiply(0.6));
        otherShip.prefHeightProperty().bind(mainVBox.heightProperty().multiply(0.5));
        otherShip.setStyle("-fx-background-color: transparent;");
        otherShip.getChildren().add(player.getShip().getNode().getValue0());

        Button backButton = new Button("Back");
        backButton.setPrefSize(200, 60);
        backButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10;");
        backButton.setOnAction(_ -> hideOverlay(newOtherPlayerPane));

        newOtherPlayerVBox.getChildren().addAll(titleLabel, otherShip, backButton);
        newOtherPlayerPane.getChildren().add(newOtherPlayerVBox);
        StackPane.setAlignment(newOtherPlayerVBox, Pos.CENTER);

        parent.getChildren().add(newOtherPlayerPane);
        newOtherPlayerPane.setVisible(false);

        Platform.runLater(() -> {

            newOtherPlayerPane.setVisible(true);
            newOtherPlayerPane.toFront(); // Poi il popup va davanti al background

            parent.layout();

            newOtherPlayerPane.setOpacity(0);

            FadeTransition fadeInContent = new FadeTransition(Duration.millis(300), newOtherPlayerPane);
            fadeInContent.setFromValue(0);
            fadeInContent.setToValue(1);

            fadeInContent.play();
        });
    }

    private void hideOverlay(StackPane paneToHide) {
        FadeTransition fadeOutContent = new FadeTransition(Duration.millis(300), paneToHide);
        fadeOutContent.setFromValue(1);
        fadeOutContent.setToValue(0);

        fadeOutContent.setOnFinished(_ -> {
            paneToHide.setVisible(false);
            parent.getChildren().remove(paneToHide); // Rimuovi dal parent, non dal resizeGroup
        });

        fadeOutContent.play();
    }

    /**
     * Sets up the interaction logic for the spaceship components.
     * Allows placing tiles on the spaceship or picking tiles from it based on the current hand component type.
     */
    private void setupSpaceShipInteraction() {
        Pair<Node, SpaceShipController> spaceShipPair = mm.getClientPlayer().getShip().getNode();
        SpaceShipController spaceShipController = spaceShipPair.getValue1();

        for (ComponentController component : spaceShipController.getShipComponentControllers()) {
            if (component.getComponentView().getType() == ComponentTypeView.GENERIC) {
                component.getParent().setOnMouseClicked(_ -> {
                    StatusEvent status = PlaceTileToSpaceship.requester(Client.transceiver, new Object()).request(new PlaceTileToSpaceship(mm.getUserID(), component.getComponentView().getRow() - 1, component.getComponentView().getCol() - 1));
                    if (status.get().equals(mm.getErrorCode())) {
                        Stage currentStage = (Stage) parent.getScene().getWindow();
                        MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                    }
                });
            } else if (component.getComponentView().getID() == mm.getClientPlayer().getShip().peekLast().getID()) {
                component.getParent().setOnMouseClicked(_ -> {
                    StatusEvent status = PickTileFromSpaceship.requester(Client.transceiver, new Object())
                            .request(new PickTileFromSpaceship(mm.getUserID()));
                    if (status.get().equals(mm.getErrorCode())) {
                        Stage currentStage = (Stage) parent.getScene().getWindow();
                        MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                    }
                });
            } else {
                component.getParent().setOnMouseClicked(null);
            }
        }

        if (mm.getClientPlayer().getHand().getType() == ComponentTypeView.GENERIC) {
            spaceShipController.getReserveLostGrid().setOnMouseClicked(null);
            for (ComponentController component : spaceShipController.getReservedComponentControllers()) {
                component.getParent().setOnMouseClicked(_ -> {
                    StatusEvent status = PickTileFromReserve.requester(Client.transceiver, new Object()).request(new PickTileFromReserve(mm.getUserID(), component.getComponentView().getID()));
                    if (status.get().equals(mm.getErrorCode())) {
                        Stage currentStage = (Stage) parent.getScene().getWindow();
                        MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                    }
                });
            }
        } else {
            for (ComponentController component : spaceShipController.getReservedComponentControllers()) {
                component.getParent().setOnMouseClicked(null);
            }
            spaceShipController.getReserveLostGrid().setOnMouseClicked(_ -> {
                StatusEvent status = PlaceTileToReserve.requester(Client.transceiver, new Object()).request(new PlaceTileToReserve(mm.getUserID()));
                if (status.get().equals(mm.getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
            });
        }
    }


    /**
     * Sets up the interaction logic for the viewable pile based on the current hand component type.
     * If the hand contains a GENERIC component, allows picking tiles from the pile.
     * Otherwise, allows placing the current hand component to the board.
     */
    private void setupPileInteraction() {
        Pair<Node, ViewablePileController> viewablePile = mm.getViewablePile().getNode();
        ViewablePileController viewablePileCtrl = viewablePile.getValue1();

        if (mm.getClientPlayer().getHand().getType() == ComponentTypeView.GENERIC) {
            upperRightStackPane.setOnMouseClicked(null);

            for (int i = 0; i < viewablePileCtrl.getComponentControllers().size(); i++) {
                int finalI = i;

                viewablePileCtrl.getComponentControllers().get(i).getParent().setOnMouseClicked(_ -> {
                    int ID = mm.getViewablePile().getViewableComponents().stream()
                            .skip(finalI)
                            .map(ComponentView::getID)
                            .findFirst()
                            .orElse(-1);

                    StatusEvent status = PickTileFromBoard.requester(Client.transceiver, new Object())
                            .request(new PickTileFromBoard(mm.getUserID(), ID));
                    if (status.get().equals(mm.getErrorCode())) {
                        Stage currentStage = (Stage) parent.getScene().getWindow();
                        MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                    }
                });
            }
        } else {
            for (ComponentController component : viewablePileCtrl.getComponentControllers()) {
                component.getParent().setOnMouseClicked(null);
            }

            upperRightStackPane.setOnMouseClicked(_ -> {
                StatusEvent status = PlaceTileToBoard.requester(Client.transceiver, new Object())
                        .request(new PlaceTileToBoard(mm.getUserID()));
                if (status.get().equals(mm.getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
            });
        }
    }

    @Override
    public void react() {
        Platform.runLater(() -> {

            setupSpaceShipInteraction();

            setupPileInteraction();

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
