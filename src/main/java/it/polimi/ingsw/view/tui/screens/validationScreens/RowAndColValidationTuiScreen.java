package it.polimi.ingsw.view.tui.screens.validationScreens;

import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.ValidationTuiScreen;
import org.javatuples.Pair;

import java.util.function.Supplier;

public class RowAndColValidationTuiScreen extends ValidationTuiScreen {
    private final TuiScreenView oldScreen;
    private Pair<Integer, Integer> rowAndCol;
    public RowAndColValidationTuiScreen(TuiScreenView oldScreen) {
        super();

        options.clear();
        this.oldScreen = oldScreen;
    }

    @Override
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        rowAndCol = parser.getRowAndCol("Type coordinates to destroy a component (row col):", totalLines, isStillCurrentScreen);

    }

    @Override
    public TuiScreenView setNewScreen() {
        destroyTiles.add(rowAndCol);
        spaceShipView.removeComponent(rowAndCol.getValue0(), rowAndCol.getValue1());
        return oldScreen;
    }
}
