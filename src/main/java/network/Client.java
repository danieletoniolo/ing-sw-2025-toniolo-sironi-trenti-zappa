package network;

import network.interfaces.IGameController;
import network.rmi.ClientRMIManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    String username;
    IGameController gameController;
    boolean RMI;

    public Client(String address, int port, String username, boolean RMI) throws NotBoundException, RemoteException {
        this.username = username;
        this.RMI = RMI;

        if(RMI) {
            ClientRMIManager.setRegistryIPPort(address, port);
            // TODO: handle exception
            gameController = ClientRMIManager.getStub("gameController");
            gameController.addRMIUser();
        } else {
            // TODO: tcp connection
        }

    }

    public void ping() throws RemoteException {
        // TODO: Ping ricevuto dal server
    }
    // TODO: implement methods to use the server, such as setUsername etc...
}
