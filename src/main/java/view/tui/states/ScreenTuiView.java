package view.tui.states;
import org.jline.terminal.Terminal;
import view.tui.input.Parser;

public interface ScreenTuiView {
    void readCommand(Parser parser) throws Exception;

    ScreenTuiView isViewCommand();

    void sendCommandToServer();

    void printTui(Terminal terminal);
}
