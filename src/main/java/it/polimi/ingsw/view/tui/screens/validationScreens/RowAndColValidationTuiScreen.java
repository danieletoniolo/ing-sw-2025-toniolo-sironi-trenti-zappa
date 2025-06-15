package it.polimi.ingsw.view.tui.screens.validationScreens;

import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.ValidationTuiScreen;
import org.javatuples.Pair;

import java.awt.event.PaintEvent;

public class RowAndColValidationTuiScreen extends ValidationTuiScreen {
    private final TuiScreenView oldScreen;
    private Pair<Integer, Integer> correctRowAndCol;
    public RowAndColValidationTuiScreen(TuiScreenView oldScreen) {
        super();

        options.clear();
        this.oldScreen = oldScreen;
    }

    @Override
    public void readCommand(Parser parser) throws Exception {
        Pair<Integer, Integer> rowAndCol = parser.getRowAndCol("Type coordinates to destroy a component (row col): ", totalLines);
        correctRowAndCol = new Pair<>(rowAndCol.getValue0() - 1, rowAndCol.getValue1() - 1);
    }

    @Override
    public TuiScreenView setNewScreen() {
        destroyTiles.add(correctRowAndCol);
        try {
            spaceShipView.removeComponent(correctRowAndCol.getValue0(), correctRowAndCol.getValue1());
        } catch (Exception _) {
        }
        return oldScreen;
    }
}
