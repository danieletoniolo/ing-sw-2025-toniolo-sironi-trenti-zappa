package event.game.serverToClient;

import event.EventTransceiver;
import event.Responder;
import event.eventType.Event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * This event is used when a player has used the cannons.
 * It is used to notify the other players that the player has used the cannons.
 * @param nickname     is the user nickname. Only the user know his ID, so the event is not faked.
 * @param cannonsIDs   The IDs of the cannons to use.
 * @param batteriesIDs The IDs of the batteries to use, in order to reach the power.
 * */
public record CannonsUsed(
        String nickname,
        List<Integer> cannonsIDs,
        List<Integer> batteriesIDs
) implements Event, Serializable {}
