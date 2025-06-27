package it.polimi.ingsw.view.tui.screens.validation;

import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.Validation;
import org.javatuples.Pair;

import java.util.List;

/**
 * Validation class for handling row and column input in the TUI.
 * Prompts the user to enter coordinates and validates them for component destruction.
 */
public class RowAndColValidation extends Validation {
    /** The previous screen to return to after validation. */
    private final TuiScreenView oldScreen;

    /** The validated row and column pair (zero-based). */
    private Pair<Integer, Integer> correctRowAndCol;

    /**
     * Constructs a new RowAndColValidation.
     *
     * @param oldScreen the previous TuiScreenView to return to
     */
    public RowAndColValidation(TuiScreenView oldScreen) {
        super(List.of(""));

        options.clear();
        options.add("");
        this.oldScreen = oldScreen;
    }

    /**
     * Reads the row and column command from the user using the provided parser.
     * Converts the input to zero-based indices.
     *
     * @param parser the Parser to read user input
     */
    @Override
    public void readCommand(Parser parser) {
        Pair<Integer, Integer> rowAndCol = parser.getRowAndCol("Type coordinates to destroy a component (row col): ", totalLines);
        correctRowAndCol = new Pair<>(rowAndCol.getValue0() - 1, rowAndCol.getValue1() - 1);
    }

    /**
     * Attempts to remove a component at the validated coordinates.
     * If the coordinates are invalid, sets an error message.
     *
     * @return the previous TuiScreenView
     */
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
