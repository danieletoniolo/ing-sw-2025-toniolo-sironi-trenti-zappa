package controller;

import controller.event.Event;

import java.util.function.Consumer;

public record EventHandler<T extends Event>(
        Consumer<T> handler,
        int uuid
) {
    @Override
    public String toString() {
        return "EventHandler{" +
                "handler=" + handler +
                ", UUID='" + uuid + '\'' +
                '}';
    }
}