package view.structures.cards;

import view.structures.good.GoodView;

import java.util.ArrayList;
import java.util.List;

public class PlanetsView extends CardView{
    private int numberOfPlanets;
    private int flightDays;
    private List<List<GoodView>> planets;

    public PlanetsView(int ID, boolean covered, int level, int flightDays, List<List<GoodView>> planets) {
        super(ID, covered, level);
        this.numberOfPlanets = planets.size();
        this.flightDays = flightDays;
        this.planets = planets;
    }

    @Override
    public void drawGui() {

    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        StringBuilder line = new StringBuilder(switch(l) {
            case 0 -> Up;
            case 1 -> "│      PLANETS      │";
            case 2,7 -> Clear;
            case 3 -> numberOfPlanets >= 1 ? "│  P1: " + printPlanet(getPlanet(0)) : Clear;
            case 4 -> numberOfPlanets >= 2 ? "│  P2: " + printPlanet(getPlanet(1)) : Clear;
            case 5 -> numberOfPlanets >= 3 ? "│  P3: " + printPlanet(getPlanet(2)) : Clear;
            case 6 -> numberOfPlanets >= 4 ? "│  P4: " + printPlanet(getPlanet(3)) : Clear;
            case 8 -> numberOfPlanets >= 5 ? "│  P5: " + printPlanet(getPlanet(4)) : Clear;
            case 9 -> Down;
            default -> null;
        });

        while (line.toString().replaceAll("\033\\[[0-9;]*m", "").length() < getColsToDraw() - 1) {
            line.append(" ");
        }
        if (line.toString().replaceAll("\033\\[[0-9;]*m", "").length() == getColsToDraw() - 1) {
            line.append("│");
        }
        return line.toString();
    }

    public int getFlightDays() {
        return flightDays;
    }

    public void setFlightDays(int flightDays) {
        this.flightDays = flightDays;
    }

    public List<GoodView> getPlanet(int n) {
        return new ArrayList<>(planets.get(n));
    }

    public void setPlanet(List<GoodView> planet) {
        this.planets.add(planet);
    }

    private String printPlanet(List<GoodView> planet) {
        StringBuilder line = new StringBuilder();
        for (GoodView good : planet) {
            line.append(good.drawTui()).append(" ");
        }
        return line.toString();
    }
}
