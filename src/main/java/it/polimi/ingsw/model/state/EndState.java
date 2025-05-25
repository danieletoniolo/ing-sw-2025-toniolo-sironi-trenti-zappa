package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.BestLookingShips;
import it.polimi.ingsw.event.game.serverToClient.Score;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class EndState extends State {
    private final Map<PlayerData, Integer> scores;
    private final Level level;
    private EndInternalState endInternalState;

    /**
     * Enum to represent the internal state of the end state.
     */
    private enum EndInternalState {
        FINISH_ORDER,
        BEST_LOOKING_SHIP,
        SALE_OF_GOODS,
        LOSSES
    }

    EndState (Board board, EventCallback callback, Level level, StateTransitionHandler transitionHandler) {
        // Super constructor to initialize the board and players
        // Note: the super constructor will get only the players that have not given up
        super(board, callback, transitionHandler);
        // Add the player that has given up to the players list
        players.addAll(board.getGaveUpPlayers());
        // Set the player status to waiting for the players that have given up
        for (PlayerData player : players) {
            if (player.hasGivenUp()) {
                playersStatus.put(player.getColor(), PlayerStatus.WAITING);
            }
        }
        this.scores = new HashMap<>();
        this.level = level;
    }

    /**
     * Getter for the players scores
     * @return The players scores
     */
    public Map<PlayerData, Integer> getScores() {
        return scores;
    }

    /**
     * Getter for the level
     * @return The level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Getter for the end internal state
     * @return The end internal state
     */
    public EndInternalState getEndInternalState() {
        return endInternalState;
    }

    /**
     * Setter for the end internal state
     * @param endInternalState
     */
    public void setEndInternalState(EndInternalState endInternalState) {
        this.endInternalState = endInternalState;
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
        playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);

        // Check if all players have confirmed their end of turn
        if (players.indexOf(player) == players.size() - 1) {
            // Execute the end of turn actions
            switch (endInternalState) {
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

                    scoreEvent = new Score(eventScores);
                    eventCallback.trigger(scoreEvent);

                    // Go to the next scoring state
                    endInternalState = EndInternalState.BEST_LOOKING_SHIP;
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
                    BestLookingShips bestLookingShipsEvent = new BestLookingShips((ArrayList<String>) playersWithLeastConnectors.stream().map(PlayerData::getUsername).toList());
                    eventCallback.trigger(bestLookingShipsEvent);

                    // Calculate the new score based on the best looking ship
                    for (PlayerData p : playersWithLeastConnectors) {
                        int value = scores.get(p);
                        scores.replace(p, value + 2 * level.getValue());
                        eventScores.add(new Pair<>(p.getUsername(), scores.get(p)));
                    }

                    scoreEvent = new Score(eventScores);
                    eventCallback.trigger(scoreEvent);

                    // Go to the next scoring state
                    endInternalState = EndInternalState.SALE_OF_GOODS;
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

                    scoreEvent = new Score(eventScores);
                    eventCallback.trigger(scoreEvent);

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

                    scoreEvent = new Score(eventScores);
                    eventCallback.trigger(scoreEvent);

                    break;
                default:
                    throw new IllegalStateException("Unknown EndInternalState: " + endInternalState);
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
