package network.rmi;

import event.Event;
import event.game.HeartBeat;
import network.Connection;
import network.exceptions.BadPortException;
import network.exceptions.DisconnectedConnection;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class RMIConnection implements Connection {

    /**
     * The queue of messages that will be sent to the other end of the connection.
     */
    private final RemoteQueue sender;

    /**
     * The queue of messages that will be received from the other end of the connection.
     */
    private final RemoteQueue receiver;

    /**
     * Saved messages that are yet to be returned by the "receive" method.
     */
    private final Queue<Event> pendingMessages;

    /**
     * Used to keep track of connection state. Methods "send" and "receive" can only work if this
     * boolean is false.
     */
    private boolean disconnected;

    /**
     * Lock needed to protect portions of object state that need to be modified by threads, such as
     * the "disconnected" boolean, and both remote queues.
     */
    private final Object lock = new Object();

    /**
     * Lock used to handle concurrently send by multiple threads.
     */
    private final Object lockSendTimeout = new Object();

    /**
     * Lock used to handle concurrently read by multiple threads.
     */
    private final Object lockReadTimeout = new Object();

    /**
     * Indicates if the message was sent.
     */
    private boolean sent;

    /**
     * Indicates which message has been read.
     */
    private Event read;

    /**
     * Timeout for the heartbeat and read/send messages.
     */
    private final static long TIMEOUT = 5000;

    /**
     * Constructor of the class when it is instanced on the client. Initializes the inner lock, the connection object, and the
     * connection queue.
     *
     * @param address is the address of the server's host.
     * @param port    is the port used by {@link network.ConnectionAcceptor the server} for RMI communication.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     * @throws NotBoundException will be thrown if a failure occurs in the process of connecting to the server.
     * @throws BadPortException will be thrown if a failure occurs in the process of connecting to the server.
     */
    public RMIConnection(String address, int port) throws RemoteException, NotBoundException, BadPortException {
        if (port < 1024 || port > 49151) {
            throw new BadPortException("port " + port + " out of range");
        }

        pendingMessages = new LinkedList<>();
        disconnected = false;

        try {
            Registry registry = LocateRegistry.getRegistry(address, port);
            RemoteServer server = (RemoteServer) registry.lookup("SERVER");

            String boundName = server.getBoundName();
            sender = (RemoteQueue) registry.lookup("SENDER_" + boundName);
            receiver = (RemoteQueue) registry.lookup("RECEIVER_" + boundName);
        } catch (NotBoundException e) {
            throw new NotBoundException(e.getMessage());
        }

        heartbeat();
        read();
    }

    /**
     * Constructor of the class when it is instanced on the server. Initializes the inner lock, the connection object, and the
     * connection queue.
     * @param address is the address of the server's host.
     * @param port is the port used by {@link network.ConnectionAcceptor the server} for RMI communication.
     * @param boundName is the name of the remote queue pair.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     * @throws NotBoundException will be thrown if a failure occurs in the process of connecting to the server.
     * @throws BadPortException will be thrown if a failure occurs in the process of connecting to the server.
     */
    public RMIConnection(String address, int port, String boundName) throws RemoteException, NotBoundException, BadPortException {
        pendingMessages = new LinkedList<>();
        disconnected = false;

        try {
            Registry registry = LocateRegistry.getRegistry(address, port);
            sender = (RemoteQueue) registry.lookup("RECEIVER_" + boundName);
            receiver = (RemoteQueue) registry.lookup("SENDER_" + boundName);
        } catch (NotBoundException e) {
            throw new NotBoundException(e.getMessage());
        }

        heartbeat();
        read();
    }

    /**
     * This method is used to send a heartbeat message to the other end of the connection in order to check if the connection is still alive.
     */
    private void heartbeat() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    send(new HeartBeat());
                } catch (DisconnectedConnection e) {
                    throw new DisconnectedConnection("Connection is closed", e);
                }
            }
        }, 0, TIMEOUT/2);
    }

    /**
     * This method is used to send a message to the other end of the connection.
     * It has a timeout of TIMEOUT seconds, if the message is not sent in this time,
     * the connection is considered closed.
     *
     * @param message is the message to be sent.
     * @return true if the message was sent, false otherwise.
     */
    private boolean sendWithTimeout(Event message) {
        sent = false;

        new Thread(() -> {
            synchronized (lockSendTimeout) {
                try {
                    sender.add(message);

                    synchronized (lockSendTimeout) {
                        sent = true;
                        lockSendTimeout.notifyAll();
                    }
                } catch (RemoteException e) {
                    throw new DisconnectedConnection("Connection close while sending message", e);
                }
            }
        }).start();

        synchronized (lockSendTimeout) {
            try {
                lockSendTimeout.wait(TIMEOUT);
            } catch (InterruptedException e) {
                throw new DisconnectedConnection("Connection close while sending message", e);
            }

            return sent;
        }
    }

    /**
     * This method is used to read a message from the other end of the connection.
     * It has a timeout of TIMEOUT seconds, if the message is not read in this time,
     * the connection is considered closed.
     *
     * @return an Optional containing the message read, or an empty Optional if the connection is closed.
     */
    private Optional<Event> readWithTimeout() {
        read = null;

        new Thread(() -> {
            synchronized (lockReadTimeout) {
                try {
                    Event message = receiver.poll();

                    synchronized (lockReadTimeout) {
                        read = message;
                        lockReadTimeout.notifyAll();
                    }
                } catch (RemoteException e) {
                    throw new DisconnectedConnection("Connection close while reading message", e);
                }
            }
        }).start();

        synchronized (lockReadTimeout) {
            try {
                lockReadTimeout.wait(TIMEOUT);
            } catch (InterruptedException e) {
                throw new DisconnectedConnection("Connection close while reading message", e);
            }

            return Optional.ofNullable(read);
        }
    }

    /**
     * This method is used to read messages from the other end of the connection.
     * It runs in a separate thread and will keep reading messages until the connection is closed.
     * If a message is read and it is not a heartbeat, it is added to the pendingMessages queue.
     */
    private void read() {
        new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    if (disconnected) {
                        return;
                    }
                }

                Optional<Event> message = readWithTimeout();

                if (message.isEmpty()) {
                    disconnect();
                    return;
                }

                if (!message.get().equals(new HeartBeat())) {
                    synchronized (lock) {
                        pendingMessages.add(message.get());
                        lock.notifyAll();
                    }
                }
            }
        }).start();
    }

    /**
     * This method is used to send a message to the other end of the connection.
     * It will throw a DisconnectedConnection exception if the connection is closed.
     *
     * @param message is the message to be sent.
     * @throws DisconnectedConnection will be thrown if the connection is closed.
     */
    @Override
    public void send(Event message) throws DisconnectedConnection {
        synchronized (lock) {
            if (disconnected) {
                throw new DisconnectedConnection("Connection is closed");
            }

            if (!sendWithTimeout(message)) {
                disconnect();
                throw new DisconnectedConnection("The user is disconnected, TIMEOUT reached while sending message");
            }
        }
    }

    /**
     * This method is used to receive a message from the other end of the connection.
     * It will throw a DisconnectedConnection exception if the connection is closed.
     *
     * @return the message received from the other end of the connection.
     * @throws DisconnectedConnection will be thrown if the connection is closed.
     */
    @Override
    public Event receive() throws DisconnectedConnection {
        synchronized (lock) {
            if (disconnected) {
                throw new DisconnectedConnection("Connection is closed");
            }

            while (pendingMessages.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new DisconnectedConnection("Connection close while waiting for message", e);
                }

                if(disconnected) {
                    throw new DisconnectedConnection("Connection close while waiting for message");
                }
            }

            return pendingMessages.poll();
        }
    }

    /**
     * This method is used to close the connection.
     * It will set the disconnected boolean to true and notify all waiting threads.
     */
    @Override
    public void disconnect() {
        synchronized (lock) {
            disconnected = true;
            lock.notifyAll();
        }
    }


}
