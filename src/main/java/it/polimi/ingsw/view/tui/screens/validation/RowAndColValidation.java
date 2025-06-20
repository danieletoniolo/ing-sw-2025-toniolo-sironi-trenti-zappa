package it.polimi.ingsw.view.tui.screens.validation;

import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.ValidationTuiScreen;
import org.javatuples.Pair;

import java.util.List;

public class RowAndColValidation extends ValidationTuiScreen {
    private final TuiScreenView oldScreen;
    private Pair<Integer, Integer> correctRowAndCol;
    public RowAndColValidation(TuiScreenView oldScreen) {
        super(List.of(""));

        options.clear();
        options.add("");
        this.oldScreen = oldScreen;
    }

    @Override
    public void readCommand(Parser parser) {
        Pair<Integer, Integer> rowAndCol = parser.getRowAndCol("Type coordinates to destroy a component (row col): ", totalLines);
        correctRowAndCol = new Pair<>(rowAndCol.getValue0() - 1, rowAndCol.getValue1() - 1);
    }

    @Override
    public TuiScreenView setNewScreen() {
        try {
            spaceShipView.removeComponent(correctRowAndCol.getValue0(), correctRowAndCol.getValue1());
            destroyTiles.add(correctRowAndCol);
        } catch (Exception _) {
            setMessage("Invalid coordinates. Please try again.");
        }
        return oldScreen;
    }
}
