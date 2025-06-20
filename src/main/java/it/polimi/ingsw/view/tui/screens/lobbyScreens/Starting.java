package it.polimi.ingsw.view.tui.screens.lobbyScreens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.Lobby;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;
import org.jline.terminal.Terminal;

import java.util.ArrayList;

public class Starting extends Lobby {

    public Starting() {
        options.clear();
    }

    @Override
    protected String lineBeforeInput() {
        return "";
    }

    @Override
    public void readCommand(Parser parser) {
        parser.getCommand(new ArrayList<>(), totalLines);
    }

    @Override
    public TuiScreenView setNewScreen() {
        return this;
    }

    @Override
    public void printTui(Terminal terminal) {
        setMessage(MiniModel.getInstance().getCountDown().drawLineTui(0));

        super.printTui(terminal);
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.StartingLobby;
    }
}