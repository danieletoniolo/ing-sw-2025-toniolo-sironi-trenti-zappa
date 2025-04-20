package controller.event.game;

import controller.event.Event;

import java.io.Serializable;

public record NoPayload() implements Event, Serializable { }
