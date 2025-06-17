package it.polimi.ingsw.view.miniModel.cards;

import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.miniModel.player.MarkerView;

import java.util.ArrayList;
import java.util.List;

public class PlanetsView extends CardView{
    private final int numberOfPlanets;
    private final int flightDays;
    private final List<List<GoodView>> planets;
    private final MarkerView[] playersPosition;
    private int planetSelected;

    public PlanetsView(int ID, boolean covered, int level, int flightDays, List<List<GoodView>> planets) {
        super(ID, covered, level);
        this.numberOfPlanets = planets.size();
        this.flightDays = flightDays;
        this.planets = planets;
        playersPosition = new MarkerView[5];
    }

    public int getFlightDays() {
        return flightDays;
    }

    public List<GoodView> getPlanet(int n) {
        return new ArrayList<>(planets.get(n));
    }

    public int getNumberOfPlanets() {
        return numberOfPlanets;
    }

    public void setPlanetSelected(int planetSelected) {
        this.planetSelected = planetSelected;
    }

    public int getPlanetSelected() {
        return planetSelected;
    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        StringBuilder line = new StringBuilder(switch(l) {
            case 0 -> Up;
            case 1 -> "│      PLANETS      │";
            case 2 -> Clear;
            case 3 -> numberOfPlanets >= 1 ? "│ " + drawPlayer(0) + " P1: " + printPlanet(getPlanet(0)) : Clear;
            case 4 -> numberOfPlanets >= 2 ? "│ " + drawPlayer(1) + " P2: " + printPlanet(getPlanet(1)) : Clear;
            case 5 -> numberOfPlanets >= 3 ? "│ " + drawPlayer(2) + " P3: " + printPlanet(getPlanet(2)) : Clear;
            case 6 -> numberOfPlanets >= 4 ? "│ " + drawPlayer(3) + " P4: " + printPlanet(getPlanet(3)) : Clear;
            case 7 -> numberOfPlanets >= 5 ? "│ " + drawPlayer(4) + " P5: " + printPlanet(getPlanet(4)) : Clear;
            case 8 -> "│   FlightDays: " + getFlightDays();
            case 9 -> Down;
            default -> "";
        });

        while (line.toString().replaceAll("\033\\[[0-9;]*m", "").length() < getColsToDraw() - 1) {
            line.append(" ");
        }
        if (line.toString().replaceAll("\033\\[[0-9;]*m", "").length() == getColsToDraw() - 1) {
            line.append("│");
        }
        return line.toString();
    }

    private String printPlanet(List<GoodView> planet) {
        StringBuilder line = new StringBuilder();
        for (GoodView good : planet) {
            line.append(good.drawTui()).append(" ");
        }
        return line.toString();
    }

    private String drawPlayer(int planet) {
        return playersPosition[planet] == null ? " " : playersPosition[planet].drawTui();
    }

    public void setPlayersPosition(int planet, MarkerView player) {
        playersPosition[planet] = player;
    }

    public MarkerView[] getPlayersPositions() {
        return playersPosition;
    }

    @Override
    public CardViewType getCardViewType() {
        return CardViewType.PLANETS;
    }
}
