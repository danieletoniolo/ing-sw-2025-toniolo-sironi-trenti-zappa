package view;

import java.util.Scanner;

public class ViewMain {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("""
                     ________  ________  ___       ________     ___    ___ ___    ___      _________  ________  ___  ___  ________  ___  __    _______   ________    \s
                    |\\   ____\\|\\   __  \\|\\  \\     |\\   __  \\   |\\  \\  /  /|\\  \\  /  /|    |\\___   ___\\\\   __  \\|\\  \\|\\  \\|\\   ____\\|\\  \\|\\  \\ |\\  ___ \\ |\\   __  \\   \s
                    \\ \\  \\___|\\ \\  \\|\\  \\ \\  \\    \\ \\  \\|\\  \\  \\ \\  \\/  / | \\  \\/  / /    \\|___ \\  \\_\\ \\  \\|\\  \\ \\  \\\\\\  \\ \\  \\___|\\ \\  \\/  /|\\ \\   __/|\\ \\  \\|\\  \\  \s
                     \\ \\  \\  __\\ \\   __  \\ \\  \\    \\ \\   __  \\  \\ \\    / / \\ \\    / /          \\ \\  \\ \\ \\   _  _\\ \\  \\\\\\  \\ \\  \\    \\ \\   ___  \\ \\  \\_|/_\\ \\   _  _\\ \s
                      \\ \\  \\|\\  \\ \\  \\ \\  \\ \\  \\____\\ \\  \\ \\  \\  /     \\/   \\/  /  /            \\ \\  \\ \\ \\  \\\\  \\\\ \\  \\\\\\  \\ \\  \\____\\ \\  \\\\ \\  \\ \\  \\_|\\ \\ \\  \\\\  \\|\s
                       \\ \\_______\\ \\__\\ \\__\\ \\_______\\ \\__\\ \\__\\/  /\\   \\ __/  / /               \\ \\__\\ \\ \\__\\\\ _\\\\ \\_______\\ \\_______\\ \\__\\\\ \\__\\ \\_______\\ \\__\\\\ _\\\s
                        \\|_______|\\|__|\\|__|\\|_______|\\|__|\\|__/__/ /\\ __\\\\___/ /                 \\|__|  \\|__|\\|__|\\|_______|\\|_______|\\|__| \\|__|\\|_______|\\|__|\\|__|
                                                               |__|/ \\|__\\|___|/                                                                                     \s
                    
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
