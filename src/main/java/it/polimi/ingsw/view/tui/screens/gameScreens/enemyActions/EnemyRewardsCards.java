package it.polimi.ingsw.view.tui.screens.gameScreens.enemyActions;

import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.cards.CardViewType;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.MenuGoodsCards;

import java.util.List;

public class EnemyRewardsCards extends CardsGame {

    public EnemyRewardsCards() {
        super(List.of("Claim rewards", "Refuse rewards"));
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        StatusEvent status;
        switch (selected) {
            case 0:
                if (MiniModel.getInstance().getShuffledDeckView().getDeck().peek().getCardViewType().equals(CardViewType.SMUGGLERS)) {
                    return new MenuGoodsCards();
                }


                break;
            case 1:

                break;
        }

        return this;
    }
}
