package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingGiveUp;
import it.polimi.ingsw.event.game.serverToClient.player.CurrentPlayer;
import it.polimi.ingsw.event.game.serverToClient.player.EnemyDefeat;
import it.polimi.ingsw.event.game.serverToClient.player.MoveMarker;
import it.polimi.ingsw.event.game.serverToClient.player.UpdateCoins;
import it.polimi.ingsw.event.game.serverToClient.spaceship.CanProtect;
import it.polimi.ingsw.event.game.serverToClient.spaceship.NextHit;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.Pirates;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.Component;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.model.state.utils.MutablePair;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PiratesState extends State {
    private final Pirates card;
    private final Map<PlayerData, Float> cannonsStrength;
    private PiratesInternalState internalState;
    private Boolean piratesDefeat;
    private final ArrayList<PlayerData> playersDefeated;
    private final List<List<Pair<Integer, Integer>>> fragments;
    private MutablePair<Component, Integer> protectionResult;
    private final MutablePair<Integer, Integer> dice;
    private int hitIndex;
    private PlayerData currentPlayerCanProtect;
    private final List<PlayerData> playersGivenUp;

    /**
     * Enum to represent the internal state of the pirates state.
     */
    enum PiratesInternalState {
        ENEMY_DEFEAT,
        REWARD,
        CAN_PROTECT,
        PENALTY,
        GIVE_UP
    }

    /**
     * Constructor whit players and card
     * @param board The board associated with the game
     * @param card Pirates card associated with the state
     */
    public PiratesState(Board board, EventCallback callback, Pirates card, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        this.card = card;
        this.cannonsStrength = new HashMap<>();
        this.piratesDefeat = false;
        this.internalState = PiratesInternalState.ENEMY_DEFEAT;
        this.playersDefeated = new ArrayList<>();
        this.fragments = new ArrayList<>();
        this.protectionResult = new MutablePair<>(null, -1);
        this.hitIndex = 0;
        this.dice = new MutablePair<>(-1, -1);
        this.currentPlayerCanProtect = null;
        this.playersGivenUp = new ArrayList<>();
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
        if (internalState != PiratesInternalState.PENALTY && internalState != PiratesInternalState.CAN_PROTECT) {
            throw new IllegalStateException("setProtect not allowed in this state");
        }
        Event event = Handler.protectFromHit(player, protectionResult, batteryID);
        if (event != null) {
            eventCallback.trigger(event);
        }
        event = Handler.checkForFragments(player, fragments);
        eventCallback.trigger(event);
    }

    /**
     * Implementation of the {@link State#rollDice(PlayerData)} to roll the dice and set the value in the fight handler.
     */
    @Override
    public void rollDice(PlayerData player) throws IllegalStateException {
        if (internalState != PiratesInternalState.CAN_PROTECT) {
            throw new IllegalStateException("setDice not allowed in this state");
        }
        Event event = Handler.rollDice(player, dice);
        eventCallback.trigger(event);
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
                if (internalState != PiratesInternalState.ENEMY_DEFEAT) {
                    throw new IllegalStateException("Cannot use double cannons in this state");
                }
                Event event = Handler.useExtraStrength(player, type, IDs, batteriesID);
                this.cannonsStrength.merge(player, player.getSpaceShip().getCannonsStrength(IDs), Float::sum);
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
            this.cannonsStrength.put(player, initialStrength);
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
        SpaceShip spaceShip = player.getSpaceShip();
        boolean sendCurrentPlayer = false;

        switch (internalState) {
            case ENEMY_DEFEAT:
                int cannonStrengthRequired = card.getCannonStrengthRequired();

                if (cannonsStrength.get(player) > cannonStrengthRequired) {
                    piratesDefeat = true;
                    internalState = PiratesInternalState.REWARD;
                } else if (cannonsStrength.get(player) < cannonStrengthRequired) {
                    piratesDefeat = false;
                    this.playersDefeated.add(player);
                    if (players.indexOf(player) != players.size() - 1) {
                        sendCurrentPlayer = true;
                    }
                    super.execute(player);
                } else {
                    piratesDefeat = null;
                    if (players.indexOf(player) != players.size() - 1) {
                        sendCurrentPlayer = true;
                    }
                    super.execute(player);
                }

                EnemyDefeat enemyDefeat = new EnemyDefeat(player.getUsername(), piratesDefeat);
                eventCallback.trigger(enemyDefeat);

                if (players.indexOf(player) == players.size() - 1 && (piratesDefeat == null || !piratesDefeat)) {
                    for (PlayerData p : playersDefeated) {
                        playersStatus.replace(p.getColor(), PlayerStatus.WAITING);
                    }
                    internalState = PiratesInternalState.CAN_PROTECT;

                    NextHit nextHitEvent = new NextHit(getCurrentPlayer().getUsername());
                    eventCallback.trigger(nextHitEvent);
                    currentPlayerCanProtect = getCurrentPlayer();
                }
                break;
            case REWARD:
                if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYING) {
                    player.addCoins(card.getCredit());

                    UpdateCoins updateCoinsEvent = new UpdateCoins(player.getUsername(), player.getCoins());
                    eventCallback.trigger(updateCoinsEvent);

                    board.addSteps(player, -card.getFlightDays());
                    MoveMarker stepsEvent = new MoveMarker(player.getUsername(),  player.getModuleStep(board.getStepsForALap()));
                    eventCallback.trigger(stepsEvent);
                }

                for (PlayerData p: players) {
                    super.execute(p);
                }

                if (!playersDefeated.isEmpty()) {
                    for (PlayerData p: playersDefeated) {
                        playersStatus.replace(p.getColor(), PlayerStatus.WAITING);
                    }
                    internalState = PiratesInternalState.CAN_PROTECT;

                    NextHit nextHitEvent = new NextHit(getCurrentPlayer().getUsername());
                    eventCallback.trigger(nextHitEvent);
                    currentPlayerCanProtect = getCurrentPlayer();
                }
                break;
            case PENALTY:
                if (fragments.size() > 1) {
                    break;
                }
                fragments.clear();
                // TODO: TO TEST THE GIVE UP
                if (spaceShip.getHumanCrewNumber() == 0) {
                    this.playersGivenUp.add(player);
                }

                super.execute(player);

                try {
                    currentPlayerCanProtect = getCurrentPlayer();
                } catch (IllegalStateException e) {
                    hitIndex++;
                    if (hitIndex >= card.getFires().size()) {
                        if (!playersGivenUp.isEmpty()) {
                            internalState = PiratesInternalState.GIVE_UP;
                            ForcingGiveUp forcingGiveUpEvent = new ForcingGiveUp(playersGivenUp.getFirst().getUsername(), "You are forced to give up, you have no human crew left");
                            eventCallback.trigger(forcingGiveUpEvent);

                            for (PlayerData p : playersGivenUp) {
                                playersStatus.replace(p.getColor(), PlayerStatus.WAITING);
                            }
                        } else {
                            sendCurrentPlayer = true;
                        }
                    } else {
                        for (PlayerData p : playersDefeated) {
                            playersStatus.replace(p.getColor(), PlayerStatus.WAITING);
                        }
                        currentPlayerCanProtect = getCurrentPlayer();

                        NextHit nextHitEvent = new NextHit(currentPlayerCanProtect.getUsername());
                        eventCallback.trigger(nextHitEvent);

                        internalState = PiratesInternalState.CAN_PROTECT;
                    }
                    break;
                }
            case CAN_PROTECT:
                int diceValue = dice.getFirst() + dice.getSecond() - 1;
                protectionResult = new MutablePair<>(currentPlayerCanProtect.getSpaceShip().canProtect(diceValue, card.getFires().get(hitIndex)));
                Component component = protectionResult.getFirst();
                CanProtect canProtectEvent = new CanProtect(currentPlayerCanProtect.getUsername(), new Pair<>(component != null ? component.getID() : null, protectionResult.getSecond()));
                eventCallback.trigger(canProtectEvent);

                internalState = PiratesInternalState.PENALTY;
                break;
            case GIVE_UP:
                super.execute(player);

                try {
                    ForcingGiveUp forcingGiveUpEvent = new ForcingGiveUp(getCurrentPlayer().getUsername(), "You are forced to give up, you have no human crew left");
                    eventCallback.trigger(forcingGiveUpEvent);
                } catch (IllegalStateException e) {
                    sendCurrentPlayer = true;
                }
                break;
        }

        if (sendCurrentPlayer) {
            try {
                CurrentPlayer currentPlayerEvent = new CurrentPlayer(this.getCurrentPlayer().getUsername());
                eventCallback.trigger(currentPlayerEvent);
            } catch (Exception e) {
                // Ignore the exception
            }
        }

        super.nextState(GameState.CARDS);
    }

    @Override
    public void exit() throws IllegalStateException{
        super.exit();
    }
}
