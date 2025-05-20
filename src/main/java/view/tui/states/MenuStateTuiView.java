package view.tui.states;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import view.structures.MiniModel;
import view.structures.board.LevelView;
import view.structures.lobby.LobbyView;
import view.tui.input.Command;
import view.tui.input.Parser;

import java.util.ArrayList;

public class MenuStateTuiView implements StateTuiView {
    private final ArrayList<String> options;
    private int totlaLines = 1;

    public MenuStateTuiView() {
        options = new ArrayList<>();
        int i = 0;
        for (LobbyView lobby : MiniModel.getInstance().lobbyViews) {
            i++;
            options.add(i + ". Join " + lobby.getLobbyName() + " - " + lobby.getMaxPlayer() + " players - " + lobby.getLevel().toString());
        }
        options.add("Exit");
    }

    @Override
    public int getTotalLines() {
        return totlaLines;
    }

    @Override
    public ArrayList<String> getOptions() {
        return options;
    }

    @Override
    public StateTuiView internalViewState(Command command) {
        return null; // Placeholder for the next state
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        writer.print("\033[H\033[2J");
        writer.flush();

        System.out.println("\nAvailable lobbies:");
        writer.flush();
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

        MenuStateTuiView menuStateView = new MenuStateTuiView();
        menuStateView.printTui(terminal);
        parser.getCommand(menuStateView.getOptions(), menuStateView.getTotalLines());

    }
}
