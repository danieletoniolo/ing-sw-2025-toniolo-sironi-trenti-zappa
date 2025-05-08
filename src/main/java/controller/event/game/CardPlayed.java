package controller.event.game;

import controller.event.Event;

import java.io.Serializable;

public record CardPlayed(

) implements Event, Serializable {
}
