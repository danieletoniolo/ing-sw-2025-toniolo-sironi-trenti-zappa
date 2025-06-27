package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.state.State;

/**
 * Interface for handling state transitions in the application.
 * Provides methods to change the current state of the system.
 * @author Vittorio Sironi
 */
public interface StateTransitionHandler {
    /**
     * Changes the current state to the specified state.
     *
     * @param state the new state to transition to
     */
    void changeState(State state);
}
