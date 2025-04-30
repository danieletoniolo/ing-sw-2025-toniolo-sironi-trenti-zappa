package network.messages;

import java.io.Serializable;

/**
 * This class is used to send a heartbeat message over the network.
 * The client has to send a heartbeat message periodically in order to not be disconnected.
 */
public class HearBeat extends ZeroArgMessage {
    public HearBeat() {
        super(MessageType.HEARTBEAT);
    }
}
