package event.game.serverToClient;

import event.eventType.Event;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * This event is used when the timer has been flipped.
 * @param nickname      is the nickname of the player who flipped the timer.
 * @param startingTime  is the time when the timer was flipped.
 * @param timerDuration is the duration of the timer in seconds.
 */
public record TimerFlipped(
        String nickname,
        String startingTime,
        long timerDuration
) implements Event, Serializable {
}
