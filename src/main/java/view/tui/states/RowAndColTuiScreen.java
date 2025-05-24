package view.tui.states;

import org.javatuples.Pair;
import view.tui.input.Parser;

public class RowAndColTuiScreen extends BuildingTuiScreen {
    private Pair<Integer, Integer> rowAndCol;

    public RowAndColTuiScreen() {

    }

    @Override
    public void readCommand(Parser parser) throws Exception {
        rowAndCol = parser.getRowAndCol("Type coordinates (row col): ", totalLines);
    }

    @Override
    public TuiScreenView setNewScreen() {
        return new BuildingTuiScreen();
    }
}
