package it.polimi.ingsw.view.tui.screens.gameScreens.openSpaceAcitons;

import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.engineActions.ChooseDoubleEngineCards;

import java.util.List;

/**
 * Represents the screen for the Open Space phase where players can turn on their engines.
 * Extends {@link CardsGame} to provide specific actions and messages for this phase.
 */
public class OpenSpaceCards extends CardsGame {

    /**
     * Constructs an OpenSpaceCards screen with a predefined action and message.
     */
    public OpenSpaceCards() {
        super(List.of("Turn on engines"));
        setMessage("Now's the time! Turn on your engines and go as fast as you can!");
    }

    /**
     * Sets the new screen based on the player's selection.
     * If the first option is selected, clones the player's ship and transitions to the
     * {@link ChooseDoubleEngineCards} screen.
     *
     * @return the next {@link TuiScreenView} to display, or this screen if no valid selection is made
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == 0) {
            spaceShipView = clientPlayer.getShip().clone();
            return new ChooseDoubleEngineCards();
        }

        return this;
    }
}
