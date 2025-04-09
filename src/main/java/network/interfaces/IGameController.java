package network.interfaces;

import Model.Good.Good;
import org.javatuples.Triplet;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;

public interface IGameController extends Remote {
    void startGame() throws RemoteException;
    void joinGame(int uuid) throws RemoteException;
    void leaveGame(int uuid) throws RemoteException;
    void endGame() throws RemoteException;
    void pickTile(int uuid, int tileID) throws RemoteException;
    void placeTile(UUID uuid, int tileID, int row, int col) throws RemoteException;
    void removeTile(int tileID) throws RemoteException;
    void reserveTile(UUID uuid, int tileID) throws RemoteException;
    void rotateTile(UUID uuid, int tileID) throws RemoteException;
    void choseFragment(UUID uuid, int fragmentID) throws RemoteException;
    void showDeck(int deckID) throws RemoteException;
    void leaveDeck(int deckID) throws RemoteException;
    void placeMarker(UUID uuid, int position) throws RemoteException;
    void flipTimer(UUID uuid, int time) throws RemoteException;
    void startTurn(UUID uuid) throws RemoteException;
    void endTurn(UUID uuid) throws RemoteException;
    void giveUp(UUID uuid) throws RemoteException;
    void selectPlanet(UUID uuid, int planetID) throws RemoteException;
    void exchangeGoods(UUID uuid, ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData) throws RemoteException;
    void swapGoods(UUID uuid, int storageID1, int storageID2, ArrayList<Good> goods1to2, ArrayList<Good> goods2to1) throws RemoteException;
    void useCannons(UUID uuid, float cannonsPowerToUse) throws RemoteException;
    void useEngines(UUID uuid, float enginesPowerToUse) throws RemoteException;
    void removeCrew(UUID uuid, int cabinID, int numberOfCrew) throws RemoteException;
    void addCrew(UUID uuid, int cabinID, int numberOfCrew) throws RemoteException;
    void rollDice(UUID uuid) throws RemoteException;
}
