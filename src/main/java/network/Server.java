package network;

import controller.GameController;
import network.rmi.ServerRMIManager;

import java.rmi.RemoteException;

public class Server {
    public static void main(String[] args) throws RemoteException {
        // TODO: to understand if we need to set the port or using the DEFAULT is okay
        Integer port = null;

        ServerRMIManager.createRegistry(port);
        GameController gameController = ServerRMIManager.bindGameController();

        // TODO: connessione TCP
    }
}
