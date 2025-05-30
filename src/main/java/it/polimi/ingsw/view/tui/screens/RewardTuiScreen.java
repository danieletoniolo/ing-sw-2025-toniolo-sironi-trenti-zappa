package it.polimi.ingsw.view.tui.screens;

import org.jline.terminal.Terminal;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.function.Supplier;

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
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        selected = parser.getCommand(options, totalLines, isStillCurrentScreen);
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

    @Override
    public TuiScreens getType() {
        return TuiScreens.Reward;
    }
}
