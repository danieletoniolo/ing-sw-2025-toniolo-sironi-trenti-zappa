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
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.view.Manager;
import it.polimi.ingsw.view.gui.controllers.misc.MessageController;
import it.polimi.ingsw.view.gui.screens.CardsGameController;
import it.polimi.ingsw.view.miniModel.GamePhases;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.cards.CardView;
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
import java.util.Objects;

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

    public static Scene getScene() {
        return scene;
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
        stage.setOnCloseRequest(_ -> {
            Platform.exit();
            System.exit(0);
        });
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
            Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has created a new lobby"));
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
            Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has joined the lobby"));
            controller.react();
        }
    }

    @Override
    public void notifyLobbyLeft(LobbyLeft data) {
        if (mm.getNickname().equals(data.nickname())) {
            this.setMenuScene();
        }
        else {
            Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has left the lobby"));
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
            Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has picked deck " + (data.deckIndex() + 1)));
        } else if (data.usage() == 1) {
            Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has left the deck " + (data.deckIndex() + 1)));
        }
    }

    @Override
    public void notifyDiceRolled(DiceRolled data) {
        rollDice = (mm.getNickname().equals(mm.getClientPlayer().getUsername()) ? "" : data.nickname() + " ") + "Rolled the dice: " + data.diceValue1() + " + " + data.diceValue2() + " = " + (data.diceValue1() + data.diceValue2());
        Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " rolled the dice: " + data.diceValue1() + " + " + data.diceValue2() + " = " + (data.diceValue1() + data.diceValue2())));
        controller.react();
    }

    @Override
    public void notifyBatteriesLoss(BatteriesLoss data) {
        controller.react();
    }

    @Override
    public void notifyForcingGiveUp(ForcingGiveUp data) {
        String message;
        if (mm.getNickname().equals(data.nickname())) {
            message = data.message();
            CardsGameController.actionGiveUp();
        } else {
            message = data.nickname() + " is forced to give up. Waiting for his turn...";
            CardsGameController.waitingActionState();
        }
        Platform.runLater(() -> MessageController.showInfoMessage(message));
        controller.react();
    }

    @Override
    public void notifyForcingPenalty(ForcingPenalty data) {
        controller.react();

        String message = "";
        if (mm.getNickname().equals(data.nickname())) {
            switch (data.penaltyType()) {
                case 0:
                    message = "You have to leave crew members";
                    CardsGameController.actionCabins();
                    break;
                case 1:
                    message = "You have to discard goods";
                    CardsGameController.actionDiscardGoods();
                    break;
                case 2:
                    message = "You have no more goods, you must discard batteries";
                    CardsGameController.actionDiscardBatteries();
                    break;
                case 3:
                    message = "New hit is coming! Good luck";
                    CardsGameController.actionRollDice();
                    break;
            }
        } else {
            CardsGameController.resetActionState();
            message = "Waiting for " + data.nickname() + " to manage the penalty";
        }
        String finalMessage = message;
        Platform.runLater(() -> MessageController.showInfoMessage(finalMessage));
    }

    @Override
    public void notifyForcingPlaceMarker(ForcingPlaceMarker data) {
        if (mm.getNickname().equals(data.nickname())) {
            Platform.runLater(() -> MessageController.showInfoMessage("You have to place a marker on the board"));
        }
    }

    @Override
    public void notifyUpdateGoodsExchange(UpdateGoodsExchange data) {
        if (!data.exchangeData().isEmpty()) {
            Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has modified own goods"));
        }
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
        Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has selected planet " + (data.planetNumber() + 1)));
    }

    @Override
    public void notifyCardPlayed(CardPlayed data) {
        controller.react();
        Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has accepted the card"));
    }

    @Override
    public void notifyCombatZonePhase(CombatZonePhase data) {
        mm.setCombatZonePhase(data.phaseNumber());
        controller.react();
    }

    @Override
    public void notifyEnemyDefeat(EnemyDefeat data) {
        String message;

        CardsGameController.resetActionState();
        if (data.enemyDefeat() == null) {
            if (mm.getNickname().equals(data.nickname())) {
                CardsGameController.waitingActionState();
            }
            message = "It's a tie! Enemies lose interest... and seek a new target.";
        } else if (data.enemyDefeat()) {
            if (mm.getNickname().equals(data.nickname())) {
                CardsGameController.actionAccept();
            }
            message = data.nickname() + " has defeated enemies! Everyone is safe";
        } else {
            if (mm.getNickname().equals(data.nickname())) {
                CardViewType cardViewType = mm.getShuffledDeckView().getDeck().peek().getCardViewType();
                if (Objects.requireNonNull(cardViewType) == CardViewType.SLAVERS) {
                    message = "";
                    CardsGameController.actionCabins();
                } else if (cardViewType == CardViewType.SMUGGLERS) {
                    message = "";
                    CardsGameController.actionDiscardGoods();
                } else if (cardViewType == CardViewType.PIRATES) {
                    message = "You have lost the fight against pirates, prepare your defenses! At the end of the turn you will have to avoid their fires!";
                } else {
                    CardsGameController.waitingActionState();
                    message = "Waiting for other players to play...";
                }
            } else {
                CardsGameController.waitingActionState();
                message = data.nickname() + " has lost! Enemies are seeking a new target";
            }
        }

        controller.react();
        if (!message.isEmpty()) {
            Platform.runLater(() -> MessageController.showInfoMessage(message));
        }
    }

    @Override
    public void notifyMinPlayer(MinPlayer data) {
        controller.react();

    }

    @Override
    public void notifyMoveMarker(MoveMarker data) {
        controller.react();
        Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has moved"));

    }

    @Override
    public void notifyRemoveMarker(RemoveMarker data) {
        Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has remove the marker"));
    }

    @Override
    public void notifyPlayerGaveUp(PlayerGaveUp data) {
        controller.react();
        Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has given up"));
    }

    @Override
    public void notifyCurrentPlayer(CurrentPlayer data) {
        String message;

        if (!mm.getNickname().equals(data.nickname())) {
            message = "Waiting for " + data.nickname() + " to play";
            CardsGameController.waitingActionState();
        } else {
            message = "Your turn!";
            CardView card = mm.getShuffledDeckView().getDeck().peek();
            switch (card.getCardViewType()) {
                case OPENSPACE:
                    CardsGameController.actionEngine();
                    message += " Select the double engine to use or end turn";
                    break;
                case SLAVERS:
                case SMUGGLERS:
                case PIRATES:
                    CardsGameController.actionCannon();
                    message += " Select the double cannon to use or end turn";
                    break;
                case STARDUST:
                    CardsGameController.resetActionState();
                    message += " End turn in order to play the Stardust card";
                    break;
                case EPIDEMIC:
                    CardsGameController.resetActionState();
                    message += " End turn in order to play the Epidemic card";
                    break;
                case PLANETS:
                case ABANDONEDSHIP:
                case ABANDONEDSTATION:
                    CardsGameController.actionAccept();
                    message += " Choose if accept the card! Otherwise, you can end your turn";
                    break;
                case METEORSSWARM:
                    CardsGameController.actionRollDice();
                    message += " Roll the dice for all the player";
                    break;
                case COMBATZONE:
                    switch (mm.getCombatZonePhase()) {
                        case 0:
                            if (mm.getShuffledDeckView().getDeck().peek().getLevel() == 1) {
                                CardsGameController.resetActionState();
                                message += " Take the penalty! You are the player with the lowest number of crew";
                            } else {
                                CardsGameController.actionCannon();
                                message += " Select the double cannon to use or end turn";
                            }
                            break;
                        case 1:
                            CardsGameController.actionEngine();
                            message += " Select the double engine to use or end turn";
                            break;
                        case 2:
                            if (mm.getShuffledDeckView().getDeck().peek().getLevel() == 1) {
                                CardsGameController.actionCannon();
                                message += " Select the double cannon to use or end turn";
                            } else {
                                message = "";
                            }
                            break;
                    }
            }
        }

        controller.react();

        if (!message.isEmpty()) {
            String finalMessage = message;
            Platform.runLater(() -> MessageController.showInfoMessage(finalMessage));
        }
    }

    @Override
    public void notifyScore(Score data) {
        controller.react();
    }

    @Override
    public void notifyUpdateCoins(UpdateCoins data) {
        controller.react();
        Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has updated coins"));
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
        Platform.runLater(() -> MessageController.showInfoMessage(message.toString()));
    }

    @Override
    public void notifyCanProtect(CanProtect data) {
        controller.react();
        if (data.nickname().equals(mm.getNickname())) {
            String message = rollDice;
            switch (data.canProtect().getValue1()) {
                case -1:
                    CardsGameController.actionCantProtect();
                    message += " -> You can't protect from the hit";
                    break;
                case 0:
                    CardsGameController.actionShield();
                    message += " -> You can protect from the hit, select a battery to use";
                    break;
                case 1:
                    CardsGameController.actionProtectionNotRequired();
                    message += " -> You don't need to protect from the hit";
                    break;
            };

            String finalMessage = message;
            Platform.runLater(() -> MessageController.showInfoMessage(finalMessage));
        } else {
            Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " is deciding"));
        }
    }

    @Override
    public void notifyComponentDestroyed(ComponentDestroyed data) {
        controller.react();

    }

    @Override
    public void notifyFragments(Fragments data) {
        if (data.nickname().equals(mm.getNickname())) {
            if (data.fragments().size() > 1) {
                if (mm.getGamePhase() == GamePhases.CARDS) {
                    CardsGameController.actionFragments();
                }
            }
        }
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
        Platform.runLater(() -> MessageController.showInfoMessage("Crew on " + data.nickname() + "'s spaceship is changed"));
    }

    @Override
    public void notifyTimer(TimerFlipped data, boolean firstSecond) {
        controller.react();
        if (firstSecond && data.nickname() != null) {
            Platform.runLater(() -> MessageController.showInfoMessage("Timer flipped by " + data.nickname()));
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
                this.setRewardScene();
                break;
            case FINISHED:
                this.setMenuScene();
                countDownStarted = false;
                Platform.runLater(() -> MessageController.showInfoMessage("You are back to the lobbies menu, a player disconnected or the game is over"));
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screens/reward.fxml"));
            root = loader.load();

            controller = loader.getController();
            controller.react();

            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        currentScene = GuiScene.REWARD;
    }
}