package Model.State;

import Model.Cards.AbandonedStation;
import Model.Good.Good;
import Model.Player.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Model.SpaceShip.SpaceShip;
import Model.SpaceShip.Storage;
import org.javatuples.Pair;

public class AbandonedStationState extends State {
    AbandonedStation card;
    Map<PlayerData, Float> cannonStrength;

    public AbandonedStationState(ArrayList<PlayerData> players, AbandonedStation card) {
        super(players);
        this.card = card;
    }

    public void addCannonStrength(PlayerData player, float strength) {
        float oldCannonStrength = cannonStrength.get(player);
        cannonStrength.replace(player, oldCannonStrength + strength);
    }

    public void exchangeGoods(PlayerData player, ArrayList<Good> goodsToGet, ArrayList<Good> goodsToLeave, int row, int column) throws IllegalStateException {
        // Get the goods available in the station and check if the station has the goods that the player wants to get
        List<Good> goodsAvailable = card.getGoods();
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
        // TODO: This method is almost same to the super one but it is more efficient to have it here
        if (player == null) {
            throw new NullPointerException("player is null");
        }
        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue0().equals(player)) {
                if (p.getValue1() == PlayerStatus.PLAYING) {
                    p.setAt1(PlayerStatus.PLAYED);
                    super.played = true;
                } else {
                    p.setAt1(PlayerStatus.WAITING);
                }
            }
        }
    }

    @Override
    public void exit() throws IllegalStateException{
        for (Pair<PlayerData, PlayerStatus> player : players) {
            if (player.getValue1() == PlayerStatus.PLAYED) {
                int flightDays = card.getFlightDays();
                player.getValue0().addSteps(-flightDays);
                break;
            } else if (player.getValue1() == PlayerStatus.WAITING || player.getValue1() == PlayerStatus.PLAYING) {
                throw new IllegalStateException("Not all players have played");
            }
        }
    }
}
