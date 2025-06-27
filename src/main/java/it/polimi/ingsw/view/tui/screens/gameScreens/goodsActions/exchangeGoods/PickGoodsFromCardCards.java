package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen for picking goods from a card during the exchange goods action in the TUI.
 * Extends {@link ManagerExchangeGoodsCards} to manage the selection and exchange process.
 */
public class PickGoodsFromCardCards extends ManagerExchangeGoodsCards {
    /**
     * The previous screen to return to after the action is completed or cancelled.
     */
    private final TuiScreenView oldScreen;

    /**
     * The list of goods currently available on the card.
     */
    private final List<GoodView> goodsOnCard;

    /**
     * Constructs a new PickGoodsFromCardCards screen.
     *
     * @param goodsOnCard the list of goods available to pick from the card
     * @param oldScreen   the previous screen to return to
     */
    public PickGoodsFromCardCards(List<GoodView> goodsOnCard, TuiScreenView oldScreen) {
        super(new ArrayList<>(){{
            for (GoodView good : goodsOnCard) {
                add(good.drawTui());
            }
            add("Done");
            add("Cancel");
        }});

        this.goodsOnCard = goodsOnCard;
        this.oldScreen = oldScreen;
    }

    /**
     * Handles the logic for setting the new screen after a selection is made.
     * Adds selected goods to the exchange, manages the flow for "Done" and "Cancel" actions,
     * and updates the message shown to the user.
     *
     * @return the next {@link TuiScreenView} to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == goodsOnCard.size()) {
            exchanges.add(new Triplet<>(
                    new ArrayList<>(goodsToGet),
                    new ArrayList<>(goodsToLeave),
                    storage.getID()));
            goodsToLeave.clear();
            goodsToGet.clear();
            return new StorageExchangeCards(oldScreen);
        }

        if (selected == goodsOnCard.size() + 1) {
            setMessage(null);
            destroyStatics();
            return oldScreen;
        }

        goodsToGet.add(goodsOnCard.get(selected).getValue());
        storage.addGood(goodsOnCard.get(selected));
        goodsOnCard.remove(selected);

        StringBuilder line = new StringBuilder();
        for (Integer good : goodsToGet) {
            line.append(GoodView.fromValue(good).drawTui()).append(" ");
        }

        setMessage("You have selected " + line);
        return new PickGoodsFromCardCards(goodsOnCard, oldScreen);
    }

    /**
     * Returns the line to display before the user input, based on the card type.
     *
     * @return the prompt string to show to the user
     */
    @Override
    protected String lineBeforeInput() {
        return switch (MiniModel.getInstance().getShuffledDeckView().getDeck().peek().getCardViewType()) {
            case PLANETS -> "Choose GOODS from the planet:";
            case SMUGGLERS -> "Claim your REWARDS:";
            case ABANDONEDSTATION -> "Choose your GOODS:";
            default -> "Commands:";
        };
    }
}
