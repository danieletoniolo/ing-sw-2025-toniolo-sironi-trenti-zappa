package view.tui.states;
import org.jline.terminal.Terminal;
import view.tui.input.Command;

import java.util.ArrayList;

public interface StateView {
    StateView internalViewState(Command command);

    void printTui(Terminal terminal);

    ArrayList<String> getOptions();

    int getTotalLines();
}
