package event;

import event.eventType.Event;
import event.game.EventWrapper;
import event.eventType.StatusEvent;
import event.receiver.CastEventReceiver;
import event.receiver.EventReceiver;
import event.trasmitter.EventTransmitter;
import network.exceptions.DisconnectedConnection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// TODO: disconnectListener
public class Requester<S extends Event> {
    private final EventReceiver<EventWrapper<StatusEvent>> receiver;
    private final EventTransmitter transmitter;

    private final EventListener<EventWrapper<StatusEvent>> responseListener;
    //private final EventListener<EventWrapper> disconnectListener;

    //private final EventListener<PlayerDisconnectedInternalEventData> disconnectedListener;

    private final Map<Integer, StatusEvent> responses = new HashMap<>();

    private final Object responsesLock;

    private final Set<Integer> waitingFor = new HashSet<>();

    private int nextRequest;
    private final Object nextRequestLock = new Object();

    private boolean disconnected;

    public Requester(EventTransceiver transceiver, Object responsesLock) {
        this.transmitter = transceiver;
        this.receiver = new CastEventReceiver<>(transceiver);
        this.responsesLock = responsesLock;

        this.responseListener = data -> {
            synchronized (responsesLock) {
                if (waitingFor.contains(data.ID())) {
                    waitingFor.remove(data.ID());
                    responses.put(data.ID(), data.event());

                    this.responsesLock.notifyAll();
                }
            }
        };

        /*
        this.disconnectListener = data -> {
            synchronized (responsesLock) {
                disconnected = true;
                this.responsesLock.notifyAll();
            }
        };
        */
    }

    public StatusEvent request(S data) {
        int count;

        synchronized (nextRequestLock) {
            count = nextRequest;
            nextRequest++;
        }

        synchronized (responsesLock) {
            waitingFor.add(count);
        }

        transmitter.broadcast(new EventWrapper<>(count, data));

        synchronized (responsesLock) {
            while(responses.get(count) == null) {
                if (disconnected) {
                    throw new DisconnectedConnection();
                }

                try {
                    responsesLock.wait();
                } catch (InterruptedException e) {
                    // Ignore the catch
                }
            }

            return responses.remove(count);
        }
    }

    public void registerListeners() {
        synchronized (responsesLock) {
            receiver.registerListener(responseListener);
            //transceiver.registerListener(disconnectListener);

            disconnected = false;
        }
    }

    public void unregisterListeners() {
        synchronized (responsesLock) {
            receiver.unregisterListener(responseListener);
            //transceiver.unregisterListener(disconnectedListener);

            disconnected = true;

            responsesLock.notifyAll();
        }
    }
}
