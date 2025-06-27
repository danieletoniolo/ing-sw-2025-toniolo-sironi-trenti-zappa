package it.polimi.ingsw.view.tui.screens.rewardScreens;

import it.polimi.ingsw.view.tui.screens.Reward;

/**
 * Represents a screen shown to the player while waiting to claim rewards.
 * Displays a message inviting the player to view other players' ships.
 */
public class WaitingReward extends Reward {
    /**
     * Constructs a WaitingReward screen.
     * Calls the superclass constructor with null as parameter.
     */
    public WaitingReward() {
        super(null);
    }

    /**
     * Returns the message to display before the input prompt.
     *
     * @return a string inviting the player to view other players' ships
     */
    protected String lineBeforeInput() {
        return "View players' ship while waiting for your turn to claim rewards:";
    }
}
