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


public class RewardState extends State {
    private final Map<PlayerData, Integer> scores;
    private final Level level;
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

    RewardState(Board board, EventCallback callback, StateTransitionHandler transitionHandler) {
        // Super constructor to initialize the board and players
        // Note: the super constructor will get only the players that have not given up
        super(board, callback, transitionHandler);
        // Add the player that has given up to the players list
        players.addAll(board.getGaveUpPlayers());
        for (PlayerData player : players) {
            this.playersStatus.put(player.getColor(), PlayerStatus.WAITING);
        }
        // Set the player status to waiting for the players that have given up
        for (PlayerData player : players) {
            if (player.hasGivenUp()) {
                playersStatus.put(player.getColor(), PlayerStatus.WAITING);
            }
        }
        this.scores = new HashMap<>();
        this.level = board.getBoardLevel();
        this.internalState = EndInternalState.FINISH_ORDER;
    }

    @Override
    public PlayerData getCurrentPlayer() throws SynchronousStateException {
        throw new SynchronousStateException("Cannot invoke getCurrentPlayer in a synchronous state RewardState");
    }

    @Override
    public void entry() {
         for (PlayerData player : players) {
             scores.put(player, player.getCoins());
         }
    }

    @Override
    public void execute(PlayerData player) {
        ArrayList<Pair<String, Integer>> eventScores = new ArrayList<>();
        Score scoreEvent;
        // We wait for all the player to confirm their end of turn (watching the partial score
        super.execute(player);

        // Check if all players have confirmed their end of turn
        if (players.indexOf(player) == players.size() - 1) {
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

    @Override
    public void exit() {
        super.exit();
        // TODO: This is the end of the game, we should do something here
    }
}
