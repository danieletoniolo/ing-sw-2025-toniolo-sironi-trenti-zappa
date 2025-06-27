package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.tui.input.Parser;

/**
 * Represents the screen shown when the program is closing in the TUI.
 * Implements the TuiScreenView interface.
 */
public class ClosingProgram implements TuiScreenView{

    /**
     * Sets the new screen. In this implementation, always returns itself.
     * @return this instance of ClosingProgram
     */
    @Override
    public TuiScreenView setNewScreen() {
        return this;
    }

    /**
     * Prints the TUI for the closing program screen.
     * Currently, this method does nothing.
     */
    @Override
    public void printTui() {

    }

    /**
     * Reads a command from the user input.
     * Currently, this method does nothing.
     * @param parser the parser to read commands from
     */
    @Override
    public void readCommand(Parser parser) {

    }

    /**
     * Sets a message to be displayed on the screen.
     * Currently, this method does nothing.
     * @param message the message to set
     */
    @Override
    public void setMessage(String message) {

    }

    /**
     * Gets the type of this screen.
     * @return TuiScreens.Ending
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.Ending;
    }

    /**
     * Sets the next screen to be displayed.
     * Currently, this method does nothing.
     * @param nextScreen the next screen to set
     */
    @Override
    public void setNextScreen(TuiScreenView nextScreen) {

    }
}
