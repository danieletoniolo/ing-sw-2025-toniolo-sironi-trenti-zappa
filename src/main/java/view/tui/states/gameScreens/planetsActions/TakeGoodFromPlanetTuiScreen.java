package view.tui.states.gameScreens.planetsActions;

import view.miniModel.MiniModel;
import view.miniModel.cards.PlanetsView;
import view.miniModel.good.GoodView;
import view.tui.states.TuiScreenView;
import view.tui.states.gameScreens.PlanetsTuiScreen;

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
