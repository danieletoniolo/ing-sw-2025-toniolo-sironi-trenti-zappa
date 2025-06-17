package it.polimi.ingsw.view.tui.screens.crewScreens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.ClosingProgram;
import it.polimi.ingsw.view.tui.screens.ModifyCrewTuiScreen;
import it.polimi.ingsw.view.tui.screens.PlayerTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

public class WaitingCrew extends ModifyCrewTuiScreen {

    public WaitingCrew() {
        super();
        options.clear();
        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.getUsername() + "'s spaceship");
        }
        options.add("Close program");
    }

    @Override
    public TuiScreenView setNewScreen() {
        if ((selected < options.size() - 1) && (selected >= options.size() - 1 - MiniModel.getInstance().getOtherPlayers().size())) {
            int i = selected - (options.size() - MiniModel.getInstance().getOtherPlayers().size() - 1);
            return new PlayerTuiScreen(MiniModel.getInstance().getOtherPlayers().get(i), this);
        }

        if (selected == options.size() - 1) {
            return new ClosingProgram();
        }

        return this;
    }
}
