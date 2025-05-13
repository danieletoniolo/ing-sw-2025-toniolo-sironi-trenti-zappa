package view.tui.states;

import view.structures.logIn.LogInView;
import view.tui.input.Command;

import java.util.List;

public class LogInStateView implements StateView {
    private LogInView logInView;
    private List<String> validCommands = List.of("/login", "/help");

    public LogInStateView(LogInView logInView) {
        this.logInView = logInView;
    }

    @Override
    public StateView isValidCommand(Command command) {
        if (!validCommands.contains(command.name())) {
            return this;
        }

        switch (command.name()) {
            case "/help":
                System.out.println("Type '/login' and your nickname to log in");
                return this;
        }

        return null;
    }

    @Override
    public void printTui() {
        for (int i = 0; i < LogInView.getRowsToDraw(); i++) {
            System.out.println(logInView.drawLineTui(i));
        }
    }
}
