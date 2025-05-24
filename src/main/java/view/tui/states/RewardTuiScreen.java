package view.tui.states;

import org.jline.terminal.Terminal;
import view.tui.input.Parser;

import java.util.ArrayList;

public class RewardTuiScreen implements TuiScreenView {
    private ArrayList<String> options;
    private int totalLines;
    private int selected;
    private String message;

    public RewardTuiScreen() {
        this.options = new ArrayList<>();
        totalLines = 1;
    }


    @Override
    public void readCommand(Parser parser) throws Exception {
        selected = parser.getCommand(options, totalLines);
    }

    @Override
    public TuiScreenView setNewScreen() {
        return null;
    }

    @Override
    public void printTui(Terminal terminal) {

    }

    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
    }
}
