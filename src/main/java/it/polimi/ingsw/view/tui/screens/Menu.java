package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.lobby.clientToServer.JoinLobby;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.lobby.LobbyView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.menuScreens.ChooseNumberPlayers;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the main menu screen in the TUI (Text User Interface).
 * Allows the user to view available lobbies, create a new lobby, reload lobbies, or close the program.
 * Handles user input and navigation between different screens.
 */
public class Menu implements TuiScreenView {
    /**
     * List of menu options displayed to the user.
     */
    protected final ArrayList<String> options = new ArrayList<>();
    /**
     * Indicates if the screen is newly created and needs to be rendered.
     */
    private boolean isNewScreen;

    /**
     * Number of lines in the menu (excluding options).
     */
    protected int totalLines = 6;
    /**
     * Index of the currently selected menu option.
     */
    protected int selected;
    /**
     * Message to be displayed to the user (e.g., errors or notifications).
     */
    protected String message;

    /**
     * Maximum number of players allowed in a lobby.
     */
    protected static int maxPlayers;
    /**
     * Level of the lobby or menu (usage context-specific).
     */
    protected int level;

    /**
     * Constructs the Menu, initializing the options list with available lobbies and actions.
     */
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

    /**
     * Reads the user's command from the parser and updates the selected option.
     *
     * @param parser the parser used to read user input
     */
    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
    }

    /**
     * Returns the line to be displayed before the input prompt.
     *
     * @return a string describing the section before input
     */
    protected String lineBeforeInput(){
        return "Available lobbies:";
    }

    /**
     * Determines and returns the next screen based on the user's selection.
     *
     * @return the next TuiScreenView to display
     */
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
            // Join the selected lobby
            StatusEvent status = JoinLobby.requester(Client.transceiver, new Object())
                    .request(new JoinLobby(MiniModel.getInstance().getUserID(), MiniModel.getInstance().getLobbiesView().get(selected).getLobbyName()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }

            return new Lobby();
        }

        return this;
    }

    /**
     * Prints the TUI menu to the terminal, including the welcome message, any notifications, and the list of options.
     */
    @Override
    public void printTui() {
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
     * @return the TuiScreens enum value representing this screen
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.Menu;
    }

    /**
     * Sets the next screen to be displayed. (Currently not implemented)
     *
     * @param nextScreen the next TuiScreenView to display
     */
    @Override
    public void setNextScreen(TuiScreenView nextScreen) {

    }
}
