package controller;

import Model.Game.Board.Board;
import Model.Good.Good;

import java.util.List;

public class GameController {
    private final Board board;

    public GameController() {
        board = null;
        // TODO
    }

    public void startGame() {
        // TODO
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
    public void placeTile(int uuid, int tileID, int row, int col) {
        // TODO
    }

    public void removeTile(int tileID) {
        // TODO
    }

    public void reserveTile(int uuid, int tileID) {
        // TODO
    }

    public void rotateTile(int uuid, int tileID) {
        // TODO
    }

    public void choseFragment(int uuid, int fragmentID) {
        // TODO
    }

    public void showDeck(int deckID) {
        // TODO
    }

    public void leaveDeck(int deckID) {
        // TODO
    }

    public void placeMarker(int uuid, int position) {
        // TODO
    }

    public void flipTimer(int uuid, int time) {
        // TODO
    }

    public void startTurn(int uuid) {
        // TODO
    }

    // ex finish()
    public void endTurn(int uuid) {
        // TODO
    }

    public void giveUp(int uuid) {
        // TODO
    }

    public void selectPlanet(int uuid, int planetID) {
        // TODO
    }

    public void exchangeGoods(int uuid, List<Good> goodsToGet, List<Good> goodsToLeave, int storageID) {
        // TODO
    }

    public void swapGoods(int uuid, int storageID1, int storageID2, List<Good> goods1to2, List<Good> goods2to1) {
        // TODO
    }

    public void useCannons(int uuid, int cannonsPowerToUse) {
        // TODO
    }

    public void useEngines(int uuid, int enginesPowerToUse) {
        // TODO
    }

    public void removeCrew(int uuid, int cabinID, int numberOfCrew) {
        // TODO
    }

    public void addCrew(int uuid, int cabinID, int numberOfCrew) {
        // TODO
    }

    public void rollDice(int uuid) {
        // TODO
    }

}
