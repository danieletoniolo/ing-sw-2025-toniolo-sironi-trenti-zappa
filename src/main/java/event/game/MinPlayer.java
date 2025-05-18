package event.game;

import event.Event;

import java.io.Serializable;

/**
 * This record represents the minimum player for the combat zone state.
 * @param userID is the user ID of the player.
 */
public record MinPlayer(
        String userID
) implements Event, Serializable {
}
