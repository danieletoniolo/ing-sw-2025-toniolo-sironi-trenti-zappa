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

/**
 * Main GUI manager class for the application.
 * Extends JavaFX Application and implements the Manager interface.
 * Handles scene transitions, event notifications, and GUI updates.
 */
public class GuiManager extends Application implements Manager {
    /** Main JavaFX scene for the application. */
    private static Scene scene;
    /** Root node of the current scene. */
    private static Parent root;
    /** Controller observing the MiniModel for GUI updates. */
    private static MiniModelObserver controller;
    /** Singleton instance of the MiniModel. */
    private final MiniModel mm = MiniModel.getInstance();
    /** Flag indicating if the countdown has started. */
    private boolean countDownStarted = false;
    /** Stores the result of the last dice roll as a string. */
    private String rollDice;

    /**
     * Enum representing the different scenes in the GUI.
     */
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

    /**
     * Constructs a new GuiManager and initializes the root node as a StackPane.
     */
    public GuiManager() {
        root = new StackPane();
    }

    public static Scene getScene() {
        return scene;
    }

    /**
     * Starts the JavaFX application, sets up the initial login scene, and initializes the controller.
     *
     * @param stage the primary stage for this application
     */
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

    /**
     * Notifies that the nickname has been set and transitions to the menu scene.
     *
     * @param data the NicknameSet event data
     */
    @Override
    public void notifyNicknameSet(NicknameSet data) {
        this.setMenuScene();
    }

    /**
     * Notifies that the connection has been lost and transitions to the menu scene.
     */
    @Override
    public void notifyConnectionLost() {
        this.setMenuScene();
    }

    /**
     * Notifies that the list of lobbies has been updated and triggers a GUI update.
     */
    @Override
    public void notifyLobbies() {
        Platform.runLater(() -> {
            controller.react();
        });
    }

    /**
     * Notifies that a new lobby has been created.
     * Updates the GUI to the lobby scene if the current user created the lobby,
     * otherwise shows an informational message and updates the lobby list.
     *
     * @param data the LobbyCreated event data
     */
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

    /**
     * Notifies that a player has joined a lobby.
     * If the current user is the one who joined, transitions to the lobby scene.
     * Otherwise, shows an informational message and updates the lobby view.
     *
     * @param data the LobbyJoined event data
     */
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

    /**
     * Notifies that a player has left the lobby.
     * If the current user is the one who left, transitions to the menu scene.
     * Otherwise, shows an informational message and updates the lobby view.
     *
     * @param data the LobbyLeft event data
     */
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

    /**
     * Notifies that a lobby has been removed.
     * If the current scene is the lobby, transitions to the menu scene.
     * Otherwise, triggers a GUI update.
     *
     * @param data the LobbyRemoved event data
     */
    @Override
    public void notifyLobbyRemoved(LobbyRemoved data) {
        if (currentScene == GuiScene.LOBBY) {
            this.setMenuScene();
        }
        else {
            controller.react();
        }
    }

    /**
     * Notifies that a player is ready in the lobby.
     * Triggers a GUI update.
     */
    @Override
    public void notifyReadyPlayer() {
        controller.react();
    }

    /**
     * Notifies that the countdown has started.
     * Ensures the countdown is only started once and triggers a GUI update.
     */
    @Override
    public void notifyCountDown() {
        if (!countDownStarted) {
            countDownStarted = true;
            controller.react();
        }
    }

    /**
     * Notifies that a player has picked or left the left deck.
     * Updates the GUI and shows an informational message based on the action.
     *
     * @param data the PickedLeftDeck event data
     */
    @Override
    public void notifyPickedLeftDeck(PickedLeftDeck data) {
        controller.react();
        if (data.usage() == 0) {
            Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has picked deck " + (data.deckIndex() + 1)));
        } else if (data.usage() == 1) {
            Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has left the deck " + (data.deckIndex() + 1)));
        }
    }

