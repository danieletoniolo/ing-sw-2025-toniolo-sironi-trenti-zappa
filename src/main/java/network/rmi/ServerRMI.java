package network.rmi;

import controller.GameController;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerRMI {
    private static final int DEFAULT_REGISTRY_PORT = 1099;
    private static final String bindingName = "gameController";

    public static void createRegistry() throws RemoteException {
        try {
            Registry registry = LocateRegistry.createRegistry(DEFAULT_REGISTRY_PORT);
            GameController gameController = new GameController();
            registry.bind(bindingName, gameController);
        } catch (RemoteException | AlreadyBoundException e) {
            throw new RemoteException("Error creating RMI registry", e);
        }
    }

    public static void destroyRegistry() throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(DEFAULT_REGISTRY_PORT);
            registry.unbind(bindingName);
            UnicastRemoteObject.unexportObject(registry, true);
        } catch (RemoteException | NotBoundException e) {
            throw new RemoteException("Error destroying RMI registry", e);
        }
    }
}
