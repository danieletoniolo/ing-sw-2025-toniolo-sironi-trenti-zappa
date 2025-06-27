package it.polimi.ingsw.view.tui.screens.gameScreens;

import it.polimi.ingsw.view.tui.screens.CardsGame;

/**
 * Represents the screen shown to the client when it is not their turn.
 * Extends {@link CardsGame} and customizes the message displayed to the user.
 */
public class NotClientTurnCards extends CardsGame {

    /**
     * Constructs a new NotClientTurnCards screen.
     * Calls the superclass constructor with null as parameter.
     */
    public NotClientTurnCards() {
        super(null);
    }

    /**
     * Returns the message to display before the input prompt,
     * informing the user they can view other players' ships while waiting.
     *
     * @return a string message for the user
     */
    @Override
    protected String lineBeforeInput() {
        return "View other players' ship while waiting for your turn.";
    }
}
