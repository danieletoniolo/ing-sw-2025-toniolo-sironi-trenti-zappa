package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.lobby.LobbyInfo;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.state.LobbyState;
import it.polimi.ingsw.model.state.State;
import it.polimi.ingsw.model.state.SynchronousStateException;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.*;

public class GameController implements Serializable, StateTransitionHandler {
    private State state;
    private final ServerEventManager eventManager;
    private final UUID uuid = UUID.randomUUID();

    public GameController(Board board, LobbyInfo lobbyInfo) {
        this.eventManager = new ServerEventManager(lobbyInfo);
        this.state = new LobbyState(board, this.eventManager, this);
    }

    @Override
    public String toString() {
        return uuid.toString();
    }

    @Override
    public void changeState(State newState) {
        state = newState;
        state.entry();
    }

    public UUID getUUID() {
        return uuid;
    }

    public void startGame(LocalTime startTime, int timerDuration) {
        try {
            state.startGame(startTime, timerDuration);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot start game in this state: " + e.getMessage() + ". Current state: " + state.getClass().getSimpleName());
        }
    }

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

    public void useDeck(PlayerData player, int usage, int deckIndex) throws IllegalStateException{
        try {
            state.useDeck(player, usage, deckIndex);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public void pickTile(PlayerData player, int fromWhere, int tileID) throws IllegalStateException {
        try {
            state.pickTile(player, fromWhere, tileID);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public void placeTile(PlayerData player, int toWhere, int row, int col) throws IllegalStateException, IllegalArgumentException {
        state.placeTile(player, toWhere, row, col);
    }

    public void rotateTile(PlayerData player) throws IllegalStateException {
        try {
            state.rotateTile(player);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public void placeMarker(PlayerData player, int position) throws IllegalStateException {
        try {
            state.placeMarker(player, position);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

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

    public void flipTimer(PlayerData player) throws IllegalStateException {
        try {
            state.flipTimer(player);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public void giveUp(UUID uuid) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            state.giveUp(player);
        } else {
            throw new IllegalStateException("Not the current player");
        }
    }

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
                throw new IllegalArgumentException("Invalid exchange data: " + exchangeData);
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
                throw new IllegalArgumentException("Invalid swap data: " + goods1to2 + " " + goods2to1);
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

    public void setPenaltyLoss(UUID userID, int type, List<Integer> penaltyLoss) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(userID)) {
            state.setPenaltyLoss(player, type, penaltyLoss);
        } else {
            throw new IllegalStateException("Not the current player");
        }
    }

    public void rollDice(UUID uuid) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            state.rollDice(player);
        } else {
            throw new IllegalStateException("Not the current player");
        }
    }

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

    public void setProtect(UUID uuid, int batteryID) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            state.setProtect(player, batteryID);
        } else {
            throw new IllegalStateException("Not the current player");
        }
    }

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
