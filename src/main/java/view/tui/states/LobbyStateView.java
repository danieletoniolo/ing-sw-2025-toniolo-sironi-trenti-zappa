package view.tui.states;

import view.structures.lobby.LobbyView;
import view.tui.input.Command;

import java.util.ArrayList;
import java.util.List;

public class LobbyStateView implements StateView {
    private ArrayList<String> validCommands = (ArrayList<String>) List.of("/ready", "/not", "/leave", "/help");
    private LobbyView lobbyView;

    public LobbyStateView(LobbyView lobbyView) {
        this.lobbyView = lobbyView;
    }

    @Override
    public StateView isValidCommand(Command command) {
        // Implement the logic for handling commands in the lobby state
        if (!validCommands.contains(command.name())) {
            return null;
        }

        switch (command.name()) {
            case "/help":
                System.out.println("To set your status, type '/ready' or '/not ready'.\nTo exit the lobby, type '/leave'.");
                break;
        };

        return null;
    }

    @Override
    public void printTui() {
        // Implement the logic for printing the lobby state to the TUI
        for (int i = 0; i < lobbyView.getRowsToDraw(); i++) {
            System.out.println(lobbyView.drawLineTui(i));
        }

        System.out.println("\nSet status (ready/not ready) or leave the lobby: ");
    }
}
