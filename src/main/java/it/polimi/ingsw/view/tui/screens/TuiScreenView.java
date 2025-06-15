package it.polimi.ingsw.view.tui.screens;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.view.tui.input.Parser;

public interface TuiScreenView {
    void readCommand(Parser parser);

    /**
     * Set a new Screen -> when the command is only part of the view or the command is sent to the server
     * @return a new ScreenTuiView
     */
    TuiScreenView setNewScreen();

    void printTui(Terminal terminal);

    void setMessage(String message);

    TuiScreens getType();

    /**
     * This method is used when the TuiManger has to decide which screen to show to the client
     * as a consequence of an event, while the client is waiting for the TAC
     * @param nextScreen the screen tu show after the TAC
     */
    void setNextScreen(TuiScreenView nextScreen);
}
