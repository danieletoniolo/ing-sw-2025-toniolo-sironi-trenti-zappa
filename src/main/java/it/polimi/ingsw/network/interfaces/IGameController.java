package it.polimi.ingsw.network.interfaces;

import it.polimi.ingsw.model.good.Good;
import org.javatuples.Triplet;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;

public interface IGameController extends Remote {
    /**
     * Adds a new RMI user to the game.
     * @throws RemoteException if a communication-related exception occurs
     */
    void addRMIUser() throws RemoteException;

    /**
     * Starts the game session.
     * @throws RemoteException if a communication-related exception occurs
     */
    void startGame() throws RemoteException;

    /**
     * Allows a user to join the game.
     * @param uuid the unique identifier of the user joining the game
     * @throws RemoteException if a communication-related exception occurs
     */
    void joinGame(int uuid) throws RemoteException;

    /**
     * Allows a user to leave the game.
     * @param uuid the unique identifier of the user leaving the game
     * @throws RemoteException if a communication-related exception occurs
     */
    void leaveGame(int uuid) throws RemoteException;

    /**
     * Ends the current game session.
     * @throws RemoteException if a communication-related exception occurs
     */
    void endGame() throws RemoteException;

    /**
     * Allows a user to pick a tile from the game board.
     * @param uuid the unique identifier of the user picking the tile
     * @param tileID the identifier of the tile to be picked
     * @throws RemoteException if a communication-related exception occurs
     */
    void pickTile(int uuid, int tileID) throws RemoteException;

    /**
     * Places a tile on the game board at the specified position.
     * @param uuid the unique identifier of the user placing the tile
     * @param tileID the identifier of the tile to be placed
     * @param row the row position where the tile will be placed
     * @param col the column position where the tile will be placed
     * @throws RemoteException if a communication-related exception occurs
     */
    void placeTile(UUID uuid, int tileID, int row, int col) throws RemoteException;

    /**
     * Removes a tile from the game board.
     * @param tileID the identifier of the tile to be removed
     * @throws RemoteException if a communication-related exception occurs
     */
    void removeTile(int tileID) throws RemoteException;

    /**
     * Reserves a tile for a specific user.
     * @param uuid the unique identifier of the user reserving the tile
     * @param tileID the identifier of the tile to be reserved
     * @throws RemoteException if a communication-related exception occurs
     */
    void reserveTile(UUID uuid, int tileID) throws RemoteException;

    /**
     * Rotates a tile on the game board.
     * @param uuid the unique identifier of the user rotating the tile
     * @param tileID the identifier of the tile to be rotated
     * @throws RemoteException if a communication-related exception occurs
     */
    void rotateTile(UUID uuid, int tileID) throws RemoteException;

    /**
     * Allows a user to choose a specific fragment.
     * @param uuid the unique identifier of the user choosing the fragment
     * @param fragmentID the identifier of the fragment to be chosen
     * @throws RemoteException if a communication-related exception occurs
     */
    void choseFragment(UUID uuid, int fragmentID) throws RemoteException;

    /**
     * Displays the contents of a specific deck.
     * @param deckID the identifier of the deck to be shown
     * @throws RemoteException if a communication-related exception occurs
     */
    void showDeck(int deckID) throws RemoteException;

    /**
     * Allows leaving or closing a specific deck.
     * @param deckID the identifier of the deck to be left
     * @throws RemoteException if a communication-related exception occurs
     */
    void leaveDeck(int deckID) throws RemoteException;

    /**
     * Places a marker at the specified position for a user.
     * @param uuid the unique identifier of the user placing the marker
     * @param position the position where the marker will be placed
     * @throws RemoteException if a communication-related exception occurs
     */
    void placeMarker(UUID uuid, int position) throws RemoteException;

    /**
     * Flips the timer for a specific user.
     * @param uuid the unique identifier of the user flipping the timer
     * @param time the time value for the timer
     * @throws RemoteException if a communication-related exception occurs
     */
    void flipTimer(UUID uuid, int time) throws RemoteException;

    /**
     * Starts the turn for a specific user.
     * @param uuid the unique identifier of the user starting their turn
     * @throws RemoteException if a communication-related exception occurs
     */
    void startTurn(UUID uuid) throws RemoteException;

    /**
     * Ends the turn for a specific user.
     * @param uuid the unique identifier of the user ending their turn
     * @throws RemoteException if a communication-related exception occurs
     */
    void endTurn(UUID uuid) throws RemoteException;

    /**
     * Allows a user to give up or surrender from the game.
     * @param uuid the unique identifier of the user giving up
     * @throws RemoteException if a communication-related exception occurs
     */
    void giveUp(UUID uuid) throws RemoteException;

    /**
     * Allows a user to select a planet.
     * @param uuid the unique identifier of the user selecting the planet
     * @param planetID the identifier of the planet to be selected
     * @throws RemoteException if a communication-related exception occurs
     */
    void selectPlanet(UUID uuid, int planetID) throws RemoteException;

    /**
     * Allows a user to exchange goods with specified exchange data.
     * @param uuid the unique identifier of the user performing the exchange
     * @param exchangeData the exchange data containing source goods, target goods, and quantities
     * @throws RemoteException if a communication-related exception occurs
     */
    void exchangeGoods(UUID uuid, ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData) throws RemoteException;

    /**
     * Allows a user to swap goods between two storage locations.
     * @param uuid the unique identifier of the user performing the swap
     * @param storageID1 the identifier of the first storage location
     * @param storageID2 the identifier of the second storage location
     * @param goods1to2 the goods to move from storage 1 to storage 2
     * @param goods2to1 the goods to move from storage 2 to storage 1
     * @throws RemoteException if a communication-related exception occurs
     */
    void swapGoods(UUID uuid, int storageID1, int storageID2, ArrayList<Good> goods1to2, ArrayList<Good> goods2to1) throws RemoteException;

    /**
     * Allows a user to use cannons with specified power.
     * @param uuid the unique identifier of the user using the cannons
     * @param cannonsPowerToUse the amount of cannon power to use
     * @throws RemoteException if a communication-related exception occurs
     */
    void useCannons(UUID uuid, float cannonsPowerToUse) throws RemoteException;

    /**
     * Allows a user to use engines with specified power.
     * @param uuid the unique identifier of the user using the engines
     * @param enginesPowerToUse the amount of engine power to use
     * @throws RemoteException if a communication-related exception occurs
     */
    void useEngines(UUID uuid, float enginesPowerToUse) throws RemoteException;

    /**
     * Removes crew members from a specific cabin.
     * @param uuid the unique identifier of the user removing crew
     * @param cabinID the identifier of the cabin from which crew will be removed
     * @param numberOfCrew the number of crew members to remove
     * @throws RemoteException if a communication-related exception occurs
     */
    void removeCrew(UUID uuid, int cabinID, int numberOfCrew) throws RemoteException;

    /**
     * Adds crew members to a specific cabin.
     * @param uuid the unique identifier of the user adding crew
     * @param cabinID the identifier of the cabin to which crew will be added
     * @param numberOfCrew the number of crew members to add
     * @throws RemoteException if a communication-related exception occurs
     */
    void addCrew(UUID uuid, int cabinID, int numberOfCrew) throws RemoteException;

    /**
     * Allows a user to roll dice.
     * @param uuid the unique identifier of the user rolling the dice
     * @throws RemoteException if a communication-related exception occurs
     */
    void rollDice(UUID uuid) throws RemoteException;
}
