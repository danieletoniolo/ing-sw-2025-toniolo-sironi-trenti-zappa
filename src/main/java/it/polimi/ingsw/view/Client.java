package it.polimi.ingsw.view;


import it.polimi.ingsw.event.NetworkTransceiver;
import it.polimi.ingsw.event.lobby.clientToServer.SetNickname;
import it.polimi.ingsw.network.Connection;
import it.polimi.ingsw.network.rmi.RMIConnection;
import it.polimi.ingsw.network.tcp.TCPConnection;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.TuiManager;

import java.util.Scanner;
import java.util.UUID;

public class Client {
    public static NetworkTransceiver transceiver;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Logger.getInstance().setUp(false, true);
        transceiver = new NetworkTransceiver();

        System.out.println("""
                  _______      ___       __          ___      ___   ___ ____    ____    .___________..______       __    __    ______  __  ___  _______ .______     \s
                 /  _____|    /   \\     |  |        /   \\     \\  \\ /  / \\   \\  /   /    |           ||   _  \\     |  |  |  |  /      ||  |/  / |   ____||   _  \\    \s
                |  |  __     /  ^  \\    |  |       /  ^  \\     \\  V  /   \\   \\/   /     `---|  |----`|  |_)  |    |  |  |  | |  ,----'|  '  /  |  |__   |  |_)  |   \s
                |  | |_ |   /  /_\\  \\   |  |      /  /_\\  \\     >   <     \\_    _/          |  |     |      /     |  |  |  | |  |     |    <   |   __|  |      /    \s
                |  |__| |  /  _____  \\  |  `----./  _____  \\   /  .  \\      |  |            |  |     |  |\\  \\----.|  `--'  | |  `----.|  .  \\  |  |____ |  |\\  \\----.
                 \\______| /__/     \\__\\ |_______/__/     \\__\\ /__/ \\__\\     |__|            |__|     | _| `._____| \\______/   \\______||__|\\__\\ |_______|| _| `._____|
                
                    """);

        String tuiOrGui;
        do {
            System.out.print("Choose 'tui' or 'gui': ");
            tuiOrGui = sc.nextLine();
            if (!tuiOrGui.equals("tui") && !tuiOrGui.equals("gui")) {
                System.out.println("Invalid input. Please enter 'tui' or 'gui'.");
            }
        } while (!tuiOrGui.equals("tui") && !tuiOrGui.equals("gui"));

        String rmiOrSocket;
        do {
            System.out.print("Choose 'rmi' or 'socket': ");
            rmiOrSocket = sc.nextLine();
            if (!rmiOrSocket.equals("rmi") && !rmiOrSocket.equals("socket")) {
                System.out.println("Invalid input. Please enter 'rmi' or 'socket'.");
            }
        } while (!rmiOrSocket.equals("rmi") && !rmiOrSocket.equals("socket"));

        if (tuiOrGui.equals("tui")) {
            TuiManager tui = new TuiManager();
            tui.startTui();

            if (rmiOrSocket.equals("rmi")) {
                EventHandlerClient manager = new EventHandlerClient(transceiver, tui);

                /*System.out.print("Enter IP: ");
                String address = sc.nextLine();*/
                //String address = "140.238.173.150";
                //String address = "127.0.0.1";
                String address = "192.168.67.224";
                Connection connection = new RMIConnection(address, 2551);
                transceiver.connect(UUID.randomUUID(), connection);

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
            } else {
                EventHandlerClient manager = new EventHandlerClient(transceiver, tui);

                /*System.out.print("Enter IP: ");
                String address = sc.nextLine();*/
                //String address = "140.238.173.150";
                //String address = "127.0.0.1";
                String address = "192.168.67.224";
                Connection connection = new TCPConnection(address, 2550);
                transceiver.connect(UUID.randomUUID(), connection);

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
            }
        }
        else {
            if (rmiOrSocket.equals("rmi")) {
                //TODO: far partire rmi e gui
            }
            else {
                //TODO: far partire socket e gui
            }
        }
    }
}
