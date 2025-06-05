package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.good.Good;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Planets extends Card {
    @JsonProperty("planets")
    private List<List<Good>> planets;
    private int flightDays;

    /**
     * Constructor
     * @param ID ID of the card
     * @param level level of the card
     * @param planets matrix for the planets: planets[row] = planet, planets[row][col] = good
     * @param flightDays number of flight days lost
     * @throws NullPointerException if planets == null
     */
    public Planets(int level, int ID, List<List<Good>> planets, int flightDays) throws NullPointerException {
        super(level, ID);
        this.planets = planets;
        if (planets == null || planets.isEmpty()) {
            throw new NullPointerException("planets is null");
        }
        this.flightDays = flightDays;
    }

    public Planets(){
        super();
    }

    /**
     * Get the number of planets
     * @return number of planets
     */
    public int getPlanetNumbers() {
        return planets.size();
    }

    /**
     * Get a list of goods
     * @param nPlanet number of the planet to visit: 0 = first planet
     * @return ArrayList of goods that are on the "nPlanet" planet
     * @throws IndexOutOfBoundsException if nPlanet aut of bounds
     */
    public /*@ pure @*/ ArrayList<Good> getPlanet(int nPlanet) throws IndexOutOfBoundsException {
        if (nPlanet < 0 || nPlanet >= planets.size()) {
            throw new IndexOutOfBoundsException("nPlanet: " + nPlanet + " is out of bounds");
        }

        return new ArrayList<>(planets.get(nPlanet));
    }

    /**
     * Get the list of planets
     * @return list of planets
     */
    public List<List<Good>> getPlanets() {
        return planets;
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

}
