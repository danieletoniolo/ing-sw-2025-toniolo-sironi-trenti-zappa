package it.polimi.ingsw;

import it.polimi.ingsw.controller.MatchController;
import it.polimi.ingsw.event.NetworkTransceiver;
import it.polimi.ingsw.event.lobby.serverToClient.UserIDSet;
import it.polimi.ingsw.network.Connection;
import it.polimi.ingsw.network.ConnectionAcceptor;
import it.polimi.ingsw.network.exceptions.ConnectionException;
import it.polimi.ingsw.utils.Logger;

import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.UUID;

public class Server {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("""
                  ___   __   __     __   _  _  _  _    ____  ____  _  _   ___  __ _  ____  ____    ____  ____  ____  _  _  ____  ____\s
                 / __) / _\\ (  )   / _\\ ( \\/ )( \\/ )  (_  _)(  _ \\/ )( \\ / __)(  / )(  __)(  _ \\  / ___)(  __)(  _ \\/ )( \\(  __)(  _ \\
                ( (_ \\/    \\/ (_/\\/    \\ )  (  )  /     )(   )   /) \\/ (( (__  )  (  ) _)  )   /  \\___ \\ ) _)  )   /\\ \\/ / ) _)  )   /
                 \\___/\\_/\\_/\\____/\\_/\\_/(_/\\_)(__/     (__) (__\\_)\\____/ \\___)(__\\_)(____)(__\\_)  (____/(____)(__\\_) \\__/ (____)(__\\_)
                """);
        System.out.print("Enter the server's hostname: ");
        String hostname = scanner.nextLine();

        // Start up Logger
        Logger logger = Logger.getInstance();
        logger.setUp(true, true);

        // Create a new NetworkTransceiver object (this will be the main transceiver for the pre-game phase)
        NetworkTransceiver networkTransceiver = new NetworkTransceiver();

        // Set up the MatchController with the NetworkTransceiver
        MatchController.setUp(networkTransceiver);

        // Try to create a new ConnectionAcceptor object to accept incoming connections
        ConnectionAcceptor connectionAcceptor = null;
        try {
            ConnectionAcceptor.initialize(hostname);
            connectionAcceptor = new ConnectionAcceptor(2550, 2551);
        } catch (RemoteException | ConnectionException exception) {
            logger.logError(exception.toString(), true);
            System.exit(1);
        }

        logger.logInfo("Server started at address: " + hostname + " (TCP port: 2550; RMI port: 2551)", false);

        while (true) {
            Connection connection = connectionAcceptor.accept();

            UUID uuid = UUID.randomUUID();
            networkTransceiver.connect(uuid, connection);
            networkTransceiver.send(uuid, new UserIDSet(uuid.toString()));

            logger.logInfo("Connection established with user: " + uuid, false);
        }

    }
}
