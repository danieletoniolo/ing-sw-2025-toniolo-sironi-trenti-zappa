package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.cards.AbandonedStationView;
import it.polimi.ingsw.view.miniModel.cards.CardView;
import it.polimi.ingsw.view.miniModel.cards.PlanetsView;
import it.polimi.ingsw.view.miniModel.cards.SmugglersView;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods.StorageExchangeCards;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoods.StorageFromCards;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the menu screen for goods card actions in the TUI.
 * Allows the player to swap, exchange, or finish modifying goods.
 * Manages the static list of goods associated with the current card.
 */
public class MenuGoodsCards extends CardsGame {
    /**
     * Static list of goods present on the current card.
     */
    protected static List<GoodView> cardGoods;

    /**
     * Constructs the menu for goods card actions.
     * Initializes the card goods if not already set, based on the type of the current card.
     */
    public MenuGoodsCards() {
        super(List.of("Swap goods", "Exchange goods", "Done"));

        if (cardGoods == null) {
            cardGoods = new ArrayList<>();
            CardView card = shuffledDeckView.getDeck().peek();
            switch (card.getCardViewType()) {
                case PLANETS:
                    for (GoodView good : ((PlanetsView) card).getPlanet(((PlanetsView) card).getPlanetSelected())) {
                        cardGoods.add(GoodView.fromValue(good.getValue()));
                    }
                    break;
                case SMUGGLERS:
                    for (GoodView good : ((SmugglersView) card).getGoods()) {
                        cardGoods.add(GoodView.fromValue(good.getValue()));
                    }
                    break;
                case ABANDONEDSTATION:
                    for (GoodView good : ((AbandonedStationView) card).getGoods()) {
                        cardGoods.add(GoodView.fromValue(good.getValue()));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Sets the new screen based on the selected action.
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        return switch (selected) {
            case 0 -> {
                spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
                yield new StorageFromCards(this);
            }
            case 1 -> {
                spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
                yield new StorageExchangeCards(this);
            }
            case 2 -> {
                // Player has finished to modify own goods, send EndTurn event
                StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
                if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                    setMessage(((Pota) status).errorMessage());
                    yield this;
                }
                destroyStatics();
                yield nextScreen;
            }
            default -> this;
        };
    }

    /**
     * Returns the line to display before the user input.
     * @return the prompt string
     */
    @Override
    protected String lineBeforeInput() {
        return "Select an action:";
    }

    /**
     * Returns a copy of the current card goods.
     * @return the list of goods on the card
     */
    public static List<GoodView> getCopy() {
        return cardGoods;
    }
    
    /**
     * Sets the static card goods list.
     * @param goods the new list of goods to set
     */
    public static void setCardGoods(List<GoodView> goods) {
        cardGoods = goods;
    }

    /**
     * Destroys static data related to card goods and spaceship view.
     * Resets cardGoods and updates spaceShipView to the current player's ship.
     */
    public static void destroyStatics() {
        cardGoods = null;
        PlayerDataView player = MiniModel.getInstance().getClientPlayer();
        spaceShipView = player == null ? null : player.getShip();
    }
}
