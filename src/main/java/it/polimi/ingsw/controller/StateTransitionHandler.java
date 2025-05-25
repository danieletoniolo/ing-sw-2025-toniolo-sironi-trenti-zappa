package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.state.State;

public interface StateTransitionHandler {
    void changeState(State state);
}
