package view.tui.states;

import org.jline.terminal.Terminal;
import view.miniModel.MiniModel;
import view.miniModel.board.LevelView;
import view.miniModel.lobby.LobbyView;
import view.tui.TerminalUtils;
import view.tui.input.Parser;
import view.tui.states.menuScreens.ChooseNumberPlayersTuiScreen;

import java.util.ArrayList;
import java.util.function.Supplier;

public class MenuTuiScreen implements TuiScreenView {
    protected final ArrayList<String> options = new ArrayList<>();
    protected int totalLines = 5;
    protected int selected;
    private int row;
    protected String message;
    protected boolean isNewScreen;

    protected int maxPlayers;
    protected int level;

    public MenuTuiScreen() {

        int i = 0;
        for (LobbyView lobby : MiniModel.getInstance().lobbiesView) {
            i++;
            options.add(i + ". Join " + lobby.getLobbyName() + " - " + lobby.getNumberOfPlayers() + "/" + lobby.getMaxPlayer() + " players - " + lobby.getLevel().toString());
        }
        options.add("Create lobby");
        options.add("Exit");

        isNewScreen = true;
    }

    @Override
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        selected = parser.getCommand(options, totalLines, isStillCurrentScreen);
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        row = 1;

        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, message == null ? "df" : message, row++);
        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, lineBeforeInput(), row++);

        if (isNewScreen) {
            isNewScreen = false;
            for (int i = totalLines + options.size(); i < terminal.getSize().getRows(); i++ ) {
                TerminalUtils.printLine(writer, "", i);
            }
        }
    }

    public String lineBeforeInput(){
        return "Available lobbies:";
    }

    @Override
    public TuiScreenView setNewScreen() {

        if (selected == options.size() - 2) { //TODO: togliere il nome della lobby, lo crea il server
            return new ChooseNumberPlayersTuiScreen();
        }

        if (selected == options.size() - 1) {
            return new LogInTuiScreen();
        }


        /*StatusEvent status = JoinLobby.requester(Client.transceiver, new Object()).request(new JoinLobby(MiniModel.getInstance().userID, MiniModel.getInstance().lobbiesView.get(selected - 1).getLobbyName()));
        if (status.get().equals("POTA")) {
            return this;
        }*/

        return new LobbyTuiScreen();
    }

    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Menu;
    }
}
