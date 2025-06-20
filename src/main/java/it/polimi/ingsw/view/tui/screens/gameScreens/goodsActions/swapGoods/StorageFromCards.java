package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoods;

import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class StorageFromCards extends ManagerSwapGoodCards {
    private final TuiScreenView oldScreen;

    public StorageFromCards(TuiScreenView oldScreen) {
        super(new ArrayList<>(){{
            spaceShipView.getMapStorages().forEach(
                    (key, value) -> {
                        add("Swap from the storage " + "(" + value.getRow() + "," + value.getCol() + ")");
                    });
            add("Back");
        }});
        this.oldScreen = oldScreen;
    }

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

    @Override
    protected String lineBeforeInput() {
        return "Select a storage to swap FROM:";
    }
}
