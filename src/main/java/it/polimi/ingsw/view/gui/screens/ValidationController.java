package it.polimi.ingsw.view.gui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.player.PlaceMarker;
import it.polimi.ingsw.event.game.clientToServer.spaceship.ChooseFragment;
import it.polimi.ingsw.event.game.clientToServer.spaceship.DestroyComponents;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.gui.controllers.misc.MessageController;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.javatuples.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ValidationController implements MiniModelObserver, Initializable {

    @FXML private StackPane parent;

    @FXML private Group resizeGroup;

    @FXML private VBox mainVBox;

    @FXML private Label titleLabel;

    @FXML private HBox centerHBox;

    @FXML private StackPane board;

    @FXML private StackPane clientShip;

    @FXML private HBox lowerHBox;

    private StackPane newValidationOptionsPane;

    private StackPane newOtherPlayerPane;

    private Button destroyComponentsButton;
    private Button cancelSelectionButton;
    private Button placeMarkerButton;
    private Button endTurnButton;

    private final MiniModel mm = MiniModel.getInstance();
    private final List<Pair<Integer, Integer>> componentsToDestroy = new ArrayList<>();

    private boolean placedMarker;

    private final double ORIGINAL_MAIN_VBOX_WIDTH = 1600.0;
    private final double ORIGINAL_MAIN_VBOX_HEIGHT = 900.0;

    private int totalButtons;

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

        // Initialize lower HBox buttons
        int totalButtons = 4 + mm.getOtherPlayers().size();

        destroyComponentsButton = new Button("Destroy");
        destroyComponentsButton.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold");
        destroyComponentsButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        cancelSelectionButton = new Button("Cancel");
        cancelSelectionButton.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold");
        cancelSelectionButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        endTurnButton = new Button("End Turn");
        endTurnButton.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold");
        endTurnButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        placeMarkerButton = new Button("Place Marker");
        placeMarkerButton.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold");
        placeMarkerButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        lowerHBox.getChildren().addAll(destroyComponentsButton, cancelSelectionButton, endTurnButton, placeMarkerButton);

        for (PlayerDataView player : mm.getOtherPlayers()) {
            Button otherButtonPlayer = new Button("View " + player.getUsername() + "'s spaceship");
            otherButtonPlayer.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold");
            otherButtonPlayer.setOnMouseClicked(_ -> showOtherPlayer(player));
            otherButtonPlayer.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
            lowerHBox.getChildren().add(otherButtonPlayer);
        }

        placedMarker = true;

        Platform.runLater(this::react);
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

    @Override
    public void react() {
        Platform.runLater(() -> {
            for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
                for (ComponentView component : row) {
                    if (component != null && component.getIsWrong()) {
                        placedMarker = false;
                        break;
                    }
                }
            }

            placeMarkerButton.setOnMouseClicked(_ -> showMarkerPositionSelector());

            destroyComponentsButton.setOnMouseClicked(_ -> {
                StatusEvent status = DestroyComponents.requester(Client.transceiver, new Object()).request(new DestroyComponents(mm.getUserID(), componentsToDestroy));
                if (status.get().equals(mm.getErrorCode())) {
                    error(status);
                }
                for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
                    for (ComponentView component : row) {
                        if (component != null) {
                            Node node = component.getNode().getValue0();

                            node.setDisable(false);
                            node.setOpacity(1.0);
                        }
                    }
                }
                componentsToDestroy.clear();
            });

            cancelSelectionButton.setOnMouseClicked(_ -> {
                for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
                    for (ComponentView component : row) {
                        if (component != null) {
                            Node node = component.getNode().getValue0();

                            node.setDisable(false);
                            node.setOpacity(1.0);
                        }
                    }
                }
                componentsToDestroy.clear();
            });

            endTurnButton.setOnMouseClicked(_ -> {
                if (!placedMarker) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, "You need to place the marker");
                }
                else{
                    StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(mm.getUserID()));
                    if (status.get().equals(mm.getErrorCode())) {
                        error(status);
                    }
                    else{
                        Stage currentStage = (Stage) parent.getScene().getWindow();
                        MessageController.showInfoMessage(currentStage, "Confirmed choices");
                    }
                }
            });

            for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
                for (ComponentView component : row) {
                    if (component != null) {
                        Node node = component.getNode().getValue0();

                        if (component.getIsWrong()) {
                            DropShadow redGlow = new DropShadow();
                            redGlow.setColor(Color.RED);
                            redGlow.setRadius(20);
                            redGlow.setSpread(0.6);

                            Glow glow = new Glow(0.7);
                            glow.setInput(redGlow);

                            node.setEffect(glow);
                        } else {
                            node.setEffect(null);
                        }

                        node.setOnMouseClicked(_ -> {
                            node.setDisable(true); // disable clicks on the component

                            node.setOpacity(0.5); // Set opacity to indicate selection

                            componentsToDestroy.add(new Pair<>(component.getRow() - 1, component.getCol() - 1));
                        });
                    }
                }
            }

            Button chooseFragments = new Button("Choose fragments");
            chooseFragments.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

            chooseFragments.setOnMouseClicked(_ -> {
                List<Color> colors = new ArrayList<>();
                colors.add(Color.RED);
                colors.add(Color.GREEN);
                colors.add(Color.BLUE);
                colors.add(Color.YELLOW);
                colors.add(Color.ORANGE);
                colors.add(Color.PURPLE);
                colors.add(Color.PINK);
                colors.add(Color.BROWN);
                colors.add(Color.GRAY);
                colors.add(Color.BLACK);
                colors.add(Color.WHITE);
                colors.add(Color.CYAN);
                colors.add(Color.MAGENTA);
                colors.add(Color.LIME);
                colors.add(Color.OLIVE);
                colors.add(Color.NAVY);
                colors.add(Color.TEAL);
                colors.add(Color.MAROON);
                colors.add(Color.AQUA);
                colors.add(Color.GOLD);
                colors.add(Color.SILVER);
                colors.add(Color.CORAL);
                colors.add(Color.INDIGO);
                colors.add(Color.VIOLET);
                colors.add(Color.KHAKI);
                colors.add(Color.TURQUOISE);
                colors.add(Color.SALMON);
                int i = 0;
                for (List<Pair<Integer, Integer>> group : mm.getClientPlayer().getShip().getFragments()) {
                    for (Pair<Integer, Integer> pair : group) {
                        Node node = mm.getClientPlayer().getShip().getComponent(pair.getValue0(), pair.getValue1()).getNode().getValue0();

                        DropShadow redGlow = new DropShadow();
                        redGlow.setColor(colors.get(i));
                        redGlow.setRadius(20);
                        redGlow.setSpread(0.6);

                        Glow glow = new Glow(0.7);
                        glow.setInput(redGlow);

                        node.setEffect(glow);

                        int finalI = i;
                        node.setOnMouseClicked(_ -> {
                            StatusEvent status = ChooseFragment.requester(Client.transceiver, new Object()).request(new ChooseFragment(mm.getUserID(), finalI));
                            if (status.get().equals(mm.getErrorCode())) {
                                error(status);
                            }
                        });
                    }
                    i++;
                }
            });

            board.getChildren().clear();
            board.getChildren().add(mm.getBoardView().getNode().getValue0());

            clientShip.getChildren().clear();
            clientShip.getChildren().add(mm.getClientPlayer().getShip().getNode().getValue0());
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
                placeMarkerAtPosition(position);
                hideOverlay(markerSelectorPane);
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
            error(status);
        } else {
            placedMarker = true;
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

    private void hideValidationOptions(StackPane pane) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), pane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(_ -> pane.setVisible(false));
        fadeOut.play();
    }

    private void resetHandlers() {
        for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
            for (ComponentView component : row) {
                if (component != null) {
                    Node node = component.getNode().getValue0();
                    node.setOnMouseClicked(null);
                    node.setEffect(null);
                }
            }
        }
    }

    private void error(StatusEvent status) {
        Stage currentStage = (Stage) parent.getScene().getWindow();
        MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
        resetHandlers();
    }
}
