package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.player.CurrentPlayer;
import it.polimi.ingsw.event.game.serverToClient.player.EnemyDefeat;
import it.polimi.ingsw.event.game.serverToClient.player.UpdateCoins;
import it.polimi.ingsw.event.game.serverToClient.spaceship.NextHit;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.Pirates;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.Component;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PiratesState extends State {
    private final Pirates card;
    private final Map<PlayerData, Float> stats;
    private PiratesInternalState internalState;
    private Boolean piratesDefeat;
    private final ArrayList<PlayerData> playersDefeated;
    private final List<List<Pair<Integer, Integer>>> fragments;
    private final Pair<Component, Integer> protectionResult;
    private int hitIndex;
    private boolean diceRolled;

    /**
     * Enum to represent the internal state of the pirates state.
     */
    private enum PiratesInternalState {
        ENEMY_DEFEAT,
        REWARD,
        PENALTY
    }

    /**
     * Constructor whit players and card
     * @param board The board associated with the game
     * @param card Pirates card associated with the state
     */
    public PiratesState(Board board, EventCallback callback, Pirates card, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        this.card = card;
        this.stats = new HashMap<>();
        this.piratesDefeat = false;
        this.internalState = PiratesInternalState.ENEMY_DEFEAT;
        this.playersDefeated = new ArrayList<>();
        this.fragments = new ArrayList<>();
        this.protectionResult = new Pair<>(null, -1);
        this.hitIndex = 0;
        this.diceRolled = false;
    }

    /**
     * Implementation of {@link State#setFragmentChoice(PlayerData, int)} to set the fragment choice.
     */
    @Override
    public void setFragmentChoice(PlayerData player, int fragmentChoice) throws IllegalStateException {
        if (internalState != PiratesInternalState.PENALTY) {
            throw new IllegalStateException("Fragment choice not allowed in this state");
        }
        if (fragments.isEmpty()) {
            throw new IllegalArgumentException("No fragments available to choose from");
        }
        for (int i = 0; i < fragments.size(); i++) {
            if (i != fragmentChoice) {
                List<Event> events = Handler.destroyFragment(player, fragments.get(i));
                for (Event e : events) {
                    eventCallback.trigger(e);
                }
            }
        }
        fragments.clear();
    }

    /**
     * Implementation of the {@link State#setProtect(PlayerData, int)} to set whether the player wants to protect or not.
     */
    @Override
    public void setProtect(PlayerData player, int batteryID) throws IllegalStateException, IllegalArgumentException {
        if (internalState != PiratesInternalState.PENALTY || !diceRolled) {
            throw new IllegalStateException("setProtect not allowed in this state");
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
        if (internalState != PiratesInternalState.PENALTY) {
            throw new IllegalStateException("setDice not allowed in this state");
        }
        if (diceRolled) {
            throw new IllegalStateException("Dice already rolled in this state");
        }
        Pair<Event, Event> event = Handler.rollDice(player, card.getFires().get(hitIndex), protectionResult);
        eventCallback.trigger(event.getValue0());
        eventCallback.trigger(event.getValue1());
        diceRolled = true;
    }

    /**
     * Implementation of the {@link State#useExtraStrength(PlayerData, int, List, List)} to use double engines
     * in this state.
     * @throws IllegalArgumentException if the type is not 0 or 1.
     */
    @Override
    public void useExtraStrength(PlayerData player, int type, List<Integer> IDs, List<Integer> batteriesID) throws IllegalStateException, IllegalArgumentException {
        switch (type) {
            case 0 -> throw new IllegalStateException("Cannot use double engine in this state");
            case 1 -> {
                if (internalState == PiratesInternalState.ENEMY_DEFEAT) {
                    throw new IllegalStateException("Cannot use double cannons in this state");
                }
                Event event = Handler.useExtraStrength(player, type, IDs, batteriesID);
                this.stats.merge(player, player.getSpaceShip().getCannonsStrength(IDs), Float::sum);
                eventCallback.trigger(event);
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0 or 1.");
        }
    }

    /**
     * Entry method, set the stats for the players
     */
    @Override
    public void entry() {
        for (PlayerData player : super.players) {
            float initialStrength = player.getSpaceShip().getSingleCannonsStrength();
            if (player.getSpaceShip().hasPurpleAlien()) {
                initialStrength += SpaceShip.getAlienStrength();
            }
            this.stats.put(player, initialStrength);
        }
        super.entry();
    }

    /**
     * Execute.
     * @param player PlayerData of the player to play
     * @throws IllegalStateException if acceptCredits not set
     */
    @Override
    public void execute(PlayerData player) throws IllegalStateException {
        int cardValue = card.getCannonStrengthRequired();

        switch (internalState) {
            case ENEMY_DEFEAT:
                if (stats.get(player) > cardValue) {
                    piratesDefeat = true;
                    internalState = PiratesInternalState.REWARD;
                } else if (stats.get(player) < cardValue) {
                    piratesDefeat = false;
                    this.playersDefeated.add(player);
                } else {
                    piratesDefeat = null;
                }

                EnemyDefeat enemyDefeat = new EnemyDefeat(player.getUsername(), piratesDefeat);
                eventCallback.trigger(enemyDefeat);
                break;
            case REWARD:
                if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYING) {
                    player.addCoins(card.getCredit());
                    board.addSteps(player, -card.getFlightDays());

                    UpdateCoins updateCoinsEvent = new UpdateCoins(player.getUsername(), player.getCoins());
                    eventCallback.trigger(updateCoinsEvent);
                }
                super.execute(player);
                for (PlayerData p: playersDefeated) {
                    playersStatus.put(p.getColor(), PlayerStatus.WAITING);
                }

                internalState = PiratesInternalState.PENALTY;
                break;
            case PENALTY:
                if (!playersDefeated.contains(player)) {
                    throw new IllegalStateException("Other player was not defeated");
                }

                hitIndex++;
                NextHit nextHitEvent = new NextHit(player.getUsername());
                eventCallback.trigger(nextHitEvent);
                if (hitIndex < card.getFires().size()) {
                    playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
                    playersDefeated.remove(player);
                    hitIndex = 0;
                    diceRolled = false;
                }
                break;
        }

        try {
            CurrentPlayer currentPlayerEvent = new CurrentPlayer(this.getCurrentPlayer().getUsername());
            eventCallback.trigger(currentPlayerEvent);
        }
        catch(Exception e) {
            // Ignore the exception
        }

        super.nextState(GameState.CARDS);
    }
}
