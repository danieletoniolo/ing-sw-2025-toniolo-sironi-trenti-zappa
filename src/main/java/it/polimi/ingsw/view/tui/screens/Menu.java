package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.lobby.clientToServer.JoinLobby;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.lobby.LobbyView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.menuScreens.ChooseNumberPlayers;

import java.util.ArrayList;
import java.util.List;

public class Menu implements TuiScreenView {
    protected final ArrayList<String> options = new ArrayList<>();
    private boolean isNewScreen;

    protected int totalLines = 6;
    protected int selected;
    protected String message;

    //Creation of the lobby
    protected static int maxPlayers;
    protected int level;

    public Menu() {
        int i = 0;
        for (LobbyView lobby : MiniModel.getInstance().getLobbiesView()) {
            i++;
            options.add(i + ". Join " + lobby.getLobbyName() + " - " + lobby.getNumberOfPlayers() + "/" + lobby.getMaxPlayer() + " players - " + lobby.getLevel().toString());
        }
        options.add("Create lobby");
        options.add("Reload lobbies");
        options.add("Close program");
        this.isNewScreen = true;
    }

    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
    }

    @Override
    public void printTui(Terminal terminal) {
        List<String> newLines = new ArrayList<>();

        newLines.add("Welcome " + MiniModel.getInstance().getNickname());

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

    protected String lineBeforeInput(){
        return "Available lobbies:";
    }

    @Override
    public TuiScreenView setNewScreen() {
        if (selected == options.size() - 3) {
            return new ChooseNumberPlayers();
        }

        if (selected == options.size() - 2) {
            // Request refresh Lobbies
            return new Menu();
        }

        if (selected == options.size() - 1) {
            return new ClosingProgram();
        }

        if (selected >= 0 && selected < MiniModel.getInstance().getLobbiesView().size()) {
            StatusEvent status = JoinLobby.requester(Client.transceiver, new Object())
                    .request(new JoinLobby(MiniModel.getInstance().getUserID(), MiniModel.getInstance().getLobbiesView().get(selected).getLobbyName()));
            if (status.get().equals("POTA")) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }

            return new Lobby();
        }

        return this;
    }

    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Menu;
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {

    }
}
