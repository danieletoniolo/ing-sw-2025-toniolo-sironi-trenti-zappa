package it.polimi.ingsw.network.tcp;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.game.HeartBeat;
import it.polimi.ingsw.network.Connection;
import it.polimi.ingsw.network.exceptions.BadHostException;
import it.polimi.ingsw.network.exceptions.DisconnectedConnection;
import it.polimi.ingsw.network.exceptions.SocketCreationException;
import it.polimi.ingsw.utils.Logger;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class TCPConnection implements Connection {
    /** The TCP socket used for network communication */
    private final Socket socket;

    /** Output stream for sending serialized objects over the network */
    private final ObjectOutputStream out;

    /** Input stream for receiving serialized objects from the network */
    private final ObjectInputStream in;

    /** Flag indicating whether the connection has been disconnected */
    private boolean disconnected;

    /** Queue storing incoming events that haven't been processed yet */
    private final Queue<Event> pendingMessages;

    /** Synchronization lock for thread-safe access to shared resources */
    private final Object lock = new Object();

    /**
     * Creates a TCPConnection object for client-side connections.
     * Establishes a TCP socket connection to the specified address and port,
     * initializes input/output streams, and starts the heartbeat and reader threads.
     *
     * @param address the hostname or IP address of the server to connect to
     * @param port the port number on the server to connect to
     * @throws BadHostException if the address is null or the host is unknown
     * @throws SocketCreationException if an error occurs while creating the socket or streams
     */
    public TCPConnection(String address, int port) {
        disconnected = false;
        pendingMessages = new LinkedList<>();

        try {
            socket = new Socket(address, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch(IllegalArgumentException e) {
            throw new BadHostException("address is null", e);
        } catch(UnknownHostException e) {
            throw new BadHostException("unknown host: " + address, e);
        } catch(IOException e) {
            throw new SocketCreationException("error while creating socket", e);
        }

        // Start the reader thread to handle incoming messages
        read();

        // Start the heartbeat thread to maintain connection
        hearBeat();
    }

    /**
     * Creates a TCPConnection object from an already created socket. This constructor is used server-side.
     * @param socket Socket object to be used for the connection
     * @throws SocketCreationException if an error occurs while setting the input and output streams for the socket
     */
    public TCPConnection(Socket socket) throws SocketCreationException {
        disconnected = false;
        pendingMessages = new LinkedList<>();

        this.socket = socket;

        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            socket.setSoTimeout(5000);
        } catch (IOException ioException) {
            throw new SocketCreationException("error while setting input and output streams", ioException);
        }

        // Start the heartbeat thread
        hearBeat();

        // Start the reader thread
        read();
    }

    /**
     * Starts a background thread that continuously reads incoming messages from the network connection.
     * The thread reads objects from the input stream, filters out heartbeat messages, and adds valid
     * events to the pending messages queue. The thread terminates when the connection is disconnected.
     */
    private void read() {
        new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    if (disconnected) {
                        try {
                            socket.close();
                            return;
                        } catch (IOException e) {
                            Logger.getInstance().logError("Error while closing socket", true);
                        }
                    }
                }

                Object read;
                try {
                    socket.setSoTimeout(5000);
                    read = in.readObject();

                    if (read instanceof Event && !(read instanceof HeartBeat)) {
                        synchronized (lock) {
                            pendingMessages.add((Event) read);
                            lock.notifyAll();
                        }
                    } else if (!(read instanceof HeartBeat)) {
                        disconnect();
                    }

                } catch (Exception e) {
                    disconnect();
                }
            }
        }).start();
    }

    /**
     * Sends a heartbeat message to the other end of the connection every 5 seconds.
     * This is used to keep the connection alive and to check if the other end is still connected.
     * @throws DisconnectedConnection if the connection is broken
     */
    private void hearBeat() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    send(new HeartBeat());
                } catch (DisconnectedConnection e) {
                    timer.cancel();
                }
            }
        }, 0, 2500);
    }

    /**
     * Sends an event message to the remote end of the connection.
     * This method is thread-safe and will throw an exception if the connection
     * has been disconnected.
     *
     * @param message the Event object to send over the connection
     * @throws DisconnectedConnection if the connection is already disconnected
     *                                or if an IOException occurs during sending
     */
    @Override
    public void send(Event message) {
        synchronized (lock) {
            // If the connection is broken, throw a DisconnectedConnection exception
            if (disconnected) {
                throw new DisconnectedConnection("already disconnected");
            }
            // Send the message to the other end of the connection
            try {
                out.writeObject(message);
                out.reset();
                // TODO: RESET
                out.flush();
            } catch (IOException e) {
                // If a IOException is thrown, we consider the connection broken
                throw new DisconnectedConnection("sending while disconnected", e);
            }
        }
    }

    /**
     * Receives the next available event from the connection.
     * This method blocks until an event is available or the connection is disconnected.
     * Events are processed in FIFO order from the pending messages queue.
     *
     * @return the next Event object from the pending messages queue
     * @throws DisconnectedConnection if the connection is already disconnected or becomes
     *                                disconnected while waiting for a message
     * @throws InterruptedException if the thread is interrupted while waiting for a message
     */
    @Override
    public Event receive() throws DisconnectedConnection, InterruptedException {
        synchronized (lock) {
            // If the connection is broken, throw a DisconnectedConnection exception
            if (disconnected) {
                throw new DisconnectedConnection("Already disconnected");
            }

            // Wait for a message to be received
            while (pendingMessages.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new InterruptedException("Interrupted while waiting for message");
                }
                // If the connection is broken, throw a DisconnectedConnection exception
                if (disconnected) {
                    throw new DisconnectedConnection("Disconnected while waiting for message");
                }
            }

            // Return the first message in the queue
            return pendingMessages.poll();
        }
    }

    /**
     * Disconnects the TCP connection by setting the disconnected flag to true.
     * This method is thread-safe and notifies all waiting threads that the connection
     * has been terminated. Once called, subsequent send() and receive() operations
     * will throw DisconnectedConnection exceptions.
     */
    @Override
    public void disconnect() {
        synchronized (lock) {
            disconnected = true;
            lock.notifyAll();
        }
    }

}
