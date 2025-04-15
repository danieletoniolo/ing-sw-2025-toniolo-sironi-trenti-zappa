package network.tcp;

import network.Connection;
import network.exceptions.BadHostException;
import network.exceptions.DisconnectedConnection;
import network.exceptions.SocketCreationException;
import network.messages.HearBeat;
import network.messages.Message;


import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class TCPConnection implements Connection {
    private final Socket socket;

    private final ObjectOutputStream out;

    private final ObjectInputStream in;

    private boolean disconnected;

    private Queue<Message> pendingMessages;

    private final Object lock = new Object();

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
        // TODO: Check if we are handling all the possible exceptions

        // Start the heartbeat thread
        reader();

        // Start the reader thread
        hearBeat();
    }

    private void reader() {
        Thread reader = new Thread(() -> {
            while(true) {
                synchronized (lock) {
                    if (disconnected) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // TODO: handle exception
                        }
                    }
                }

                Object read;
                try {
                    socket.setSoTimeout(5000);
                    read = in.readObject();

                    // Check if the read object is Message
                    if (read instanceof Message) {
                        synchronized (lock) {
                            pendingMessages.add((Message) read);
                            lock.notifyAll();
                        }
                    }

                    // If the read object is not Message and neither a Heartbeat we consider the connection broken
                    if (!(read instanceof HearBeat)) {
                        disconnect();
                    }

                } catch (Exception e) {
                    disconnect();
                }
            }
        });
        reader.start();
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
                    send(new HearBeat());
                } catch (DisconnectedConnection e) {
                    timer.cancel();
                }
            }
        }, 0, 5000);
        // TODO: Decide what should be the frequency of the heartbeat
    }

    @Override
    public void send(Message message) {
        synchronized (lock) {
            // If the connection is broken, throw a DisconnectedConnection exception
            if (disconnected) {
                throw new DisconnectedConnection("already disconnected");
            }
            // Send the message to the other end of the connection
            try {
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                // If a IOException is thrown, we consider the connection broken
                throw new DisconnectedConnection("sending while disconnected", e);
            }
        }
    }

    @Override
    public Message receive() {
        synchronized (lock) {
            // If the connection is broken, throw a DisconnectedConnection exception
            if (disconnected) {
                throw new DisconnectedConnection("already disconnected");
            }

            // Wait for a message to be received
            while (pendingMessages.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new DisconnectedConnection("interrupted while waiting for message", e);
                }
                // If the connection is broken, throw a DisconnectedConnection exception
                if (disconnected) {
                    throw new DisconnectedConnection("disconnected while waiting for message");
                }
            }

            // Return the first message in the queue
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
