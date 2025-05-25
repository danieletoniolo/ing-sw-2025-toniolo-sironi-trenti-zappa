package view.tui.states.buildingScreens;

import org.javatuples.Pair;
import view.tui.input.Parser;
import view.tui.states.BuildingTuiScreen;
import view.tui.states.TuiScreenView;
import view.tui.states.TuiScreens;

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
