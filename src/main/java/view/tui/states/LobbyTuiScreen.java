package view.tui.states;

import view.miniModel.MiniModel;
import view.miniModel.lobby.LobbyView;
import org.jline.terminal.Terminal;
import view.tui.TerminalUtils;
import view.tui.input.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;


public class LobbyTuiScreen implements TuiScreenView {
    private final ArrayList<String> options = new ArrayList<>(Arrays.asList("Ready", "Not ready", "Leave"));
    private final LobbyView currentLobbyView = MiniModel.getInstance().currentLobby;
    private int selected;
    private final int totalLines = LobbyView.getRowsToDraw() + 3 + 1;
    private int row;
    protected String message;
    protected boolean isNewScreen;


    public LobbyTuiScreen() {
        isNewScreen = true;
    }

    @Override
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        selected = parser.getCommand(options, totalLines, isStillCurrentScreen);
    }

    @Override
    public TuiScreenView setNewScreen() {
        switch (selected) {
            case 0:
                return new BuildingTuiScreen();
            case 1:
                return this;
            case 2:
                return new MenuTuiScreen();
            default:
                throw new IllegalStateException("Unexpected value: " + selected);
        }
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        row = 1;

        for (int i = 0; i < LobbyView.getRowsToDraw(); i++) {
            TerminalUtils.printLine(writer, currentLobbyView.drawLineTui(i), row++);
        }

        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, message == null ? "" : message, row++);
        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, "Set status (ready/not ready) or leave the lobby:", row++);

        if (isNewScreen) {
            isNewScreen = false;
            for (int i = totalLines + options.size(); i < terminal.getSize().getRows(); i++ ) {
                TerminalUtils.printLine(writer, "", i);
            }
        }
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Lobby;
    }
}
