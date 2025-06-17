package it.polimi.ingsw.view.tui.screens.lobbyScreens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.LobbyTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class StartingTuiScreen extends LobbyTuiScreen {

    public StartingTuiScreen() {
        options.clear();
        message = MiniModel.getInstance().getCountDown().drawLineTui(0);
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
}