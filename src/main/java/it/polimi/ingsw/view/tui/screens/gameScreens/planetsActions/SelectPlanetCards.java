package it.polimi.ingsw.view.tui.screens.gameScreens.planetsActions;

import it.polimi.ingsw.event.game.clientToServer.planets.SelectPlanet;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.cards.CardView;
import it.polimi.ingsw.view.miniModel.cards.PlanetsView;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.MenuGoodsCards;

import java.util.ArrayList;

public class SelectPlanetCards extends CardsGame {
    private final TuiScreenView oldScreen;

    public SelectPlanetCards(TuiScreenView oldScreen) {
        super(new ArrayList<>() {{
            CardView card = MiniModel.getInstance().getShuffledDeckView().getDeck().peek();
            for (int i = 0; i < ((PlanetsView) card).getNumberOfPlanets(); i++) {
                add((i + 1) + "");
            }
            add("Cancel");
        }});
        this.oldScreen = oldScreen;
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        CardView card = MiniModel.getInstance().getShuffledDeckView().getDeck().peek();
        if (selected == ((PlanetsView) card).getNumberOfPlanets()) {
            return oldScreen;
        }

        // Send the request to select the planet
        StatusEvent status = SelectPlanet.requester(Client.transceiver, new Object()).request(new SelectPlanet(MiniModel.getInstance().getUserID(), selected));
        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }

        return new MenuGoodsCards();
    }

    @Override
    protected String lineBeforeInput() {
        return "Select a planet from the card:";
    }
}
