package Model.Cards;

import Model.Good.Good;
import Model.Player.PlayerData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Planets extends Card {
    private Good[][] planets;
    private int flightDays;

    /**
     *
     * @param level level of the card
     * @param planets matrix for the planets: planets[row] = planet, planets[row][col] = good
     * @param flightDays number of flight days lost
     * @throws NullPointerException if planets == null
     */
    public Planets(int level, Good[][] planets, int flightDays) throws NullPointerException {
        super(level);
        if (planets == null || planets.length == 0 || planets[0].length == 0 ) {
            throw new NullPointerException("planets is null");
        }
        this.planets = planets;
        this.flightDays = flightDays;
    }

    /**
     * Get the number of planets
     * @return number of planets
     */
    public int getPlanetNumbers() {
        return planets.length;
    }

    /**
     * Get a list of goods
     * @param nPlanet number of the planet to visit: 0 = first planet
     * @return ArrayList of goods that are on the "nPlanet" planet
     * @throws IndexOutOfBoundsException if nPlanet aut of bounds
     */
    public /*@ pure @*/ List<Good> getPlanet(int nPlanet) throws IndexOutOfBoundsException {
        if (nPlanet < 0 || nPlanet >= planets.length) {
            throw new IndexOutOfBoundsException("nPlanet: " + nPlanet + " is out of bounds");
        }

        return new ArrayList<>(Arrays.asList(planets[nPlanet]));
    }

    /**
     * Get the number of flight days lost
     * @return number of flight days lost
     */
    public int getFlightDays() {
        return flightDays;
    }

    /**
     * Get the card type
     * @return card type
     */
    @Override
    public CardType getCardType() {
        return CardType.PLANETS;
    }

    @Override
    public void entry(ArrayList<PlayerData> players) {
        //TODO
    }

    @Override
    public void execute(PlayerData player) {
        //TODO

    }

    @Override
    public void exit() {
        //TODO
    }
}
