package network.rmi;

import network.interfaces.IGameController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRMI {
    public static void main(String args[]) throws RemoteException, NotBoundException {
        // TODO: by default I'm using localhost, I need to change this
        Registry registry = LocateRegistry.getRegistry();

        String[] namesBound = registry.list();
        String remoteObjectName = "gameController";
        IGameController gameController = (IGameController) registry.lookup(remoteObjectName);
        // TODO: use gameController to call methods on the server
    }
}
