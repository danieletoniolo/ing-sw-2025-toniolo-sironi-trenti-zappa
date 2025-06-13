package it.polimi.ingsw.view.tui.screens.menuScreens;

import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.MenuTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

public class ChooseNumberPlayersTuiScreen extends MenuTuiScreen {

    public ChooseNumberPlayersTuiScreen() {
        options.clear();
        options.add("2 players");
        options.add("3 players");
        options.add("4 players");
    }

    @Override
    public void readCommand(Parser parser) throws Exception {
        selected = parser.getCommand(options, totalLines);
        maxPlayers = selected + 2;
    }

    @Override
    public TuiScreenView setNewScreen() {
        return new ChooseLevelTuiScreen();
    }

    @Override
    protected String lineBeforeInput() {
        return "Choose the number of player for the game:";
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.ChooseNumberPlayersTuiScreen;
    }
}
