package it.polimi.ingsw;


import it.polimi.ingsw.event.NetworkTransceiver;
import it.polimi.ingsw.network.Connection;
import it.polimi.ingsw.network.rmi.RMIConnection;
import it.polimi.ingsw.network.tcp.TCPConnection;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.view.EventHandlerClient;
import it.polimi.ingsw.view.Manager;
import it.polimi.ingsw.view.gui.GuiManager;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.TuiManager;
import it.polimi.ingsw.view.tui.input.Parser;
import javafx.application.Application;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.UUID;

/**
 * Main client class for the application.
 * Handles user interface selection, network connection setup, and application startup.
 */
public class Client {
    /**
     * Shared network transceiver instance for client-server communication.
     */
    public static NetworkTransceiver transceiver;

    /**
     * Entry point for the client application.
     * Initializes the terminal, handles user input for UI and connection type,
     * and starts the appropriate user interface.
     *
     * @param args command line arguments
     * @throws IOException if an I/O error occurs during terminal initialization
     */
    public static void main(String[] args) throws IOException {
        Terminal terminal;
        try {
            terminal = TerminalBuilder.builder()
                    .system(true)
                    .build();
        } catch (Exception e) {
            System.err.println("Creation terminal error: " + e.getMessage());
            return;
        }
        Parser parser = new Parser(terminal);
        TerminalUtils.setTerminal(terminal);
        for (int i = 1; i < terminal.getSize().getRows(); i++ ) {
            TerminalUtils.printLine("", i);
        }
        int row = 1;

        Logger.getInstance(false, true);
        transceiver = new NetworkTransceiver();

        TerminalUtils.printLine("  ___   __   __     __   _  _  _  _    ____  ____  _  _   ___  __ _  ____  ____  ", row++);
        TerminalUtils.printLine(" / __) / _\\ (  )   / _\\ ( \\/ )( \\/ )  (_  _)(  _ \\/ )( \\ / __)(  / )(  __)(  _ \\ ", row++);
        TerminalUtils.printLine("( (_ \\/    \\/ (_/\\/    \\ )  (  )  /     )(   )   /) \\/ (( (__  )  (  ) _)  )   / ", row++);
        TerminalUtils.printLine(" \\___/\\_/\\_/\\____/\\_/\\_/(_/\\_)(__/     (__) (__\\_)\\____/ \\___)(__\\_)(____)(__\\_) ", row++);
        TerminalUtils.printLine("", row++);

        String tuiOrGui;
        do {
            tuiOrGui = parser.readNickname("Choose 'tui' or 'gui': ", row++);
            if (!tuiOrGui.equals("tui") && !tuiOrGui.equals("gui")) {
                TerminalUtils.printLine("Invalid input. Please enter 'tui' or 'gui'.", row--);
            }
        } while (!tuiOrGui.equals("tui") && !tuiOrGui.equals("gui"));

        String rmiOrSocket;
        do {
            rmiOrSocket = parser.readNickname("Choose 'rmi' or 'tcp': ", row++);
            if (!rmiOrSocket.equals("rmi") && !rmiOrSocket.equals("tcp")) {
                TerminalUtils.printLine("Invalid input. Please enter 'rmi' or 'tcp'.", row--);
            }
        } while (!rmiOrSocket.equals("rmi") && !rmiOrSocket.equals("tcp"));

        Manager ui;
        if (tuiOrGui.equals("tui")) {
            ui = new TuiManager(parser);
        }
        else {
            ui = new GuiManager();
        }

        new EventHandlerClient(transceiver, ui);

        String address = parser.readNickname("IP Address: ", row++);
        /*
        System.out.print("Enter IP: ");
        String address = sc.nextLine();
        */
        //String address = "127.0.0.1";
        // 140.238.173.150
        // 129.152.14.114

        if (rmiOrSocket.equals("rmi")) {
            Connection connection = new RMIConnection(address, 2551);
            transceiver.connect(UUID.randomUUID(), connection);
        } else {
            Connection connection = new TCPConnection(address, 2550);
            transceiver.connect(UUID.randomUUID(), connection);
        }

        MiniModel mm = MiniModel.getInstance();
        synchronized (mm) {
            while (MiniModel.getInstance().getUserID() == null) {
                try {
                    mm.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (tuiOrGui.equals("tui")) {
            ((TuiManager) ui).startTui();
        }
        else {
            Application.launch(GuiManager.class, args);
        }
    }
}
