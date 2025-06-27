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
import it.polimi.ingsw.view.gui.controllers.misc.MessageController;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.cards.CardViewType;
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
    private String rollDice;

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
        stage.setWidth(1024);
        stage.setHeight(768);
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
            Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), data.nickname() + " has created a new lobby"));
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
            Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), data.nickname() + " has joined the lobby"));
            controller.react();
        }
    }

    @Override
    public void notifyLobbyLeft(LobbyLeft data) {
        if (mm.getNickname().equals(data.nickname())) {
            this.setMenuScene();
        }
        else {
            Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), data.nickname() + " has left the lobby"));
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
        if (data.usage() == 0) {
            Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), data.nickname() + " has picked deck " + (data.deckIndex() + 1)));
        }
    }

    @Override
    public void notifyDiceRolled(DiceRolled data) {
        Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), data.nickname() + " rolled the dice: " + data.diceValue1() + " + " + data.diceValue2() + " = " + (data.diceValue1() + data.diceValue2())));
        controller.react();
    }

    @Override
    public void notifyBatteriesLoss(BatteriesLoss data) {
        controller.react();
    }

    @Override
    public void notifyForcingGiveUp(ForcingGiveUp data) {
        controller.react();

        String message;
        if (mm.getNickname().equals(data.nickname())) {
            message = data.message();
        } else {
            message = data.nickname() + " is forced to give up";
        }
        Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), message));
    }

    @Override
    public void notifyForcingPenalty(ForcingPenalty data) {
        controller.react();

        String message = "";
        if (mm.getNickname().equals(data.nickname())) {
            message = switch (data.penaltyType()) {
                case 0 -> "You have to leave crew members";
                case 1 -> "You have to discard goods";
                case 2 -> "You have no more goods, you must discard batteries";
                case 3 -> "New hit is coming! Good luck";
                default -> message;
            };
        } else {
            message = "Waiting for " + data.nickname() + " to manage the penalty";
        }
        String finalMessage = message;
        Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), finalMessage));
    }

    @Override
    public void notifyForcingPlaceMarker(ForcingPlaceMarker data) {
        Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), "You have to place a marker on the board"));
    }

    @Override
    public void notifyUpdateGoodsExchange(UpdateGoodsExchange data) {
        Platform.runLater(() ->MessageController.showInfoMessage(scene.getWindow(), data.nickname() + " has modified own goods"));
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
        Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), data.nickname() + " has selected planet " + (data.planetNumber() + 1)));
    }

    @Override
    public void notifyCardPlayed(CardPlayed data) {
        controller.react();
        Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), data.nickname() + " has accepted the card"));
    }

    @Override
    public void notifyCombatZonePhase(CombatZonePhase data) {
        controller.react();

    }

    @Override
    public void notifyEnemyDefeat(EnemyDefeat data) {
        controller.react();

        String message;
        if (data.enemyDefeat() == null) {
            message = "It's a tie! Enemies lose interest... and seek a new target.";
        } else if (data.enemyDefeat()) {
            message = data.nickname() + " has defeated enemies! Everyone is safe";
        } else {
            if (mm.getNickname().equals(data.nickname())) {

                CardViewType cardViewType = mm.getShuffledDeckView().getDeck().peek().getCardViewType();
                if (cardViewType == CardViewType.PIRATES) {
                    message = "You have lost the fight against pirates, prepare your defenses! At the end of the turn you will have to avoid their fires!";
                } else {
                    message = "";
                }
            } else {
                message = data.nickname() + " has lost! Enemies are seeking a new target";
            }
        }

        if (!message.isEmpty()) {
            Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), message));
        }
    }

    @Override
    public void notifyMinPlayer(MinPlayer data) {
        controller.react();

    }

    @Override
    public void notifyMoveMarker(MoveMarker data) {
        controller.react();
        Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), data.nickname() + " has moved"));

    }

    @Override
    public void notifyRemoveMarker(RemoveMarker data) {
        Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), data.nickname() + " has remove the marker"));
    }

    @Override
    public void notifyPlayerGaveUp(PlayerGaveUp data) {
        controller.react();
        Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), data.nickname() + " has given up"));
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
        Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), data.nickname() + " has updated coins"));
    }

    @Override
    public void notifyRotatedTile(RotatedTile data) {
        controller.react();

    }

    @Override
    public void notifyBestLookingShips(BestLookingShips data) {
        controller.react();
        StringBuilder message = new StringBuilder();
        if (data.nicknames().size() == 1) {
            message.append(data.nicknames().getFirst()).append(" has the best looking ship!");
        }
        else {
            message.append("The best looking ships are:\n");
            for (int i = 0; i < data.nicknames().size(); i++) {
                message.append(data.nicknames().get(i));
                if (i != data.nicknames().size() - 1) message.append(", ");
            }
        }
        Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), message.toString()));
    }

    @Override
    public void notifyCanProtect(CanProtect data) {
        controller.react();
        if (data.nickname().equals(mm.getNickname())) {
            String message;
            message = switch (data.canProtect().getValue1()) {
                case -1 -> rollDice + " -> You can't protect from the hit";
                case 0 -> rollDice + " -> You can protect from the hit, select a battery to use";
                case 1 -> rollDice + " -> You don't need to protect from the hit";
                default -> "";
            };
            Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), message));
        } else {
            Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), data.nickname() + " is deciding"));
        }
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
        Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), "Crew on " + data.nickname() + "'s spaceship is changed"));
    }

    @Override
    public void notifyTimer(TimerFlipped data, boolean firstSecond) {
        controller.react();
        if (firstSecond && data.nickname() != null) {
            Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), "Timer flipped by " + data.nickname()));
        }
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
        switch (mm.getGamePhase()) {
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
                if (currentScene != GuiScene.CARDS) {
                    this.setCardsGameScene();
                }
                break;
            case REWARD:
                break;
            case FINISHED:
                this.setMenuScene();
                countDownStarted = false;
                Platform.runLater(() -> MessageController.showInfoMessage(scene.getWindow(), "You are back to the lobbies menu, a player disconnected or the game is over"));
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

    private void setCardsGameScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screens/cardsGame.fxml"));
            root = loader.load();

            controller = loader.getController();
            controller.react();

            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        currentScene = GuiScene.CARDS;
    }

    private void setRewardScene() {
        currentScene = GuiScene.REWARD;
    }
}