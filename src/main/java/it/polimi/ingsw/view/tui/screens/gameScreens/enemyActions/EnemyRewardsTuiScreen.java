package it.polimi.ingsw.view.tui.screens.gameScreens.enemyActions;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.cards.CardViewType;
import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.NotClientTurnTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.MenuGoodsTuiScreen;

import java.util.List;

public class EnemyRewardsTuiScreen extends GameTuiScreen {

    public EnemyRewardsTuiScreen() {
        super(List.of("Claim rewards", "Refuse rewards"));
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        switch (selected) {
            case 0:
                if (MiniModel.getInstance().getShuffledDeckView().getDeck().peek().getCardViewType().equals(CardViewType.SMUGGLERS)) {
                    return new MenuGoodsTuiScreen(new NotClientTurnTuiScreen());
                }

                break;
            case 1:

                break;
        }

        return this;
    }
}
