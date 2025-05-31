package it.polimi.ingsw.event.game.serverToClient.deck;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

public record DrawCard() implements Event, Serializable {
}
