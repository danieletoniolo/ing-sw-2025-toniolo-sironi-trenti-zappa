package it.polimi.ingsw.view.tui.states.gameScreens.planetsActions;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.cards.PlanetsView;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.tui.states.TuiScreenView;
import it.polimi.ingsw.view.tui.states.gameScreens.PlanetsTuiScreen;

import java.util.ArrayList;

public class TakeGoodFromPlanetTuiScreen extends PlanetsTuiScreen {

    public TakeGoodFromPlanetTuiScreen(int planet) {
        super(new ArrayList<>(){{
            for (GoodView good : ((PlanetsView) MiniModel.getInstance().shuffledDeckView.getDeck().peek()).getPlanet(planet)) {
                add(good.drawTui());
            }
        }});
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;



        return this;
    }
}
