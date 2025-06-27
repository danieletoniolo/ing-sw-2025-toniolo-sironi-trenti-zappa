package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoods;

import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

/**
 * The StorageWithCards class represents a screen in the TUI where the user can select a storage
 * to swap goods with, excluding the current storage. It extends ManagerSwapGoodCards and manages
 * the list of possible storages to swap with, as well as the navigation back to the previous screen.
 */
public class StorageWithCards extends ManagerSwapGoodCards {
    /**
     * The previous screen to return to if the user cancels the swap.
     */
    private final TuiScreenView oldScreen;

    /**
     * Constructs a StorageWithCards screen, initializing the list of possible storages to swap with,
     * excluding the current storage, and adding a cancel option.
     *
     * @param oldScreen the previous TuiScreenView to return to on cancel
     */
    public StorageWithCards(TuiScreenView oldScreen) {
        super(new ArrayList<>(){{
            spaceShipView.getMapStorages().forEach(
                    (key, value) -> {
                        if (!key.equals(fromStorage.getID())) {
                            add("Swap with the storage " + "(" + value.getRow() + "," + value.getCol() + ")");
                        }
                    });
            add("Cancel");
        }});
        this.oldScreen = oldScreen;
    }

    /**
     * Sets the new screen based on the user's selection.
     * If the user selects cancel, returns to the previous screen.
     * Otherwise, prepares the swap with the selected storage and navigates to the swap screen.
     *
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = 0;
        for (var entry : spaceShipView.getMapStorages().entrySet()) {
            if (!entry.getKey().equals(fromStorage.getID())) {
                num++;
            }
        }

        if (selected == num) {
            destroyStatics();
            setMessage(null);
            return oldScreen;
        }

        int cont = 0;
        int ID = -1;
        for (var entry : spaceShipView.getMapStorages().entrySet()) {
            if (!entry.getKey().equals(fromStorage.getID())) {
                if (cont == selected) {
                    ID = entry.getKey();
                    break;
                }
                cont++;
            }
        }

        withStorage = spaceShipView.getMapStorages().get(ID);
        setMessage("You are swapping goods from (" + fromStorage.getRow() + " " + fromStorage.getCol() +
                ") with goods in (" + withStorage.getRow() + " " + withStorage.getCol() + ")");
        return new SwapGoodsFromCards(oldScreen);
    }

    /**
     * Returns the line to display before the user input prompt.
     *
     * @return the prompt line as a String
     */
    @Override
    protected String lineBeforeInput() {
        return "Select a storage to swap WITH:";
    }
}
