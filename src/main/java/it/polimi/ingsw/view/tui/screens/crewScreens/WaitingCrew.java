package it.polimi.ingsw.view.tui.screens.crewScreens;

import it.polimi.ingsw.view.tui.screens.ModifyCrew;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

/**
 * Represents a screen shown to the crew while waiting for an action or event.
 * Extends {@link ModifyCrew} to inherit crew modification behavior.
 */
public class WaitingCrew extends ModifyCrew {

    /**
     * Constructs a new WaitingCrew screen.
     * Calls the superclass constructor with null as parameter.
     */
    public WaitingCrew() {
        super(null);
    }

    /**
     * Sets and returns the new screen to be displayed.
     * If the superclass provides a new screen, it is returned.
     * Otherwise, this screen remains active.
     *
     * @return the next {@link TuiScreenView} to display, or this instance if no change.
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        return this;
    }
}
