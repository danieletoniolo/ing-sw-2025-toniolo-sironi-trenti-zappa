package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;
import org.jline.terminal.Terminal;

public class ConnectionLost implements TuiScreenView {
    @Override
    public TuiScreenView setNewScreen() {
        return this;
    }

    @Override
    public void printTui(Terminal terminal) {
        TerminalUtils.clearLastLines(1);

        TerminalUtils.printLine("Connection with the server lost. Closing program...", 1);
        TerminalUtils.printLine("", 2);
    }

    @Override
    public void readCommand(Parser parser) {
        // No commands to read in this screen
    }

    @Override
    public void setMessage(String message) {
        // No message to set in this screen
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Ending;
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        // No next screen to set in this case
    }
}
