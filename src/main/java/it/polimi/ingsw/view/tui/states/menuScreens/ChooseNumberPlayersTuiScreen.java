package it.polimi.ingsw.view.tui.states.menuScreens;

import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.states.MenuTuiScreen;
import it.polimi.ingsw.view.tui.states.TuiScreenView;
import it.polimi.ingsw.view.tui.states.TuiScreens;

import java.util.function.Supplier;

public class ChooseNumberPlayersTuiScreen extends MenuTuiScreen {

    public ChooseNumberPlayersTuiScreen() {
        options.clear();
        options.add("2 players");
        options.add("3 players");
        options.add("4 players");
        isNewScreen = true;
    }

    @Override
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        selected = parser.getCommand(options, totalLines, isStillCurrentScreen);
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
