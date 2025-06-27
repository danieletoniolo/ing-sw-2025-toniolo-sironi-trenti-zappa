package it.polimi.ingsw.view.tui.screens.validation;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.ClosingProgram;
import it.polimi.ingsw.view.tui.screens.OtherPlayer;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.Validation;

/**
 * Represents the validation screen shown while waiting for other players
 * to validate their spaceships in the TUI.
 */
public class WaitingValidation extends Validation {

    /**
     * Constructs a WaitingValidation screen with a waiting message.
     */
    public WaitingValidation() {
        super(null);

        super.setMessage("Waiting for other players to validate their spaceships...");
    }

    /**
     * Returns the line to display before the input prompt.
     *
     * @return a string describing the other players' ships
     */
    @Override
    protected String lineBeforeInput() {
        return "Other players' ship:";
    }

    /**
     * Determines and returns the next screen to display based on the user's selection.
     *
     * @return the next TuiScreenView to display
     */
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
