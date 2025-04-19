package network.rmi;

import controller.GameController;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerRMIManager {
    private static final int DEFAULT_REGISTRY_PORT = 1099;
    private static Registry registry = null;

    public static void createRegistry(Integer port) throws RemoteException {
        try {
            ServerRMIManager.registry = LocateRegistry.createRegistry(port == null ? DEFAULT_REGISTRY_PORT : port);
        } catch (RemoteException e) {
            throw new RemoteException("Error creating RMI registry", e);
        }
    }

    public static void destroyRegistry(String bindingName, Integer port) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(port == null ? DEFAULT_REGISTRY_PORT : port);
            registry.unbind(bindingName);
            UnicastRemoteObject.unexportObject(registry, true);
        } catch (RemoteException | NotBoundException e) {
            throw new RemoteException("Error destroying RMI registry", e);
        }
    }

    public static GameController bindGameController() throws RemoteException, IllegalStateException {
        if (registry == null) {
            throw new IllegalStateException("RMI registry has not been created yet");
        }
        try {
            GameController gameController = new GameController();
            registry.bind("gameController", gameController);
            return gameController;
        } catch (AlreadyBoundException e) {
            throw new RemoteException("Error binding GameController to RMI registry", e);
        }
    }
}
