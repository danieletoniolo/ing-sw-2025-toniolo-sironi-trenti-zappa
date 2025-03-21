package Model.State;

import Model.Cards.Planets;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.SpaceShip.Storage;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

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
     * Check if the exchange command is valid and store the command to be applied in the execute method.
     * @param player Player that wants to exchange goods
     * @param goodsToGet Goods that the player wants to get from the planet
     * @param goodsToLeave Goods that the player wants to leave in the planet
     * @param storageID ID of the cabin where the exchange is going to happen
     * @throws IllegalStateException If the player does not have enough space in the storage component or the goods to leave are not in the storage component
     * @apiNote This method should be called after the player has selected a planet
     */
    public void exchangeGoods(PlayerData player, ArrayList<Good> goodsToGet, ArrayList<Good> goodsToLeave, int storageID) throws IllegalStateException {
        // Find the planet number that the player has selected
        int planetNumber = 0;
        while (planetNumber < planetSelected.length && planetSelected[planetNumber] != player) {
            planetNumber++;
        }
        if (planetNumber == planetSelected.length) {
            //TODO: consider throwing a custom exception
            throw new IllegalStateException("Player has not selected a planet");
        }

        // Get the goods available in the planet and check if the planet has the goods that the player wants to get
        List<Good> goodsAvailable = card.getPlanet(planetNumber);
        for (Good good : goodsToGet) {
            if (goodsAvailable.contains(good)) {
                goodsAvailable.remove(good);
            } else {
                throw new IllegalStateException("Planet " + planetNumber + " has not the goods that the player wants to get");
            }
        }

        SpaceShip ship = player.getSpaceShip();
        Storage storage = ship.getStorage(storageID);
        for (Good good : goodsToLeave) {
            if (storage.getGoods().contains(good)) {
                storage.removeGood(good);
            } else {
                throw new IllegalStateException("Storage does not contain the goods that the player wants to leave");
            }
        }

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
    public void execute(PlayerData player) throws NullPointerException {
        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue0().equals(player)) {
                if (p.getValue1() == PlayerStatus.PLAYING) {
                    p.setAt1(PlayerStatus.PLAYED);
                } else if (p.getValue1() == PlayerStatus.WAITING) {
                    p.setAt1(PlayerStatus.SKIPPED);
                }
            }
        }
    }

    /**
     * Exits the state and removes the flight days from the players that have selected a planet
     * If a player has not selected a planet, the flight days are not removed
     * @throws IllegalStateException If not all players have played
     */
    @Override
    public void exit() throws IllegalStateException{
        super.exit();
        int flightDays = card.getFlightDays();
        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue1() == PlayerStatus.PLAYED) {
                p.getValue0().addSteps(-flightDays);
            } else if (p.getValue1() == PlayerStatus.WAITING || p.getValue1() == PlayerStatus.PLAYING) {
                throw new IllegalStateException("Not all players have played");
            }
        }
    }
}
