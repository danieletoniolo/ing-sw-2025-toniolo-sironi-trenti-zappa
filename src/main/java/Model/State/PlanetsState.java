package Model.State;

import Model.Cards.Planets;
import Model.Game.Board.Board;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.State.interfaces.ExchangeableGoods;

import controller.EventCallback;
import event.game.ExchangeGoods;
import event.game.MoveMarker;
import org.javatuples.Triplet;

import java.util.ArrayList;

public class PlanetsState extends State implements ExchangeableGoods {
    private final Planets card;
    private PlayerData[] planetSelected;
    private ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData;
    /**
     * Constructor for PlanetsState
     * @param board The board associated with the game
     * @param card Planet card associated with the state
     */
    public PlanetsState(Board board, EventCallback callback, Planets card) {
        super(board, callback);
        this.card = card;
        planetSelected = new PlayerData[card.getPlanetNumbers()];
    }

    /**
     * Getter for the card
     * @return The card
     */
    public Planets getCard() {
        return card;
    }

    /**
     * Getter for the planet selected
     * @return The planet selected
     */
    public PlayerData[] getPlanetSelected() {
        return planetSelected;
    }

    /**
     * Implementation of {@link State#selectPlanet(PlayerData, int)} to select a planet to land on.
     * @throws IllegalArgumentException If the planet number is invalid.
     * @throws IllegalStateException If the planet is already selected by another player.
     */
    @Override
    public void selectPlanet(PlayerData player, int planetNumber) throws IllegalStateException{
        if (planetNumber < 0 || planetNumber >= card.getPlanetNumbers()) {
            throw new IllegalArgumentException("Invalid planet number: " + planetNumber);
        }
        if (planetSelected[planetNumber] == null) {
            planetSelected[planetNumber] = player;
        } else {
            throw new IllegalStateException("Planet already selected by" + planetSelected[planetNumber].getUsername());
        }
    }

    public void setGoodsToExchange(PlayerData player, ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData) {
        this.exchangeData = exchangeData;
    }

    @Override
    public void entry() {
        super.entry();
    }

    /**
     * Marks the player as played
     * @param player PlayerData of the player to play
     */
    @Override
    public void execute(PlayerData player) throws NullPointerException {
        if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYING) {
            playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);

            // Execute the exchange
            for (Triplet<ArrayList<Good>, ArrayList<Good>, Integer> triplet : exchangeData) {
                SpaceShip ship = player.getSpaceShip();
                ship.exchangeGood(triplet.getValue0(), triplet.getValue1(), triplet.getValue2());
            }

            ExchangeGoods exchangeGoodsEvent = new ExchangeGoods(player.getUsername(), exchangeData);
            eventCallback.trigger(exchangeGoodsEvent);

        } else if (playersStatus.get(player.getColor()) == PlayerStatus.WAITING) {
            playersStatus.replace(player.getColor(), PlayerStatus.SKIPPED);
        }
    }

    /**
     * Exits the state and removes the flight days from the players that have selected a planet
     * If a player has not selected a planet, the flight days are not removed
     * @throws IllegalStateException If not all players have played
     */
    @Override
    public void exit() throws IllegalStateException{
        int flightDays = card.getFlightDays();
        PlayerStatus status;
        for (PlayerData p : players) {
            status = playersStatus.get(p.getColor());
            if (status == PlayerStatus.PLAYED) {
                board.addSteps(p, -flightDays);

                MoveMarker stepsEvent = new MoveMarker(p.getUsername(), p.getStep());
                eventCallback.trigger(stepsEvent);
            } else if (status == PlayerStatus.WAITING || status == PlayerStatus.PLAYING) {
                throw new IllegalStateException("Not all players have played");
            }
        }
        super.exit();
    }
}
