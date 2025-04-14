package network.rmi;

import network.interfaces.IGameController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ManagerClientRMI {
    private static final int DEFAULT_REGISTRY_PORT = 1099;
    private static String registryIP = null;
    private static Integer registryPort = null;

    public static void setRegistryIPPort(String registryIP, Integer registryPort) {
        ManagerClientRMI.registryIP = registryIP;
        ManagerClientRMI.registryPort = registryPort;
    }

    public static void setRegistryIP(String registryIP) {
        ManagerClientRMI.setRegistryIPPort(registryIP, DEFAULT_REGISTRY_PORT);
    }

    public static IGameController getStub(String remoteObjectName) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry();

        String[] namesBound = registry.list();
        return (IGameController) registry.lookup(remoteObjectName);
    }
}
