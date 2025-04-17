package network.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteServer extends Remote {
    String getBoundName() throws RemoteException;
}
