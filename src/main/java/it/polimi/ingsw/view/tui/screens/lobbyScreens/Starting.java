package it.polimi.ingsw.view.tui.screens.lobbyScreens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.Lobby;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

import java.util.ArrayList;

/**
 * Represents the starting screen of the lobby in the TUI.
 * This screen is shown when the lobby is initializing or waiting to start.
 */
public class Starting extends Lobby {

    /**
     * Constructs a new Starting lobby screen.
     * Clears any existing options.
     */
    public Starting() {
        options.clear();
    }

    /**
     * Returns the line to display before the input prompt.
     * In this screen, it returns an empty string.
     *
     * @return an empty string
     */
    @Override
    protected String lineBeforeInput() {
        return "";
    }

    /**
     * Reads a command from the user using the provided parser.
     * Passes an empty list and the total number of lines to the parser.
     *
     * @param parser the parser to use for reading commands
     */
    @Override
    public void readCommand(Parser parser) {
        parser.getCommand(new ArrayList<>(), totalLines);
    }

    /**
     * Sets the new screen to be displayed.
     * In this implementation, it returns itself.
     *
     * @return this screen instance
     */
    @Override
    public TuiScreenView setNewScreen() {
        return this;
    }

    /**
     * Prints the TUI for the starting lobby screen.
     * Displays the countdown and then calls the superclass print method.
     */
    @Override
    public void printTui() {
        setMessage(MiniModel.getInstance().getCountDown().drawLineTui(0));

        super.printTui();
    }

    /**
     * Returns the type of this TUI screen.
     *
     * @return the TuiScreens.StartingLobby enum value
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.StartingLobby;
    }
}