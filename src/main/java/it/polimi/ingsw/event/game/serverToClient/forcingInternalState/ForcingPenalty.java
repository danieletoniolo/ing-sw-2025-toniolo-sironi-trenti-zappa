package it.polimi.ingsw.event.game.serverToClient.forcingInternalState;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This record represents the minimum player for the combat zone state.
 * @param nickname The userID of the player that is the minimum player.
 * @param penaltyType The type of penalty that the player will receive.
 *                    0 crew penalty, 1 for goods penalty, 2 for batteries penalty.
 */
public record ForcingPenalty(
        String nickname,
        int penaltyType
) implements Event, Serializable {
}
