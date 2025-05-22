package event.game.serverToClient;

import event.EventTransceiver;
import event.Responder;
import event.eventType.Event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * This event is used when the user wants to use the engines.
 * It is used to notify the other players that the user wants to use the engines.
 * @param nickname     The nickname of the user. Only the user know his nickname, so the event is not faked.
 * @param enginesIDs   The IDs of the engines to use.
 * @param batteriesIDs The IDs of the batteries to use, in order to reach the power.
 */
public record EnginesUsed(
        String nickname,
        List<Integer> enginesIDs,
        List<Integer> batteriesIDs
) implements Event, Serializable {}
