package controller;

import Model.Game.Board.Board;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.State.State;
import Model.State.interfaces.ExchangeableGoods;
import Model.State.interfaces.UsableCannon;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.UUID;

public class GameController {
    private final Board board;
    private State state;

    public GameController() {
        board = null;
        state = null;
    }

    public void startGame() {
        // state = new BuildingState(board);
    }

    public void joinGame(int uuid) {
        // TODO
    }

    public void leaveGame(int uuid) {
        // TODO
    }

    public void endGame() {
        // TODO
    }

    // Game actions

    public void pickTile(int uuid, int tileID) {
        // TODO
    }

    /**
     * Place a tile on the spaceship at the given row and column
     * @param uuid player's uuid
     * @param tileID tile's id
     * @param row row to place the tile
     * @param col column to place the tile
     */
    public void placeTile(UUID uuid, int tileID, int row, int col) {
        // TODO
    }

    public void removeTile(int tileID) {
        // TODO
    }

    public void reserveTile(UUID uuid, int tileID) {
        // TODO
    }

    public void rotateTile(UUID uuid, int tileID) {
        // TODO
    }

    public void choseFragment(UUID uuid, int fragmentID) {
        // TODO
    }

    public void showDeck(int deckID) {
        // TODO
    }

    public void leaveDeck(int deckID) {
        // TODO
    }

    public void placeMarker(UUID uuid, int position) {
        // TODO
    }

    public void flipTimer(UUID uuid, int time) {
        // TODO
    }

    public void startTurn(UUID uuid) {
        // TODO
    }

    // ex finish()
    public void endTurn(UUID uuid) {
        // TODO
    }

    public void giveUp(UUID uuid) {
        // TODO
    }

    public void selectPlanet(UUID uuid, int planetID) {
        // TODO
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
            if (state.getCurrentPlayer().getUUID().equals(uuid)) {
                state.getCurrentPlayer().getSpaceShip().exchangeGood(goods2to1, goods1to2, storageID1);
                state.getCurrentPlayer().getSpaceShip().exchangeGood(goods1to2, goods2to1, storageID2);
            }
        }
    }

    /**
     * Use the cannons of the spaceship
     * @param uuid player's uuid
     * @param cannonsPowerToUse cannons power to use (float)
     */
    public void useCannons(UUID uuid, float cannonsPowerToUse) {
        if (state instanceof UsableCannon) {
            PlayerData player = state.getCurrentPlayer();
            if (player.getUUID().equals(uuid)) {
                ((UsableCannon) state).useCannon(player, cannonsPowerToUse);
            }
        }
    }

    public void useEngines(UUID uuid, int enginesPowerToUse) {
        // TODO
    }

    public void removeCrew(UUID uuid, int cabinID, int numberOfCrew) {
        // TODO
    }

    public void addCrew(UUID uuid, int cabinID, int numberOfCrew) {
        // TODO
    }

    public void rollDice(UUID uuid) {
        // TODO
    }

}
