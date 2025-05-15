package Model.State;

import Model.Cards.AbandonedStation;
import Model.Game.Board.Board;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.State.interfaces.ExchangeableGoods;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import Model.SpaceShip.SpaceShip;
import controller.event.game.MoveMarker;
import org.javatuples.Triplet;

public class AbandonedStationState extends State implements ExchangeableGoods {
    private final AbandonedStation card;
    private Map<UUID, Float> cannonStrength;
    private ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData;

    public AbandonedStationState(Board board, AbandonedStation card) {
        super(board);
        this.card = card;
        this.cannonStrength = new java.util.HashMap<>();
        this.exchangeData = new ArrayList<>();
    }

    /**
     * Getter for the card
     * @return The card
     */
    public AbandonedStation getCard() {
        return card;
    }

    /**
     * Getter for the cannon strength
     * @return The cannon strength
     */
    public Map<UUID, Float> getCannonStrength() {
        return cannonStrength;
    }

    /**
     * Getter for the exchange data
     * @return The exchange data
     */
    public ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> getExchangeData() {
        return exchangeData;
    }

    public void addCannonStrength(UUID uuid, float strength) {
        float oldCannonStrength = cannonStrength.get(uuid);
        cannonStrength.replace(uuid, oldCannonStrength + strength);
    }

    public void setGoodsToExchange(PlayerData player, ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData) {
        this.exchangeData = exchangeData;
    }

    @Override
    public void entry() {
        for (PlayerData player : players) {
            SpaceShip ship = player.getSpaceShip();
            cannonStrength.put(player.getUUID(), ship.getSingleCannonsStrength());
        }
    }

    @Override
    public void execute(PlayerData player) throws NullPointerException {
        // TODO: This method is almost same to the super one but it is more efficient to have it here
        if (player == null) {
            throw new NullPointerException("player is null");
        }

        if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYING) {
            playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);

            // Execute the exchange
            for (Triplet<ArrayList<Good>, ArrayList<Good>, Integer> triplet : exchangeData) {
                SpaceShip ship = player.getSpaceShip();
                ship.exchangeGood(triplet.getValue0(), triplet.getValue1(), triplet.getValue2());
            }

            super.played = true;

            // TODO: EVENT EXCHANGEGOODS

        } else {
            playersStatus.replace(player.getColor(), PlayerStatus.SKIPPED);
        }
    }

    @Override
    public void exit() throws IllegalStateException{
        for (PlayerData player : players) {
            PlayerStatus status = playersStatus.get(player.getColor());
            if (status == PlayerStatus.PLAYED) {
                int flightDays = card.getFlightDays();
                board.addSteps(player, -flightDays);

                // TODO: EVENT ADD STEPS
                MoveMarker stepEvent = new MoveMarker(player.getUsername(), player.getStep());

                break;
            } else if (status == PlayerStatus.WAITING || status == PlayerStatus.PLAYING) {
                throw new IllegalStateException("Not all players have played");
            }
        }
        super.played = true;
    }
}
