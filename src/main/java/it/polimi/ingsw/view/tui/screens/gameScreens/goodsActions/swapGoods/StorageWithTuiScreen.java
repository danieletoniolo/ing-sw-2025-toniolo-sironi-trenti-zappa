package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoods;

import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

import java.util.ArrayList;

public class StorageWithTuiScreen extends ManagerSwapGoodTuiScreen {
    private final TuiScreenView oldScreen;

    public StorageWithTuiScreen(TuiScreenView oldScreen) {
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
        return new SwapGoodsFromTuiScreen(oldScreen);
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.StorageTo;
    }

    @Override
    protected String lineBeforeInput() {
        return "Select a storage to swap WITH:";
    }
}
