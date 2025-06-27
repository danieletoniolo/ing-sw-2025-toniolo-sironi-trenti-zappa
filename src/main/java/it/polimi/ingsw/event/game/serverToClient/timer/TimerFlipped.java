package it.polimi.ingsw.event.game.serverToClient.timer;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This event is used when the timer has been flipped.
 * @param nickname         is the userID of the player who flipped the timer.
 * @param startingTime     is the time when the timer was flipped.
 * @param maxNumberOfFlips is the maximum number of times the timer can be flipped.
 * @param timerDuration    is the duration of the timer in milliseconds.
 * @author Vittorio Sironi
 */
public record TimerFlipped(
        String nickname,
        String startingTime,
        int numberOfFlips,
        int maxNumberOfFlips,
        long timerDuration
) implements Event, Serializable {
}
