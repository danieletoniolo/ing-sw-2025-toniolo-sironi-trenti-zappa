package event.game.clientToServer;

import event.Requester;
import event.eventType.Event;
import event.EventTransceiver;
import event.Responder;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * This event is used when a player have to destroy components of his ship.
 * @param userID              is the user ID. Only the user know his ID, so the event is not faked.
 * @param componentsToDestroy The list of components that the player has to destroy, the pair represent the row and the columns of the component
 */
public record DestroyComponents(
        String userID,
        List<Pair<Integer, Integer>> componentsToDestroy
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the DestroyComponents event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the DestroyComponents event.
     */
    public static <T extends Event> Responder<DestroyComponents, T> responder(EventTransceiver transceiver, Function<DestroyComponents, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the DestroyComponents event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the DestroyComponents event
     */
    public static Requester<DestroyComponents> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
