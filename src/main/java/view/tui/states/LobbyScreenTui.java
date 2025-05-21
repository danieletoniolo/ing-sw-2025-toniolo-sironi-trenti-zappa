package view.tui.states;

import view.miniModel.MiniModel;
import view.miniModel.lobby.LobbyView;
import org.jline.terminal.Terminal;
import view.tui.input.Parser;

import java.util.ArrayList;
import java.util.Arrays;


public class LobbyScreenTui implements ScreenTuiView {
    private final ArrayList<String> options = new ArrayList<>(Arrays.asList("Ready", "Not ready", "Leave"));
    private final LobbyView currentLobbyView;
    private int selected;
    private final int totalLines = LobbyView.getRowsToDraw() + 1;


    public LobbyScreenTui() {
        currentLobbyView = MiniModel.getInstance().currentLobby;
    }

    @Override
    public void readCommand(Parser parser) throws Exception {
        selected = parser.getCommand(options, totalLines);
    }

    @Override
    public ScreenTuiView isViewCommand() {
        return null;
    }

    @Override
    public void sendCommandToServer() {

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
