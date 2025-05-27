package it.polimi.ingsw.view;


import it.polimi.ingsw.event.NetworkTransceiver;

import java.util.Scanner;

public class Client {
    public static NetworkTransceiver transceiver = new NetworkTransceiver();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

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
            if (rmiOrSocket.equals("rmi")) {
                //TODO: far partire rmi e tui
            } else {
                // TODO: far partire socket e tui
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
