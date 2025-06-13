package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.spaceship.NextHit;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.MeteorSwarm;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.Component;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class MeteorSwarmState extends State {
    private final MeteorSwarm card;
    private final Pair<Component, Integer> protectionResult;
    private final List<List<Pair<Integer, Integer>>> fragments;
    private boolean diceRolled = false;
    private int hitIndex;

    /**
     * Constructor
     * @param board The board associated with the game
     * @param card card type
     */
    public MeteorSwarmState(Board board, EventCallback callback, MeteorSwarm card, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        this.card = card;
        this.protectionResult = new Pair<>(null, -1);
        this.fragments = new ArrayList<>();
        this.hitIndex = 0;
    }

    /**
     * Implementation of {@link State#setFragmentChoice(PlayerData, int)} to set the fragment choice.
     */
    @Override
    public void setFragmentChoice(PlayerData player, int fragmentChoice) throws IllegalStateException {
        if (fragments.isEmpty()) {
            throw new IllegalStateException("No fragments to choose from");
        }
        for (int i = 0; i < fragments.size(); i++) {
            if (i != fragmentChoice) {
                Event event = Handler.destroyFragment(player, fragments.get(i));
                eventCallback.trigger(event);
            }
        }
        fragments.clear();
    }

    /**
     * Implementation of the {@link State#setProtect(PlayerData, int)} to set whether the player wants to protect or not.
     */
    @Override
    public void setProtect(PlayerData player, int batteryID) throws IllegalStateException, IllegalArgumentException {
        if (!diceRolled) {
            throw new IllegalStateException("Dice not rolled yet");
        }
        Event event = Handler.protectFromHit(player, protectionResult, batteryID);
        if (event != null) {
            eventCallback.trigger(event);
        }
        event = Handler.checkForFragments(player, fragments);
        if (event != null) {
            eventCallback.trigger(event);
        } else {
            fragments.clear();
        }
    }

    /**
     * Implementation of the {@link State#rollDice(PlayerData)} to roll the dice and set the value in the fight handler.
     */
    @Override
    public void rollDice(PlayerData player) throws IllegalStateException {
        if (diceRolled) {
            throw new IllegalStateException("Dice already rolled for this hit");
        }
        Pair<Event, Event> event = Handler.rollDice(player, card.getMeteors().get(hitIndex), protectionResult);
        eventCallback.trigger(event.getValue0());
        eventCallback.trigger(event.getValue1());
        diceRolled = true;
    }

    /**
     * Execute: Check if player can protect, destroy components if necessary, choose which components to keep if necessary
     * @param player PlayerData of the player to play
     * @throws IndexOutOfBoundsException hitIndex out of bounds, toKeepComponents is out of bounds
     * @throws IllegalStateException Dice not set
     */
    @Override
    public void execute(PlayerData player) throws IndexOutOfBoundsException, IllegalStateException {
        super.execute(player);
        if (players.indexOf(player) == players.size() - 1) {
            hitIndex++;
            NextHit nextHitEvent = new NextHit(player.getUsername());
            eventCallback.trigger(nextHitEvent);
            if (hitIndex < card.getMeteors().size()) {
                for (PlayerData p : players) {
                    playersStatus.put(p.getColor(), PlayerStatus.WAITING);
                }
                diceRolled = false;
            }
        }
        super.nextState(GameState.CARDS);
    }
}