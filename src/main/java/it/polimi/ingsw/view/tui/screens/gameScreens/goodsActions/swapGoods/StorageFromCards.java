package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoods;

import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

/**
 * This class represents a screen for swapping goods from storage cards in the TUI.
 * It extends ManagerSwapGoodCards and manages the selection of a storage to swap goods from.
 */
public class StorageFromCards extends ManagerSwapGoodCards {
    /**
     * The previous screen to return to when the user selects "Back".
     */
    private final TuiScreenView oldScreen;

    /**
     * Constructs a StorageFromCards screen.
     * Initializes the options list with all available storages and a "Back" option.
     *
     * @param oldScreen the previous screen to return to
     */
    public StorageFromCards(TuiScreenView oldScreen) {
        super(new ArrayList<>(){{
            spaceShipView.getMapStorages().forEach(
                    (_, value) -> add("Swap from the storage " + "(" + value.getRow() + "," + value.getCol() + ")"));
            add("Back");
        }});
        this.oldScreen = oldScreen;
    }

    /**
     * Sets the new screen based on the user's selection.
     * If "Back" is selected, returns to the previous screen.
     * Otherwise, sets the selected storage and proceeds to the next screen.
     *
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == spaceShipView.getMapStorages().size()) {
            destroyStatics();
            setMessage(null);
            return oldScreen;
        }

        int cont = 0;
        int ID = -1;
        for (var entry : spaceShipView.getMapStorages().entrySet()) {
            if (cont == selected) {
                ID = entry.getKey();
                break;
            }
            cont++;
        }
        fromStorage = spaceShipView.getMapStorages().get(ID);
        setMessage("You are swapping goods from (" + fromStorage.getRow() + "," + fromStorage.getCol() + ")");
        return new StorageWithCards(oldScreen);
    }

    /**
     * Returns the prompt line shown before the user input.
     *
     * @return the prompt string
     */
    @Override
    protected String lineBeforeInput() {
        return "Select a storage to swap FROM:";
    }
}
