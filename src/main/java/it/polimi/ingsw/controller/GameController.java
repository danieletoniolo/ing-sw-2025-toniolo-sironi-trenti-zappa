package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.lobby.LobbyInfo;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.state.LobbyState;
import it.polimi.ingsw.model.state.State;
import it.polimi.ingsw.model.state.exception.SynchronousStateException;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.*;

/**
 * Controller class that manages the game state and handles player actions.
 * Implements the State pattern to manage different game phases.
 * @author Vittorio Sironi
 */
public class GameController implements Serializable, StateTransitionHandler {
    /** Current state of the game */
    private State state;
    /** Event manager for handling server-side events */
    private final ServerEventManager eventManager;
    /** Unique identifier for this game controller instance */
    private final UUID uuid = UUID.randomUUID();

    /**
     * Constructs a new GameController with the specified board and lobby information.
     * Initializes the game in LobbyState.
     *
     * @param board the game board
     * @param lobbyInfo information about the lobby
     */
    public GameController(Board board, LobbyInfo lobbyInfo) {
        this.eventManager = new ServerEventManager(lobbyInfo);
        this.state = new LobbyState(board, this.eventManager, this);
    }

    /**
     * Returns the string representation of this controller using its UUID.
     *
     * @return the UUID as a string
     */
    @Override
    public String toString() {
        return uuid.toString();
    }

    /**
     * Changes the current state of the game and triggers the entry action.
     *
     * @param newState the new state to transition to
     */
    @Override
    public void changeState(State newState) {
        state = newState;
        state.entry();
    }

    /**
     * Returns the unique identifier for this game controller instance.
     *
     * @return the UUID of this controller
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Starts the game with the specified start time and timer duration.
     * Delegates the action to the current state.
     *
     * @param startTime the time when the game should start
     * @param timerDuration the duration of the timer in the game
     * @throws IllegalStateException if the current state does not allow starting the game
     */
    public void startGame(LocalTime startTime, int timerDuration) {
        try {
            state.startGame(startTime, timerDuration);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot start game in this state: " + e.getMessage() + ". Current state: " + state.getClass().getSimpleName());
        }
    }

    /**
     * Manages lobby operations for a player.
     * Delegates the action to the current state.
     *
     * @param player the player performing the lobby action
     * @param type the type of lobby management operation
     * @throws IllegalStateException if the current state is not a joinable game
     */
    public void manageLobby(PlayerData player, int type) {
        try {
            state.manageLobby(player, type);
        } catch (Exception e) {
            throw new IllegalStateException("State is not a JoinableGame");
        }
    }

    // Game actions

