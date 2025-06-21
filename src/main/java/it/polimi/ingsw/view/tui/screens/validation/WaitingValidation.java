package it.polimi.ingsw.view.tui.screens.validation;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.ClosingProgram;
import it.polimi.ingsw.view.tui.screens.OtherPlayer;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.Validation;

public class WaitingValidation extends Validation {

    public WaitingValidation() {
        super(null);

        super.setMessage("Waiting for other players to validate their spaceships...");
    }

    @Override
    protected String lineBeforeInput() {
        return "Other players' ship:";
    }

    @Override
    public TuiScreenView setNewScreen() {
        if ((selected < options.size() - 1) && (selected >= options.size() - 1 - MiniModel.getInstance().getOtherPlayers().size())) {
            int i = selected - (options.size() - MiniModel.getInstance().getOtherPlayers().size() - 1);
            return new OtherPlayer(MiniModel.getInstance().getOtherPlayers().get(i), this);
        }

        if (selected == options.size() - 1) {
            return new ClosingProgram();
        }

        return this;
    }
}
