package network.rmi;

import network.Connection;
import network.exceptions.BadPortException;
import network.exceptions.DisconnectedConnection;
import network.messages.HearBeat;
import network.messages.Message;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class RMIConnection implements Connection {
    private final IRemoteQueue sender;

    private final IRemoteQueue receiver;

    private final Queue<Message> pendingMessages;

    private boolean disconnected;

    private final Object lock = new Object();

    private final Object lockSendTimeout = new Object();

    private final Object lockReadTimeout = new Object();

    private boolean sent;

    private Message read;

    private final static long TIMEOUT = 5000;

    public RMIConnection(String address, int port) throws RemoteException, NotBoundException, BadPortException {
        if (port < 1024 || port > 49151) {
            throw new BadPortException("port " + port + " out of range");
        }

        pendingMessages = new LinkedList<>();
        disconnected = false;

        try {
            Registry registry = LocateRegistry.getRegistry(address, port);
            IRemoteServer server = (IRemoteServer) registry.lookup("SERVER");

            String boundName = server.getBoundName();
            sender = (IRemoteQueue) registry.lookup("SENDER_" + boundName);
            receiver = (IRemoteQueue) registry.lookup("RECEIVER_" + boundName);
        } catch (NotBoundException e) {
            throw new NotBoundException(e.getMessage());
        }

        heartbeat();
        read();
    }

    public RMIConnection(String address, int port, String boundName) throws RemoteException, NotBoundException, BadPortException {
        pendingMessages = new LinkedList<>();
        disconnected = false;

        try {
            Registry registry = LocateRegistry.getRegistry(address, port);
            sender = (IRemoteQueue) registry.lookup("RECEIVER_" + boundName);
            receiver = (IRemoteQueue) registry.lookup("SENDER_" + boundName);
        } catch (NotBoundException e) {
            throw new NotBoundException(e.getMessage());
        }

        heartbeat();
        read();
    }

    private void heartbeat() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    send(new HearBeat());
                } catch (DisconnectedConnection e) {
                    throw new DisconnectedConnection("Connection is closed", e);
                }
            }
        }, 0, TIMEOUT);
    }

    private boolean sendWithTimeout(Message message) {
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

    private Optional<Message> readWithTimeout() {
        read = null;

        new Thread(() -> {
            synchronized (lockReadTimeout) {
                try {
                    Message message = receiver.poll();

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

    private void read() {
        new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    if (disconnected) {
                        return;
                    }
                }

                Optional<Message> message = readWithTimeout();

                if (message.isEmpty()) {
                    disconnect();
                    return;
                }

                if (!message.get().equals(new HearBeat())) {
                    synchronized (lock) {
                        pendingMessages.add(message.get());
                        lock.notifyAll();
                    }
                }
            }
        }).start();
    }

    @Override
    public void send(Message message) throws DisconnectedConnection {
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

    @Override
    public Message receive() throws DisconnectedConnection {
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

    @Override
    public void disconnect() {
        synchronized (lock) {
            disconnected = true;
            lock.notifyAll();
        }
    }


}
