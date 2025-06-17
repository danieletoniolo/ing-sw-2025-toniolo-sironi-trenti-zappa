package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoods;

import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

import java.util.ArrayList;

public class StorageFromTuiScreen extends ManagerSwapGoodTuiScreen {
    private final TuiScreenView oldScreen;

    public StorageFromTuiScreen(TuiScreenView oldScreen) {
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
        return new StorageWithTuiScreen(oldScreen);
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.StorageFrom;
    }

    @Override
    protected String lineBeforeInput() {
        return "Select a storage to swap FROM:";
    }
}
