package network.rmi;

import network.interfaces.IGameController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRMIManager {
    private static final int DEFAULT_REGISTRY_PORT = 1099;
    private static String registryIP = null;
    private static Integer registryPort = null;

    public static void setRegistryIPPort(String registryIP, Integer registryPort) {
        ClientRMIManager.registryIP = registryIP;
        ClientRMIManager.registryPort = registryPort;
    }

    public static void setRegistryIP(String registryIP) {
        ClientRMIManager.setRegistryIPPort(registryIP, DEFAULT_REGISTRY_PORT);
    }

    public static IGameController getStub(String remoteObjectName) throws RemoteException, NotBoundException, IllegalStateException {
        if (registryIP == null && registryPort == null) {
            throw new IllegalStateException("Registry IP and port have not been set yet");
        }

        Registry registry = LocateRegistry.getRegistry();
        return (IGameController) registry.lookup(remoteObjectName);
    }
}
