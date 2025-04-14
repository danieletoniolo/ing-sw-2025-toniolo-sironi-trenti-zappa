package controller;

import Model.Game.Board.Board;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.State.State;
import Model.State.interfaces.ExchangeableGoods;
import Model.State.interfaces.UsableCannon;
import Model.State.interfaces.UsableEngine;
import network.User;
import network.interfaces.IGameController;
import org.javatuples.Triplet;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class GameController extends UnicastRemoteObject implements IGameController {
    private final Board board;
    private State state;
    private ArrayList<User> users;

    public GameController() throws RemoteException {
        board = null;
        state = null;
        users = new ArrayList<>();
    }

    public void addRMIUser() throws RemoteException {
        users.add(new User(UUID.randomUUID(), true));
    }

    public void startGame() throws RemoteException {
        // state = new BuildingState(board);
    }

    public void joinGame(int uuid) throws RemoteException {
        // TODO
    }

    public void leaveGame(int uuid) throws RemoteException {
        // TODO
    }

    public void endGame() throws RemoteException {
        // TODO
    }

    // Game actions

    public void pickTile(int uuid, int tileID) throws RemoteException {
        // TODO
    }

    /**
     * Place a tile on the spaceship at the given row and column
     * @param uuid player's uuid
     * @param tileID tile's id
     * @param row row to place the tile
     * @param col column to place the tile
     */
    public void placeTile(UUID uuid, int tileID, int row, int col) throws RemoteException {
        // TODO
    }

    public void removeTile(int tileID) throws RemoteException {
        // TODO
    }

    public void reserveTile(UUID uuid, int tileID) throws RemoteException {
        // TODO
    }

    public void rotateTile(UUID uuid, int tileID) throws RemoteException {
        // TODO
    }

    public void choseFragment(UUID uuid, int fragmentID) throws RemoteException {
        // TODO
    }

    public void showDeck(int deckID) throws RemoteException {
        // TODO
    }

    public void leaveDeck(int deckID) throws RemoteException {
        // TODO
    }

    public void placeMarker(UUID uuid, int position) throws RemoteException {
        // TODO
    }

    public void flipTimer(UUID uuid, int time) throws RemoteException {
        // TODO
    }

    public void startTurn(UUID uuid) throws RemoteException {
        // TODO
    }

    // ex finish()
    public void endTurn(UUID uuid) throws RemoteException {
        // TODO
    }

    public void giveUp(UUID uuid) throws RemoteException {
        // TODO
    }

    public void selectPlanet(UUID uuid, int planetID) throws RemoteException {
        // TODO
    }

    /**
     * Exchange goods between in an adventure state
     * @param uuid player's uuid
     * @param exchangeData ArrayList of Triplet containing (in order) the goods to get, the goods to leave and the ID of the storage
     */
    public void exchangeGoods(UUID uuid, ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData) throws RemoteException {
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
    public void swapGoods(UUID uuid, int storageID1, int storageID2, ArrayList<Good> goods1to2, ArrayList<Good> goods2to1) throws RemoteException {
        if (state instanceof ExchangeableGoods) {
            PlayerData player = state.getCurrentPlayer();
            if (player.getUUID().equals(uuid)) {
                player.getSpaceShip().exchangeGood(goods2to1, goods1to2, storageID1);
                player.getSpaceShip().exchangeGood(goods1to2, goods2to1, storageID2);
            }
        }
    }

    /**
     * Use the cannons of the spaceship
     * @param uuid player's uuid
     * @param cannonsPowerToUse cannons power to use (float)
     */
    public void useCannons(UUID uuid, float cannonsPowerToUse) throws RemoteException {
        if (state instanceof UsableCannon) {
            PlayerData player = state.getCurrentPlayer();
            if (player.getUUID().equals(uuid)) {
                ((UsableCannon) state).useCannon(player, cannonsPowerToUse);
            }
        }
    }

    /**
     * Use the engines of the spaceship
     * @param uuid player's uuid
     * @param enginesPowerToUse engines power to use (float)
     */
    public void useEngines(UUID uuid, float enginesPowerToUse) throws RemoteException {
        if (state instanceof UsableEngine) {
            PlayerData player = state.getCurrentPlayer();
            if (player.getUUID().equals(uuid)) {
                ((UsableEngine) state).useEngine(player, enginesPowerToUse);
            }
        }
    }

    public void removeCrew(UUID uuid, int cabinID, int numberOfCrew) throws RemoteException {
        // TODO
    }

    public void addCrew(UUID uuid, int cabinID, int numberOfCrew) throws RemoteException {
        // TODO
    }

    public void rollDice(UUID uuid) throws RemoteException {
        // TODO
    }
}
