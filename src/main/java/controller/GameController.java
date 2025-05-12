package controller;

import Model.Game.Lobby.LobbyInfo;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.State.State;
import Model.State.interfaces.*;
import controller.event.EventType;
import controller.event.game.*;
import controller.event.lobby.JoinLobbySuccessful;
import controller.event.lobby.LobbyEvents;
import controller.event.lobby.UserJoinedLobby;
import controller.event.lobby.UserLeftLobby;
import network.User;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.*;

public class GameController {
    private State state;

    public GameController() {
        this.state = null;
    }

    // TODO: add the list of states already initialize to the game controller?
    public void nextState(State newState) {
        if (state != null) {
            state.exit();
        }

        state = newState;
        state.entry();
    }

    public ArrayList<User> getUsers() {
        // TODO: RETURN FROM BOARD OF PLAYERS, USE A MAP TO REMAP PLAYER_DATA TO USER
        return new ArrayList<>();
    }

    public void startGame() {
    }

    public void joinGame(User user, LobbyInfo lobby) {
    }

    public void leaveGame(UUID uuid) {
        if (state instanceof JoinableGame) {
            PlayerData player = state.getCurrentPlayer();
            if (player.getUUID().equals(uuid)) {
                ((JoinableGame) state).leaveGame(player);
            }
        } else {
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
        if (state instanceof ChoosableFragment) {
            ((ChoosableFragment) state).setFragmentChoice(fragmentID);
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
        if (state instanceof SelectablePlanet) {
            PlayerData player = state.getCurrentPlayer();
            if (player.getUUID().equals(uuid)) {
                ((SelectablePlanet) state).selectPlanet(player, planetID);
            }
        } else {
            throw new IllegalStateException("State is not a SelectablePlanet");
        }
    }

    /**
     * Exchange goods between in an adventure state
     * @param uuid player's uuid
     * @param exchangeData ArrayList of Triplet containing (in order) the goods to get, the goods to leave and the ID of the storage
     */
    public void exchangeGoods(UUID uuid, ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData) {
        if (state instanceof ExchangeableGoods) {
            PlayerData player = state.getCurrentPlayer();
            if (player.getUUID().equals(uuid)) {
                ((ExchangeableGoods) state).setGoodsToExchange(player, exchangeData);
            }
        } else {
            throw new IllegalStateException("State is not a ExchangeableGoods");
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
        if (state instanceof ExchangeableGoods) {
            PlayerData player = state.getCurrentPlayer();
            if (player.getUUID().equals(uuid)) {
                player.getSpaceShip().exchangeGood(goods2to1, goods1to2, storageID1);
                player.getSpaceShip().exchangeGood(goods1to2, goods2to1, storageID2);
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

    public void removeCrew(UUID uuid, ArrayList<Pair<Integer, Integer>> cabinsIDs) {
        if (state instanceof RemovableCrew) {
            PlayerData player = state.getCurrentPlayer();
            if (player.getUUID().equals(uuid)) {
                ((RemovableCrew) state).setCrewLoss(cabinsIDs);
            }
        }
    }

    public void addCrew(UUID uuid, int cabinID, int numberOfCrew) {
    }

    public void rollDice(UUID uuid, int numberOfDice) {
        if (state instanceof Fightable) {
            PlayerData player = state.getCurrentPlayer();
            if (player.getUUID().equals(uuid)) {
                ((Fightable) state).setDice(numberOfDice);
                // TODO: SHOULD THE VIEW ROLL THE DICE?
            }
        } else {
            throw new IllegalStateException("State is not a Fightable");
        }
    }
}
