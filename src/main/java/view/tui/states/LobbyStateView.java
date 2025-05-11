package view.tui.states;

import view.structures.lobby.LobbyView;
import view.tui.input.Command;

import java.util.ArrayList;
import java.util.List;

public class LobbyStateView extends StateView {
    private ArrayList<String> validCommands = (ArrayList<String>) List.of("ready", "not", "leave", "help");
    private LobbyView lobbyView;
    private final int maxPlayers;
    private Command command;

    public LobbyStateView(String lobbyName, int maxPlayers, LobbyView lobbyView) {
        this.maxPlayers = maxPlayers;
        this.lobbyView = lobbyView;
    }

    @Override
    public StateView isValidCommand(Command command) {
        // Implement the logic for handling commands in the lobby state
        if (!validCommands.contains(command)) {
            this.command = null;
            throw new IllegalStateException("Invalid command: " + command.name() + " with parameters: " + String.join(", ", command.parameters()));
        }

        if (lobbyView.getPlayers().size() == maxPlayers && lobbyView.getPlayers().values().stream().allMatch(value -> value.equals(Boolean.TRUE))) {
            return new BuidingStateView();
        }

        return switch (command.name()) {
            case "help" -> {;
                this.command = command;
                yield this; // Don't change state
            }
            default -> {
                this.command = command;
                yield null; // To delegate the choice to the controller
            }
        };
    }

    @Override
    public void printTui() {
        // Implement the logic for printing the lobby state to the TUI
        for (int i = 0; i < lobbyView.getRowsToDraw(); i++) {
            System.out.println(lobbyView.drawLineTui(i));
        }

        System.out.println("\nSet status (ready/not ready) or leave the lobby: ");

        if (this.command != null && this.command.name().equals("help")) {
            System.out.println("To set your status, type 'ready' or 'not'.\nTo exit the lobby, type 'back'.");
        }

        /*
        if (command != null && command.name().equals("leave")) {
            System.out.println("Leaving the lobby...");
        }
        */

    }
}
