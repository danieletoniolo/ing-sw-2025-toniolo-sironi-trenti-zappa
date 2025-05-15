package view.tui;

import controller.event.Event;
import controller.event.lobby.CreateLobby;
import view.structures.MiniModel;
import view.structures.Structure;
import view.structures.lobby.LobbyView;
import view.structures.logIn.LogInView;
import view.tui.input.Command;
import view.tui.input.Parser;
import view.tui.states.LobbyStateView;
import view.tui.states.LogInStateView;
import view.tui.states.StateView;
import view.tui.translater.CommandHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TuiManager implements Manager {
    private final BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<>();
    private final Object stateLock = new Object();
    private StateView currentState = new LogInStateView(new LogInView());
    private CommandHandler commandHandler = new CommandHandler();

    private final Map<Class<? extends Event>, Class<? extends StateView>> stateMap = new HashMap<>();

    public TuiManager() {
        stateMap.put(CreateLobby.class, LobbyStateView.class);
    }

    @Override
    public void setEvent(Structure oldStructure, Structure newStructure, Event data) {

        StateView possibleNewState = switch (stateMap.get(event)) {
            case LobbyStateView.class -> {
                LobbyView lobbyView = MiniModel.getInstance().lobbyViews.stream()
                        .filter(lobby -> lobby.getLobbyName().equals(data.lobbyID()))
                        .findFirst();
                new LobbyStateView(new LobbyView(()));
            }
            default -> null;
        };
    }

    public void startTui(){
        //Reading inputs thread
        Thread parserThread = new Thread(() -> {
            while (true) {
                Command command = Parser.readCommand();
                try {
                    //Event newEvent = commandHandler.createEvent(command, currentState);
                    //if (newEvent != null) {

                    //}
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
