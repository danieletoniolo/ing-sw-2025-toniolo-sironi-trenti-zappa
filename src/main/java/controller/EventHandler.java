package controller;

import controller.event.Event;

import java.util.UUID;
import java.util.function.Consumer;

public record EventHandler<T extends Event>(
        Consumer<T> handler,
        UUID userID
) {
    @Override
    public String toString() {
        return "EventHandler{" +
                "handler=" + handler +
                ", UUID='" + userID + '\'' +
                '}';
    }
}