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

/**
 * Screen for selecting a planet card in the TUI game interface.
 * Extends {@link CardsGame} to provide planet selection options.
 */
public class SelectPlanetCards extends CardsGame {
    /**
     * Reference to the previous screen to return to if needed.
     */
    private final TuiScreenView oldScreen;

    /**
     * Constructs the SelectPlanetCards screen.
     * Initializes the selectable options based on the number of planets in the current card.
     *
     * @param oldScreen the previous screen to return to on cancel
     */
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

    /**
     * Handles the logic for setting the new screen after a selection is made.
     * Sends a request to select a planet and handles possible errors.
     *
     * @return the next {@link TuiScreenView} to display
     */
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

    /**
     * Provides the prompt line before user input.
     *
     * @return the prompt string
     */
    @Override
    protected String lineBeforeInput() {
        return "Select a planet from the card:";
    }
}
