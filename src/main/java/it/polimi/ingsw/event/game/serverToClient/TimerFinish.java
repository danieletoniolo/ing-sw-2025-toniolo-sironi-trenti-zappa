package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

public record TimerFinish() implements Event, Serializable {
}
