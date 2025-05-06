package controller;

import controller.event.Event;
import controller.event.EventType;

import java.util.*;
import java.util.function.Consumer;

public class EventManager {
    private final Map<EventType<? extends Event>, List<EventHandler<? extends Event>>> eventHandlers;

    public EventManager() {
        this.eventHandlers = new HashMap<>();
    }

    private <T extends Event> List<Consumer<T>> getEventHandlers(UUID userID, EventType<T> type) {
        List<EventHandler<? extends Event>> presentConsumers = eventHandlers.get(type);
        if (presentConsumers != null) {
            return presentConsumers
                    .stream()
                    .map(h -> (EventHandler<T>) h)
                    .filter(h -> h.userID().equals(userID) || userID == null)
                    .map(EventHandler::handler)
                    .toList();
        }
        return new ArrayList<>();
    }

    public<T extends Event> void addEventHandler(UUID userID, EventType<T> type, Consumer<T> consumer) {
        eventHandlers.computeIfAbsent(type, k -> new ArrayList<>());
        eventHandlers.get(type).add(new EventHandler<>(consumer, userID));
    }

    public<T extends Event> void removeEventHandlersOfUser(UUID userID) {
        eventHandlers.values().forEach(handlers -> handlers.removeIf(handler -> handler.userID().equals(userID)));
    }

    public <T extends Event> void executeHandlers(EventType<T> type, T info) {
        getEventHandlers(null, type).forEach(handler -> (new Thread(() -> handler.accept(info))).start());
    }

    public <T extends Event> void executeUserHandler(UUID userID, EventType<T> type, T info) {
        getEventHandlers(userID, type).forEach(consumer -> (new Thread(() -> consumer.accept(info))).start());
    }

    public <T extends Event> void executeOthersHandlers(UUID excludedUsername, EventType<T> type, T info) {
        List<Consumer<T>> allEventHandlers = new ArrayList<>(this.getEventHandlers(null, type));
        List<Consumer<T>> mineEventHandlers = this.getEventHandlers(excludedUsername, type);
        allEventHandlers.removeAll(mineEventHandlers);

        allEventHandlers.forEach(consumer -> (new Thread(() -> consumer.accept(info))).start());
    }
}
