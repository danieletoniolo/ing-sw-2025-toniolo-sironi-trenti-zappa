package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.spaceship.BestLookingShips;
import it.polimi.ingsw.event.game.serverToClient.player.Score;
import it.polimi.ingsw.model.state.exception.SynchronousStateException;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * State class representing the reward calculation phase at the end of the game.
 * This state handles the sequential scoring phases: finish order, best looking ship,
 * sale of goods, and component losses.
 * @author Vittorio Sironi
 */
public class RewardState extends State {
    /** Map storing the current scores for each player during the reward calculation */
    private final Map<PlayerData, Integer> scores;
    /** The difficulty level of the current game board */
    private final Level level;
    /** Current internal state tracking which scoring phase is being processed */
    private EndInternalState internalState;

    /**
     * Enum to represent the internal state of the end state.
     */
    enum EndInternalState {
        FINISH_ORDER(0),
        BEST_LOOKING_SHIP(1),
        SALE_OF_GOODS(2),
        LOSSES(3),
        LEAVE_GAME(4);

        private final int value;
        EndInternalState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * Constructs a new RewardState for handling end-game scoring calculations.
     * Initializes the state with all players (including those who gave up) and sets up
     * the initial scoring phase for finish order rewards.
     *
     * @param board the game board containing player data and configuration
     * @param callback the event callback for triggering score update events
     * @param transitionHandler the handler for managing state transitions
     */
    RewardState(Board board, EventCallback callback, StateTransitionHandler transitionHandler) {
        // Super constructor to initialize the board and players
        // Note: the super constructor will get only the players that have not given up
        super(board, callback, transitionHandler);
        // Add the player that has given up to the players list
        players.addAll(board.getGaveUpPlayers());
        // Set the player status to waiting for the players that have given up
        for (PlayerData player : board.getGaveUpPlayers()) {
            playersStatus.put(player.getColor(), PlayerStatus.WAITING);
        }
        this.scores = new HashMap<>();
        this.level = board.getBoardLevel();
        this.internalState = EndInternalState.FINISH_ORDER;
    }

    /**
     * Returns the current player in this state.
     * Since RewardState is a synchronous state where all players participate simultaneously,
     * there is no concept of a "current player".
     *
     * @return never returns normally
     * @throws SynchronousStateException always thrown to indicate this operation is not supported
     */
    @Override
    public PlayerData getCurrentPlayer() throws SynchronousStateException {
        throw new SynchronousStateException("Cannot invoke getCurrentPlayer in a synchronous state RewardState");
    }

    /**
     * Entry point method called when transitioning into the RewardState.
     * Initializes the scoring system by setting each player's score to their current coin count
     * and triggers the initial Score event to notify clients of the starting scores for the
     * finish order scoring phase.
     */
    @Override
    public void entry() {
        ArrayList<Pair<String, Integer>> eventScores = new ArrayList<>();
         for (PlayerData player : players) {
             scores.put(player, player.getCoins());
             eventScores.add(new Pair<>(player.getUsername(),scores.get(player)));
         }
        Score scoreEvent = new Score(eventScores, internalState.getValue());
        eventCallback.trigger(scoreEvent);
    }

