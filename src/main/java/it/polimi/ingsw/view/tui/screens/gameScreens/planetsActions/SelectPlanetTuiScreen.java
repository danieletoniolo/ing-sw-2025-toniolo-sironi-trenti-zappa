package it.polimi.ingsw.view.tui.screens.gameScreens.planetsActions;

import it.polimi.ingsw.event.game.clientToServer.planets.SelectPlanet;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.cards.CardView;
import it.polimi.ingsw.view.miniModel.cards.PlanetsView;
import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.NotClientTurnTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.MenuGoodsTuiScreen;

import java.util.ArrayList;

public class SelectPlanetTuiScreen extends GameTuiScreen {
    private final TuiScreenView oldScreen;

    public SelectPlanetTuiScreen(TuiScreenView oldScreen) {
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

        StatusEvent status = SelectPlanet.requester(Client.transceiver, new Object()).request(new SelectPlanet(MiniModel.getInstance().getUserID(), selected));
        if (status.get().equals("POTA")) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }

        return new MenuGoodsTuiScreen();
    }

    @Override
    protected String lineBeforeInput() {
        return "Select a planet from the card:";
    }
}
