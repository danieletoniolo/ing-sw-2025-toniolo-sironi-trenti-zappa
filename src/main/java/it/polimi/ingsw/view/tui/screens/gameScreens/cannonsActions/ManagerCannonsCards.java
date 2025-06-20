package it.polimi.ingsw.view.tui.screens.gameScreens.cannonsActions;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.CardsGame;

import java.util.List;

public abstract class ManagerCannonsCards extends CardsGame {
    protected static List<Integer> cannonsIDs;
    protected static List<Integer> batteriesIDs;

    public ManagerCannonsCards(List<String> options) {
        super(options);
    }

    public static void destroyStatics() {
        cannonsIDs = null;
        batteriesIDs = null;
        PlayerDataView player = MiniModel.getInstance().getClientPlayer();
        spaceShipView = player == null ? null : player.getShip();
    }
}
