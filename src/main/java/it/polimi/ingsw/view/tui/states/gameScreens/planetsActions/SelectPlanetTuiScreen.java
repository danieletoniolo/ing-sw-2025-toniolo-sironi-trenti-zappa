package it.polimi.ingsw.view.tui.states.gameScreens.planetsActions;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.cards.CardView;
import it.polimi.ingsw.view.miniModel.cards.PlanetsView;
import it.polimi.ingsw.view.tui.states.GameTuiScreen;
import it.polimi.ingsw.view.tui.states.TuiScreenView;

import java.util.ArrayList;

public class SelectPlanetTuiScreen extends GameTuiScreen {

    public SelectPlanetTuiScreen() {
        super(new ArrayList<>() {{
            CardView card = MiniModel.getInstance().shuffledDeckView.getDeck().peek();
            for (int i = 0; i < ((PlanetsView) card).getNumberOfPlanets(); i++) {
                add((i + 1) + "");
            }
        }});
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;


        return new TakeGoodFromPlanetTuiScreen(selected);
    }

    @Override
    protected String lineBeforeInput() {
        return "Select a planet from the card:";
    }
}
