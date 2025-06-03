package it.polimi.ingsw.view.tui.screens.gameScreens.smugglersActions;

import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.NotClientTurnTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.MenuGoodsTuiScreen;

import java.util.List;

public class RewardSmugglersTuiScreen extends GameTuiScreen {

    public RewardSmugglersTuiScreen() {
        super(List.of("Claim rewards", "Refuse rewards"));
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        switch (selected) {
            case 0:


                return new MenuGoodsTuiScreen(new NotClientTurnTuiScreen());
            case 1:

                break;
        }

        return this;
    }
}
