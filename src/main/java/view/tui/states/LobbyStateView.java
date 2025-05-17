package view.tui.states;

import view.structures.MiniModel;
import view.structures.lobby.LobbyView;
import org.jline.terminal.Terminal;
import view.tui.input.Command;

import java.util.ArrayList;
import java.util.Arrays;


public class LobbyStateView implements StateView {
    private final ArrayList<String> options = new ArrayList<>(Arrays.asList("Ready", "Not ready", "Leave"));
    private final String lobbyID;
    private final LobbyView currentLobbyView;
    private int totalLines;


    public LobbyStateView(String lobbyID) {
        this.lobbyID = lobbyID;
        currentLobbyView = MiniModel.getInstance().lobbyViews.stream()
                .filter(lobbyView -> lobbyView.getLobbyName().equals(this.lobbyID))
                .findFirst()
                .orElse(null);
        totalLines = currentLobbyView.getRowsToDraw() + 1;
    }

    @Override
    public ArrayList<String> getOptions() {
        return options;
    }

    @Override
    public int getTotalLines() {
        return totalLines;
    }

    @Override
    public StateView internalViewState(Command command) {
        return null;
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        terminal.writer().print("\033[H\033[2J");
        writer.flush();
        for (int i = 0; i < currentLobbyView.getRowsToDraw(); i++) {
            writer.println(currentLobbyView.drawLineTui(i));
        }

        writer.println("\nSet status (ready/not ready) or leave the lobby: ");

        writer.flush();
    }
}
