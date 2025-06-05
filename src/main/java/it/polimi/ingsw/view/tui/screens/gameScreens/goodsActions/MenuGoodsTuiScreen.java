package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods.StorageExchangeTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoods.StorageFromTuiScreen;

import java.util.List;

public class MenuGoodsTuiScreen extends GameTuiScreen {
    private final TuiScreenView oldScreen;

    public MenuGoodsTuiScreen(TuiScreenView oldScreen) {
        super(List.of("Swap goods", "Exchange goods", "Done"));
        this.oldScreen = oldScreen;
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        return switch (selected) {
            case 0 -> {
                spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
                yield new StorageFromTuiScreen(this);
            }
            case 1 -> {
                spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
                yield new StorageExchangeTuiScreen(this);
            }
            case 2 -> {
                spaceShipView = clientPlayer.getShip();
                yield oldScreen;
            }
            default -> this;
        };

    }
}