    /**
     * Executes the reward calculation process for the current scoring phase.
     * This method handles the sequential progression through different scoring phases:
     * finish order rewards, best looking ship bonus, sale of goods calculation,
     * and component loss penalties. The method waits for all players to confirm
     * before proceeding to the next phase and triggers appropriate score events.
     *
     * @param player the player data for the player confirming their current phase
     */
    @Override
    public void execute(PlayerData player) {
        boolean allPlayersConfirmed = true;
        ArrayList<Pair<String, Integer>> eventScores = new ArrayList<>();
        Score scoreEvent;
        // We wait for all the player to confirm their end of turn (watching the partial score)
        super.execute(player);

        try {
            allPlayersPlayed();
        } catch (IllegalStateException e) {
            allPlayersConfirmed = false;
        }

        // Check if all players have confirmed their end of turn
        if (allPlayersConfirmed) {
            // Execute the end of turn actions
            switch (internalState) {
                case FINISH_ORDER:
                    // Calculate the new score based on the finish order and the level
                    int reward = 4 * level.getValue();
                    for (Map.Entry<PlayerData, Integer> entry : scores.entrySet()) {
                        if (!entry.getKey().hasGivenUp()) {
                            scores.replace(entry.getKey(), entry.getValue() + reward);
                            reward -= level.getValue();
                            eventScores.add(new Pair<>(entry.getKey().getUsername(),scores.get(entry.getKey())));
                        }
                    }

                    // Go to the next scoring state
                    internalState = EndInternalState.BEST_LOOKING_SHIP;

                    scoreEvent = new Score(eventScores, internalState.getValue());
                    eventCallback.trigger(scoreEvent);

                    for (PlayerData p : players) {
                        // Reset the player status to waiting for the next state
                        playersStatus.replace(p.getColor(), PlayerStatus.WAITING);
                    }
                    break;
                case BEST_LOOKING_SHIP:
                    // Find the player (could be more than one) with the least exposed connectors (best looking ship)
                    int minExposedConnectors = Integer.MAX_VALUE;
                    ArrayList<PlayerData> playersWithLeastConnectors = new ArrayList<>();
                    for (PlayerData p : players) {
                        if (!p.hasGivenUp()) {
                            int connectors = p.getSpaceShip().getExposedConnectors();
                            if (connectors < minExposedConnectors) {
                                minExposedConnectors = connectors;
                                playersWithLeastConnectors.clear();
                                playersWithLeastConnectors.add(p);
                            } else if (connectors == minExposedConnectors) {
                                playersWithLeastConnectors.add(p);
                            }
                        }
                    }
                    BestLookingShips bestLookingShipsEvent = new BestLookingShips(playersWithLeastConnectors.stream().map(PlayerData::getUsername).toList());
                    eventCallback.trigger(bestLookingShipsEvent);

                    // Calculate the new score based on the best looking ship
                    for (PlayerData p : playersWithLeastConnectors) {
                        int value = scores.get(p);
                        scores.replace(p, value + 2 * level.getValue());
                        eventScores.add(new Pair<>(p.getUsername(), scores.get(p)));
                    }

                    // Go to the next scoring state
                    internalState = EndInternalState.SALE_OF_GOODS;

                    scoreEvent = new Score(eventScores, internalState.getValue());
                    eventCallback.trigger(scoreEvent);

                    for (PlayerData p : players) {
                        // Reset the player status to waiting for the next state
                        playersStatus.replace(p.getColor(), PlayerStatus.WAITING);
                    }
                    break;
                case SALE_OF_GOODS:
                    // Calculate the new score based on the sale of goods
                    for (Map.Entry<PlayerData, Integer> entry : scores.entrySet()) {
                        PlayerData p = entry.getKey();
                        int sales;
                        if (!p.hasGivenUp()) {
                            sales = p.getSpaceShip().getGoodsValue();
                        } else {
                            sales = Math.round((float) p.getSpaceShip().getGoodsValue() / 2);
                        }
                        scores.replace(p, entry.getValue() + sales);
                        eventScores.add(new Pair<>(p.getUsername(), scores.get(p)));
                    }

                    internalState = EndInternalState.LOSSES;

                    scoreEvent = new Score(eventScores, internalState.getValue());
                    eventCallback.trigger(scoreEvent);

                    for (PlayerData p : players) {
                        // Reset the player status to waiting for the next state
                        playersStatus.replace(p.getColor(), PlayerStatus.WAITING);
                    }
                    break;
                case LOSSES:
                    // Calculate the new score based on the component losses
                    for (Map.Entry<PlayerData, Integer> entry : scores.entrySet()) {
                        PlayerData p = entry.getKey();
                        SpaceShip ship = p.getSpaceShip();
                        int penalty = ship.getLostComponents().size() + ship.getReservedComponents().size();
                        scores.replace(p, entry.getValue() - penalty);
                        eventScores.add(new Pair<>(p.getUsername(), scores.get(p)));
                    }

                    internalState = EndInternalState.LEAVE_GAME;

                    scoreEvent = new Score(eventScores, internalState.getValue());
                    eventCallback.trigger(scoreEvent);

                    for (PlayerData p : players) {
                        // Reset the player status to waiting for the next state
                        playersStatus.replace(p.getColor(), PlayerStatus.WAITING);
                    }
                    break;
                case LEAVE_GAME:
                    break;
                default:
                    throw new IllegalStateException("Unknown EndInternalState: " + internalState);
            }
        }
        super.nextState(GameState.FINISHED);
    }

    /**
     * Exit method called when transitioning out of the RewardState.
     * Performs cleanup operations by calling the parent class exit method.
     * This method is invoked after all scoring phases have been completed
     * and the game is transitioning to the FINISHED state.
     */
    @Override
    public void exit() {
        super.exit();
    }
}
