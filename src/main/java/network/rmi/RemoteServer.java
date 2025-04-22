package network.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteServer extends Remote {
    /**
     * This method will be called remotely by the connection that wants to be paired via RMI.
     * It will return the name of the pair's connection (undecorated).
     *
     * @return a string which represents the name of the newly created connection pair RMI.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    String getBoundName() throws RemoteException;
}
