package it.polimi.ingsw.view.tui.states;

import it.polimi.ingsw.event.game.clientToServer.PlayerReady;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.lobby.LobbyView;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.function.Supplier;


public class LobbyTuiScreen implements TuiScreenView {
    protected final ArrayList<String> options = new ArrayList<>();
    private final LobbyView currentLobbyView = MiniModel.getInstance().getCurrentLobby();
    private int selected;
    private final int totalLines = LobbyView.getRowsToDraw() + 4 + 1;
    private int row;
    protected String message;
    protected boolean isNewScreen;


    public LobbyTuiScreen() {
        options.add("Ready");
        options.add("Not ready");
        options.add("Leave");
        isNewScreen = true;
    }

    @Override
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        selected = parser.getCommand(options, totalLines, isStillCurrentScreen);
    }

    @Override
    public TuiScreenView setNewScreen() {
        StatusEvent status;
        switch (selected) {
            case 0, 1:
                status = PlayerReady.requester(Client.transceiver, new Object()).request(new PlayerReady(MiniModel.getInstance().getUserID(), selected == 0));
                if (status.get().equals("POTA")) {
                    setMessage("You are already " + ((selected == 0) ? "Ready" : "Not Ready"));
                }
                return this;
            case 2:
                //status =
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
        TerminalUtils.printLine(writer, lineBeforeInput(), row++);

        if (isNewScreen) {
            isNewScreen = false;
            for (int i = totalLines + options.size(); i < terminal.getSize().getRows(); i++ ) {
                TerminalUtils.printLine(writer, "", i);
            }
        }
    }

    protected String lineBeforeInput() {
        return "Set status (ready/not ready) or leave the lobby:";
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
