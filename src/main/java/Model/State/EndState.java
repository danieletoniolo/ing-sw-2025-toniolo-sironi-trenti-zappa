package Model.State;

import Model.Game.Board.Level;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import org.javatuples.Pair;

import java.util.ArrayList;

enum EndInternalState {
    FINISH_ORDER,
    BEST_LOOKING_SHIP,
    SALE_OF_GOODS,
    LOSSES
}

public class EndState extends State {
    private ArrayList<Pair<PlayerData, Integer>> scores;
    private final Level level;
    private EndInternalState endInternalState;

    EndState (ArrayList<PlayerData> players, Level level) {
        super(players);
        this.scores = new ArrayList<>();
        this.level = level;
    }

    @Override
    public void entry() {
         for (Pair<PlayerData, PlayerStatus> player : players) {
             scores.add(new Pair<>(player.getValue0(), player.getValue0().getCoins()));
         }
    }

    @Override
    public void execute(PlayerData playerData) {
        for (Pair<PlayerData, PlayerStatus> player : players) {
            if (player.getValue0().equals(playerData)) {
                // We wait for all the player to confirm their end of turn (watching the partial score
                player.setAt1(PlayerStatus.PLAYED);

                // Check if all players have confirmed their end of turn
                if (player.indexOf(player) == players.size() - 1) {
                    // Execute the end of turn actions
                    switch (endInternalState) {
                        case FINISH_ORDER:
                            // Calculate the new score based on the finish order and the level
                            int reward = 4 * level.getValue();
                            for (int i = 0; i < players.size(); i++) {
                                if (!scores.get(i).getValue0().hasGivenUp()) {
                                    scores.get(i).setAt1(scores.get(i).getValue1() + reward);
                                    reward -= level.getValue();
                                }
                            }

                            // Go to the next scoring state
                            endInternalState = EndInternalState.BEST_LOOKING_SHIP;
                        case BEST_LOOKING_SHIP:
                            // Find the player (could be more than one) with the least exposed connectors (best looking ship)
                            int minExposedConnectors = Integer.MAX_VALUE;
                            ArrayList<PlayerData> playersWithLeastConnectors = new ArrayList<>();
                            for (Pair<PlayerData, PlayerStatus> p : players) {
                                if (!p.getValue0().hasGivenUp()) {
                                    int connectors = p.getValue0().getSpaceShip().getExposedConnectors();
                                    if (connectors < minExposedConnectors) {
                                        minExposedConnectors = connectors;
                                        playersWithLeastConnectors.clear();
                                        playersWithLeastConnectors.add(p.getValue0());
                                    } else if (connectors == minExposedConnectors) {
                                        playersWithLeastConnectors.add(p.getValue0());
                                    }
                                }
                            }

                            // Calculate the new score based on the best looking ship
                            for (PlayerData p : playersWithLeastConnectors) {
                                int index = scores.indexOf(p);
                                scores.get(index).setAt1(scores.get(index).getValue1() + 2 * level.getValue());
                            }

                            // Go to the next scoring state
                            endInternalState = EndInternalState.SALE_OF_GOODS;
                        case SALE_OF_GOODS:
                            // Calculate the new score based on the sale of goods
                            for (Pair<PlayerData, PlayerStatus> p : players) {
                                int index = scores.indexOf(p);
                                int sales = 0;
                                if (!p.getValue0().hasGivenUp()) {
                                    sales = p.getValue0().getSpaceShip().getGoodsValue();
                                } else {
                                    sales = Math.round((float) p.getValue0().getSpaceShip().getGoodsValue() / 2);
                                }
                                scores.get(index).setAt1(scores.get(index).getValue1() + sales);
                            }
                        case LOSSES:
                            // Calculate the new score based on the component losses
                            for (Pair<PlayerData, PlayerStatus> p : players) {
                                SpaceShip ship = p.getValue0().getSpaceShip();
                                int penalty = ship.getLostComponents().size() + ship.getReservedComponents().size();
                                int index = scores.indexOf(p);
                                scores.get(index).setAt1(scores.get(index).getValue1() - penalty);
                            }
                        default:
                            throw new IllegalStateException("Unknown EndInternalState: " + endInternalState);
                    }
                }
            }
        }
    }

    @Override
    public void exit() {
        super.exit();
        // TODO: This is the end of the game, we should do something here
    }
}
