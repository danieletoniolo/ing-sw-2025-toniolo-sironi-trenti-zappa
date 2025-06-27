package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.event.lobby.clientToServer.PlayerReady;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.lobby.clientToServer.LeaveLobby;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.lobby.LobbyView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Lobby screen in the TUI.
 * Allows the user to set their ready status, leave the lobby, or close the program.
 */
public class Lobby implements TuiScreenView {
    /**
     * List of available options in the lobby screen.
     */
    protected final ArrayList<String> options = new ArrayList<>();
    /**
     * The next screen to display after this one.
     */
    private TuiScreenView nextScreen;
    /**
     * The current lobby view model.
     */
    private final LobbyView currentLobbyView = MiniModel.getInstance().getCurrentLobby();
    /**
     * The index of the selected option.
     */
    private int selected;
    /**
     * The total number of lines to draw in the TUI.
     */
    protected final int totalLines = LobbyView.getRowsToDraw() + 4 + 1;
    /**
     * Message to display to the user.
     */
    protected String message;
    /**
     * Indicates if this is a new screen (for clearing the terminal).
     */
    private boolean isNewScreen;

    /**
     * Constructs a new Lobby screen and initializes the options.
     */
    public Lobby() {
        options.add("Ready");
        options.add("Not ready");
        options.add("Leave");
        options.add("Close program");
        isNewScreen = true;
    }

    /**
     * Reads the user's command from the parser and updates the selected option.
     *
     * @param parser the input parser
     */
    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
    }

    /**
     * Handles the transition to a new screen based on the user's selection.
     *
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        StatusEvent status;
        switch (selected) {
            case 0, 1:
                // Request to set player status (ready/not ready)
                status = PlayerReady.requester(Client.transceiver, new Object()).request(new PlayerReady(MiniModel.getInstance().getUserID(), selected == 0));
                setMessage(null);
                if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                    setMessage(((Pota) status).errorMessage());
                }

                return nextScreen != null ? nextScreen : this;
            case 2:
                // Request to leave the lobby
                status = LeaveLobby.requester(Client.transceiver, new Object()).request(new LeaveLobby(MiniModel.getInstance().getUserID(), MiniModel.getInstance().getCurrentLobby().getLobbyName()));
                if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                    setMessage(((Pota) status).errorMessage());
                    return this;
                }
                return new Menu();
            case 3:
                return new ClosingProgram();
        }

        return this;
    }

    /**
     * Prints the TUI for the lobby screen.
     */
    @Override
    public void printTui() {
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

    /**
     * Returns the prompt line before user input.
     *
     * @return the prompt string
     */
    protected String lineBeforeInput() {
        return "Set status (ready/not ready) or leave the lobby:";
    }

    /**
     * Sets the message to be displayed to the user.
     *
     * @param message the message to display
     */
    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the type of this TUI screen.
     *
     * @return the TuiScreens enum value for Lobby
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.Lobby;
    }

    /**
     * Sets the next screen to be displayed after this one.
     *
     * @param nextScreen the next TuiScreenView
     */
    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        this.nextScreen = nextScreen;
    }
}
