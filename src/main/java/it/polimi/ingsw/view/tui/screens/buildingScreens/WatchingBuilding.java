package it.polimi.ingsw.view.tui.screens.buildingScreens;

import it.polimi.ingsw.view.tui.screens.Building;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

/**
 * Screen representing the state where the player is watching the building phase,
 * waiting for other players to complete their actions.
 */
public class WatchingBuilding extends Building {

    /**
     * Constructs a WatchingBuilding screen.
     * Calls the superclass constructor with null as parameter.
     */
    public WatchingBuilding() {
        super(null);
    }

    /**
     * Returns the message to display before the input prompt.
     *
     * @return a string indicating the player is waiting for others
     */
    @Override
    protected String lineBeforeInput() {
        return "Waiting for other players...";
    }

    /**
     * Returns the type of this screen.
     *
     * @return the TuiScreens enum value representing WatchingBuilding
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.WatchingBuilding;
    }
}
