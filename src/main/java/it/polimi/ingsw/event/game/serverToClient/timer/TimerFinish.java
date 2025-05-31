package it.polimi.ingsw.event.game.serverToClient.timer;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

public record TimerFinish() implements Event, Serializable {
}
