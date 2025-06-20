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
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods.StorageExchangeCards;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoods.StorageFromCards;

import java.util.ArrayList;
import java.util.List;

public class MenuGoodsCards extends CardsGame {
    protected static List<GoodView> cardGoods;

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
                StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
                if (status.get().equals("POTA")) {
                    setMessage(((Pota) status).errorMessage());
                    yield this;
                }
                spaceShipView = clientPlayer.getShip();
                yield nextScreen;
            }
            default -> this;
        };
    }

    @Override
    protected String lineBeforeInput() {
        return "Select an action:";
    }

    public static List<GoodView> getCopy() {
        return cardGoods;
    }
    
    public static void setCardGoods(List<GoodView> goods) {
        cardGoods = goods;
    }
}
