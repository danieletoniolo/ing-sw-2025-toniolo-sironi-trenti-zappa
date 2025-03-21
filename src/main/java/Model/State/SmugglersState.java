package Model.State;

import Model.Cards.Smugglers;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.SpaceShip.Storage;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SmugglersState extends State {
    Smugglers card;
    Map<PlayerData, Float> cannonStrength;

    public SmugglersState(ArrayList<PlayerData> players, Smugglers card) {
        super(players);
        this.card = card;
    }

    public void addCannonStrength(PlayerData player, float strength) {
        float oldCannonStrength = cannonStrength.get(player);
        cannonStrength.replace(player, oldCannonStrength + strength);
    }

    public void exchangeGoods(PlayerData player, ArrayList<Good> goodsToGet, ArrayList<Good> goodsToLeave, int row, int column) throws IllegalStateException {
        if (cannonStrength.get(player) <= card.getCannonStrengthRequired()) {
            throw new IllegalStateException("Not enough cannon strength to beat the smugglers");
        }
        // Get the goods available in the smuggler ship and check if the smuggler has the goods that the player wants to get
        List<Good> goodsAvailable = card.getGoodsReward();
        for (Good good : goodsToGet) {
            if (!goodsAvailable.contains(good)) {
                //TODO: consider throwing a custom exception
                throw new IllegalStateException("The station does not have the good");
            }
        }

        // Get the storage component of the player's spaceship and exchange the goods
        SpaceShip ship = player.getSpaceShip();
        Storage storage = (Storage) ship.getComponent(row, column);
        storage.exchangeGood(goodsToGet, goodsToLeave);
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
                    if (cannonStrength.get(player) > card.getCannonStrengthRequired()) {
                        super.played = true;
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
