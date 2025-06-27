package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

/**
 * Represents the screen shown when the connection to the server is lost.
 * Implements the TuiScreenView interface for the TUI.
 */
public class ConnectionLost implements TuiScreenView {

    /**
     * Returns the current screen as the new screen.
     *
     * @return this instance of ConnectionLost
     */
    @Override
    public TuiScreenView setNewScreen() {
        return this;
    }

    /**
     * Prints the connection lost message to the terminal.
     */
    @Override
    public void printTui() {
        TerminalUtils.clearLastLines(1);

        TerminalUtils.printLine("Connection with the server lost. Closing program...", 1);
        TerminalUtils.printLine("", 2);
    }

    /**
     * No commands to read on this screen.
     *
     * @param parser the parser for user input (unused)
     */
    @Override
    public void readCommand(Parser parser) {
        // No commands to read in this screen
    }

    /**
     * No message to set on this screen.
     *
     * @param message the message to set (unused)
     */
    @Override
    public void setMessage(String message) {
        // No message to set in this screen
    }

    /**
     * Returns the type of this screen.
     *
     * @return TuiScreens.Ending
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.Ending;
    }

    /**
     * No next screen to set in this case.
     *
     * @param nextScreen the next screen to set (unused)
     */
    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        // No next screen to set in this case
    }
}