    /**
     * Notifies that the dice have been rolled.
     * Shows the result in an informational message and updates the GUI.
     *
     * @param data the DiceRolled event data
     */
    @Override
    public void notifyDiceRolled(DiceRolled data) {
        rollDice = (mm.getNickname().equals(mm.getClientPlayer().getUsername()) ? "" : data.nickname() + " ") + "Rolled the dice: " + data.diceValue1() + " + " + data.diceValue2() + " = " + (data.diceValue1() + data.diceValue2());
        Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " rolled the dice: " + data.diceValue1() + " + " + data.diceValue2() + " = " + (data.diceValue1() + data.diceValue2())));
        controller.react();
    }

    /**
     * Notifies that there has been a battery loss.
     * Triggers a GUI update.
     *
     * @param data the BatteriesLoss event data
     */
    @Override
    public void notifyBatteriesLoss(BatteriesLoss data) {
        controller.react();
    }

    /**
     * Notifies that a player is being forced to give up.
     * Updates the GUI and shows an appropriate informational message.
     *
     * @param data the ForcingGiveUp event data
     */
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

    /**
     * Notifies that a penalty must be managed by a player.
     * Shows a specific message depending on the penalty type and whether the current user is involved.
     *
     * @param data the ForcingPenalty event data containing the penalty type and the player nickname
     */
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

    /**
     * Notifies that a player is being forced to place a marker on the board.
     * Shows a message if the current user is the one required to act.
     *
     * @param data the ForcingPlaceMarker event data
     */
    @Override
    public void notifyForcingPlaceMarker(ForcingPlaceMarker data) {
        if (mm.getNickname().equals(data.nickname())) {
            Platform.runLater(() -> MessageController.showInfoMessage("You have to place a marker on the board"));
        }
    }

    /**
     * Notifies that the goods exchange has been updated.
     * If the exchange data is not empty, shows an informational message.
     * Always triggers a GUI update.
     *
     * @param data the UpdateGoodsExchange event data
     */
    @Override
    public void notifyUpdateGoodsExchange(UpdateGoodsExchange data) {
        if (!data.exchangeData().isEmpty()) {
            Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has modified own goods"));
        }
        controller.react();

    }

    /**
     * Notifies that a tile has been picked from the board.
     * Triggers a GUI update.
     */
    @Override
    public void notifyPickedTileFromBoard() {
        controller.react();
    }

    /**
     * Notifies that a tile has been picked from the spaceship.
     * Triggers a GUI update.
     *
     * @param data the PickedTileFromSpaceship event data
     */
    @Override
    public void notifyPickedTileFromSpaceShip(PickedTileFromSpaceship data) {
        controller.react();
    }

    /**
     * Notifies that a hidden tile has been picked.
     * Triggers a GUI update.
     *
     * @param nickname the nickname of the player who picked the hidden tile
     */
    @Override
    public void notifyPickedHiddenTile(String nickname) {
        controller.react();

    }

    /**
     * Notifies that a tile has been placed on the board.
     * Triggers a GUI update.
     *
     * @param data the PlacedTileToBoard event data
     */
    @Override
    public void notifyPlacedTileToBoard(PlacedTileToBoard data) {
        controller.react();

    }

    /**
     * Notifies that a tile has been placed in the reserve.
     * Triggers a GUI update.
     *
     * @param data the PlacedTileToReserve event data
     */
    @Override
    public void notifyPlacedTileToReserve(PlacedTileToReserve data) {
        controller.react();

    }

    /**
     * Notifies that a tile has been placed on the spaceship.
     * Triggers a GUI update.
     *
     * @param data the PlacedTileToSpaceship event data
     */
    @Override
    public void notifyPlacedTileToSpaceship(PlacedTileToSpaceship data) {
        controller.react();
    }

    /**
     * Notifies that a planet has been selected by a player.
     * Triggers a GUI update and shows an informational message.
     *
     * @param data the PlanetSelected event data
     */
    @Override
    public void notifyPlanetSelected(PlanetSelected data) {
        controller.react();
        Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has selected planet " + (data.planetNumber() + 1)));
    }

    /**
     * Notifies that a card has been played.
     * Triggers a GUI update and shows an informational message.
     *
     * @param data the CardPlayed event data
     */
    @Override
    public void notifyCardPlayed(CardPlayed data) {
        controller.react();
        Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has accepted the card"));
    }

    /**
     * Notifies that the combat zone phase has started.
     * Triggers a GUI update.
     *
     * @param data the CombatZonePhase event data
     */
    @Override
    public void notifyCombatZonePhase(CombatZonePhase data) {
        mm.setCombatZonePhase(data.phaseNumber());
        controller.react();
    }

    /**
     * Notifies that an enemy defeat event has occurred.
     * Updates the GUI and displays an appropriate message based on the outcome.
     *
     * @param data the EnemyDefeat event data
     */
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

    /**
     * Notifies that the minimum player has been determined.
     * Updates the GUI accordingly.
     *
     * @param data the MinPlayer event containing information about the minimum player
     */
    @Override
    public void notifyMinPlayer(MinPlayer data) {
        controller.react();

    }

    /**
     * Notifies that a marker has been moved by a player.
     * Updates the GUI and shows an informational message.
     *
     * @param data the MoveMarker event containing information about the movement
     */
    @Override
    public void notifyMoveMarker(MoveMarker data) {
        controller.react();
        Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has moved"));

    }

    /**
     * Notifies that a marker has been removed by a player.
     * Shows an informational message in the GUI.
     *
     * @param data the RemoveMarker event containing information about the removal
     */
    @Override
    public void notifyRemoveMarker(RemoveMarker data) {
        Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has remove the marker"));
    }

    /**
     * Notifies that a player has given up.
     * Updates the GUI and shows an informational message.
     *
     * @param data the PlayerGaveUp event containing information about the player who gave up
     */
    @Override
    public void notifyPlayerGaveUp(PlayerGaveUp data) {
        controller.react();
        Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has given up"));
    }

    /**
     * Notifies that the current player has been updated.
     * Updates the GUI and shows an informational message about the turn.
     *
     * @param data the CurrentPlayer event containing information about the current player
     */
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

    /**
     * Notifies that the score has been updated.
     * Triggers a GUI update.
     *
     * @param data the Score event data
     */
    @Override
    public void notifyScore(Score data) {
        controller.react();
    }

    /**
     * Notifies that the coins have been updated for a player.
     * Triggers a GUI update and shows an informational message.
     *
     * @param data the UpdateCoins event data
     */
    @Override
    public void notifyUpdateCoins(UpdateCoins data) {
        controller.react();
        Platform.runLater(() -> MessageController.showInfoMessage(data.nickname() + " has updated coins"));
    }

    /**
     * Notifies that a tile has been rotated.
     * Triggers a GUI update.
     *
     * @param data the RotatedTile event data
     */
    @Override
    public void notifyRotatedTile(RotatedTile data) {
        controller.react();
    }

    /**
     * Notifies that the best looking ships have been determined.
     * Triggers a GUI update and shows an informational message.
     *
     * @param data the BestLookingShips event data
     */
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

    /**
     * Notifies that a player can protect from a hit.
     * Shows a message to the current user if they are the one who can protect, otherwise informs that another player is deciding.
     *
     * @param data the CanProtect event data containing the protection status and the player's nickname
     */
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

    /**
     * Notifies that a component has been destroyed.
     * Triggers a GUI update.
     *
     * @param data the ComponentDestroyed event data
     */
    @Override
    public void notifyComponentDestroyed(ComponentDestroyed data) {
        controller.react();

    }

    /**
     * Notifies that fragments have been updated.
     * Triggers a GUI update.
     *
     * @param data the Fragments event data
     */
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

    /**
     * Notifies that invalid components have been detected.
     * Triggers a GUI update.
     *
     * @param data the InvalidComponents event data
     */
    @Override
    public void notifyInvalidComponents(InvalidComponents data) {
        controller.react();

    }

    /**
     * Notifies that the cannon strength has been set.
     * Triggers a GUI update.
     *
     * @param data the SetCannonStrength event data
     */
    @Override
    public void notifySetCannonStrength(SetCannonStrength data) {
        controller.react();

    }

    /**
     * Notifies that the engine strength has been set.
     * Triggers a GUI update.
     *
     * @param data the SetEngineStrength event data
     */
    @Override
    public void notifySetEngineStrength(SetEngineStrength data) {
        controller.react();

    }

    /**
     * Notifies that the crew members have been updated.
     * Triggers a GUI update and shows an informational message.
     *
     * @param data the UpdateCrewMembers event data
     */
    @Override
    public void notifyUpdateCrewMembers(UpdateCrewMembers data) {
        controller.react();
        Platform.runLater(() -> MessageController.showInfoMessage("Crew on " + data.nickname() + "'s spaceship is changed"));
    }

    /**
     * Notifies that the timer has been flipped.
     * Triggers a GUI update and shows an informational message if it is the first second and a nickname is present.
     *
     * @param data the TimerFlipped event data
     * @param firstSecond true if it is the first second after the timer was flipped
     */
    @Override
    public void notifyTimer(TimerFlipped data, boolean firstSecond) {
        controller.react();
        if (firstSecond && data.nickname() != null) {
            Platform.runLater(() -> MessageController.showInfoMessage("Timer flipped by " + data.nickname()));
        }
    }

    /**
     * Notifies that the timer has finished.
     * Triggers a GUI update.
     *
     * @param data the TimerFlipped event data
     */
    @Override
    public void notifyTimerFinished(TimerFlipped data) {
        controller.react();

    }

    /**
     * Notifies that the last timer has been flipped.
     * Triggers a GUI update.
     */
    @Override
    public void notifyLastTimerFlipped() {
        controller.react();
    }

    /**
     * Notifies that the game state has changed.
     * Updates the GUI according to the new game phase.
     */
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

    /**
     * Sets the scene to the main menu.
     * Loads the menu FXML, updates the root node, initializes the controller,
     * and updates the current scene state to MENU.
     */
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

    /**
     * Sets the scene to the lobby view.
     * Loads the lobby FXML, updates the root node, initializes the controller,
     * and updates the current scene state to LOBBY.
     */
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

    /**
     * Sets the scene to the building phase.
     * Loads the building FXML, updates the root node, initializes the controller,
     * and updates the current scene state to BUILDING.
     */
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

    /**
     * Sets the scene to the validation phase.
     * Loads the validation FXML, updates the root node, initializes the controller,
     * and updates the current scene state to VALIDATION.
     */
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

    /**
     * Sets the scene to the crew phase.
     * Loads the crew FXML, updates the root node, initializes the controller,
     * and updates the current scene state to CREW.
     */
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

    /**
     * Sets the scene to the cards game phase.
     * Loads the cards game FXML, updates the root node, initializes the controller,
     * and updates the current scene state to CARDS.
     */
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

    /**
     * Sets the scene to the reward phase.
     * Loads the reward FXML, updates the root node, initializes the controller,
     * and updates the current scene state to REWARD.
     */
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