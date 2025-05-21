package view.tui.states;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import view.miniModel.MiniModel;
import view.miniModel.board.LevelView;
import view.miniModel.lobby.LobbyView;
import view.tui.input.Parser;

import java.util.ArrayList;

public class MenuScreenTui implements ScreenTuiView {
    private final ArrayList<String> options;
    private int selected;

    public MenuScreenTui() {
        options = new ArrayList<>();
        int i = 0;
        for (LobbyView lobby : MiniModel.getInstance().lobbyViews) {
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
    public ScreenTuiView isViewCommand() {
        return null;
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        writer.print("\033[H\033[2J");
        writer.flush();

        System.out.println("\nAvailable lobbies:");
        writer.flush();
    }

    @Override
    public void sendCommandToServer() {
        //TODO: Implements logic
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

        ArrayList<LobbyView> currentLobbies = MiniModel.getInstance().lobbyViews;
        currentLobbies.add(new LobbyView("pippo", 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("nico", 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("eli", 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("lolo", 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("lore", 4, LevelView.LEARNING));
        currentLobbies.add(new LobbyView("vitto", 4, LevelView.LEARNING));

        MenuScreenTui menuStateView = new MenuScreenTui();
        menuStateView.printTui(terminal);
        menuStateView.readCommand(parser);

    }
}
