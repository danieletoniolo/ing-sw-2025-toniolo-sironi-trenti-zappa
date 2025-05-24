package view.tui.states;

import event.eventType.StatusEvent;
import event.lobby.clientToServer.CreateLobby;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import view.Client;
import view.miniModel.MiniModel;
import view.miniModel.board.LevelView;
import view.miniModel.lobby.LobbyView;
import view.tui.input.Parser;

import java.util.ArrayList;

public class MenuTuiScreen implements TuiScreenView {
    private final ArrayList<String> options;
    private int selected;
    private String message;

    public MenuTuiScreen() {
        options = new ArrayList<>();
        options.add("Create lobby");
        int i = 0;
        for (LobbyView lobby : MiniModel.getInstance().lobbiesView) {
            i++;
            options.add(i + ". Join " + lobby.getLobbyName() + " - " + lobby.getMaxPlayer() + " players - " + lobby.getLevel().toString());
        }
        options.add("Exit");
    }

    @Override
    public void readCommand(Parser parser) throws Exception {
        int totalLines = 1;
        selected = parser.getCommand(options, totalLines);
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        writer.print("\033[H\033[2J");
        writer.flush();

        if (message != null) {
            writer.println(message);
            writer.println();
            writer.flush();
        }

        System.out.println("\nAvailable lobbies:");
        writer.flush();
    }

    @Override
    public TuiScreenView setNewScreen() {

        if (selected == 0) {

            /*StatusEvent status = CreateLobby.requester(Client.transceiver, new Object()).request(new CreateLobby()) ;
            if (status.get().equals("POTA")) {
                return this;
            }*/

            return new LobbyTuiScreen();
        }


        return this;
    }

    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
    }

    public static void main(String[] args) throws Exception {
        Terminal terminal;
        try {
            terminal = TerminalBuilder.builder()
                    .jna(true)
                    .jansi(true)
                    .build();
        } catch (Exception e) {
            System.err.println("Creation terminal error: " + e.getMessage());
            return;
        }
        Parser parser = new Parser(terminal);

        ArrayList<LobbyView> currentLobbies = MiniModel.getInstance().lobbiesView;
        currentLobbies.add(new LobbyView("pippo", 0, 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("nico", 0, 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("eli", 0, 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("lolo", 0, 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("lore", 0, 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("vitto", 0, 4, LevelView.LEARNING));

        MenuTuiScreen menuStateView = new MenuTuiScreen();
        menuStateView.printTui(terminal);
        menuStateView.readCommand(parser);

    }
}
