package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.event.lobby.clientToServer.PlayerReady;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.lobby.clientToServer.LeaveLobby;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.lobby.LobbyView;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

public class Lobby implements TuiScreenView {
    protected final ArrayList<String> options = new ArrayList<>();
    private TuiScreenView nextScreen;

    private final LobbyView currentLobbyView = MiniModel.getInstance().getCurrentLobby();
    private int selected;
    protected final int totalLines = LobbyView.getRowsToDraw() + 4 + 1;
    protected String message;
    private boolean isNewScreen;

    public Lobby() {
        options.add("Ready");
        options.add("Not ready");
        options.add("Leave");
        options.add("Close program");
        isNewScreen = true;
    }

    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
    }

    @Override
    public TuiScreenView setNewScreen() {
        StatusEvent status;
        switch (selected) {
            case 0, 1:
                status = PlayerReady.requester(Client.transceiver, new Object()).request(new PlayerReady(MiniModel.getInstance().getUserID(), selected == 0));
                if (status.get().equals("POTA")) {
                    setMessage(((Pota) status).errorMessage());
                }
                setMessage(null);
                if (nextScreen != null) {
                    return nextScreen;
                }
                return this;
            case 2:
                status = LeaveLobby.requester(Client.transceiver, new Object()).request(new LeaveLobby(MiniModel.getInstance().getUserID(), MiniModel.getInstance().getCurrentLobby().getLobbyName()));
                if (status.get().equals("POTA")) {
                    setMessage(((Pota) status).errorMessage());
                    return this;
                }
                return new Menu();
            case 3:
                return new ClosingProgram();
        }

        return this;
    }

    @Override
    public void printTui(Terminal terminal) {
        List<String> newLines = new ArrayList<>();

        for (int i = 0; i < LobbyView.getRowsToDraw(); i++) {
            newLines.add(currentLobbyView.drawLineTui(i));
        }

        newLines.add("");
        newLines.add(message == null ? "" : message);
        newLines.add("");
        newLines.add(lineBeforeInput());

        TerminalUtils.printScreen(newLines, totalLines + options.size());

        if (isNewScreen) {
            isNewScreen = false;
            TerminalUtils.clearLastLines(totalLines + options.size());
        }
    }

    protected String lineBeforeInput() {
        return "Set status (ready/not ready) or leave the lobby:";
    }

    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Lobby;
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        this.nextScreen = nextScreen;
    }
}
