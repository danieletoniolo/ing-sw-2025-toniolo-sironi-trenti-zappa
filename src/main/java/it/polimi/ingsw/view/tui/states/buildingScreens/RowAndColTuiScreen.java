package it.polimi.ingsw.view.tui.states.buildingScreens;

import org.javatuples.Pair;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.states.BuildingTuiScreen;
import it.polimi.ingsw.view.tui.states.TuiScreenView;
import it.polimi.ingsw.view.tui.states.TuiScreens;

import java.util.function.Supplier;

public class RowAndColTuiScreen extends BuildingTuiScreen {
    private Pair<Integer, Integer> rowAndCol;

    public RowAndColTuiScreen() {
        options.clear();
        isNewScreen = true;
    }

    @Override
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        rowAndCol = parser.getRowAndCol("Type coordinates (row col): ", totalLines, isStillCurrentScreen);
    }

    @Override
    public TuiScreenView setNewScreen() {
        return new BuildingTuiScreen();
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.RowAndCol;
    }
}
