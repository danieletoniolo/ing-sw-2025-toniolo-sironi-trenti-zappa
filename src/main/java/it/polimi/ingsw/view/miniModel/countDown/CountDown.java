package it.polimi.ingsw.view.miniModel.countDown;

import it.polimi.ingsw.view.miniModel.Structure;

/**
 * Represents a countdown structure for the mini model view.
 * Displays the seconds remaining before the game starts.
 */
public class CountDown implements Structure {
    /**
     * The number of seconds remaining before the game starts.
     */
    private int secondsRemaining;

    /**
     * Sets the number of seconds remaining before the game starts.
     *
     * @param secondsRemaining the seconds left until the game starts
     */
    public void setSecondsRemaining(int secondsRemaining) {
        this.secondsRemaining = secondsRemaining;
    }

    /**
     * Returns a string representation of the countdown for the TUI.
     *
     * @param line the line number to draw (not used in this implementation)
     * @return a string showing the seconds remaining
     */
    @Override
    public String drawLineTui(int line) {
        return "Game starting in " + secondsRemaining + "...";
    }
}