    /**
     * It will set the player to PLAYING (it is like starting a turn).
     * @param player                 the player that wants to play
     * @throws IllegalStateException if the current state does not allow playing
     */
    public void play(PlayerData player) throws IllegalStateException {
        try {
            state.play(player);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Ends the current player's turn and executes their actions.
     * Validates that the requesting player is the current player before proceeding.
     * In synchronous states, the current player validation is skipped.
     *
     * @param player the player attempting to end their turn
     * @throws IllegalStateException if the player is not the current player or if execution fails
     */
    public void endTurn(PlayerData player) {
        try {
            PlayerData currentPlayer = state.getCurrentPlayer();
            if (!currentPlayer.equals(player)) {
                throw new IllegalStateException("Not the current player");
            }
        } catch (SynchronousStateException e) {
            // Ignore the exception, it is expected in synchronous states
        }

        try {
            state.execute(player);
        } catch (Exception exception) {
            throw new IllegalStateException(exception.getMessage());
        }
    }

    /**
     * Allows a player to use a deck with specified usage parameters.
     * Delegates the action to the current state.
     *
     * @param player the player using the deck
     * @param usage the type of usage for the deck
     * @param deckIndex the index of the deck to use
     * @throws IllegalStateException if the current state does not allow deck usage or if the operation fails
     */
    public void useDeck(PlayerData player, int usage, int deckIndex) throws IllegalStateException{
        try {
            state.useDeck(player, usage, deckIndex);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Allows a player to pick a tile from a specified location.
     * Delegates the action to the current state.
     *
     * @param player the player picking the tile
     * @param fromWhere the location from where to pick the tile
     * @param tileID the ID of the tile to pick
     * @throws IllegalStateException if the current state does not allow tile picking or if the operation fails
     */
    public void pickTile(PlayerData player, int fromWhere, int tileID) throws IllegalStateException {
        try {
            state.pickTile(player, fromWhere, tileID);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Places a tile at the specified position on the board.
     * Delegates the action to the current state.
     *
     * @param player the player placing the tile
     * @param toWhere the destination where to place the tile
     * @param row the row position for tile placement
     * @param col the column position for tile placement
     * @throws IllegalStateException if the current state does not allow tile placement
     * @throws IllegalArgumentException if the placement position is invalid
     */
    public void placeTile(PlayerData player, int toWhere, int row, int col) throws IllegalStateException, IllegalArgumentException {
        state.placeTile(player, toWhere, row, col);
    }

    /**
     * Rotates the current tile for the specified player.
     * Delegates the action to the current state.
     *
     * @param player the player rotating the tile
     * @throws IllegalStateException if the current state does not allow tile rotation or if the operation fails
     */
    public void rotateTile(PlayerData player) throws IllegalStateException {
        try {
            state.rotateTile(player);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Places a marker at the specified position for the given player.
     * Delegates the action to the current state.
     *
     * @param player the player placing the marker
     * @param position the position where to place the marker
     * @throws IllegalStateException if the current state does not allow marker placement or if the operation fails
     */
    public void placeMarker(PlayerData player, int position) throws IllegalStateException {
        try {
            state.placeMarker(player, position);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Manages crew member operations for the specified player.
     * Validates that the requesting player is the current player before proceeding.
     * In synchronous states, the current player validation is skipped.
     *
     * @param player the player managing the crew member
     * @param mode the mode of crew member management
     * @param crewType the type of crew member to manage
     * @param cabinID the ID of the cabin associated with the crew member
     * @throws IllegalStateException if the player is not the current player or if the operation fails
     */
    public void manageCrewMember(PlayerData player, int mode, int crewType, int cabinID) throws IllegalStateException {
        try {
            PlayerData currentPlayer = state.getCurrentPlayer();
            if (!currentPlayer.equals(player)) {
                throw new IllegalStateException("Not the current player");
            }
        } catch (SynchronousStateException e) {
            // Ignore the exception, it is expected in synchronous states
        }

        try {
            state.manageCrewMember(player, mode, crewType, cabinID);
        } catch (Exception exception) {
            throw new IllegalStateException(exception.getMessage());
        }
    }

    /**
     * Flips the timer for the specified player.
     * Delegates the action to the current state.
     *
     * @param player the player attempting to flip the timer
     * @throws IllegalStateException if the current state does not allow timer flipping or if the operation fails
     */
    public void flipTimer(PlayerData player) throws IllegalStateException {
        try {
            state.flipTimer(player);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Allows a player to give up the game.
     * Validates that the requesting player is the current player before proceeding.
     *
     * @param uuid the UUID of the player attempting to give up
     * @throws IllegalStateException if the player is not the current player
     */
    public void giveUp(UUID uuid) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            state.giveUp(player);
        } else {
            throw new IllegalStateException("Not the current player");
        }
    }

    /**
     * Allows a player to select a planet.
     * Validates that the requesting player is the current player before proceeding.
     *
     * @param uuid the UUID of the player selecting the planet
     * @param planetID the ID of the planet to select
     * @throws IllegalStateException if the player is not the current player or if the operation fails
     * @throws IllegalArgumentException if the planet ID is invalid
     */
    public void selectPlanet(UUID uuid, int planetID) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            try {
                state.selectPlanet(player, planetID);
            } catch (IllegalStateException e) {
                throw new IllegalStateException(e.getMessage());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid planet ID: " + planetID);
            }
        }
    }

    /**
     * Exchange goods between in an adventure state
     * @param uuid player's uuid
     * @param exchangeData List of Triplet containing (in order) the goods to get, the goods to leave and the ID of the storage
     */
    public void exchangeGoods(UUID uuid, List<Triplet<List<Good>, List<Good>, Integer>> exchangeData) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            try {
                state.setGoodsToExchange(player, exchangeData);
            } catch (IllegalStateException e) {
                throw new IllegalStateException(e.getMessage());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid exchange data");
            }
        }
    }

    /**
     * Swap goods between two storage
     * @param uuid player's uuid
     * @param storageID1 storage ID 1
     * @param storageID2 storage ID 2
     * @param goods1to2 List of goods to exchange from storage 1 to storage 2
     * @param goods2to1 List of goods to exchange from storage 2 to storage 1
     */
    public void swapGoods(UUID uuid, int storageID1, int storageID2, List<Good> goods1to2, List<Good> goods2to1) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            try {
                state.swapGoods(player, storageID1, storageID2, goods1to2, goods2to1);
            } catch (IllegalStateException e) {
                throw new IllegalStateException(e.getMessage());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid swap data");
            }
        }
    }

    /**
     * Use double engines or double cannons in the state.
     * @param uuid player's uuid
     * @param type type of the extra strength to use: 0 = engine, 1 = cannon
     * @param IDs List of Integers representing the ID of the engines or cannons to use
     * @param batteriesID List of Integers representing the batteryID from which we take the energy to use the cannon
     */
    public void useExtraStrength(UUID uuid, int type, List<Integer> IDs, List<Integer> batteriesID) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            try {
                state.useExtraStrength(player, type, IDs, batteriesID);
            } catch (IllegalStateException e) {
                throw new IllegalStateException(e.getMessage());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0 or 1.");
            }
        }
    }

    /**
     * Sets the penalty loss for a player in the current state.
     * Validates that the requesting player is the current player before proceeding.
     *
     * @param userID the UUID of the player setting the penalty loss
     * @param type the type of penalty loss
     * @param penaltyLoss list of integers representing the penalty loss values
     * @throws IllegalStateException if the player is not the current player
     */
    public void setPenaltyLoss(UUID userID, int type, List<Integer> penaltyLoss) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(userID)) {
            state.setPenaltyLoss(player, type, penaltyLoss);
        } else {
            throw new IllegalStateException("Not the current player");
        }
    }

