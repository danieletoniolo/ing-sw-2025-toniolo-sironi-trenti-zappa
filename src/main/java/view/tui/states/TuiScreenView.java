package view.tui.states;
import org.jline.terminal.Terminal;
import view.tui.input.Parser;

public interface TuiScreenView {
    void readCommand(Parser parser) throws Exception;

    /**
     * Set a new Screen -> when the command is only part of the view or the command is sent to the server
     * @return a new ScreenTuiView
     */
    TuiScreenView setNewScreen();

    void printTui(Terminal terminal);

    void setMessage(String message);
}
