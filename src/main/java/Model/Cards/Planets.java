package Model.Cards;

import Model.Good.Good;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Planets extends Card {
    private Good[][] planets;
    private int flightDays;
    private boolean[] taken;

    public Planets(int level, Good[][] planets, int flightDays) {
        super(level);
        this.planets = planets;
        this.flightDays = flightDays;
        taken = new boolean[planets.length];
    }

    public int getPlanetNumbers() {
        return planets.length;
    }

    //@ (nPlanet >= planets.size() || nPlanet < 0 || taken[nPlanet]) ? null :
    //@ (\forall Good x; ; (\forall int j; ; planets[nPlanet][j].contains(x) ==> \result.contains(x))
    public /*@ pure @*/ List<Good> getPlanets(int nPlanet) {
        if (nPlanet < 0 || nPlanet >= planets.length || taken[nPlanet]) {
            return null;
        }

        taken[nPlanet] = true;
        return new ArrayList<>(Arrays.asList(planets[nPlanet]));
    }

    public int getFlightDays() {
        return flightDays;
    }

    @Override
    public CardType getCardType() {
        return CardType.PLANETS;
    }

    @Override
    public void apply(PlayerData player) {

    }
}