    /**
     * Rolls the dice for the specified player.
     * Validates that the requesting player is the current player before proceeding.
     * In synchronous states, the current player validation is skipped.
     *
     * @param player the player rolling the dice
     * @throws IllegalStateException if the player is not the current player or if the operation fails
     */
    public void rollDice(PlayerData player) {
        try {
            PlayerData currentPlayer = state.getCurrentPlayer();
            if (!currentPlayer.equals(player)) {
                throw new IllegalStateException("Not the current player");
            }
        } catch (SynchronousStateException e) {
            // Ignore the exception, it is expected in synchronous states
        }

        try {
            state.rollDice(player);
        } catch (Exception exception) {
            throw new IllegalStateException(exception.getMessage());
        }
    }

    /**
     * Sets the fragment choice for the specified player.
     * Validates that the requesting player is the current player before proceeding.
     * In synchronous states, the current player validation is skipped.
     *
     * @param player the player making the fragment choice
     * @param fragmentChoice the fragment choice value to set
     * @throws IllegalStateException if the player is not the current player or if the operation fails
     */
    public void setFragmentChoice(PlayerData player, int fragmentChoice) throws IllegalStateException {
        try {
            PlayerData currentPlayer = state.getCurrentPlayer();
            if (!currentPlayer.equals(player)) {
                throw new IllegalStateException("Not the current player");
            }
        } catch (SynchronousStateException e) {
            // Ignore the exception, it is expected in synchronous states
        }

        try {
            state.setFragmentChoice(player, fragmentChoice);
        } catch (Exception exception) {
            throw new IllegalStateException(exception.getMessage());
        }
    }

    /**
     * Sets the components to destroy for the specified player.
     * Validates that the requesting player is the current player before proceeding.
     * In synchronous states, the current player validation is skipped.
     *
     * @param player the player setting the components to destroy
     * @param componentsToDestroy list of pairs representing the components to destroy,
     *                           where each pair contains component coordinates or identifiers
     * @throws IllegalStateException if the player is not the current player or if the operation fails
     */
    public void setComponentToDestroy(PlayerData player, List<Pair<Integer, Integer>> componentsToDestroy) {
        try {
            PlayerData currentPlayer = state.getCurrentPlayer();
            if (!currentPlayer.equals(player)) {
                throw new IllegalStateException("Not the current player");
            }
        } catch (SynchronousStateException e) {
            // Ignore the exception, it is expected in synchronous states
        }

        try {
            state.setComponentToDestroy(player, componentsToDestroy);
        } catch (Exception exception) {
            throw new IllegalStateException(exception.getMessage());
        }
    }

    /**
     * Sets the protection using batteries for the specified player.
     * Validates that the requesting player is the current player before proceeding.
     * In synchronous states, the current player validation is skipped.
     *
     * @param player the player setting the protection
     * @param batteryID list of battery IDs to use for protection
     * @throws IllegalStateException if the player is not the current player or if the operation fails
     */
    public void setProtect(PlayerData player, List<Integer> batteryID) {
        try {
            PlayerData currentPlayer = state.getCurrentPlayer();
            if (!currentPlayer.equals(player)) {
                throw new IllegalStateException("Not the current player");
            }
        } catch (SynchronousStateException e) {
            // Ignore the exception, it is expected in synchronous states
        }

        try {
            state.setProtect(player, batteryID);
        } catch (Exception exception) {
            throw new IllegalStateException(exception.getMessage());
        }
    }

    /**
     * Executes a cheat code for the specified player with the given ship index.
     * Validates that the requesting player is the current player before proceeding.
     * In synchronous states, the current player validation is skipped.
     *
     * @param player the player executing the cheat code
     * @param shipIndex the index of the ship to apply the cheat code to
     * @throws IllegalStateException if the player is not the current player or if the operation fails
     */
    public void cheatCode(PlayerData player, int shipIndex) {
        try {
            PlayerData currentPlayer = state.getCurrentPlayer();
            if (!currentPlayer.equals(player)) {
                throw new IllegalStateException("Not the current player");
            }
        } catch (SynchronousStateException e) {
            // Ignore the exception, it is expected in synchronous states
        }

        try {
            state.cheatCode(player, shipIndex);
        } catch (Exception exception) {
            throw new IllegalStateException(exception.getMessage());
        }

    }
}
