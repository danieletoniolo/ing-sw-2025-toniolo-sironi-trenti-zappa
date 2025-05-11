package view.tui;

import view.tui.input.Command;
import view.tui.input.Parser;
import view.tui.states.LogInStateView;
import view.tui.states.StateView;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TuiManager {
    private final BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<>();
    private final Object stateLock = new Object();
    private StateView currentState = new LogInStateView();
    private ModuleLayer.Controller controller;

    public void startTui(){
        //Reading inputs thread
        Thread parserThread = new Thread(() -> {
            while (true) {
                Command command = Parser.readCommand();
                try {
                    commandQueue.put(command);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        //Thread for managing the state of the TUI
        Thread communicationThread = new Thread(() -> {
            while (true) {
                try {
                    Command command = commandQueue.take();

                    try {
                        // View change state local (ex: , "help", "change what to see without changing the data structure)
                        StateView possibleNewState = currentState.isValidCommand(command);
                        if (possibleNewState != null) {
                            // Change viewable screen
                            currentState = possibleNewState;
                            continue;
                        }

                        // Controller changes tha state of the view (ex: "ready", "not", "leave")
                        controller.handleCommand(command, newState -> {
                            if (newState != null) {
                                currentState = newState;
                            }
                        });
                    } catch (Exception e) {
                        System.out.println("Invalid command: " + command.name() + " with parameters: " + String.join(", ", command.parameters()));
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        //Thread for printing the TUI
        Thread viewThread = new Thread(() -> {
            while (true) {
                try {
                    stateLock.wait();
                    currentState.printTui();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        parserThread.start();
        viewThread.start();
        //communicationThread.start();
    }
}
