package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoodsActions;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

import java.util.ArrayList;

public class StorageFromTuiScreen extends ManagerSwapGoodTuiScreen {
    private final TuiScreenView oldScreen;

    public StorageFromTuiScreen(TuiScreenView oldScreen) {
        super(new ArrayList<>(){{
            spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
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

        if (selected == options.size() - MiniModel.getInstance().getOtherPlayers().size() - 1) {
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
        TuiScreenView newScreen = new StorageWithTuiScreen(oldScreen);
        newScreen.setMessage("You are swapping goods from (" + fromStorage.getRow() + "," + fromStorage.getCol() + ")");
        return newScreen;
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
