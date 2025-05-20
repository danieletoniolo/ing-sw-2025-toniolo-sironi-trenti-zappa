package view.tui.states;

import view.structures.MiniModel;
import view.structures.lobby.LobbyView;
import org.jline.terminal.Terminal;
import view.tui.input.Command;

import java.util.ArrayList;
import java.util.Arrays;


public class LobbyStateTuiView implements StateTuiView {
    private final ArrayList<String> options = new ArrayList<>(Arrays.asList("Ready", "Not ready", "Leave"));
    private final LobbyView currentLobbyView;
    private final int totalLines = LobbyView.getRowsToDraw() + 1;


    public LobbyStateTuiView() {
        currentLobbyView = MiniModel.getInstance().lobbyViews.stream()
                .filter(lobbyView -> lobbyView.getLobbyName().equals(MiniModel.getInstance().lobbyViews))
                .findFirst()
                .orElse(null);
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
    public StateTuiView internalViewState(Command command) {
        return null;
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        terminal.writer().print("\033[H\033[2J");
        writer.flush();
        for (int i = 0; i < LobbyView.getRowsToDraw(); i++) {
            writer.println(currentLobbyView.drawLineTui(i));
        }

        writer.println("\nSet status (ready/not ready) or leave the lobby: ");

        writer.flush();
    }
}
