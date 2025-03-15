package Model.State;

import Model.Cards.Planets;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.SpaceShip.Storage;
import org.javatuples.Pair;

import java.util.ArrayList;

public class PlanetsState extends State {
    private final Planets card;

    private PlayerData[] planetSelected;

    /**
     * Constructor for PlanetsState
     * @param players List of players in the current order to play
     * @param card Planet card associated with the state
     */
    public PlanetsState(ArrayList<PlayerData> players, Planets card) {
        super(players);
        this.card = card;
        planetSelected = new PlayerData[card.getPlanetNumbers()];
    }

    /**
     * Selects a planet for a player if it is not already selected
     * @param player Player that wants to select a planet
     * @param planetNumber Number (index) of the planet to select
     * @throws IllegalStateException If the planet is already selected
     */
    public void selectPlanet(PlayerData player, int planetNumber) throws IllegalStateException{
        if (planetSelected[planetNumber] == null) {
            planetSelected[planetNumber] = player;
        } else {
            throw new IllegalStateException("Planet already selected by" + planetSelected[planetNumber].getUsername());
        }
    }

    /**
     * Exchange goods between the player and the planet
     * @param player Player that wants to exchange goods
     * @param goodsToGet Goods that the player wants to get from the planet
     * @param goodsToLeave Goods that the player wants to leave in the planet
     * @param row Row of the storage component in the player's spaceship
     * @param column Column of the storage component in the player's spaceship
     * @throws IllegalStateException If the player does not have enough space in the storage component or the goods to leave are not in the storage component
     */
    public void exchangeGoods(PlayerData player, ArrayList<Good> goodsToGet, ArrayList<Good> goodsToLeave, int row, int column) throws IllegalStateException {
        SpaceShip ship = player.getSpaceShip();
        Storage storage = (Storage) ship.getComponent(row, column);
        storage.exchangeGood(goodsToGet, goodsToLeave);
    }

    @Override
    public void entry() {
        super.entry();
        //TODO: Implement the entry method
    }

    /**
     * Marks the player as played
     * @param player PlayerData of the player to play
     */
    @Override
    public void execute(PlayerData player) {
        super.execute(player);
        //TODO: Implement the execute method
    }

    /**
     * Exits the state and removes the flight days from the players that have selected a planet
     * If a player has not selected a planet, the flight days are not removed
     */
    @Override
    public void exit() {
        super.exit();
        int flightDays = card.getFlightDays();
        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue1() == PlayerStatus.PLAYED) {
                p.getValue0().addSteps(-flightDays);
            }
        }
    }
}
