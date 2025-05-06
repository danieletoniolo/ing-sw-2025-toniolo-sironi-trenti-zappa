package view.structures.cards;

import java.util.ArrayList;

public class PlanetsView extends CardView{
    private int numberOfPlanets;
    private int flightDays;
    private ArrayList<String> planets;

    @Override
    public void drawCardGui() {

    }

    public int getNumberOfPlanets() {
        return numberOfPlanets;
    }

    public void setNumberOfPlanets(int numberOfPlanets) {
        this.numberOfPlanets = numberOfPlanets;
    }

    public int getFlightDays() {
        return flightDays;
    }

    public void setFlightDays(int flightDays) {
        this.flightDays = flightDays;
    }

    public ArrayList<String> getPlanets() {
        return planets;
    }

    public void setPlanets(ArrayList<String> planets) {
        this.planets = planets;
    }
}
