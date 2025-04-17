package network;

import network.rmi.IRemoteServer;
import network.rmi.RMIConnection;
import network.rmi.RemoteQueue;
import network.tcp.TCPConnection;

import java.net.ConnectException;
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
 */
public class ConnectionAcceptor extends UnicastRemoteObject implements IRemoteServer {
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


    private final Queue<Connection> connectionQueue = new LinkedList<>();

    public ConnectionAcceptor(int TCPPort, int RMIPort) throws RemoteException, ConnectException {
        this.RMIPort = RMIPort;

        try {
            registry = LocateRegistry.createRegistry(RMIPort);
            registry.bind("SERVER", this);
            serverSocket = new ServerSocket(TCPPort);
        } catch (Exception exception) {
            throw new ConnectException();
        }
    }

    public static void initialize(String hostName) {
         System.setProperty("java.rmi.server.hostname", hostName);
    }

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
                // TODO: Replace with a custom exception or custom logging
                exception.printStackTrace();
            }
        });
        tcpThread.start();

        synchronized (lock) {
            while (connectionQueue.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            return connectionQueue.poll();
        }
    }

    @Override
    public String getBoundName() {
        synchronized (lock) {
            String boundName = "QUEUE" + nextBoundIndex++;

            try {
                registry.bind("SENDER_" + boundName, new RemoteQueue());
                registry.bind("RECEIVER_" + boundName, new RemoteQueue());
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
                // TODO: Replace with a custom exception or custom logging
            }

            return boundName;
        }
    }
}
