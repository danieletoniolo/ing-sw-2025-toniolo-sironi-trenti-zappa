package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;
import org.jline.terminal.Terminal;

public class ConnectionLostScreen implements TuiScreenView {
    @Override
    public TuiScreenView setNewScreen() {
        return null; // No new screen to set
    }

    @Override
    public void printTui(Terminal terminal) {
        for (int i = 1; i < terminal.getSize().getRows(); i++ ) {
            TerminalUtils.printLine(terminal.writer(), "", i);
        }
        TerminalUtils.printLine(terminal.writer(), "Connection with the server lost. Closing program...", 1);
        TerminalUtils.printLine(terminal.writer(), "", 2);
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
