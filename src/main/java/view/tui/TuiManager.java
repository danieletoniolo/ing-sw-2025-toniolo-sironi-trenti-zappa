package view.tui;

import view.structures.logIn.LogInView;
import view.tui.input.Command;
import view.tui.input.Parser;
import view.tui.states.LogInStateView;
import view.tui.states.StateView;
import view.tui.translater.CommandHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TuiManager {
    private final BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<>();
    private final Object stateLock = new Object();
    private StateView currentState = new LogInStateView(new LogInView());
    private CommandHandler commandHandler = new CommandHandler();

    public void startTui(){
        //Reading inputs thread
        Thread parserThread = new Thread(() -> {
            while (true) {
                Command command = Parser.readCommand();
                try {
                    Event newEvent = commandHandler.createEvent(command, currentState);
                    if (newEvent != null) {

                    }
                    commandQueue.put(command);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        //Thread for printing the TUI
        Thread viewThread = new Thread(() -> {
            while (true) {
                try {
                    synchronized (stateLock){
                        stateLock.wait();
                    }
                    currentState.printTui();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        parserThread.start();
        viewThread.start();
    }
}
