package view.tui.states;

import view.structures.logIn.LogInView;
import view.tui.input.Command;

public class LogInStateView extends StateView {
    private LogInView logInView;

    public LogInStateView() {
        logInView = new LogInView();
    }

    @Override
    public StateView isValidCommand(Command command) {
        return null; // Placeholder for the next state
    }

    @Override
    public void printTui() {
        for (int i = 0; i < logInView.getRowsToDraw(); i++) {
            System.out.println(logInView.drawLineTui(i));
        }
    }
}
