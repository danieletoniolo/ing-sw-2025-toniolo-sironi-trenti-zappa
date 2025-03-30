package Model.State;

import Model.Cards.AbandonedStation;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.State.interfaces.ExchangeableGoods;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import Model.SpaceShip.SpaceShip;
import Model.SpaceShip.Storage;
import org.javatuples.Pair;
import org.javatuples.Triplet;

public class AbandonedStationState extends State implements ExchangeableGoods {
    private final AbandonedStation card;
    private Map<UUID, Float> cannonStrength;
    private ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData;

    public AbandonedStationState(ArrayList<PlayerData> players, AbandonedStation card) {
        super(players);
        this.card = card;
        this.cannonStrength = new java.util.HashMap<>();
        this.exchangeData = new ArrayList<>();
    }

    public void addCannonStrength(UUID uuid, float strength) {
        float oldCannonStrength = cannonStrength.get(uuid);
        cannonStrength.replace(uuid, oldCannonStrength + strength);
    }

    public void exchangeGoods(PlayerData player, ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData) {
        this.exchangeData = exchangeData;
    }

    @Override
    public void entry() {
        for (Pair<PlayerData, PlayerStatus> player : players) {
            SpaceShip ship = player.getValue0().getSpaceShip();
            cannonStrength.put(player.getValue0().getUUID(), ship.getSingleCannonsStrength());
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

                    // Execute the exchange
                    for (Triplet<ArrayList<Good>, ArrayList<Good>, Integer> triplet : exchangeData) {
                        ArrayList<Good> goodsToGet = triplet.getValue0();
                        ArrayList<Good> goodsToLeave = triplet.getValue1();
                        int storageId = triplet.getValue2();

                        Storage storage = player.getSpaceShip().getStorage(storageId);
                        for (Good good : goodsToGet) {
                            storage.addGood(good);
                        }
                        for (Good good : goodsToLeave) {
                            storage.removeGood(good);
                        }
                    }

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
