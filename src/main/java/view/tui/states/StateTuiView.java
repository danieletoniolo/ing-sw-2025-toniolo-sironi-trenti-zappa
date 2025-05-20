package view.tui.states;
import org.jline.terminal.Terminal;
import view.tui.input.Command;

import java.util.ArrayList;

public interface StateTuiView {
    StateTuiView internalViewState(Command command);

    void printTui(Terminal terminal);

    ArrayList<String> getOptions();

    int getTotalLines();
}
