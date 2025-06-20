package it.polimi.ingsw.view.tui.screens.gameScreens.engineActions;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.CardsGame;

import java.util.List;

public abstract class ManagerEnginesCards extends CardsGame {
    protected static List<Integer> enginesIDs;
    protected static List<Integer> batteriesIDs;

    public ManagerEnginesCards(List<String> options) {
        super(options);
    }

    public static void destroyStatics() {
        enginesIDs = null;
        batteriesIDs = null;
        PlayerDataView player = MiniModel.getInstance().getClientPlayer();
        spaceShipView = player == null ? null : player.getShip();
    }
}
