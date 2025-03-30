package Model.State;

import Model.Cards.Smugglers;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.State.interfaces.ExchangeableGoods;

import Model.State.interfaces.UsableCannon;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.Map;

public class SmugglersState extends State implements UsableCannon, ExchangeableGoods {
    private final Smugglers card;
    private Map<PlayerData, Float> cannonStrength;
    private ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData;

    public SmugglersState(ArrayList<PlayerData> players, Smugglers card) {
        super(players);
        this.card = card;
    }
    
    public void useCannon(PlayerData player, float strength) {
        float oldCannonStrength = cannonStrength.get(player);
        cannonStrength.replace(player, oldCannonStrength + strength);
    }

    public void exchangeGoods(PlayerData player, ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData) throws IllegalStateException {
        this.exchangeData = exchangeData;
    }

    @Override
    public void entry() {
        for (Pair<PlayerData, PlayerStatus> player : players) {
            SpaceShip ship = player.getValue0().getSpaceShip();
            cannonStrength.put(player.getValue0(), ship.getSingleCannonsStrength());
        }
    }

    @Override
    public void execute(PlayerData player) throws NullPointerException {
        if (player == null) {
            throw new NullPointerException("player is null");
        }
        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue0().equals(player)) {
                if (p.getValue1() == PlayerStatus.PLAYING) {
                    p.setAt1(PlayerStatus.PLAYED);

                    // Check if the player has enough cannon strength to beat the card
                    if (cannonStrength.get(player) > card.getCannonStrengthRequired()) {
                        super.played = true;
                    } else {
                        // If the player doesn't have enough cannon strength we can't exchange goods
                        this.exchangeData = null;
                        // TODO: handle how to deal with the penalty
                    }
                } else {
                    p.setAt1(PlayerStatus.WAITING);
                }
            }
        }
    }

    @Override
    public void exit() throws IllegalStateException {
        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue1() == PlayerStatus.PLAYED) {
                int flightDays = card.getFlightDays();
                p.getValue0().addSteps(-flightDays);
            } else if (p.getValue1() == PlayerStatus.WAITING || p.getValue1() == PlayerStatus.PLAYING) {
                throw new IllegalStateException("Not all players have played");
            }
        }
    }
}
