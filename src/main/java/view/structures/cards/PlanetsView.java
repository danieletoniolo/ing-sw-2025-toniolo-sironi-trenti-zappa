package view.structures.cards;

import view.structures.good.GoodView;

import java.util.ArrayList;
import java.util.List;

public class PlanetsView extends CardView{
    private int numberOfPlanets;
    private int flightDays;
    private List<List<GoodView>> planets;

    public PlanetsView(int ID, boolean covered, int flightDays, List<List<GoodView>> planets) {
        super(ID, covered);
        this.numberOfPlanets = planets.size();
        this.flightDays = flightDays;
        this.planets = planets;
    }

    @Override
    public void drawCardGui() {

    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        return switch(l) {
            case 0 -> Up;
            case 1 -> "│       PLANETS       │";
            case 2,7 -> Clear;
            case 3 -> {
                if (numberOfPlanets < 1) {
                    yield Clear;
                } else {
                    String line = "│  P1: " + printPlanet(getPlanet(0));
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "│";
                    yield line;
                }
            }
            case 4 -> {
                if (numberOfPlanets < 2) {
                    yield Clear;
                } else {
                    String line = "│  P2: " + printPlanet(getPlanet(1));
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "│";
                    yield line;
                }
            }
            case 5 -> {
                if (numberOfPlanets < 3) {
                    yield Clear;
                } else {
                    String line = "│  P3: " + printPlanet(getPlanet(2));
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "│";
                    yield line;
                }
            }
            case 6 -> {
                if (numberOfPlanets < 4) {
                    yield Clear;
                } else {
                    String line = "│  P4: " + printPlanet(getPlanet(3));
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "│";
                    yield line;
                }
            }
            case 8 -> {
                String line = "│  FlightDays: " + getFlightDays();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 9 -> Down;
            default -> null;
        };
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
