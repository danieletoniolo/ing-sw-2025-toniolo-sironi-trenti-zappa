package it.polimi.ingsw.view.tui.screens.validationScreens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.ClosingProgram;
import it.polimi.ingsw.view.tui.screens.PlayerTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.ValidationTuiScreen;

public class WaitingValidationTuiScreen extends ValidationTuiScreen {

    public WaitingValidationTuiScreen() {
        super();
        options.clear();

        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.getUsername() + "'s spaceship");
        }
        options.add("Close program");
        super.setMessage("Waiting for other players to validate their spaceships...");
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
