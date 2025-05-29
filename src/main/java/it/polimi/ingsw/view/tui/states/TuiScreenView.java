package it.polimi.ingsw.view.tui.states;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.function.Supplier;

public interface TuiScreenView {
    void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception;

    /**
     * Set a new Screen -> when the command is only part of the view or the command is sent to the server
     * @return a new ScreenTuiView
     */
    TuiScreenView setNewScreen();

    void printTui(Terminal terminal);

    void setMessage(String message);

    TuiScreens getType();
}
