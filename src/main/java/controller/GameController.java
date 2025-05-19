package controller;

import Model.Game.Board.Board;
import Model.Game.Lobby.LobbyInfo;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.State.LobbyState;
import Model.State.State;
import org.javatuples.Triplet;

import java.io.Serializable;
import java.util.*;

public class GameController implements Serializable {
    private State state;
    private final ServerEventManager eventManager;
    private final UUID uuid = UUID.randomUUID();

    public GameController(Board board, LobbyInfo lobbyInfo) {
        this.eventManager = new ServerEventManager(lobbyInfo);
        this.state = new LobbyState(board, this.eventManager);
    }

    @Override
    public String toString() {
        return uuid.toString();
    }

    public UUID getUUID() {
        return uuid;
    }

    // TODO: add the list of states already initialize to the game controller?
    public void nextState(State newState) {
        if (state != null) {
            state.exit();
        }

        state = newState;
        state.entry();
    }

    public void startGame() {
    }

    public void manageLobby(PlayerData player, int type) {
        try {
            state.manageLobby(player, type);
        } catch (Exception e) {
            throw new IllegalStateException("State is not a JoinableGame");
        }
    }

    public void endGame() {
    }

    // Game actions

    public void useDeck(PlayerData player, int usage, int deckIndex) throws IllegalStateException{
        try {
            state.useDeck(player, usage, deckIndex);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot use deck in this state");
        }
    }

    public void pickTile(PlayerData player, int fromWhere, int tileID) throws IllegalStateException {
        try {
            state.pickTile(player, fromWhere, tileID);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot pick tile in this state");
        }
    }

    public void placeTile(PlayerData player, int toWhere, int row, int col) throws IllegalStateException {
        try {
            state.placeTile(player, toWhere, row, col);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot place tile in this state");
        }
    }

    public void rotateTile(PlayerData player) throws IllegalStateException {
        try {
            state.rotateTile(player);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot rotate tile in this state");
        }
    }

    public void placeMarker(PlayerData player, int position) throws IllegalStateException {
        try {
            state.placeMarker(player, position);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot place marker in this state");
        }
    }

    public void flipTimer(PlayerData player) throws IllegalStateException {
        try {
            state.flipTimer(player);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot flip timer in this state");
        }
    }

    public void choseFragment(UUID uuid, int fragmentID) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            try {
                state.setFragmentChoice(fragmentID);
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Cannot choose fragment in this state");
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid fragment ID: " + fragmentID);
            }
        } else {
            throw new IllegalStateException("State is not a ChoosableFragment");
        }
    }

    public void startTurn(UUID uuid) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            state.play(player);
        }
    }

    // ex finish()
    public void endTurn(UUID uuid) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            state.execute(player);
        }
    }

    public void giveUp(UUID uuid) {
        // TODO
    }

    public void selectPlanet(UUID uuid, int planetID) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            try {
                state.selectPlanet(player, planetID);
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Cannot select planet in this state");
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid planet ID: " + planetID);
            }
        }
    }

    /**
     * Exchange goods between in an adventure state
     * @param uuid player's uuid
     * @param exchangeData ArrayList of Triplet containing (in order) the goods to get, the goods to leave and the ID of the storage
     */
    public void exchangeGoods(UUID uuid, ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            try {
                state.setGoodsToExchange(player, exchangeData);
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Cannot exchange goods in this state");
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
     * @param goods1to2 ArrayList of goods to exchange from storage 1 to storage 2
     * @param goods2to1 ArrayList of goods to exchange from storage 2 to storage 1
     */
    public void swapGoods(UUID uuid, int storageID1, int storageID2, ArrayList<Good> goods1to2, ArrayList<Good> goods2to1) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            try {
                state.swapGoods(player, storageID1, storageID2, goods1to2, goods2to1);
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Cannot swap goods in this state");
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid swap data: " + goods1to2 + " " + goods2to1);
            }
        }
    }

    /**
     * Use double engines or double cannons in the state.
     * @param uuid player's uuid
     * @param type type of the extra strength to use: 0 = engine, 1 = cannon
     * @param strength strength of the extra strength to use
     * @param batteriesID List of Integers representing the batteryID from which we take the energy to use the cannon
     */
    public void useExtraStrength(UUID uuid, int type, float strength, List<Integer> batteriesID) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            try {
                state.useExtraStrength(player, type, strength, batteriesID);
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Cannot use extra power in this state");
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0 or 1.");
            }
        }
    }

    public void removeCrew(UUID uuid, List<Integer> cabinsIDs) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            try {
                state.setPenaltyLoss(player, 2, cabinsIDs);
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Cannot remove crew in this state or wrong input");
            }
        }
    }

    public void addCrew(UUID uuid, int cabinID, int numberOfCrew) {
    }

    public void rollDice(UUID uuid, int numberOfDice) {
        PlayerData player = state.getCurrentPlayer();
        if (player.getUUID().equals(uuid)) {
            try {
                state.rollDice();
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Cannot roll dice in this state");
            }
        }
    }
}
