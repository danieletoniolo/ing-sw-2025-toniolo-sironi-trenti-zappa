package view.tui;



import event.game.AddCoins;
import event.lobby.CreateLobby;
import event.lobby.JoinLobby;
import event.lobby.LeaveLobby;
import event.lobby.RemoveLobby;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import view.Manager;
import view.miniModel.MiniModel;
import view.miniModel.player.PlayerDataView;
import view.tui.input.Command;
import view.tui.input.Parser;
import view.tui.states.LobbyStateTuiView;
import view.tui.states.MenuStateTuiView;
import view.tui.states.StateTuiView;
import view.tui.translater.CommandHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TuiManager implements Manager {
    private final BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<>();
    private final Object stateLock = new Object();
    private StateTuiView currentState /*= new LogInStateView(new LogInView())*/;
    private CommandHandler commandHandler = new CommandHandler();

    private Parser parser;
    private Terminal terminal;

    public TuiManager() {
        try {
            this.terminal = TerminalBuilder.builder()
                    .jna(true)
                    .jansi(true)
                    .build();
        } catch (Exception e) {
            System.err.println("Creation terminal error: " + e.getMessage());
            return;
        }
        parser = new Parser(terminal);
    }


    @Override
    public void notifyCreateLobby(CreateLobby data) {
        if (data.userID() == null || data.userID().equals(MiniModel.getInstance().nickname)) { // Create a new lobbyState if the user is the one who created it or the server said to do so
            //currentState = new LobbyStateView(data.lobbyID());
            stateLock.notifyAll();
        }
        else {
            if (currentState instanceof MenuStateTuiView) { // Refresh the menu if another user creates a lobby because nd we are in the menu state
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyRemoveLobby(RemoveLobby data){
        // placeHolder
    }

    @Override
    public void notifyJoinLobby(JoinLobby data) {
        if (data.userID() == null || data.userID().equals(MiniModel.getInstance().nickname)) { // Create a new lobbyState if the user is the one who created it or the server said to do so
            currentState = new LobbyStateTuiView();
            stateLock.notifyAll();
        }
        else {
            if (currentState instanceof LobbyStateTuiView) { // Refresh the lobby view if another user joins because we are in the lobby state
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyLeaveLobby(LeaveLobby data) {
        if (data.userID() == null || data.userID().equals(MiniModel.getInstance().nickname)) { // Create a new MenuState if the user or the server said to do so
            //currentState = new MenuStateView();
            stateLock.notifyAll();
        }
        else {
            if (currentState instanceof LobbyStateTuiView) { // Refresh the lobby view if another user leaves because we are in the lobby state
                stateLock.notifyAll();
            }
        }
    }

    @Override
    public void notifyAddCoins(AddCoins data) {
        if (data.userID() == null || data.userID().equals(MiniModel.getInstance().nickname)) {
            currentState.notifyAll();
        }
        else {
            if (currentState instanceof PlayerDataView) {

            }
        }
    }

    public void startTui(){
        //Reading inputs thread
        Thread parserThread = new Thread(() -> {
            while (true) {
                try {
                    Command command = parser.getCommand(currentState.getOptions(), currentState.getTotalLines());
                    StateTuiView possibleNewState = currentState.internalViewState(command);
                    if (possibleNewState == null) {
                        commandHandler.createEvent(command, currentState);
                    }
                    else {
                        currentState = possibleNewState;
                        synchronized (stateLock) {
                            stateLock.notifyAll();
                        }
                    }
                } catch (Exception e) {
                    e.getMessage();
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
                    currentState.printTui(terminal);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        parserThread.start();
        viewThread.start();
    }


    public static void main(String[] args) {
        TuiManager tuiManager = new TuiManager();
        tuiManager.startTui();
    }
}
