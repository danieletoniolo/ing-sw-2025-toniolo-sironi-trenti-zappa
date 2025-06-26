package it.polimi.ingsw.network.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for server-side RMI operations.
 * This interface defines the contract for remote method invocations
 * that can be performed on the server from client connections.
 * @author Vittorio Sironi
 */
public interface RemoteServer extends Remote {
    /**
     * This method will be called remotely by the connection that wants to be paired via RMI.
     * It will return the name of the pair's connection (undecorated).
     *
     * @return a string which represents the name of the newly created connection pair RMI.
     * @throws RemoteException will be thrown in case of it.polimi.ingsw.network problems, or server communication issues.
     */
    String getBoundName() throws RemoteException;
}
