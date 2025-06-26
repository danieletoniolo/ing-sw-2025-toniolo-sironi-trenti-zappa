package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.event.game.serverToClient.deck.PickedLeftDeck;
import it.polimi.ingsw.event.game.serverToClient.dice.DiceRolled;
import it.polimi.ingsw.event.game.serverToClient.energyUsed.BatteriesLoss;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingGiveUp;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingPenalty;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingPlaceMarker;
import it.polimi.ingsw.event.game.serverToClient.goods.UpdateGoodsExchange;
import it.polimi.ingsw.event.game.serverToClient.pickedTile.PickedTileFromSpaceship;
import it.polimi.ingsw.event.game.serverToClient.placedTile.PlacedTileToBoard;
import it.polimi.ingsw.event.game.serverToClient.placedTile.PlacedTileToReserve;
import it.polimi.ingsw.event.game.serverToClient.placedTile.PlacedTileToSpaceship;
import it.polimi.ingsw.event.game.serverToClient.planets.PlanetSelected;
import it.polimi.ingsw.event.game.serverToClient.player.*;
import it.polimi.ingsw.event.game.serverToClient.rotatedTile.RotatedTile;
import it.polimi.ingsw.event.game.serverToClient.spaceship.*;
import it.polimi.ingsw.event.game.serverToClient.timer.TimerFlipped;
import it.polimi.ingsw.event.lobby.serverToClient.*;
import it.polimi.ingsw.view.Manager;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class GuiManager extends Application implements Manager {
    private static Scene scene;
    private static Parent root;
    private static MiniModelObserver controller;
    private final MiniModel mm = MiniModel.getInstance();
    private boolean countDownStarted = false;

    private enum GuiScene {
        LOGIN,
        MENU,
        LOBBY,
        BUILDING,
        VALIDATION,
        CREW,
        CARDS,
        REWARD
    }

    private GuiScene currentScene;

    public GuiManager() {
        root = new StackPane();
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screens/login.fxml"));
            root = loader.load();

            controller = loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading login screen");
            return;
        }

        scene = new Scene(root);
        stage.setTitle("Galaxy Trucker");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/image/background/galaxyTruckerIcon.png")));
        stage.setScene(scene);
        stage.show();
        currentScene = GuiScene.LOGIN;
    }

    @Override
    public void notifyNicknameSet(NicknameSet data) {
        this.setMenuScene();
    }

    @Override
    public void notifyConnectionLost() {
        this.setMenuScene();
    }

    @Override
    public void notifyLobbies() {
        Platform.runLater(() -> {
            controller.react();
        });
    }

    @Override
    public void notifyCreatedLobby(LobbyCreated data) {
        if (mm.getNickname().equals(data.nickname())) {
            this.setLobbyScene();
        }
        else {
            // controller.setMessage("Lobby " + data.lobbyName() + " created by " + data.nickname());
            controller.react();
        }
    }

    @Override
    public void notifyLobbyJoined(LobbyJoined data) {
        if (mm.getNickname().equals(data.nickname())) {
            this.setLobbyScene();
        }
        else {
            controller.react();
        }
    }

    @Override
    public void notifyLobbyLeft(LobbyLeft data) {
        if (mm.getNickname().equals(data.nickname())) {
            this.setMenuScene();
        }
        else {
            controller.react();
        }
    }

    @Override
    public void notifyLobbyRemoved(LobbyRemoved data) {
        if (currentScene == GuiScene.LOBBY) {
            this.setMenuScene();
        }
        else {
            controller.react();
        }
    }

    @Override
    public void notifyReadyPlayer() {
        controller.react();
    }

    @Override
    public void notifyCountDown() {
        if (!countDownStarted) {
            countDownStarted = true;
            controller.react();
        }
    }

    @Override
    public void notifyPickedLeftDeck(PickedLeftDeck data) {
        controller.react();
    }

    @Override
    public void notifyDiceRolled(DiceRolled data) {
        controller.react();
    }

    @Override
    public void notifyBatteriesLoss(BatteriesLoss data) {
        controller.react();
    }

    @Override
    public void notifyForcingGiveUp(ForcingGiveUp data) {
        controller.react();
    }

    @Override
    public void notifyForcingPenalty(ForcingPenalty data) {
        controller.react();
    }

    @Override
    public void notifyForcingPlaceMarker(ForcingPlaceMarker data) {

    }

    @Override
    public void notifyUpdateGoodsExchange(UpdateGoodsExchange data) {
        controller.react();

    }

    @Override
    public void notifyPickedTileFromBoard() {
        controller.react();
    }

    @Override
    public void notifyPickedTileFromSpaceShip(PickedTileFromSpaceship data) {
        controller.react();
    }

    @Override
    public void notifyPickedHiddenTile(String nickname) {
        controller.react();

    }

    @Override
    public void notifyPlacedTileToBoard(PlacedTileToBoard data) {
        controller.react();

    }

    @Override
    public void notifyPlacedTileToReserve(PlacedTileToReserve data) {
        controller.react();

    }

    @Override
    public void notifyPlacedTileToSpaceship(PlacedTileToSpaceship data) {
        controller.react();

    }

    @Override
    public void notifyPlanetSelected(PlanetSelected data) {
        controller.react();

    }

    @Override
    public void notifyCardPlayed(CardPlayed data) {
        controller.react();

    }

    @Override
    public void notifyCombatZonePhase(CombatZonePhase data) {
        controller.react();

    }

    @Override
    public void notifyEnemyDefeat(EnemyDefeat data) {
        controller.react();

    }

    @Override
    public void notifyMinPlayer(MinPlayer data) {
        controller.react();

    }

    @Override
    public void notifyMoveMarker(MoveMarker data) {
        controller.react();

    }

    @Override
    public void notifyRemoveMarker(RemoveMarker data) {

    }

    @Override
    public void notifyPlayerGaveUp(PlayerGaveUp data) {
        controller.react();

    }

    @Override
    public void notifyCurrentPlayer(CurrentPlayer data) {
        controller.react();

    }

    @Override
    public void notifyScore(Score data) {
        controller.react();

    }

    @Override
    public void notifyUpdateCoins(UpdateCoins data) {
        controller.react();

    }

    @Override
    public void notifyRotatedTile(RotatedTile data) {
        controller.react();

    }

    @Override
    public void notifyBestLookingShips(BestLookingShips data) {
        controller.react();

    }

    @Override
    public void notifyCanProtect(CanProtect data) {
        controller.react();

    }

    @Override
    public void notifyComponentDestroyed(ComponentDestroyed data) {
        controller.react();

    }

    @Override
    public void notifyFragments(Fragments data) {
        controller.react();

    }

    @Override
    public void notifyInvalidComponents(InvalidComponents data) {
        controller.react();

    }

    @Override
    public void notifySetCannonStrength(SetCannonStrength data) {
        controller.react();

    }

    @Override
    public void notifySetEngineStrength(SetEngineStrength data) {
        controller.react();

    }

    @Override
    public void notifyUpdateCrewMembers(UpdateCrewMembers data) {
        controller.react();

    }

    @Override
    public void notifyTimer(TimerFlipped data, boolean firstSecond) {
        controller.react();

    }

    @Override
    public void notifyTimerFinished(TimerFlipped data) {
        controller.react();

    }

    @Override
    public void notifyLastTimerFlipped() {
        controller.react();

    }

    @Override
    public void notifyStateChange() {
        switch (MiniModel.getInstance().getGamePhase()) {
            case LOBBY:

                break;
            case BUILDING:
                this.setBuildingScene();
                break;
            case VALIDATION:
                this.setValidationScene();
                break;
            case CREW:
                this.setCrewScene();
                break;
            case CARDS:
                break;
            case REWARD:
                break;
            case FINISHED:
                this.setMenuScene();
                countDownStarted = false;
                break;
        }
    }

    private void setMenuScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screens/menu.fxml"));
            root = loader.load();

            controller = loader.getController();
            controller.react();

            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        currentScene = GuiScene.MENU;
    }

    private void setLobbyScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screens/lobby.fxml"));
            root = loader.load();

            controller = loader.getController();
            controller.react();

            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        currentScene = GuiScene.LOBBY;
    }

    private void setBuildingScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screens/building.fxml"));
            root = loader.load();

            controller = loader.getController();
            controller.react();

            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        currentScene = GuiScene.BUILDING;
    }

    private void setCardsGameScene() {
        currentScene = GuiScene.CARDS;
    }

    private void setValidationScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screens/validation.fxml"));
            root = loader.load();

            controller = loader.getController();
            controller.react();

            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        currentScene = GuiScene.VALIDATION;
    }

    private void setCrewScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screens/crew.fxml"));
            root = loader.load();

            controller = loader.getController();
            controller.react();

            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        currentScene = GuiScene.CREW;
    }

    private void setRewardScene() {
        currentScene = GuiScene.REWARD;
    }
}