package view.structures.cards;

import Model.Good.Good;

import java.util.ArrayList;
import java.util.List;

public class PlanetsView extends CardView{
    private int numberOfPlanets;
    private int flightDays;
    private List<List<Good>> planets;

    public PlanetsView(int ID, boolean covered, int numberOfPlanets, int flightDays, List<List<Good>> planets) {
        super(ID, covered);
        this.numberOfPlanets = numberOfPlanets;
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
                if (getNumberOfPlanets() < 1) {
                    yield Clear;
                } else {
                    String line = "│  P1: " + printGoods(getPlanet(0));
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "│";
                    yield line;
                }
            }
            case 4 -> {
                if (getNumberOfPlanets() < 2) {
                    yield Clear;
                } else {
                    String line = "│  P2: " + printGoods(getPlanet(1));
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "│";
                    yield line;
                }
            }
            case 5 -> {
                if (getNumberOfPlanets() < 3) {
                    yield Clear;
                } else {
                    String line = "│  P3: " + printGoods(getPlanet(2));
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "│";
                    yield line;
                }
            }
            case 6 -> {
                if (getNumberOfPlanets() < 4) {
                    yield Clear;
                } else {
                    String line = "│  P4: " + printGoods(getPlanet(3));
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

    public List<Good> getPlanet(int n) {
        return new ArrayList<>(planets.get(n));
    }

    public void setPlanet(List<Good> planet) {
        this.planets.add(planet);
    }
}
