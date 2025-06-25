package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.event.game.serverToClient.deck.PickedLeftDeck;
import it.polimi.ingsw.event.game.serverToClient.dice.DiceRolled;
import it.polimi.ingsw.event.game.serverToClient.energyUsed.BatteriesLoss;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingGiveUp;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingPenalty;
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

    private enum GuiScene {
        LOGIN,
        MENU,
        LOBBY,
        BUILDING,
        VALIDATION,
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
        controller.react();
    }

    @Override
    public void notifyPickedLeftDeck(PickedLeftDeck data) {

    }

    @Override
    public void notifyDiceRolled(DiceRolled data) {

    }

    @Override
    public void notifyBatteriesLoss(BatteriesLoss data) {

    }

    @Override
    public void notifyForcingGiveUp(ForcingGiveUp data) {

    }

    @Override
    public void notifyForcingPenalty(ForcingPenalty data) {

    }

    @Override
    public void notifyUpdateGoodsExchange(UpdateGoodsExchange data) {

    }

    @Override
    public void notifyPickedTileFromBoard() {

    }

    @Override
    public void notifyPickedTileFromSpaceShip(PickedTileFromSpaceship data) {

    }

    @Override
    public void notifyPickedHiddenTile(String nickname) {

    }

    @Override
    public void notifyPlacedTileToBoard(PlacedTileToBoard data) {

    }

    @Override
    public void notifyPlacedTileToReserve(PlacedTileToReserve data) {

    }

    @Override
    public void notifyPlacedTileToSpaceship(PlacedTileToSpaceship data) {

    }

    @Override
    public void notifyPlanetSelected(PlanetSelected data) {

    }

    @Override
    public void notifyCardPlayed(CardPlayed data) {

    }

    @Override
    public void notifyCombatZonePhase(CombatZonePhase data) {

    }

    @Override
    public void notifyEnemyDefeat(EnemyDefeat data) {

    }

    @Override
    public void notifyMinPlayer(MinPlayer data) {

    }

    @Override
    public void notifyMoveMarker(MoveMarker data) {

    }

    @Override
    public void notifyPlayerGaveUp(PlayerGaveUp data) {

    }

    @Override
    public void notifyCurrentPlayer(CurrentPlayer data) {

    }

    @Override
    public void notifyScore(Score data) {

    }

    @Override
    public void notifyUpdateCoins(UpdateCoins data) {

    }

    @Override
    public void notifyRotatedTile(RotatedTile data) {

    }

    @Override
    public void notifyBestLookingShips(BestLookingShips data) {

    }

    @Override
    public void notifyCanProtect(CanProtect data) {

    }

    @Override
    public void notifyComponentDestroyed(ComponentDestroyed data) {

    }

    @Override
    public void notifyFragments(Fragments data) {

    }

    @Override
    public void notifyInvalidComponents(InvalidComponents data) {

    }

    @Override
    public void notifySetCannonStrength(SetCannonStrength data) {

    }

    @Override
    public void notifySetEngineStrength(SetEngineStrength data) {

    }

    @Override
    public void notifyUpdateCrewMembers(UpdateCrewMembers data) {

    }

    @Override
    public void notifyTimer(TimerFlipped data, boolean firstSecond) {

    }

    @Override
    public void notifyTimerFinished(TimerFlipped data) {

    }

    @Override
    public void notifyLastTimerFlipped() {

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
                break;
            case CREW:
                break;
            case CARDS:
                break;
            case REWARD:
                break;
            case FINISHED:
                this.setMenuScene();
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

    private void setRewardScene() {
        currentScene = GuiScene.REWARD;
    }
}