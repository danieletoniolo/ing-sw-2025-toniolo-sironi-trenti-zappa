package view.tui;

import view.tui.input.Command;
import view.tui.input.Parser;
import view.tui.states.StartingState;
import view.tui.states.ViewState;

public class TuiManager {
    private Parser parser = new Parser();
    private Command command;
    private Menu menu;

    public void startTui(){
        ViewState currentState = new StartingState();

        Thread parserThread = new Thread(() -> {
            while (true) {
                Command command = Parser.readCommand();
                try {
                    ViewState state = currentState.validCommand(command);
                    if (state!=null){
                        currentState = state;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        Thread viewThread = new Thread(() -> {
            while (true) {

            }
        });

        Thread communicationThread = new Thread(() -> {
            while (true) {

            }
        });

        viewThread.start();
        parserThread.start();
        communicationThread.start();
    }
}
