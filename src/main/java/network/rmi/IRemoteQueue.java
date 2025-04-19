package network.rmi;

import network.messages.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteQueue extends Remote {
    void add(Message message) throws RemoteException;
    Message poll() throws RemoteException;
}
