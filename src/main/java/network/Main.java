package network;

import network.rmi.ServerRMI;

import java.rmi.RemoteException;

public class Main {
    public static void main(String[] args) throws RemoteException {
        ServerRMI.createRegistry();
        System.out.println("Server started");
    }
}
