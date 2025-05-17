package view.tui.states;

import org.jline.terminal.Terminal;
import view.structures.logIn.LogInView;
import view.tui.input.Command;

import java.io.IOException;
import java.util.List;

public class LogInStateView {
    private LogInView logInView;
    private List<String> validCommands = List.of("/login", "/help");

    public LogInStateView(LogInView logInView) {
        this.logInView = logInView;
    }

    public StateView readInput(Terminal terminal) {


        return null;
    }

    public void printTui(Terminal terminal) {
        for (int i = 0; i < LogInView.getRowsToDraw(); i++) {
            System.out.println(logInView.drawLineTui(i));
        }
    }
}
