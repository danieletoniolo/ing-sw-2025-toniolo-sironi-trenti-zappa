package it.polimi.ingsw.view.gui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.spaceship.ManageCrewMember;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.gui.controllers.components.ComponentController;
import it.polimi.ingsw.view.gui.controllers.misc.MessageController;
import it.polimi.ingsw.view.gui.controllers.ship.SpaceShipController;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.javatuples.Pair;

import java.net.URL;
import java.util.ResourceBundle;

public class CrewController implements MiniModelObserver, Initializable {

    @FXML
    private StackPane parent;

    @FXML private VBox mainVBox;

    @FXML private Label titleLabel;

    @FXML private HBox centerHBox;

    @FXML private Button confirmChoiceButton;

    @FXML private StackPane spaceShipStackPane;

    @FXML private HBox lowerHBox;

    private StackPane newCrewOptionsPane;

    private VBox newCrewOptionsVBox;

    private final MiniModel mm = MiniModel.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void react() {
        Pair<Node, SpaceShipController> spaceShipPair = mm.getClientPlayer().getShip().getNode();
        SpaceShipController spaceShipController = spaceShipPair.getValue1();

        for (ComponentController component : spaceShipController.getShipComponentControllers()) {
            component.getParent().setOnMouseClicked(e -> {
                showCrewChoice(component);
            });
        }

        Platform.runLater(() -> {
            spaceShipStackPane.getChildren().clear();
            spaceShipStackPane.getChildren().add(mm.getClientPlayer().getShip().getNode().getValue0());
        });
    }

    private void showCrewChoice(ComponentController component) {
        if (newCrewOptionsPane == null) {
            createNewCrewOptionsPane(component);
        }

        Platform.runLater(() -> {
            newCrewOptionsPane.setVisible(true);
            newCrewOptionsPane.toFront();
            parent.layout();

            newCrewOptionsPane.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newCrewOptionsPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
    }

    private void createNewCrewOptionsPane(ComponentController component) {
        newCrewOptionsPane = new StackPane();
        newCrewOptionsPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        StackPane.setAlignment(newCrewOptionsPane, Pos.CENTER);

        newCrewOptionsPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        newCrewOptionsPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Create a VBox to hold the new lobby options
        newCrewOptionsVBox = new VBox(15);
        newCrewOptionsVBox.setAlignment(javafx.geometry.Pos.CENTER);
        newCrewOptionsVBox.setStyle("-fx-background-color: rgba(251,197,9, 0.8); " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: rgb(251,197,9); " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-padding: 20;");

        // Bind the size of the VBox to the main HBox
        newCrewOptionsVBox.prefWidthProperty().bind(mainVBox.widthProperty().multiply(0.3));
        newCrewOptionsVBox.prefHeightProperty().bind(mainVBox.heightProperty().multiply(0.5));
        newCrewOptionsVBox.minWidthProperty().bind(newCrewOptionsVBox.prefWidthProperty());
        newCrewOptionsVBox.minHeightProperty().bind(newCrewOptionsVBox.prefHeightProperty());
        newCrewOptionsVBox.maxWidthProperty().bind(newCrewOptionsVBox.prefWidthProperty());
        newCrewOptionsVBox.maxHeightProperty().bind(newCrewOptionsVBox.prefHeightProperty());

        // Create a title label with a drop shadow effect
        Label titleLabel = new Label("Select crew type");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleLabel.setEffect(new DropShadow());


        // ComboBox for type crew selection
        ComboBox<String> crewType = new ComboBox<>();
        crewType.getItems().addAll("Human", "Brown alien", "Purple alien");
        crewType.setValue("Human");
        crewType.setPromptText("Select crew type:");
        crewType.setMaxWidth(newCrewOptionsVBox.getMaxWidth() * 0.8);

        // ComboBox for add or remove of players
        ComboBox<String> modeChose = new ComboBox<>();
        modeChose.getItems().addAll("Add crew member", "Remove crew members");
        modeChose.setValue("Add crew member");
        modeChose.setPromptText("Remove or add crew member:");
        modeChose.setMaxWidth(newCrewOptionsVBox.getMaxWidth() * 0.8);

        // Buttons box to hold the confirm and cancel buttons
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);

        // Create confirm button
        Button confirmButton = new Button("Choose");
        confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        confirmButton.setOnAction(_ -> {
            String selectedLevel = crewType.getValue();
            int type = selectedLevel.equals("Human") ? 0 : selectedLevel.equals("Brown alien") ? 1 : 2;
            int mode = modeChose.getValue().equals("Add crew member") ? 0 : 1;
            StatusEvent status = ManageCrewMember.requester(Client.transceiver, new Object()).request(new ManageCrewMember(mm.getUserID(), mode, type, component.getComponentView().getID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
            }
        });

        // Create cancel button
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        cancelButton.setOnAction(_ -> hideNewLobbyOptions());

        buttonsBox.getChildren().addAll(confirmButton, cancelButton);

        // Add all components to the VBox
        newCrewOptionsVBox.getChildren().addAll(titleLabel,
                new Label("Crew type:"),
                crewType,
                new Label("Number of Players:"),
                modeChose,
                buttonsBox);

        newCrewOptionsPane.getChildren().add(newCrewOptionsVBox);
        StackPane.setAlignment(newCrewOptionsVBox, Pos.CENTER);

        // Add the new lobby options pane to the parent StackPane
        parent.getChildren().add(newCrewOptionsPane);
        newCrewOptionsPane.setVisible(false);

        // Force the layout to update and bring the new pane to the front
        Platform.runLater(() -> {
            newCrewOptionsPane.toFront();
            parent.layout();
        });

        // Update the sizes of the new lobby options controls
        Platform.runLater(() -> {
            newCrewOptionsPane.toFront();
            parent.layout();
            //updateNewLobbyOptionsSizes();
        });
    }

    private void hideNewLobbyOptions() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), newCrewOptionsPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(_ -> newCrewOptionsPane.setVisible(false));
        fadeOut.play();
    }
}
