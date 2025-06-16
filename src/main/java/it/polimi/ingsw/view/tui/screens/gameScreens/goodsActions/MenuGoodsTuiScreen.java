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
import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods.StorageExchangeTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoods.StorageFromTuiScreen;

import java.util.ArrayList;
import java.util.List;

public class MenuGoodsTuiScreen extends GameTuiScreen {
    private final TuiScreenView oldScreen;
    protected static List<GoodView> cardGoods;
    private TuiScreenView nextScreen;

    public MenuGoodsTuiScreen(TuiScreenView oldScreen) {
        super(List.of("Swap goods", "Exchange goods", "Done"));
        this.oldScreen = oldScreen;

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
                yield new StorageFromTuiScreen(this);
            }
            case 1 -> {
                spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
                yield new StorageExchangeTuiScreen(this);
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

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        this.nextScreen = nextScreen;
    }
}
