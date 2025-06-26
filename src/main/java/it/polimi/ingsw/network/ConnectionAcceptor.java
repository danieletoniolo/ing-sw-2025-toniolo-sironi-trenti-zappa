package it.polimi.ingsw.network;

import it.polimi.ingsw.network.exceptions.ConnectionException;
import it.polimi.ingsw.network.rmi.RemoteServer;
import it.polimi.ingsw.network.rmi.RMIConnection;
import it.polimi.ingsw.network.rmi.RemoteLinkedList;
import it.polimi.ingsw.network.tcp.TCPConnection;
import it.polimi.ingsw.utils.Logger;

import java.net.ServerSocket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This object will keep waiting for {@link Connection} objects to request a pair element.
 * A new {@link Connection} will be created for each request to communicate back. This object
 * also acts as a server, and has a remotely invokable method.
 * @author Daniele Toniolo
 */
public class ConnectionAcceptor extends UnicastRemoteObject implements RemoteServer {
    /**
     * ServerSocket object used to accept incoming connections.
     */
    private ServerSocket serverSocket;

    /**
     * Port used for RMI connections.
     */
    private final int RMIPort;

    /**
     * Lock used to handle concurrently request of names by multiple threads.
     * It is static because many ConnectionAcceptor objects can be created, and they all need to share
     * the same lastRMIConnectionIndex.
     */
    private static final Object lock = new Object();

    /**
     * Needed for RMI communication.
     * Indicates the index of the next index that will be assigned as a name to a new remote queue pair.
     * It is static because even if many ConnectionAcceptors may be instantiated,
     * the values contained in the registry are the same for all of them.
     */
    private static int nextBoundIndex;

    /**
     * Pointer to this server's registry.
     */
    private final Registry registry;


    /**
     * Queue of connections that are waiting to be accepted.
     */
    private final Queue<Connection> connectionQueue = new LinkedList<>();

    /**
     * Constructor of the class. Initializes the inner lock, the connection object, and the
     * @param TCPPort is the port used for TCP connections.
     * @param RMIPort is the port used for RMI connections.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     * @throws ConnectionException will be thrown if the server socket cannot be created.
     */
    public ConnectionAcceptor(int TCPPort, int RMIPort) throws RemoteException, ConnectionException {
        this.RMIPort = RMIPort;

        try {
            registry = LocateRegistry.createRegistry(RMIPort);
            registry.bind("SERVER", this);
            serverSocket = new ServerSocket(TCPPort);
        } catch (Exception exception) {
            throw new ConnectionException();
        }
    }

    /**
     * This method will be called remotely by the connection that wants to be paired.
     * @param hostName is the host name of the client that wants to be paired.
     */
    public static void initialize(String hostName) {
         System.setProperty("java.rmi.server.hostname", hostName);
    }

    /**
     * This method will be called by the server to accept a TCP connection.
     * @return a {@link Connection} object that represents the accepted connection.
     */
    public Connection accept() {
        // Thread that will accept a TCP connection
        Thread tcpThread = new Thread(() -> {
            try {
                TCPConnection tcpConnection = new TCPConnection(serverSocket.accept());
                synchronized (lock) {
                    connectionQueue.add(tcpConnection);
                    lock.notifyAll();
                }
            } catch (Exception exception) {
                Logger.getInstance().logWarning(exception.getMessage(), true);
            }
        });
        tcpThread.start();

        synchronized (lock) {
            while (connectionQueue.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException interruptedException) {
                    Logger.getInstance().logError(interruptedException.getMessage(), true);
                }
            }
            return connectionQueue.poll();
        }
    }

    /**
     * This method will be called remotely by the connection that wants to be paired via RMI.
     * @return a string which represents the name of the newly created connection pair in RMI.
     */
    @Override
    public String getBoundName() {
        synchronized (lock) {
            String boundName = "QUEUE" + nextBoundIndex++;

            try {
                registry.bind("SENDER_" + boundName, new RemoteLinkedList());
                registry.bind("RECEIVER_" + boundName, new RemoteLinkedList());
            } catch (AlreadyBoundException alreadyBoundException) {
                throw new RuntimeException("queue already bound");
            } catch (RemoteException remoteException) {
                throw new RuntimeException("Remote exception while binding queue", remoteException);
            }

            try {
                RMIConnection rmiConnection = new RMIConnection("localhost", RMIPort, boundName);
                connectionQueue.add(rmiConnection);
                lock.notifyAll();
            } catch (Exception e) {
                Logger.getInstance().logError(e.getMessage(), true);
            }

            return boundName;
        }
    }
}
