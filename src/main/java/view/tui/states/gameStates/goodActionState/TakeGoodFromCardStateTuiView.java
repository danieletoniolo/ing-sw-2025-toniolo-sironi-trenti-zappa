package view.tui.states.gameStates.goodActionState;

import view.miniModel.MiniModel;
import view.miniModel.cards.CardView;
import view.miniModel.cards.PlanetsView;

import java.util.ArrayList;

public class TakeGoodFromCardStateTuiView extends GoodManagerStateTuiView{
    private ArrayList<String> options = new ArrayList<>();

    public TakeGoodFromCardStateTuiView() {
        CardView card = MiniModel.getInstance().shuffledDeckView.getDeck().peek();
        switch (card.getCardViewType()) {
            case PLANETS:
                //((PlanetsView) card).
                break;
            case SMUGGLERS:
                break;
            case ABANDONEDSTATION:
                break;
        }
    }
}
